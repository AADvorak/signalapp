import WavDecoder from 'wav-decoder'

const WavCoder = {

  signalToWav(signal) {
    const data = signal.data.map(value => value / signal.maxAbsY)
    const dataView = this.encodeWAV(data, 1, signal.sampleRate)
    return new Blob([dataView], {type: 'audio/wav'})
  },

  async wavToSignals(arrayBuffer) {
    const audioData = await this.decodeWAV(arrayBuffer)
    return audioData.channelData.map(channelDataItem => ({
      xMin: 0,
      sampleRate: audioData.sampleRate,
      data: Array.prototype.slice.call(channelDataItem)
    }))
  },

  splitChannels(audioData) {
    return audioData.channelData
        .map(channelDataItem => this.encodeWAV(channelDataItem, 1, audioData.sampleRate))
        .map(dataView => new Blob([dataView], {type: 'audio/wav'}))
  },

  async decodeWAV(arrayBuffer) {
    return await WavDecoder.decode(arrayBuffer)
  },

  encodeWAV(samples, numChannels, sampleRate) {
    let buffer = new ArrayBuffer(44 + samples.length * 2);
    let view = new DataView(buffer);

    /* RIFF identifier */
    this.writeString(view, 0, 'RIFF');
    /* RIFF chunk length */
    view.setUint32(4, 36 + samples.length * 2, true);
    /* RIFF type */
    this.writeString(view, 8, 'WAVE');
    /* format chunk identifier */
    this.writeString(view, 12, 'fmt ');
    /* format chunk length */
    view.setUint32(16, 16, true);
    /* sample format (raw) */
    view.setUint16(20, 1, true);
    /* channel count */
    view.setUint16(22, numChannels, true);
    /* sample rate */
    view.setUint32(24, sampleRate, true);
    /* byte rate (sample rate * block align) */
    view.setUint32(28, sampleRate * 4, true);
    /* block align (channel count * bytes per sample) */
    view.setUint16(32, numChannels * 2, true);
    /* bits per sample */
    view.setUint16(34, 16, true);
    /* data chunk identifier */
    this.writeString(view, 36, 'data');
    /* data chunk length */
    view.setUint32(40, samples.length * 2, true);

    this.floatTo16BitPCM(view, 44, samples);

    return view;
  },

  floatTo16BitPCM(output, offset, input) {
    for (let i = 0; i < input.length; i++, offset += 2) {
      let s = Math.max(-1, Math.min(1, input[i]));
      output.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7FFF, true);
    }
  },

  writeString(view, offset, string) {
    for (let i = 0; i < string.length; i++) {
      view.setUint8(offset + i, string.charCodeAt(i));
    }
  },
}

export default WavCoder
