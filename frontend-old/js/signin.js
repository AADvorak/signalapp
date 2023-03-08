SignIn = {

  init() {
    this.selectElements()
    this.initEvents()
  },

  selectElements() {
    this.ui = {}
    this.ui.form = {}
    this.ui.form.email = $('#SignInEmail')
    this.ui.form.emailValid = $('#SignInEmailValid')
    this.ui.form.password = $('#SignInPassword')
    this.ui.form.passwordValid = $('#SignInPasswordValid')
    this.ui.signUpBtn = $('#SignInSignUp')
    this.ui.signInBtn = $('#SignInSignIn')
    this.ui.restorePasswordBtn = $('#SignInRestorePassword')
  },

  initEvents() {
    this.ui.signUpBtn.on('click', () => {
      Workspace.startModule({module: 'SignUp'}).then()
    })
    this.ui.signInBtn.on('click', () => {
      this.signIn().then()
    })
    this.ui.restorePasswordBtn.on('click', () => {
      Workspace.startModule({module: 'RestorePassword'}).then()
    })
  },

  async signIn() {
    this.removeAllInvalidIndication()
    let result = await ApiProvider.postJson('/api/sessions/', this.getFormValues())
    if (!result.ok) {
      if (result.status === 400) {
        for (let error of result.errors) {
          this.markInvalid(error.field, error.message)
        }
      }
    } else {
      EVENTS.USER_SIGNED_IN.trigger(result.data)
      Workspace.closeModule(this, true)
    }
  },

  getFormValues() {
    let form = this.ui.form
    return {
      email: form.email.val(),
      password: form.password.val(),
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
  }

}