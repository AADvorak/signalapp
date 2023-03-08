TopMenu = {

  init() {
    this.selectElements()
    this.initEvents()
    this.refreshUserSubmenu()
    this.getUserData().then()
  },

  selectElements() {
    this.ui = {}
    this.ui.modulesSubmenu = $('#TopMenuModulesSubmenu')
    this.ui.userSubmenu = $('#TopMenuUserSubmenu')
    this.ui.userButton = $('#TopMenuUser')
    this.ui.logo = $('#TopMenuLogo')
  },

  initEvents() {
    this.ui.logo.on('click', () => {
      Workspace.setStartPage().then()
    })
    EVENTS.MODULES_LOADED.subscribe(this, 'refreshModulesSubmenu')
    EVENTS.USER_SIGNED_IN.subscribe(this, 'refreshUserSubmenu')
    EVENTS.USER_SIGNED_OUT.subscribe(this, 'refreshUserSubmenu')
    EVENTS.USER_INFO_CHANGED.subscribe(this, 'refreshUserSubmenu')
  },

  refreshModulesSubmenu() {
    this.ui.modulesSubmenu.html('')
    for (let module of Workspace.getModulesForMenu()) {
      this.addModuleLink(module)
    }
  },

  addModuleLink({module, name}) {
    let linkId = 'TopMenuLink' + module
    this.appendSubmenuButton(this.ui.modulesSubmenu, linkId, name,
        () => Workspace.startModule({module}))
  },

  refreshUserSubmenu(user) {
    this.ui.userSubmenu.html('')
    if (!user) {
      this.ui.userButton.html('User')
      this.appendSubmenuButton(this.ui.userSubmenu, 'TopMenuUserSignIn', 'Sign in',
          () => Workspace.startModule({module: 'SignIn'}))
      this.appendSubmenuButton(this.ui.userSubmenu, 'TopMenuUserSignUp', 'Sign up',
          () => Workspace.startModule({module: 'SignUp'}))
    } else {
      this.ui.userButton.html(this.makeUserNameForButton(user))
      this.appendSubmenuButton(this.ui.userSubmenu, 'TopMenuUserSignOut', 'Sign out',
          () => this.signOut().then())
      this.appendSubmenuButton(this.ui.userSubmenu, 'TopMenuUserSettings', 'Settings',
          () => Workspace.startModule({module: 'UserSettings'}))
    }
  },

  makeUserNameForButton(user) {
    if (user.firstName && user.lastName && user.patronymic) {
      return user.firstName.substring(0, 1) + '.' + user.patronymic.substring(0, 1) + '. ' + user.lastName
    }
    if (user.firstName && user.lastName) {
      return user.firstName.substring(0, 1) + '. ' + user.lastName
    }
    if (user.firstName) {
      return user.firstName
    }
    if (user.lastName) {
      return user.lastName
    }
    return user.email
  },

  appendSubmenuButton(submenu, buttonId, buttonText, clickHandler) {
    submenu.append(`<button id="${buttonId}" class="dropdown-item" type="button">${buttonText}</button>`)
    let element = $('#' + buttonId)
    element.on('click', clickHandler)
  },

  async signOut() {
    await ApiProvider.del('/api/sessions/')
    EVENTS.USER_SIGNED_OUT.trigger()
  },

  async getUserData() {
    let result = await ApiProvider.get('/api/users/me/', true)
    if (result.ok) {
      EVENTS.USER_SIGNED_IN.trigger(result.data)
    }
  }

}