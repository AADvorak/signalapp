ChangePassword = {

  init() {
    this.selectElements()
    this.initEvents()
  },

  selectElements() {
    this.ui = {}
    this.ui.form = {}
    this.ui.form.oldPassword = $('#ChangePasswordOldPassword')
    this.ui.form.oldPasswordValid = $('#ChangePasswordOldPasswordValid')
    this.ui.form.password = $('#ChangePasswordPassword')
    this.ui.form.passwordValid = $('#ChangePasswordPasswordValid')
    this.ui.form.passwordRepeat = $('#ChangePasswordPasswordRepeat')
    this.ui.form.passwordRepeatValid = $('#ChangePasswordPasswordRepeatValid')
    this.ui.changePasswordBtn = $('#ChangePasswordChangePassword')
  },

  initEvents() {
    this.ui.changePasswordBtn.on('click', () => {
      this.changePassword().then()
    })
    EVENTS.USER_SIGNED_OUT.subscribe(this, 'close')
  },

  async changePassword() {
    this.removeAllInvalidIndication()
    let values = this.getFormValues()
    if (!this.validatePasswordAndRepeat(values)) {
      return
    }
    delete values.passwordRepeat
    let result = await ApiProvider.putJson('/api/users/me/password', values)
    if (!result.ok) {
      if (result.status === 400) {
        for (let error of result.errors) {
          this.markInvalid(error.field, error.message)
        }
      }
    } else {
      Workspace.closeModule(this)
      Workspace.showSuccess("Password changed successfully")
    }
  },

  getFormValues() {
    let form = this.ui.form
    return {
      oldPassword: form.oldPassword.val(),
      password: form.password.val(),
      passwordRepeat: form.passwordRepeat.val(),
    }
  },

  validatePasswordAndRepeat(values) {
    if (values.password !== values.passwordRepeat) {
      this.markInvalid('password', 'Must be the same')
      this.markInvalid('passwordRepeat', 'Must be the same')
      return false
    }
    return true
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