export const LinearAmp = {
  process(signal, params) {
    for (let i = 0; i < signal.data.length; i++) {
      signal.data[i] *= params.coefficient
    }
    return signal
  }
}
