import {Common} from "../common";

export const FrequencyModulator = {
  process(signal, params) {
    const integrated = Common.integrate(signal.data, signal.params.step)
    for (let i = 0; i < signal.data.length; i++) {
      const x = signal.xMin + signal.params.step * i
      signal.data[i] = params.amplitude * Math.sin((2 * Math.PI * x + params.coefficient * integrated[i]) * params.frequency)
    }
    return signal
  }
}
