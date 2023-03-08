export default {
  computed: {
    formValuesKey() {
      return this.$options.name + 'FormValues'
    }
  },
  methods: {
    saveFormValues() {
      localStorage.setItem(this.formValuesKey, JSON.stringify(this.form))
    },
    restoreFormValues() {
      let values = localStorage.getItem(this.formValuesKey)
      if (values) {
        let parsedValues = JSON.parse(values)
        for (let key in parsedValues) {
          if (parsedValues.hasOwnProperty(key) && this.form.hasOwnProperty(key)) {
            this.form[key] = parsedValues[key]
          }
        }
      }
    }
  }
}