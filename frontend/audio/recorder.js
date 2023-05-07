import InlineWorker from 'inline-worker';
import WavCoder from "~/audio/wav-coder";

export class Recorder {
  config = {
    bufferLen: 4096,
    numChannels: 2,
    mimeType: 'audio/wav'
  };

  recording = false;

  callbacks = {
    getBuffer: [],
    exportWAV: []
  };

  constructor(source, cfg) {
    Object.assign(this.config, cfg);
    this.context = source.context;
    this.node = (this.context.createScriptProcessor ||
        this.context.createJavaScriptNode).call(this.context,
        this.config.bufferLen, this.config.numChannels, this.config.numChannels);

    this.node.onaudioprocess = (e) => {
      if (!this.recording) return;

      var buffer = [];
      for (var channel = 0; channel < this.config.numChannels; channel++) {
        buffer.push(e.inputBuffer.getChannelData(channel));
      }
      this.worker.postMessage({
        command: 'record',
        buffer: buffer
      });
    };

    source.connect(this.node);
    this.node.connect(this.context.destination);    //this should not be necessary

    let self = {};
    this.worker = new InlineWorker(function () {
      let recLength = 0,
          recBuffers = [],
          sampleRate,
          numChannels;

      this.onmessage = function (e) {
        switch (e.data.command) {
          case 'init':
            init(e.data.config);
            break;
          case 'record':
            record(e.data.buffer);
            break;
          case 'exportWAV':
            exportWAV(e.data.type);
            break;
          case 'getBuffer':
            getBuffer();
            break;
          case 'clear':
            clear();
            break;
        }
      };

      function init(config) {
        sampleRate = config.sampleRate;
        numChannels = config.numChannels;
        initBuffers();
      }

      function record(inputBuffer) {
        for (var channel = 0; channel < numChannels; channel++) {
          recBuffers[channel].push(inputBuffer[channel]);
        }
        recLength += inputBuffer[0].length;
      }

      var exportWAV = (type) => {
        let buffers = [];
        for (let channel = 0; channel < numChannels; channel++) {
          buffers.push(mergeBuffers(recBuffers[channel], recLength));
        }
        let interleaved;
        if (numChannels === 2) {
          interleaved = interleave(buffers[0], buffers[1]);
        } else {
          interleaved = buffers[0];
        }
        let dataview = encodeWAV(interleaved);
        let audioBlob = new Blob([dataview], {type: type});

        this.postMessage({command: 'exportWAV', data: audioBlob});
      }

      var getBuffer = () => {
        let buffers = [];
        for (let channel = 0; channel < numChannels; channel++) {
          buffers.push(mergeBuffers(recBuffers[channel], recLength));
        }
        this.postMessage({command: 'getBuffer', data: buffers});
      }

      function clear() {
        recLength = 0;
        recBuffers = [];
        initBuffers();
      }

      function initBuffers() {
        for (let channel = 0; channel < numChannels; channel++) {
          recBuffers[channel] = [];
        }
      }

      function mergeBuffers(recBuffers, recLength) {
        let result = new Float32Array(recLength);
        let offset = 0;
        for (let i = 0; i < recBuffers.length; i++) {
          result.set(recBuffers[i], offset);
          offset += recBuffers[i].length;
        }
        return result;
      }

      function interleave(inputL, inputR) {
        let length = inputL.length + inputR.length;
        let result = new Float32Array(length);

        let index = 0,
            inputIndex = 0;

        while (index < length) {
          result[index++] = inputL[inputIndex];
          result[index++] = inputR[inputIndex];
          inputIndex++;
        }
        return result;
      }

      function encodeWAV(samples) {
        return WavCoder.encodeWAV(samples, numChannels, sampleRate)
      }
    }, self);

    this.worker.postMessage({
      command: 'init',
      config: {
        sampleRate: this.context.sampleRate,
        numChannels: this.config.numChannels
      }
    });

    this.worker.onmessage = (e) => {
      let cb = this.callbacks[e.data.command].pop();
      if (typeof cb == 'function') {
        cb(e.data.data);
      }
    };
  }


  record() {
    this.recording = true;
  }

  stop() {
    this.recording = false;
  }

  clear() {
    this.worker.postMessage({command: 'clear'});
  }

  getBuffer(cb) {
    cb = cb || this.config.callback;
    if (!cb) throw new Error('Callback not set');

    this.callbacks.getBuffer.push(cb);

    this.worker.postMessage({command: 'getBuffer'});
  }

  exportWAV(cb, mimeType) {
    mimeType = mimeType || this.config.mimeType;
    cb = cb || this.config.callback;
    if (!cb) throw new Error('Callback not set');

    this.callbacks.exportWAV.push(cb);

    this.worker.postMessage({
      command: 'exportWAV',
      type: mimeType
    });
  }

  static
  forceDownload(blob, filename) {
    let url = (window.URL || window.webkitURL).createObjectURL(blob);
    let link = window.document.createElement('a');
    link.href = url;
    link.download = filename || 'output.wav';
    let click = document.createEvent("Event");
    click.initEvent("click", true, true);
    link.dispatchEvent(click);
  }
}

export default Recorder;
