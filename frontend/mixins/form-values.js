export default {
  computed: {
    formValues() {
      let values = {}
      for (let field of this.formFields) {
        values[field] = this.formValue(field)
      }
      return values
    },
    formFields() {
      return Object.keys(this.form)
    },
  },
  methods: {
    formValue(field, newValue) {
      const fieldObj = this.form[field]
      if (fieldObj) {
        if (newValue !== undefined) {
          fieldObj.value = newValue
        }
        return fieldObj.value
      }
    },
    clearForm() {
      for (let field in this.form) {
        if (this.form.hasOwnProperty(field)) {
          this.formValue(field, '')
        }
      }
    },
    focusFirstFormField() {
      setTimeout(() => {
        const inputRefs = this.$refs.inputRefs
        if (inputRefs) {
          const firstInputRef = inputRefs[0]
          firstInputRef?.focus && firstInputRef.focus()
        }
      })
    }
  }
}
