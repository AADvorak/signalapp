import {Common} from "../common";
import {Counter} from "../counter";

export const Adder = {
  process(signal1, signal2, params) {
    const output = {
      sampleRate: signal1.sampleRate,
      data: []
    }
    const commonGrid = Common.makeCommonSignalsValueGrid([signal1, signal2])
    output.xMin = commonGrid[0]
    Counter.init(commonGrid.length, 'calculating')
    for (const x of commonGrid) {
      output.data.push(params.coefficient1 * Common.getSignalValue(signal1, x)
          + params.coefficient2 * Common.getSignalValue(signal2, x))
      Counter.increase()
    }
    return output
  }
}
