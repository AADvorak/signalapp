import {defineStore} from 'pinia'

export const dataStore = defineStore('dataStore', {
  state: () => {
    return {
      userInfo: undefined,
      settings: undefined,
      folders: [],
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
  getters: {
    userRepresentingString: state => {
      const userInfo = state.userInfo
      if (!userInfo) {
        return ''
      }
      if (userInfo.firstName && userInfo.lastName && userInfo.patronymic) {
        return userInfo.firstName.substring(0, 1) + '.' + userInfo.patronymic.substring(0, 1) + '. ' + userInfo.lastName
      }
      if (userInfo.firstName && userInfo.lastName) {
        return userInfo.firstName.substring(0, 1) + '. ' + userInfo.lastName
      }
      if (userInfo.firstName) {
        return userInfo.firstName
      }
      if (userInfo.lastName) {
        return userInfo.lastName
      }
      return userInfo.email
    },
    isSignedIn: state => !!state.userInfo?.id,
  },
  actions: {
    setUserInfo(userInfo) {
      this.userInfo = userInfo
    },
    setSettings(settings) {
      this.settings = settings
    },
    clearUserInfo() {
      this.userInfo = undefined
    },
    clearPersonalData() {
      this.clearUserInfo()
      this.folders = []
    },
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
