SignUp = {

  init() {
    this.selectElements()
    this.initEvents()
  },

  selectElements() {
    this.ui = {}
    this.ui.form = {}
    this.ui.form.email = $('#SignUpEmail')
    this.ui.form.emailValid = $('#SignUpEmailValid')
    this.ui.form.password = $('#SignUpPassword')
    this.ui.form.passwordValid = $('#SignUpPasswordValid')
    this.ui.form.passwordRepeat = $('#SignUpPasswordRepeat')
    this.ui.form.passwordRepeatValid = $('#SignUpPasswordRepeatValid')
    this.ui.form.firstName = $('#SignUpFirstName')
    this.ui.form.firstNameValid = $('#SignUpFirstNameValid')
    this.ui.form.lastName = $('#SignUpLastName')
    this.ui.form.lastNameValid = $('#SignUpLastNameValid')
    this.ui.form.patronymic = $('#SignUpPatronymic')
    this.ui.form.patronymicValid = $('#SignUpPatronymicValid')
    this.ui.signUpBtn = $('#SignUpSignUp')
    this.ui.signInBtn = $('#SignUpSignIn')
  },

  initEvents() {
    this.ui.signUpBtn.on('click', () => {
      this.signUp().then()
    })
    this.ui.signInBtn.on('click', () => {
      Workspace.startModule({module: 'SignIn'}).then()
    })
  },

  async signUp() {
    this.removeAllInvalidIndication()
    let values = this.getFormValues()
    if (!this.validatePasswordAndRepeat(values)) {
      return
    }
    delete values.passwordRepeat
    let result = await ApiProvider.postJson('/api/users/', values)
    if (!result.ok) {
      if (result.status === 400) {
        for (let error of result.errors) {
          this.markInvalid(error.field, error.message)
        }
      }
    } else {
      Workspace.closeModule(this, true)
      EVENTS.USER_SIGNED_IN.trigger(result.data)
    }
  },

  getFormValues() {
    let form = this.ui.form
    return {
      email: form.email.val(),
      password: form.password.val(),
      passwordRepeat: form.passwordRepeat.val(),
      firstName: form.firstName.val(),
      lastName: form.lastName.val(),
      patronymic: form.patronymic.val(),
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