SelfCorrelator = {

  init(param) {
    const {data, xMin} = SignalUtils.estimateCorrelationFunction(param, param)
    param.data = data
    param.xMin = xMin
    param.name += '\n (Self correlation function)'
    Workspace.startModule({
      module: 'Cable',
      param
    }).then()
  }

}