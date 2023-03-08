Cable = {

  /**
   * @param {Signal} signal
   */
  init(signal) {
    this.signal = signal
    this.countSignalMaxAbsY()
    this.selectElements()
    this.initEvents()
    this.fillForm()
    this.addSendLinks()
    this.showPreview()
    if (this.signalIsSaved()) this.showSaveAsNewBtn()
  },

  selectElements() {
    this.ui = {}
    this.ui.nameInp = $('#CableName')
    this.ui.nameInpValid = $('#CableNameValid')
    this.ui.descriptionInp = $('#CableDescription')
    this.ui.preview = $('#CablePreview')
    this.ui.saveBtn = $('#CableSave')
    this.ui.sendBtn = $('#CableSend')
    this.ui.sendSubmenu = $('#CableSendSubmenu')
    this.ui.backLnk = $('#CableBack')
    this.ui.saveAsNewBtn = $('#CableSaveAsNew')
    this.ui.exportToTxtBtn = $('#CableExportToTxt')
    this.ui.exportToWavBtn = $('#CableExportToWav')
  },

  initEvents() {
    this.ui.saveBtn.on('click', () => {
      this.saveSignal().then()
    })
    this.ui.saveAsNewBtn.on('click', () => {
      this.saveSignalAsNew().then()
    })
    this.ui.backLnk.on('click', () => {
      this.goBack()
    })
    this.ui.exportToTxtBtn.on('click', () => {
      FileManager.saveSignalToTxtFile(this.signal)
    })
    this.ui.exportToWavBtn.on('click', () => {
      if (!this.signalIsSaved() || SignalStack.isNotEmpty()) {
        Workspace.showAlert('Changes must be saved before export')
        return
      }
      FileManager.saveSignalToWavFile(this.signal)
    })
  },

  fillForm() {
    this.ui.nameInp.val(this.signal.name)
    this.ui.descriptionInp.val(this.signal.description)
  },

  addSendLinks() {
    for (let transformer of Workspace.getTransformers()) {
      this.addSendLink(transformer)
    }
  },

  addSendLink({id, module, name}) {
    let linkId = 'CableSendSubmenuLink' + id
    this.ui.sendSubmenu.append(`<button id="${linkId}" class="dropdown-item" type="button">${name}</button>`)
    let element = $('#' + linkId)
    element.on('click', () => this.sendSignal({module}))
  },

  showPreview() {
    ChartDrawer.drawLines({container: this.ui.preview, signals: [this.signal]})
  },

  sendSignal({module}) {
    SignalStack.addSignal(this.signal)
    Workspace.startModule({
      module,
      param: this.copySignal()
    }).then()
  },

  goBack() {
    let lastSignal = SignalStack.takeLastSignal()
    if (lastSignal) {
      Workspace.startModule({
        module: 'Cable',
        param: lastSignal
      }).then()
    } else {
      Workspace.startModule({
        module: SignalStack.initiator
      }).then()
    }
  },

  async saveSignalAsNew() {
    delete this.signal.id
    await this.saveSignal()
  },

  async saveSignal() {
    this.removeInvalidIndication('nameInp')
    let formValues = this.getFormValues()
    this.signal.name = formValues.name
    this.signal.description = formValues.description
    let response
    if (this.signalIsSaved()) {
      response = await ApiProvider.putJson('/api/signals/' + this.signal.id, this.signal)
    } else {
      response = await ApiProvider.postJson('/api/signals/', this.signal)
    }
    if (response.ok) {
      Workspace.startModule({
        module: 'SignalManager'
      }).then()
    } else if (response.status === 401) {
      Workspace.setWaitingForLoginModule('Cable', this.signal)
    } else if (response.status === 400) {
      for (let error of response.errors) {
        if (error.field === 'name') {
          this.markInvalid('nameInp', error.message)
        }
        if (error.field === 'data') {
          Workspace.showAlert('Error saving signal data: ' + error.message)
        }
      }
    }
  },

  countSignalMaxAbsY() {
    if (!this.signal.maxAbsY) {
      this.signal.maxAbsY = SignalUtils.calculateMaxAbsY(this.signal)
    }
  },

  getFormValues() {
    return {
      name: this.ui.nameInp.val(),
      description: this.ui.descriptionInp.val()
    }
  },

  showSaveAsNewBtn() {
    this.ui.saveAsNewBtn.prop('hidden', false)
  },

  signalIsSaved() {
    return !!this.signal.id
  },

  copySignal() {
    return JSON.parse(JSON.stringify(this.signal))
  },

  markInvalid(fieldName, msg) {
    this.ui[fieldName].addClass('is-invalid')
    this.ui[fieldName + 'Valid'].html(msg)
  },

  removeInvalidIndication(fieldName) {
    this.ui[fieldName].removeClass('is-invalid')
  },

}