export default {
  methods: {
    parseFloatForm({include, exclude} = {}) {
      for (let key in this.form) {
        if (this.form.hasOwnProperty(key)) {
          if ((include && include.includes(key)
              || exclude && !exclude.includes(key)
              || !include && !exclude) && !isNaN(this.form[key])) {
            this.form[key] = parseFloat(this.form[key])
          }
        }
      }
    },
    getNumberValidationMsg(field) {
      const value = this.form[field],
          minValue = this.INPUT_PARAMS[field].min,
          maxValue = this.INPUT_PARAMS[field].max
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
