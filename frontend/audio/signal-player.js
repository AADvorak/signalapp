import ApiProvider from "~/api/api-provider";
import WavCoder from "~/audio/wav-coder";

const SignalPlayer = {

  audioCtx: null,
  buffer: null,
  source: null,

  signal: null,
  blob: null,

  setSignal(signal) {
    this.stop()
    this.signal = signal
    return this
  },

  setBlob(blob) {
    this.stop()
    this.blob = blob
    return this
  },

  async play(endCallback) {
    if (!this.audioCtx) {
      this.audioCtx = new AudioContext()
    }
    if (!this.buffer) {
      if (this.signal?.data) {
        await this.decodeSignalData()
      } else if (this.signal?.id) {
        await this.load()
      } else if (this.blob) {
        await this.decodeBlob(this.blob)
      }
    }
    if (!this.buffer) {
      return
    }
    this.source = this.audioCtx.createBufferSource()
    this.source.buffer = this.buffer
    this.source.connect(this.audioCtx.destination)
    this.source.start()
    this.source.addEventListener('ended', () => {
      this.clear()
      endCallback && endCallback()
    })
  },

  stop() {
    this.source && this.source.stop()
    this.clear()
  },

  clear() {
    this.buffer = null
    this.source = null
    this.signal = null
    this.blob = null
  },

  async load() {
    const response = await ApiProvider.get('/api/signals/' + this.signal.id + '/wav')
    if (response.ok) {
      await this.decodeArrayBuffer(response.data)
    }
  },

  async decodeSignalData() {
    await this.decodeBlob(WavCoder.signalToWav(this.signal))
  },

  async decodeBlob(blob) {
    await this.decodeArrayBuffer(await blob.arrayBuffer())
  },

  async decodeArrayBuffer(arrayBuffer) {
    await this.audioCtx.decodeAudioData(arrayBuffer,
        data => this.buffer = data,
        error => console.log(error))
  }

}

export default SignalPlayer
