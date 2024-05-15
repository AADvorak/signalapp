export default {
  methods: {
    actionWithTimeout(action, objName) {
      const objectName = objName || 'formValues'
      const currentValue = JSON.stringify(this[objectName])
      setTimeout(() => {
        if (currentValue === JSON.stringify(this[objectName])) {
          action()
        }
      }, 600)
    }
  }
}
