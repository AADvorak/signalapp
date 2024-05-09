import {defineStore} from 'pinia'

export const dataStore = defineStore('dataStore', {
  state: () => {
    return {
      serverTimezoneOffset: undefined,
      waitingForAuthorization: null,
      recordedAudio: null,
      darkMode: localStorage.getItem('darkMode') === 'true',
      emailForPasswordRestore: null,
      defaultLocale: 'en',
      locale: localStorage.getItem('locale'),
      numberInputType: localStorage.getItem('numberInputType') || 'text',
      numberInputTypes: ['text', 'slider']
    }
  },
  actions: {
    setWaitingForAuthorization(waitingForAuthorization) {
      this.waitingForAuthorization = waitingForAuthorization
    },
    clearWaitingForAuthorization() {
      this.waitingForAuthorization = null
    },
    setRecordedAudio(recordedAudio) {
      this.recordedAudio = recordedAudio
    },
    setDarkMode(darkMode) {
      this.darkMode = darkMode
      localStorage.setItem('darkMode', darkMode)
    },
    setLocale(locale) {
      this.locale = locale
      localStorage.setItem('locale', locale)
    },
    setNumberInputType(numberInputType) {
      this.numberInputType = numberInputType
      localStorage.setItem('numberInputType', numberInputType)
    }
  },
})
