StartPage = {

  init() {
    $('#StartPageSMLink').on('click', () => this.startModule('SignalManager'))
    $('#StartPageSGLink').on('click', () => this.startModule('SignalGenerator'))
    $('#StartPageSRLink').on('click', () => this.startModule('SignalRecorder'))
    $('#StartPageMMLink').on('click', () => this.startModule('ModuleManager'))
  },

  startModule(module) {
    Workspace.startModule({
      module
    }).then()
  }

}
