export const Counter = {

  pointsNumber: 0,

  currentPoint: 0,

  progress: 0,

  operation: undefined,

  init(pointsNumber, operation) {
    this.pointsNumber = pointsNumber
    this.operation = operation
    this.progress = 0
    this.currentPoint = 0
  },

  increase() {
    this.currentPoint++
    const percent = 100 * this.currentPoint / this.pointsNumber
    if (percent > this.progress) {
      this.progress = Math.floor(percent)
      postMessage({
        progress: this.progress,
        operation: this.operation
      })
    }
  }

}
