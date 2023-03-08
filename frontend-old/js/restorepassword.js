RestorePassword = {

  init() {
    this.selectElements()
    this.initEvents()
  },

  selectElements() {
    this.ui = {}
    this.ui.form = {}
    this.ui.form.email = $('#RestorePasswordEmail')
    this.ui.form.emailValid = $('#RestorePasswordEmailValid')
    this.ui.restoreBtn = $('#RestorePasswordRestore')
    this.ui.formAndButtonDiv = $('#RestorePasswordFormAndButton')
    this.ui.successDiv = $('#RestorePasswordSuccess')
    this.ui.errorDiv = $('#RestorePasswordError')
  },

  initEvents() {
    this.ui.restoreBtn.on('click', () => {
      this.restorePassword().then()
    })
  },

  async restorePassword() {
    this.removeAllInvalidIndication()
    let email = this.getEmail()
    if (!email) {
      this.markInvalid('email', 'Must be not empty')
      return
    }
    let result = await ApiProvider.postJson('/api/users/restore/' + this.getEmail(), {})
    if (result && result.status === 400) {
      for (let error of result.errors) {
        this.markInvalid(error.field, error.message)
      }
    } else {
      this.ui.formAndButtonDiv.prop('hidden', true)
      if (result && result.ok) {
        this.ui.successDiv.prop('hidden', false)
      } else {
        this.ui.errorDiv.prop('hidden', false)
      }
    }
  },

  getEmail() {
    return this.ui.form.email.val()
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