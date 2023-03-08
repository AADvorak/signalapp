SpectrumAnalyser = {

  /**
   * @param {Signal} signal
   */
  init(signal) {
    let N = this.getPowOfTwoLength(signal.data.length)
    signal.description += '\nTransformed by spectrum analyser'
    signal.data = this.transformDCT(signal.data, N)
    signal.sampleRate =  2 * N / signal.sampleRate
    Workspace.startModule({
      module: 'Cable',
      param: signal
    }).then()
  },

  transformDCT (data, N) {
    let output = []
    for (let k = 0; k < N; k++) {
      let y = 0
      for (let n = 0; n < N; n++) {
        y += data[n] * this.kernel(n, k, N)
      }
      output.push(y)
    }
    this.postprocess(output)
    return output
  },

  getPowOfTwoLength(fullLength) {
    let powOfTwoLength = 1
    while (powOfTwoLength <= fullLength) {
      powOfTwoLength *= 2
    }
    return powOfTwoLength / 2
  },

  kernel(n, k, N) {
    return Math.cos(Math.PI * k * (n + 0.5) / N)
  },

  postprocess(data) {
    for (let i = 0; i < data.length; i++) {
      data[i] = Math.abs(data[i])
    }
    for (let i = 1; i < data.length - 1; i++) {
      if (data[i] < data[i - 1] && data[i] < data[i + 1]) {
        data[i] = (data[i - 1] + data[i + 1]) / 2
      }
    }
    let maximum = 0
    for (let y of data) {
      if (y > maximum) maximum = y
    }
    for (let i = 0; i < data.length; i++) {
      data[i] /= maximum
    }
  }

}
