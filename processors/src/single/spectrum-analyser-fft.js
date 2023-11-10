import {FFTNayuki} from "../3d-party/fft";

export const SpectrumAnalyserFft = {
  process(signal) {
    const postprocess = data => {
      const halfLength = data.length / 2
      for (let i = 0; i < halfLength; i++) {
        data[i] = Math.abs(data[i])
      }
      for (let i = 1; i < halfLength - 1; i++) {
        if (data[i] < data[i - 1] && data[i] < data[i + 1]) {
          data[i] = (data[i - 1] + data[i + 1]) / 2
        }
      }
      let maximum = 0
      for (let i = 0; i < halfLength; i++) {
        if (data[i] > maximum) maximum = data[i]
      }
      const output = []
      for (let i = 0; i < halfLength; i++) {
        output.push(data[i] / maximum)
      }
      return output
    }
    const getPowOfTwoLength = fullLength => {
      let powOfTwoLength = 1
      while (powOfTwoLength <= fullLength) {
        powOfTwoLength *= 2
      }
      return powOfTwoLength / 2
    }
    const N = getPowOfTwoLength(signal.data.length)
    const real = [], imag = []
    for (let i = 0; i < N; i++) {
      real.push(signal.data[i])
      imag.push(0)
    }
    new FFTNayuki(N).forward(real, imag)
    signal.data = postprocess(real)
    signal.sampleRate =  N / signal.sampleRate
    return signal
  }
}
