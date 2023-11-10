export const Counter = {

  stepsNumber: 0,
  currentStep: 0,
  value: 0,
  operation: undefined,

  init(stepsNumber, operation) {
    this.stepsNumber = stepsNumber
    this.operation = operation
    this.value = 0
    this.currentStep = 0
  },

  increase() {
    this.currentStep++
    const value = Math.floor(100 * this.currentStep / this.stepsNumber)
    if (value > this.value) {
      this.value = value
      postMessage({
        progress: {
          value: this.value,
          operation: this.operation
        }
      })
    }
  }

}
