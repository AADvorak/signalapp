UserSettings = {

  init() {
    this.selectElements()
    this.initEvents()
    this.loadUserInfo().then()
  },

  selectElements() {
    this.ui = {}
    this.ui.form = {}
    this.ui.form.email = $('#UserSettingsEmail')
    this.ui.form.emailValid = $('#UserSettingsEmailValid')
    this.ui.form.firstName = $('#UserSettingsFirstName')
    this.ui.form.firstNameValid = $('#UserSettingsFirstNameValid')
    this.ui.form.lastName = $('#UserSettingsLastName')
    this.ui.form.lastNameValid = $('#UserSettingsLastNameValid')
    this.ui.form.patronymic = $('#UserSettingsPatronymic')
    this.ui.form.patronymicValid = $('#UserSettingsPatronymicValid')
    this.ui.saveBtn = $('#UserSettingsSave')
    this.ui.signOutBtn = $('#UserSettingsSignOut')
    this.ui.changePasswordBtn = $('#UserSettingsChangePassword')
    this.ui.emailConfirmBtn = $('#UserSettingsConfirm')
    this.ui.deleteAccountBtn = $('#UserSettingsDeleteAccount')
    this.ui.emailConfirmDiv = $('#UserSettingsConfirmDiv')
    this.ui.confirmSentDiv = $('#UserSettingsConfirmationSent')
    this.ui.confirmSendErrorDiv = $('#UserSettingsConfirmationSendError')
  },

  initEvents() {
    this.ui.saveBtn.on('click', () => {
      this.save().then()
    })
    this.ui.signOutBtn.on('click', () => {
      TopMenu.signOut().then()
    })
    this.ui.changePasswordBtn.on('click', () => {
      Workspace.startModule({module: 'ChangePassword'}).then()
    })
    this.ui.deleteAccountBtn.on('click', () => {
      this.deleteAccount().then()
    })
    this.ui.emailConfirmBtn.on('click', () => {
      this.sendConfirm().then()
    })
    EVENTS.USER_SIGNED_OUT.subscribe(this, 'close')
  },

  async save() {
    this.removeAllInvalidIndication()
    let result = await ApiProvider.putJson('/api/users/me/', this.getFormValues())
    if (!result.ok) {
      if (result.status === 400) {
        for (let error of result.errors) {
          this.markInvalid(error.field, error.message)
        }
      }
    } else {
      this.refreshForm(result.data)
      EVENTS.USER_INFO_CHANGED.trigger(result.data)
      Workspace.showSuccess("User info changed")
    }
  },

  async deleteAccount() {
    let result = await ApiProvider.del('/api/users/me/')
    if (!result.ok) {
      return
    }
    EVENTS.USER_SIGNED_OUT.trigger()
    Workspace.closeModule(this)
  },

  async loadUserInfo() {
    let result = await ApiProvider.get('/api/users/me/')
    if (!result.ok) {
      return
    }
    this.refreshForm(result.data)
  },

  refreshForm(data) {
    this.setFormValues(data)
    if (!data.emailConfirmed) {
      this.ui.emailConfirmDiv.prop('hidden', false)
    }
  },

  async sendConfirm() {
    this.ui.confirmSentDiv.prop('hidden', true)
    this.ui.confirmSendErrorDiv.prop('hidden', true)
    let result = await ApiProvider.postJson('/api/users/confirm', {})
    if (result.ok) {
      this.ui.emailConfirmDiv.prop('hidden', true)
      this.ui.confirmSentDiv.prop('hidden', false)
    } else {
      this.ui.confirmSendErrorDiv.prop('hidden', false)
    }
  },

  setFormValues(values) {
    let form = this.ui.form
    form.email.val(values.email)
    form.firstName.val(values.firstName)
    form.lastName.val(values.lastName)
    form.patronymic.val(values.patronymic)
  },

  getFormValues() {
    let form = this.ui.form
    return {
      email: form.email.val(),
      firstName: form.firstName.val(),
      lastName: form.lastName.val(),
      patronymic: form.patronymic.val(),
    }
  },

  close() {
    Workspace.closeModule(this)
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
  }

}