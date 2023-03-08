Adder = {

  /**
   * @param {Signal[]} signals
   */
  init(signals) {
    let commonGrid = SignalUtils.makeCommonSignalsValueGrid(signals)
    let output = {
      name: 'Adder output signal',
      description: this.makeNewSignalDescription(signals),
      xMin: commonGrid[0],
      sampleRate: signals[0].sampleRate,
      data: []
    }
    for (let x of commonGrid) {
      let y = 0
      for (let signal of signals) {
        y += SignalUtils.getSignalValue(signal, x)
      }
      output.data.push(y)
    }
    Workspace.startModule({
      module: 'Cable',
      param: output
    }).then()
  },

  /**
   * @param {Signal[]} signals
   * @returns {string}
   */
  makeNewSignalDescription(signals) {
    let description = `Sum of ${signals.length} signals:`
    for (let signal of signals) {
      description += `\n${signal.name},`
    }
    return description
  }

}
