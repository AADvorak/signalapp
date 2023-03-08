Correlator = {

  /**
   * @param {Signal[]} signals
   */
  init(signals) {
    const {data, xMin} = SignalUtils.estimateCorrelationFunction(signals[0], signals[1])
    Workspace.startModule({
      module: 'Cable',
      param: {
        name: 'Correlator output signal',
        description: this.makeNewSignalDescription(signals),
        sampleRate: signals[0].sampleRate,
        xMin,
        data
      }
    }).then()
  },

  /**
   * @param {Signal[]} signals
   * @returns {string}
   */
  makeNewSignalDescription(signals) {
    let description = `Correlation of signals:`
    for (let signal of signals) {
      description += `\n${signal.name},`
    }
    return description
  }

}