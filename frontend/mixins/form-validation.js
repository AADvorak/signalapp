export default {
  methods: {
    pushValidationMsg(field, msg) {
      let fieldObj = this.form[field]
      if (fieldObj) {
        if (!fieldObj.validation) {
          fieldObj.validation = []
        }
        fieldObj.validation.push(msg)
      }
    },
    parseValidation(errors) {
      for (let error of errors) {
        this.pushValidationMsg(error.field, this.getLocalizedErrorMessage(error))
      }
    },
    clearValidation() {
      for (let key in this.form) {
        if (this.form.hasOwnProperty(key)) {
          this.form[key].validation = []
        }
      }
    },
  }
}
