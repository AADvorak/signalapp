export default {
  methods: {
    parseValidation(errors) {
      for (let error of errors) {
        this.validation[error.field]?.push(error.message)
      }
    },
    clearValidation() {
      for (let key in this.validation) {
        if(this.validation.hasOwnProperty(key)) {
          this.validation[key] = []
        }
      }
    },
  }
}
