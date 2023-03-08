Integrator = {

  /**
   * @param {Signal} signal
   */
  init(signal) {
    signal.description += `\nTransformed by integrator`
    signal.data = SignalUtils.integrate(signal.data, signal.params.step)
    Workspace.startModule({
      module: 'Cable',
      param: signal
    }).then()
  },

}
