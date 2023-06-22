/**
 * @typedef {Object} Signal
 * @property {number} [id]
 * @property {string} name
 * @property {string} description
 * @property {number} maxAbsY
 * @property {number} sampleRate
 * @property {number} xMin
 * @property {number[]} [data]
 * @property {SignalParams} params
 */

/**
 * @typedef {Object} SignalParams
 * @property {number} xMin
 * @property {number} xMax
 * @property {number} step
 * @property {number} length
 */

const SignalUtils = {

  MAX_DRAWING_SIGNAL_LENGTH: 20000,

  /**
   * @param {Signal} signal
   */
  calculateSignalParams(signal) {
    let xMin = signal.xMin
    let step = Number.parseFloat((1 / signal.sampleRate).toFixed(10))
    let length = signal.data.length * step
    let xMax = xMin + length
    signal.params = {
      xMin, xMax, step, length
    }
  },

  /**
   * @param {Signal[]} signals
   */
  checkSignalsHaveSameValueGrid(signals) {
    let sampleRate = signals[0].sampleRate
    for (let signal of signals) {
      if (signal.sampleRate !== sampleRate) return false
      for (let signalToCompare of signals) {
        if (!Number.isInteger((signal.xMin - signalToCompare.xMin) * sampleRate))
          return false
      }
    }
    return true
  },

  /**
   * @param {Signal} signal
   * @param {number} x
   */
  getSignalValue(signal, x) {
    if (x > signal.params.xMax || x < signal.params.xMin) {
      return 0.0
    }
    let i = Math.round((x - signal.params.xMin) / signal.params.step)
    return signal.data[i] || 0.0
  },

  /**
   * @param {Signal[]} signals
   */
  makeCommonSignalsValueGrid(signals) {
    let step = signals[0].params.step
    let xMin = signals[0].params.xMin
    let xMax = signals[0].params.xMax
    for (let signal of signals) {
      if (signal.params.xMin < xMin) xMin = signal.params.xMin
      if (signal.params.xMax > xMax) xMax = signal.params.xMax
    }
    step = this.increaseStepToReducePointsNumber(step, xMin, xMax)
    let grid = []
    let x = xMin
    while (x <= xMax) {
      grid.push(x)
      x += step
    }
    return grid
  },

  increaseStepToReducePointsNumber(step, xMin, xMax) {
    const checkNumberOfPoints = (step, xMin, xMax) => {
      return ((xMax - xMin) / step) <= this.MAX_DRAWING_SIGNAL_LENGTH
    }
    if (checkNumberOfPoints(step, xMin, xMax)) {
      return step
    }
    let multiplier = 2
    while (true) {
      if (checkNumberOfPoints(step * multiplier, xMin, xMax)) {
        return step * multiplier
      }
      multiplier++
    }
  },

  calculateMaxAbsY(signal) {
    let maxAbsY = 0
    for (let point of signal.data) {
      const absY = Math.abs(point)
      if (maxAbsY < absY) {
        maxAbsY = absY
      }
    }
    signal.maxAbsY = maxAbsY
  }

}

export default SignalUtils