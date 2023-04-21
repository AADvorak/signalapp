import {defineStore} from 'pinia'
import UserUtils from "~/utils/user-utils";

export const dataStore = defineStore('dataStore', {
  state: () => {
    return {
      userInfo: undefined,
      modules: [
        {code: 'signal-generator', forMenu: true, icon: 'sineWave'},
        {code: 'signal-recorder', forMenu: true, icon: 'microphone'},
        {code: 'signal-manager', forMenu: true, icon: 'server'},
        {code: 'signin', forMenu: false},
        {code: 'signup', forMenu: false},
        {code: 'user-settings', forMenu: false},
        {code: 'change-password', forMenu: false},
        {code: 'restore-password', forMenu: false},
        {code: 'signal', forMenu: false},
        {code: 'index', forMenu: false},
          // transformers
          // amplifiers
        {code: 'LinearAmp', forMenu: false, transformer: true, type: 'amplifier'},
        {code: 'PiecewiseLinearSymmetricSaturationAmp', forMenu: false, transformer: true, type: 'amplifier'},
        {code: 'PiecewiseLinearAsymmetricSaturationAmp', forMenu: false, transformer: true, type: 'amplifier'},
        {code: 'Inverter', forMenu: false, transformer: true, type: 'amplifier'},
          // modulators
        {code: 'AmplitudeModulator', forMenu: false, transformer: true, type: 'modulator'},
        {code: 'FrequencyModulator', forMenu: false, transformer: true, type: 'modulator'},
          // filters
        {code: 'LpRcFilter', forMenu: false, transformer: true, type: 'filter'},
        {code: 'HpRcFilter', forMenu: false, transformer: true, type: 'filter'},
          // oscillator chains
        {code: 'LinearOscillator', forMenu: false, transformer: true, type: 'oscillator'},
          // math
        {code: 'Integrator', forMenu: false, transformer: true, type: 'math'},
        {code: 'Differentiator', forMenu: false, transformer: true, type: 'math'},
        {code: 'SpectrumAnalyser', forMenu: false, transformer: true, type: 'math'},
        {code: 'SelfCorrelator', forMenu: false, transformer: true, type: 'math'},
          // double transformers
        {code: 'Adder', forMenu: false, doubleTransformer: true, type: 'amplifier'},
        {code: 'Correlator', forMenu: false, doubleTransformer: true, type: 'math'},
        {code: 'TwoSignalAmplitudeModulator', forMenu: false, doubleTransformer: true, type: 'modulator',
          signal1: 'carrierSignal', signal2: 'modulatingSignal'},
      ],
      waitingForAuthorization: null,
      signalHistory: {},
      recordedAudio: null,
      darkMode: localStorage.getItem('darkMode') === 'true',
      emailForPasswordRestore: null,
      defaultLocale: 'en',
      locale: localStorage.getItem('locale')
    }
  },
  getters: {
    getUserInfo: state => {
      return state.userInfo
    },
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
    isSignedIn: state => {
      const userInfo = state.userInfo
      return userInfo && userInfo.id
    },
    getAllModules: state => {
      return state.modules
    },
    getModulesForMenu: state => {
      return state.modules.filter(module => module.forMenu)
    },
    getTransformers: state => {
      return state.modules.filter(module => module.transformer)
    },
    getDoubleTransformers: state => {
      return state.modules.filter(module => module.doubleTransformer)
    },
    getWaitingForAuthorization: state => {
      return state.waitingForAuthorization
    },
    getRecordedAudio: state => {
      return state.recordedAudio
    },
    getDarkMode: state => {
      return state.darkMode
    }
  },
  actions: {
    async loadUserInfo() {
      this.userInfo === undefined && this.setUserInfo(await UserUtils.loadUserInfo())
    },
    setUserInfo(userInfo) {
      this.userInfo = userInfo
    },
    clearUserInfo() {
      this.userInfo = null
    },
    setWaitingForAuthorization(waitingForAuthorization) {
      this.waitingForAuthorization = waitingForAuthorization
    },
    clearWaitingForAuthorization() {
      this.waitingForAuthorization = null
    },
    addSignalToHistory(signal, currentHistoryKey) {
      const signalId = String(signal.id || 0)
      if (!this.signalHistory[signalId]) {
        this.signalHistory[signalId] = []
      }
      const historyKey = currentHistoryKey ? parseInt(currentHistoryKey) + 1 : 0
      this.signalHistory[signalId][historyKey] = JSON.stringify(signal)
      return historyKey
    },
    getSignalFromHistory(signalId, historyKey) {
      const value = this.signalHistory[signalId]?.[parseInt(historyKey)]
      if (value) {
        return JSON.parse(value)
      }
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
    }
  },
})
