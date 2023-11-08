import {Common} from "../common";

export const LpRcFilter = {
  process(signal, params) {
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
}
