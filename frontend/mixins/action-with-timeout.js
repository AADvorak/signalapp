export default {
  methods: {
    actionWithTimeout(action) {
      const objectName = 'formValues'
      const currentValue = JSON.stringify(this[objectName])
      setTimeout(() => {
        if (currentValue === JSON.stringify(this[objectName])) {
          action()
        }
      }, 600)
    }
  }
}
