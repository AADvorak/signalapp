ModuleEditor = {

  init(module) {
    this.module = module
    this.selectElements()
    this.initEvents()
    if (this.module) this.fillForm()
    this.showBackLinkIfModuleIsInMainContainer()
  },

  selectElements() {
    this.ui = {}
    this.ui.form = {}
    this.ui.form.module = $('#ModuleEditorModule')
    this.ui.form.moduleValid = $('#ModuleEditorModuleValid')
    this.ui.form.name = $('#ModuleEditorName')
    this.ui.form.nameValid = $('#ModuleEditorNameValid')
    this.ui.form.containerSel = $('#ModuleEditorContainer')
    this.ui.form.forMenuRbtn = $('#ModuleEditorForMenu')
    this.ui.form.transformerRbtn = $('#ModuleEditorTransformer')
    this.ui.form.jsGroup = $('#ModuleEditorJsGroup')
    this.ui.form.jsInput = $('#ModuleEditorJsInput')
    this.ui.form.jsDownloadBtn = $('#ModuleEditorJsDownload')
    this.ui.form.htmlGroup = $('#ModuleEditorHtmlGroup')
    this.ui.form.htmlInput = $('#ModuleEditorHtmlInput')
    this.ui.form.htmlDownloadBtn = $('#ModuleEditorHtmlDownload')
    this.ui.saveBtn = $('#ModuleEditorSave')
    this.ui.deleteBtn = $('#ModuleEditorDelete')
    this.ui.backLink = $('#ModuleEditorBack')
  },

  initEvents() {
    EVENTS.USER_SIGNED_OUT.subscribe(this, 'closeModule')
    this.ui.saveBtn.on('click', () => {
      this.saveModule().then()
    })
    this.ui.deleteBtn.on('click', () => {
      this.deleteModule().then()
    })
    this.ui.form.htmlDownloadBtn.on('click', () => {
      this.download('html').then()
    })
    this.ui.form.jsDownloadBtn.on('click', () => {
      this.download('js').then()
    })
    this.ui.form.jsInput.on('change', () => {
      this.upload('js', this.ui.form.jsInput).then()
    })
    this.ui.form.htmlInput.on('change', () => {
      this.upload('html', this.ui.form.htmlInput).then()
    })
    this.ui.backLink.on('click', () => {
      Workspace.startModule({module: 'ModuleManager'}).then()
    })
  },

  fillForm() {
    this.ui.form.module.val(this.module.module)
    this.ui.form.module.prop('disabled', !!this.module.id)
    this.ui.form.name.val(this.module.name)
    this.ui.form.containerSel.val(this.module.container)
    this.ui.form.forMenuRbtn.prop('checked', this.module.forMenu)
    this.ui.form.transformerRbtn.prop('checked', this.module.transformer)
    this.ui.form.jsGroup.prop('hidden', !this.module.id)
    this.ui.form.htmlGroup.prop('hidden', !(this.module.id && this.module.container))
    this.ui.deleteBtn.prop('hidden', !this.module.id || !this.module.personal)
    if (!this.module.personal) {
      this.ui.form.name.prop('disabled', true)
      this.ui.form.containerSel.prop('disabled', true)
      this.ui.form.forMenuRbtn.prop('disabled', true)
      this.ui.form.transformerRbtn.prop('disabled', true)
      this.ui.form.jsInput.prop('disabled', true)
      this.ui.form.htmlInput.prop('disabled', true)
      this.ui.saveBtn.prop('hidden', true)
    }
  },

  showBackLinkIfModuleIsInMainContainer() {
    let moduleInMainContainer = Workspace.modulesInContainers['main']
    if (moduleInMainContainer && moduleInMainContainer.obj === this) {
      this.ui.backLink.prop('hidden', false)
    }
  },

  getFormValues() {
    let form = this.ui.form
    return {
      module: form.module.val(),
      name: form.name.val(),
      container: form.containerSel.val() || null,
      forMenu: form.forMenuRbtn.prop('checked'),
      transformer: form.transformerRbtn.prop('checked')
    }
  },

  async saveModule() {
    this.removeAllInvalidIndication()
    let newModule = this.getFormValues()
    let result
    if (this.module && this.module.id) {
      delete newModule.module
      result = await ApiProvider.putJson('/api/modules/' + this.module.id, newModule)
    } else {
      result = await ApiProvider.postJson('/api/modules', newModule)
    }
    if (result.ok) {
      EVENTS.MODULES_CHANGED.trigger()
      this.module = result.data
      this.fillForm()
    } else if (result.status === 400) {
      for (let error of result.errors) {
        this.markInvalid(error.field, error.message)
      }
    }
  },

  async deleteModule() {
    if (this.module && this.module.id) {
      let result = await ApiProvider.del('/api/modules/' + this.module.id)
      if (result.ok) {
        Workspace.closeModule(this)
        EVENTS.MODULES_CHANGED.trigger()
      } else {
        Workspace.showAlert('Error deleting module')
      }
    }
  },

  async download(extension) {
    let result = await ApiProvider.get('/api/modules/' + this.module.id + '/' + extension)
    if (result.ok) {
      FileManager.saveToFile(result.data, this.module.module.toLowerCase() + '.' + extension)
    } else {
      Workspace.showAlert('Error downloading module ' + extension)
    }
  },

  async upload(extension, input) {
    let data = await FileManager.readFromFile(input)
    let result = await ApiProvider.putText('/api/modules/' + this.module.id + '/' + extension, data, extension)
    if (result.ok) {
      Workspace.showSuccess('Module ' + extension + ' upload success')
    } else {
      Workspace.showAlert('Error uploading module ' + extension)
    }
  },

  markInvalid(fieldName, msg) {
    let form = this.ui.form
    form[fieldName].addClass('is-invalid')
    form[fieldName + 'Valid'].html(msg)
  },

  removeInvalidIndication(fieldName) {
    let form = this.ui.form
    form[fieldName].removeClass('is-invalid')
  },

  removeAllInvalidIndication() {
    for (let fieldName in this.ui.form) {
      this.removeInvalidIndication(fieldName)
    }
  },

  closeModule() {
    Workspace.closeModule(this)
  }

}
