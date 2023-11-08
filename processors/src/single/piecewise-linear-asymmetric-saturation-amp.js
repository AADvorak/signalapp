import {Counter} from "../counter";

export const PiecewiseLinearAsymmetricSaturationAmp = {
  process(signal, params) {
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
}
