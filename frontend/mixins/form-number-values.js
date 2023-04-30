import formValues from "~/mixins/form-values";

export default {
  extends: formValues,
  methods: {
    parseFloatForm({include, exclude} = {}) {
      for (let field in this.form) {
        if (this.form.hasOwnProperty(field)) {
          if ((include && include.includes(field)
              || exclude && !exclude.includes(field)
              || !include && !exclude) && !isNaN(this.formValue(field))) {
            this.formValue(field, parseFloat(this.formValue(field)))
          }
        }
      }
    },
    getNumberValidationMsg(field) {
      const value = this.formValue(field),
          minValue = this.form[field].params.min,
          maxValue = this.form[field].params.max
      let validationMsg = ''
      if (isNaN(value)) {
        validationMsg = this._tc('validation.number')
      } else if (value < minValue || value > maxValue) {
        validationMsg = this._tc('validation.between', {minValue, maxValue})
      }
      return validationMsg
    },
  }
}
