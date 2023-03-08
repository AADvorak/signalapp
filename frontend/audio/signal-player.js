import ApiProvider from "~/api/api-provider";

const SignalPlayer = {

  audioCtx: null,
  buffer: null,
  source: null,

  signalId: undefined,

  setSignalId(signalId) {
    this.stop()
    this.signalId = signalId
    return this
  },

  async play(endCallback) {
    if (!this.audioCtx) {
      this.audioCtx = new AudioContext()
    }
    if (!this.buffer) {
      await this.load()
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
    this.signalId = undefined
  },

  async load() {
    let response = await ApiProvider.get('/api/signals/' + this.signalId + '/wav')
    if (response.ok) {
      try {
        await this.audioCtx.decodeAudioData(response.data, data => this.buffer = data)
      } catch (e) {
        // todo
      }
    }
  }

}

export default SignalPlayer