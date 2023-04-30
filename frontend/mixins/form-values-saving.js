import formValues from "~/mixins/form-values";

export default {
  extends: formValues,
  computed: {
    formValuesKey() {
      return this.$options.name + 'FormValues'
    }
  },
  methods: {
    saveFormValues() {
      localStorage.setItem(this.formValuesKey, JSON.stringify(this.formValues))
    },
    restoreFormValues() {
      const formJson = localStorage.getItem(this.formValuesKey)
      if (formJson) {
        const parsedForm = JSON.parse(formJson)
        for (let field in parsedForm) {
          if (parsedForm.hasOwnProperty(field) && this.form.hasOwnProperty(field)) {
            this.formValue(field, parsedForm[field])
          }
        }
      }
    }
  }
}