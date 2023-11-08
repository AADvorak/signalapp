import {Counter} from "../counter";

export const PiecewiseLinearSymmetricSaturationAmp = {
  process(signal, params) {
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
  }
}
