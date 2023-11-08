import {Common} from "../common";

export const LinearOscillator = {
  process(signal, params) {
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
  }
}
