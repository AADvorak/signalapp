import {Common} from "../common";
import {Counter} from "../counter";

export const TwoSignalAmplitudeModulator = {
  process(signal1, signal2, params) {
    const output = {
      sampleRate: signal1.sampleRate,
      data: []
    }
    const commonGrid = Common.makeCommonSignalsValueGrid([signal1, signal2])
    output.xMin = commonGrid[0]
    Counter.init(commonGrid.length, 'calculating')
    for (const x of commonGrid) {
      output.data.push(Common.getSignalValue(signal1, x) * (1 + params.depth * Common.getSignalValue(signal2, x) / signal2.maxAbsY))
      Counter.increase()
    }
    return output
  }
}
