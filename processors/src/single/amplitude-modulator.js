import {Counter} from "../counter";

export const AmplitudeModulator = {
  process(signal, params) {
    Counter.init(signal.data.length, 'calculating')
    for (let i = 0; i < signal.data.length; i++) {
      const x = signal.xMin + signal.params.step * i
      signal.data[i] = params.amplitude * (1 + params.depth * signal.data[i] / signal.maxAbsY) * Math.sin(2 * Math.PI * params.frequency * x)
      Counter.increase()
    }
    return signal
  }
}
