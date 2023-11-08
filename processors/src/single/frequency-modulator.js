import {Common} from "../common";
import {Counter} from "../counter";

export const FrequencyModulator = {
  process(signal, params) {
    const integrated = Common.integrate(signal.data, signal.params.step, 'preparingSignal')
    Counter.init(signal.data.length, 'calculating')
    for (let i = 0; i < signal.data.length; i++) {
      const x = signal.xMin + signal.params.step * i
      signal.data[i] = params.amplitude * Math.sin((2 * Math.PI * x + params.coefficient * integrated[i]) * params.frequency)
      Counter.increase()
    }
    return signal
  }
}
