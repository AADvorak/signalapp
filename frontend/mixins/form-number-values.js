export default {
  methods: {
    parseFloatForm({include, exclude} = {}) {
      for (let key in this.form) {
        if (this.form.hasOwnProperty(key)) {
          if (include && include.includes(key)
              || exclude && !exclude.includes(key)
              || !include && !exclude) {
            this.form[key] = parseFloat(this.form[key])
          }
        }
      }
    },
  }
}
