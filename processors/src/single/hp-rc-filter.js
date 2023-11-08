import {Common} from "../common";

export const HpRcFilter = {
  process(signal, params) {
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
}
