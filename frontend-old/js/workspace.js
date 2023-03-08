/**
 * @typedef {Object} Module
 * @property {number} [id]
 * @property {string} module
 * @property {string} name
 * @property {string} container
 * @property {boolean} forMenu
 * @property {boolean} isTransformer
 */

Workspace = {

  WIDE_SCREEN_MODE_MIN_WIDTH: 1200,

  MODULES_BASE: [
    {module: 'SignalGenerator', name: 'Signal generator', container: 'left', forMenu: true},
    {module: 'SignalRecorder', name: 'Signal recorder', container: 'left', forMenu: true},
    {module: 'SignalManager', name: 'Signal manager', container: 'main', forMenu: true},
    {module: 'SignalViewer', name: 'Signal viewer', container: 'main', forMenu: false},
    {module: 'ModuleManager', name: 'Module manager', container: 'main', forMenu: true},
    {module: 'ModuleEditor', name: 'Module editor', container: 'right', forMenu: false},
    {module: 'Cable', name: 'Cable', container: 'main', forMenu: false},
    {module: 'Adder', name: 'Signal adder', forMenu: false},
    {module: 'Correlator', name: 'Signal correlator', forMenu: false},
    {module: 'SignUp', name: 'Sign up', container: 'right', forMenu: false},
    {module: 'SignIn', name: 'Sign in', container: 'right', forMenu: false},
    {module: 'UserSettings', name: 'User settings', container: 'right', forMenu: false},
    {module: 'ChangePassword', name: 'Change password', container: 'right', forMenu: false},
    {module: 'RestorePassword', name: 'Restore password', container: 'right', forMenu: false},
  ],

  modules: [],

  modulesInContainers: {},

  waitingModule: undefined,

  async init() {
    this.selectElements()
    this.initEvents()
    this.setScrollHeight()
    await this.loadMenu()
    await this.setStartPage()
    await this.loadSignalStack()
    EVENTS.MODULES_CHANGED.trigger()
  },

  selectElements() {
    this.ui = {}
    this.ui.main = $('#WorkspaceMain')
    this.ui.mainCaption = $('#WorkspaceMainCaption')
    this.ui.mainContainer = $('#WorkspaceMainContainer')
    this.ui.left = $('#WorkspaceLeft')
    this.ui.leftCaption = $('#WorkspaceLeftCaption')
    this.ui.leftContainer = $('#WorkspaceLeftContainer')
    this.ui.leftCloseLnk = $('#WorkspaceLeftClose')
    this.ui.right = $('#WorkspaceRight')
    this.ui.rightCaption = $('#WorkspaceRightCaption')
    this.ui.rightContainer = $('#WorkspaceRightContainer')
    this.ui.rightCloseLnk = $('#WorkspaceRightClose')
    this.ui.menu = $('#WorkspaceMenu')
    this.ui.modal = $('#WorkspaceModal')
    this.ui.modalCaption = $('#WorkspaceModalCaption')
    this.ui.modalContainer = $('#WorkspaceModalContainer')
    this.ui.modalFooter = $('#WorkspaceModalFooter')
    this.ui.modalOkBtn = $('#WorkspaceModalOk')
  },

  initEvents() {
    EVENTS.MODULES_CHANGED.subscribe(this, 'loadModules')
    EVENTS.USER_SIGNED_IN.subscribe(this, 'loadModules')
    EVENTS.USER_SIGNED_OUT.subscribe(this, 'loadModules')
    this.ui.leftCloseLnk.on('click', () => {
      this.closeModule(this.modulesInContainers['left'].obj)
    })
    this.ui.rightCloseLnk.on('click', () => {
      this.closeModule(this.modulesInContainers['right'].obj)
    })
    $(window).on('resize', () => {
      this.actionOnScreenResize()
    })
    $(window).on('orientationchange', () => {
      this.actionOnScreenResize()
    })
  },

  actionOnScreenResize() {
    this.setScrollHeight()
    if (document.documentElement.clientWidth < this.WIDE_SCREEN_MODE_MIN_WIDTH) {
      if (this.modulesInContainers['left']) this.closeModule(this.modulesInContainers['left'].obj)
      if (this.modulesInContainers['right']) this.closeModule(this.modulesInContainers['right'].obj)
    }
  },

  setScrollHeight() {
    let h = document.documentElement.clientHeight - 100
    if (h < 100) h = 100
    this.ui.mainContainer.css('max-height', h)
    this.ui.leftContainer.css('max-height', h)
    this.ui.rightContainer.css('max-height', h)
  },

  async setStartPage() {
    await this.setModuleToContainer({module: 'StartPage', name: 'Start page', container: 'main'})
  },

  async loadMenu() {
    let obj = await ModuleLoader.loadModule({module: 'TopMenu', container: this.ui.menu})
    obj.init()
  },

  async loadSignalStack() {
    let obj = await ModuleLoader.loadModule({module: 'SignalStack'})
    obj.init()
  },

  getModuleByObjName(objName) {
    for (let module of this.getAllModules()) {
      if (module.module === objName) return module
    }
  },

  isUserModule(module) {
    return this.modules.includes(module)
  },

  async startModule({module, param}) {
    let moduleObj = this.getModuleByObjName(module)
    if (moduleObj) {
      let isUserModule = this.isUserModule(moduleObj)
      await this.setModuleToContainer({
        id: moduleObj.id,
        module,
        param,
        name: moduleObj.name,
        container: moduleObj.container,
        isUserModule
      })
    }
  },
  
  async setModuleToContainer({id, module, name, container, param, isUserModule}) {
    if (['left', 'right'].includes(container) && document.documentElement.clientWidth < this.WIDE_SCREEN_MODE_MIN_WIDTH) {
      container = 'main'
    }
    let obj = await ModuleLoader.loadModule({id, module, isUserModule, container: this.ui[container + 'Container']})
    if (container) {
      this.setCaption({name, container})
      if (this.modulesInContainers[container]) {
        this.unsubscribeModuleFromEvents(this.modulesInContainers[container].obj)
      }
      switch (container) {
        case 'modal':
          this.ui.modalOkBtn.off()
          this.ui.modal.modal()
          break
        case 'left':
        case 'right':
          this.openSideContainer(container)
          break
      }
    }
    this.modulesInContainers[container] = {module, obj}
    obj.init(param)
  },

  setCaption({name, container}) {
    this.ui[container + 'Caption'].html(name)
  },

  openSideContainer(container) {
    this.ui[container].attr('class', 'col-3')
    this.ui[container].prop('hidden', false)
    this.setMainContainerWidth()
  },

  closeModule(obj, reopenWaiting) {
    let container
    for (let key in this.modulesInContainers) {
      if (this.modulesInContainers[key].obj === obj) {
        container = key
      }
    }
    if (!container) return
    delete this.modulesInContainers[container]
    this.unsubscribeModuleFromEvents(obj)
    if (['left', 'right'].includes(container)) {
      this.closeSideContainer(container)
    } else if (container === 'modal') {
      this.ui.modal.modal('hide')
    } else if (container === 'main' && reopenWaiting) {
      if (this.waitingModule) {
        this.startModule(this.waitingModule).then()
        this.waitingModule = undefined
      } else {
        this.setStartPage().then()
      }
    }
  },

  setWaitingForLoginModule(module, param) {
    this.waitingModule = {module, param}
  },

  closeSideContainer(container) {
    this.ui[container].attr('class', '')
    this.ui[container].prop('hidden', true)
    this.ui[container + 'Container'].html('')
    this.setMainContainerWidth()
  },

  setMainContainerWidth() {
    let width = 12
    if (!this.ui.right.prop('hidden')) width -= 3
    if (!this.ui.left.prop('hidden')) width -= 3
    this.ui.main.attr('class', 'col-' + width)
  },

  async loadModules() {
    let result = await ApiProvider.get('/api/modules')
    if (!result.ok) {
      return
    }
    this.modules = result.data
    EVENTS.MODULES_LOADED.trigger()
  },

  getTransformers() {
    let transformers = []
    for (let module of this.modules) {
      if (module.transformer) transformers.push(module)
    }
    return transformers
  },

  getModulesForMenu() {
    let forMenu = []
    for (let module of this.getAllModules()) {
      if (module.forMenu) forMenu.push(module)
    }
    return forMenu
  },

  getAllModules() {
    return [...this.MODULES_BASE, ...this.modules]
  },

  getUserModules() {
    return this.modules
  },

  unsubscribeModuleFromEvents(obj) {
    for (let key in EVENTS) {
      if (EVENTS.hasOwnProperty(key)) {
        EVENTS[key].unsubscribe(obj)
      }
    }
  },

  showAlert(msg) {
    $('body').append(`<div class="alert alert-danger workspaceAlert" role="alert">${msg}</div>`)
    setTimeout(() => {
      $('.alert').alert('close')
    }, 5000)
  },

  showSuccess(msg) {
    $('body').append(`<div class="alert alert-success workspaceAlert" role="alert">${msg}</div>`)
    setTimeout(() => {
      $('.alert').alert('close')
    }, 3000)
  },

}