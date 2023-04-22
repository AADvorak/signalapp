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
  }
}
