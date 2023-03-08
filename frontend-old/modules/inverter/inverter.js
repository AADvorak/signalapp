Inverter = {

  init(param) {
    for (let i = 0; i < param.data.length; i++) {
      param.data[i] = -param.data[i]
    }
    param.description += '\nTransformed by inverter'
    Workspace.startModule({
      module: 'Cable',
      param
    }).then()
  }

}