Differentiator = {

  /**
   * @param {Signal} signal
   */
  init(signal) {
    signal.description += `\nTransformed by differentiator`
    signal.data = SignalUtils.differentiate(signal.data, signal.params.step)
    Workspace.startModule({
      module: 'Cable',
      param: signal
    }).then()
  },

}
