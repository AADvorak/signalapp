import {Counter} from "../counter";

export const LinearAmp = {
  process(signal, params) {
    Counter.init(signal.data.length)
    for (let i = 0; i < signal.data.length; i++) {
      signal.data[i] *= params.coefficient
      Counter.increase()
    }
    return signal
  }
}
