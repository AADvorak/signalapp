import {Counter} from "../counter";

export const Inverter = {
  process(signal) {
    Counter.init(signal.data.length)
    for (let i = 0; i < signal.data.length; i++) {
      signal.data[i] = -signal.data[i]
      Counter.increase()
    }
    return signal
  },
}
