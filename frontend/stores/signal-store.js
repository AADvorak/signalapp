import {defineStore} from "pinia";

export const signalStore = defineStore('signalStore', {
  state: () => ({
    signalHistory: {}
  }),
  actions: {
    addSignalToHistory(signal, currentHistoryKey) {
      const signalId = String(signal.id || 0)
      if (!this.signalHistory[signalId]) {
        this.signalHistory[signalId] = []
      }
      const historyKey = currentHistoryKey ? parseInt(currentHistoryKey) + 1 : 0
      this.signalHistory[signalId][historyKey] = JSON.stringify(signal)
      return historyKey
    },
    updateSignalInHistory(signal, historyKey) {
      if (!historyKey) {
        return
      }
      const signalId = String(signal.id || 0)
      this.signalHistory[signalId][historyKey] = JSON.stringify(signal)
    },
    getSignalFromHistory(signalId, historyKey) {
      const value = this.signalHistory[signalId]?.[parseInt(historyKey)]
      if (value) {
        return JSON.parse(value)
      }
    },
    clearHistoryForSignal(signalId) {
      this.signalHistory[String(signalId)] = []
    },
  }
})
