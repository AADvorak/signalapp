import SignalUtils from "~/utils/signal-utils";
import {dataStore} from "~/stores/data-store";

export default {
  methods: {
    validateSignalLength(signal) {
      const maxSamplesNumber = 1024000, samplesNumber = signal.data.length
      if (samplesNumber > maxSamplesNumber) {
        this.showMessage({
          text: this._tc('messages.wrongSignalSamplesNumber', {samplesNumber, maxSamplesNumber})
        })
        return false
      }
      return true
    },
    saveSignalToHistoryAndOpen(signal) {
      SignalUtils.calculateMaxAbsY(signal)
      SignalUtils.calculateSignalParams(signal)
      useRouter().push('/signal/0?history=' + dataStore().addSignalToHistory(signal))
    },
  }
}
