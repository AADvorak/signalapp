export const Inverter = {
  process(signal) {
    for (let i = 0; i < signal.data.length; i++) {
      signal.data[i] = -signal.data[i]
    }
    return signal
  },
}
