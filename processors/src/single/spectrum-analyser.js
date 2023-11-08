import {Counter} from "../counter";

export const SpectrumAnalyser = {
  process(signal) {
    const kernel = (n, k, N) => {
      return Math.cos(Math.PI * k * (n + 0.5) / N)
    }
    const postprocess = data => {
      for (let i = 0; i < data.length; i++) {
        data[i] = Math.abs(data[i])
      }
      for (let i = 1; i < data.length - 1; i++) {
        if (data[i] < data[i - 1] && data[i] < data[i + 1]) {
          data[i] = (data[i - 1] + data[i + 1]) / 2
        }
      }
      let maximum = 0
      for (let y of data) {
        if (y > maximum) maximum = y
      }
      for (let i = 0; i < data.length; i++) {
        data[i] /= maximum
      }
    }
    const getPowOfTwoLength = fullLength => {
      let powOfTwoLength = 1
      while (powOfTwoLength <= fullLength) {
        powOfTwoLength *= 2
      }
      return powOfTwoLength / 2
    }
    const transformDCT = (data, N) => {
      let output = []
      Counter.init(N)
      for (let k = 0; k < N; k++) {
        let y = 0
        for (let n = 0; n < N; n++) {
          y += data[n] * kernel(n, k, N)
        }
        output.push(y)
        Counter.increase()
      }
      postprocess(output)
      return output
    }
    let N = getPowOfTwoLength(signal.data.length)
    signal.data = transformDCT(signal.data, N)
    signal.sampleRate =  2 * N / signal.sampleRate
    return signal
  }
}
