const Common = {

  difEqMethod: 'Euler',

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
    return this['solveDifEq' + this.difEqMethod + 'Method']({inData, step, equations, params, initial, outNumber})
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
  estimateCorrelationFunction(signal1, signal2) {
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
   * @param {Signal[]} signals
   */
  calculateSignalsParams(signals) {
    for (let signal of signals) {
      this.calculateSignalParams(signal)
    }
  },

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

  calculateMaxAbsY(signal) {
    let maxAbsY = 0
    Counter.init(signal.data.length, 'calculatingMaxAbsValue')
    for (let point of signal.data) {
      const absY = Math.abs(point)
      if (maxAbsY < absY) {
        maxAbsY = absY
      }
      Counter.increase()
    }
    return maxAbsY
  }

}

const Counter = {

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

const TransformFunctions = {

  /**
   * amplifiers
   */

  linearAmp(signal, params) {
    Counter.init(signal.data.length)
    for (let i = 0; i < signal.data.length; i++) {
      signal.data[i] *= params.coefficient
      Counter.increase()
    }
    return signal
  },

  piecewiseLinearSymmetricSaturationAmp(signal, params) {
    Counter.init(signal.data.length)
    const maxOutputToCoefficient = params.maxOutput / params.coefficient
    for (let i = 0; i < signal.data.length; i++) {
      if (signal.data[i] < maxOutputToCoefficient && signal.data[i] > - maxOutputToCoefficient) {
        signal.data[i] *= params.coefficient
      } else if (signal.data[i] >= maxOutputToCoefficient) {
        signal.data[i] = params.maxOutput
      } else if (signal.data[i] <= - maxOutputToCoefficient) {
        signal.data[i] = - params.maxOutput
      }
      Counter.increase()
    }
    return signal
  },

  piecewiseLinearAsymmetricSaturationAmp(signal, params) {
    Counter.init(signal.data.length)
    const maxPositiveOutputToCoefficient = params.maxPositiveOutput / params.coefficient
    const maxNegativeOutputToCoefficient = params.maxNegativeOutput / params.coefficient
    for (let i = 0; i < signal.data.length; i++) {
      if (signal.data[i] < maxPositiveOutputToCoefficient && signal.data[i] > - maxNegativeOutputToCoefficient) {
        signal.data[i] *= params.coefficient
      } else if (signal.data[i] >= maxPositiveOutputToCoefficient) {
        signal.data[i] = params.maxPositiveOutput
      } else if (signal.data[i] <= - maxNegativeOutputToCoefficient) {
        signal.data[i] = - params.maxNegativeOutput
      }
      Counter.increase()
    }
    return signal
  },

  inverter(signal) {
    Counter.init(signal.data.length)
    for (let i = 0; i < signal.data.length; i++) {
      signal.data[i] = -signal.data[i]
      Counter.increase()
    }
    return signal
  },

  /**
   * modulators
   */

  amplitudeModulator(signal, params) {
    Counter.init(signal.data.length, 'calculating')
    for (let i = 0; i < signal.data.length; i++) {
      const x = signal.xMin + signal.params.step * i
      signal.data[i] = params.amplitude * (1 + params.depth * signal.data[i] / signal.maxAbsY) * Math.sin(2 * Math.PI * params.frequency * x)
      Counter.increase()
    }
    return signal
  },

  frequencyModulator(signal, params) {
    const integrated = Common.integrate(signal.data, signal.params.step, 'preparingSignal')
    Counter.init(signal.data.length, 'calculating')
    for (let i = 0; i < signal.data.length; i++) {
      const x = signal.xMin + signal.params.step * i
      signal.data[i] = params.amplitude * Math.sin((2 * Math.PI * x + params.coefficient * integrated[i]) * params.frequency)
      Counter.increase()
    }
    return signal
  },

  /**
   * filters
   */

  lpRcFilter(signal, params) {
    const equations = [
      (input, variables, params) => (1 / params.tau) * (input - variables[0])
    ]
    signal.data = Common.solveDifEq({
      inData: signal.data,
      step: signal.params.step,
      equations,
      params,
      initial: [0.0],
      outNumber: 0
    })
    return signal
  },

  hpRcFilter(signal, params) {
    const equations = [
      (input, variables, params) => input - variables[0] / params.tau
    ]
    const inData = Common.differentiate(signal.data, signal.params.step, 'preparingSignal')
    signal.data = Common.solveDifEq({
      inData,
      step: signal.params.step,
      equations,
      params,
      initial: [0.0],
      outNumber: 0
    })
    return signal
  },

  /**
   * oscillator chains
   */

  linearOscillator(signal, params) {
    const equations = [
      (input, variables, params) => variables[1],
      (input, variables, params) => input - 2 * params.damping * variables[1] -
          Math.pow(2 * Math.PI * params.frequency, 2) * variables[0]
    ]
    signal.data = Common.solveDifEq({
      inData: signal.data,
      step: signal.params.step,
      equations,
      params,
      initial: [0.0, 0.0],
      outNumber: 0
    })
    return signal
  },

  /**
   * math
   */

  integrator(signal) {
    signal.data = Common.integrate(signal.data, signal.params.step)
    return signal
  },

  differentiator(signal) {
    signal.data = Common.differentiate(signal.data, signal.params.step)
    return signal
  },

  spectrumAnalyser(signal) {
    const kernel = (n, k, N) => {
      return Math.cos(Math.PI * k * (n + 0.5) / N)
    }
    const postprocess = data => {
      for (let i = 0; i < data.length; i++) {
        data[i] = Math.abs(data[i])
      }
      for (let i = 1; i < data.length - 1; i++) {
        if (data[i] < data[i - 1] && data[i] < data[i + 1]) {
          data[i] = (data[i - 1] + data[i + 1]) / 2
        }
      }
      let maximum = 0
      for (let y of data) {
        if (y > maximum) maximum = y
      }
      for (let i = 0; i < data.length; i++) {
        data[i] /= maximum
      }
    }
    const getPowOfTwoLength = fullLength => {
      let powOfTwoLength = 1
      while (powOfTwoLength <= fullLength) {
        powOfTwoLength *= 2
      }
      return powOfTwoLength / 2
    }
    const transformDCT = (data, N) => {
      let output = []
      Counter.init(N)
      for (let k = 0; k < N; k++) {
        let y = 0
        for (let n = 0; n < N; n++) {
          y += data[n] * kernel(n, k, N)
        }
        output.push(y)
        Counter.increase()
      }
      postprocess(output)
      return output
    }
    let N = getPowOfTwoLength(signal.data.length)
    signal.data = transformDCT(signal.data, N)
    signal.sampleRate =  2 * N / signal.sampleRate
    return signal
  },

  selfCorrelator(signal) {
    Common.calculateSignalParams(signal)
    let {data, xMin} = Common.estimateCorrelationFunction(signal, signal)
    signal.data = data
    signal.xMin = xMin
    return signal
  },
}

const DoubleTransformFunctions = {

  adder(signal1, signal2, params) {
    let output = {
      sampleRate: signal1.sampleRate,
      data: []
    }
    let commonGrid = Common.makeCommonSignalsValueGrid([signal1, signal2])
    output.xMin = commonGrid[0]
    Counter.init(commonGrid.length, 'calculating')
    for (let x of commonGrid) {
      output.data.push(params.coefficient1 * Common.getSignalValue(signal1, x)
          + params.coefficient2 * Common.getSignalValue(signal2, x))
      Counter.increase()
    }
    return output
  },

  correlator(signal1, signal2) {
    return {
      sampleRate: signal1.sampleRate,
      ...Common.estimateCorrelationFunction(signal1, signal2)
    }
  },

  twoSignalAmplitudeModulator(signal1, signal2, params) {
    let output = {
      sampleRate: signal1.sampleRate,
      data: []
    }
    let commonGrid = Common.makeCommonSignalsValueGrid([signal1, signal2])
    output.xMin = commonGrid[0]
    Counter.init(commonGrid.length, 'calculating')
    for (let x of commonGrid) {
      output.data.push(Common.getSignalValue(signal1, x) * (1 + params.depth * Common.getSignalValue(signal2, x) / signal2.maxAbsY))
      Counter.increase()
    }
    return output
  }

}

onmessage = msg => {
  try {
    let signal = msg.data.signal ? JSON.parse(msg.data.signal) : null
    let signal1 = msg.data.signal1 ? JSON.parse(msg.data.signal1) : null
    let signal2 = msg.data.signal2 ? JSON.parse(msg.data.signal2) : null
    let params = JSON.parse(msg.data.params)
    if (signal) {
      signal = TransformFunctions[msg.data.transformFunctionName](signal, params)
    } else if (signal1 && signal2) {
      signal = DoubleTransformFunctions[msg.data.transformFunctionName](signal1, signal2, params)
    }
    signal.maxAbsY = Common.calculateMaxAbsY(signal)
    Common.calculateSignalParams(signal)
    signal && postMessage({signal: JSON.stringify(signal)})
  } catch (error) {
    postMessage({error})
  }
}
