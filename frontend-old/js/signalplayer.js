SignalPlayer = {

  audioCtx: null,
  buffer: null,
  source: null,

  signalId: undefined,

  setSignalId(signalId) {
    this.stop()
    this.buffer = null
    this.source = null
    this.signalId = signalId
    return this
  },

  async play() {
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
  },

  stop() {
    this.source && this.source.stop()
  },

  async load() {
    let response = await ApiProvider.get('/api/signals/' + this.signalId + '/wav')
    if (response.ok) {
      try {
        await this.audioCtx.decodeAudioData(response.data, data => this.buffer = data)
      } catch (e) {
        Workspace.showAlert('Unable to play signal')
      }
    }
  }

}