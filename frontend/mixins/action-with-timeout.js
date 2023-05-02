export default {
  methods: {
    actionWithTimeout(objectName, action) {
      const currentValue = JSON.stringify(this[objectName])
      setTimeout(() => {
        if (currentValue === JSON.stringify(this[objectName])) {
          action()
        }
      }, 600)
    }
  }
}
