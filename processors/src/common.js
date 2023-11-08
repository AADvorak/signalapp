import {Counter} from "./counter";

export const Common = {

  /**
   *
   * @param {number[]} inData
   * @param {number} step
   * @param {function[]} equations
   * @param {Object} params
   * @param {number[]} initial
   * @param {number} outNumber
   * @returns {number[]}
   */
  solveDifEq({inData, step, equations, params, initial, outNumber}) {
    return this.solveDifEqEulerMethod({inData, step, equations, params, initial, outNumber})
  },

  solveDifEqEulerMethod({inData, step, equations, params, initial, outNumber}) {
    let outData = []
    let equationsNumber = equations.length
    let currentValues = initial
    outData.push(currentValues[outNumber])
    Counter.init(inData.length)
    for (let i = 1; i < inData.length; i++) {
      let nextValues = []
      for (let n = 0; n < equationsNumber; n++) {
        nextValues.push(currentValues[n] + step * equations[n](inData[i], currentValues, params))
      }
      currentValues = nextValues
      outData.push(currentValues[outNumber])
      Counter.increase()
    }
    return outData
  },

  /**
   * @param {number[]} data
   * @param {number} step
   * @param {String} [operation]
   */
  integrate(data, step, operation) {
    return this.transformWithFunction(data, this.integrateStep, step, operation)
  },

  /**
   * @param {Number} prevY
   * @param {number} y1
   * @param {number} y2
   * @param {number} step
   */
  integrateStep(prevY, y1, y2, step) {
    return prevY + step * (y2 + y1) / 2
  },

  /**
   * @param {number[]} data
   * @param {number} step
   * @param {String} [operation]
   */
  differentiate(data, step, operation) {
    return this.transformWithFunction(data, this.differentiateStep, step, operation)
  },

  /**
   * @param {Number} prevY
   * @param {number} y1
   * @param {number} y2
   * @param {number} step
   */
  differentiateStep(prevY, y1, y2, step) {
    return (y2 - y1) / step
  },

  /**
   * @param {number[]} data
   * @param {Function} func
   * @param {number} step
   * @param {String} [operation]
   */
  transformWithFunction(data, func, step, operation) {
    let output = [0]
    let currY = 0
    Counter.init(data.length, operation)
    for (let i = 1; i < data.length; i++) {
      currY = func(currY, data[i-1], data[i], step)
      output.push(currY)
      Counter.increase()
    }
    return output
  },

  /**
   * @param {Signal} signal1
   * @param {Signal} signal2
   */
  calculateCorrelationFunction(signal1, signal2) {
    let step = signal1.params.step
    let tauMin = - signal2.params.xMax + signal1.params.xMin
    let tauMax = - signal2.params.xMin + signal1.params.xMax
    let xMin = signal1.params.xMin - signal2.params.length
    let xMax = signal1.params.xMax + signal2.params.length
    let tau = tauMin
    let data = []
    Counter.init(Math.floor((tauMax - tauMin) / step))
    while (tau <= tauMax) {
      data.push(this.calculateCorrelationValue(signal1, signal2, tau, xMin, xMax, step))
      tau += step
      Counter.increase()
    }
    return {
      xMin: tauMin,
      data
    }
  },

  /**
   *
   * @param {Signal} signal1
   * @param {Signal} signal2
   * @param {number} tau
   * @param {number} xMin
   * @param {number} xMax
   * @param {number} step
   */
  calculateCorrelationValue(signal1, signal2, tau, xMin, xMax, step) {
    let x = xMin
    let value = 0.0
    while (x <= xMax) {
      value += step * (this.getSignalValue(signal1, x) * this.getSignalValue(signal2, x - tau)
          + this.getSignalValue(signal1, x - step) * this.getSignalValue(signal2, x - step - tau)) / 2
      x += step
    }
    return value
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

  makeCommonSignalsValueGrid(signals) {
    let step = signals[0].params.step
    let xMin = signals[0].params.xMin
    let xMax = signals[0].params.xMax
    for (let signal of signals) {
      if (signal.params.xMin < xMin) xMin = signal.params.xMin
      if (signal.params.xMax > xMax) xMax = signal.params.xMax
    }
    let grid = []
    let x = xMin
    Counter.init(Math.floor((xMax - xMin) / step), 'preparingSignals')
    while (x <= xMax) {
      grid.push(x)
      x += step
      Counter.increase()
    }
    return grid
  },

  /**
   * @param {Signal} signal
   */
  calculateMaxAbsYAndParams(signal) {
    let maxAbsY = 0
    Counter.init(signal.data.length, 'calculatingMaxAbsValue')
    for (let point of signal.data) {
      const absY = Math.abs(point)
      if (maxAbsY < absY) {
        maxAbsY = absY
      }
      Counter.increase()
    }
    signal.maxAbsY = maxAbsY
    const xMin = signal.xMin
    const step = Number.parseFloat((1 / signal.sampleRate).toFixed(10))
    const length = signal.data.length * step
    const xMax = xMin + length
    signal.params = {
      xMin, xMax, step, length
    }
  }

}
