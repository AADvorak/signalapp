export default {
  methods: {
    pushValidationMsg(field, msg) {
      const fieldObj = this.form[field]
      if (fieldObj) {
        if (!fieldObj.validation) {
          fieldObj.validation = []
        }
        fieldObj.validation.push(msg)
      }
    },
    isFieldValidated(field) {
      return !this.form[field].validation?.length
    },
    isAllFieldsValidated(fields) {
      for (const field of fields) {
        if (!this.isFieldValidated(field)) {
          return false
        }
      }
      return true
    },
    parseValidation(errors) {
      for (let error of errors) {
        error.field && this.pushValidationMsg(error.field, this.getLocalizedErrorMessage(error))
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
