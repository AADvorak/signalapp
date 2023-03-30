import {defineStore} from 'pinia'
import UserUtils from "~/utils/user-utils";

export const dataStore = defineStore('dataStore', {
  state: () => {
    return {
      userInfo: undefined,
      modules: [
        {module: 'signal-generator', name: 'Signal generator', forMenu: true},
        {module: 'signal-recorder', name: 'Signal recorder', forMenu: true},
        {module: 'signal-manager', name: 'Signal manager', forMenu: true},
        {module: 'signin', name: 'Sign in', forMenu: false},
        {module: 'signup', name: 'Sign up', forMenu: false},
        {module: 'user-settings', name: 'User settings', forMenu: false},
        {module: 'change-password', name: 'Change password', forMenu: false},
        {module: 'signal', name: 'Signal', forMenu: false},
        {module: 'index', name: 'Start page', forMenu: false},
          // transformers
          // amplifiers
        {module: 'LinearAmp', name: 'Linear amplifier', forMenu: false, transformer: true, type: 'Amplifier'},
        {module: 'PiecewiseLinearSymmetricSaturationAmp', name: 'Piecewise linear symmetric amplifier with saturation', forMenu: false, transformer: true, type: 'Amplifier'},
        {module: 'PiecewiseLinearAsymmetricSaturationAmp', name: 'Piecewise linear asymmetric amplifier with saturation', forMenu: false, transformer: true, type: 'Amplifier'},
        {module: 'Inverter', name: 'Inverter', forMenu: false, transformer: true, type: 'Amplifier'},
          // modulators
        {module: 'AmplitudeModulator', name: 'Amplitude modulator', forMenu: false, transformer: true, type: 'Modulator'},
        {module: 'FrequencyModulator', name: 'Frequency modulator', forMenu: false, transformer: true, type: 'Modulator'},
          // filters
        {module: 'LpRcFilter', name: 'Low-pass RC filter', forMenu: false, transformer: true, type: 'Filter'},
        {module: 'HpRcFilter', name: 'High-pass RC filter', forMenu: false, transformer: true, type: 'Filter'},
          // oscillator chains
        {module: 'LinearOscillator', name: 'Linear oscillator', forMenu: false, transformer: true, type: 'Oscillator'},
          // math
        {module: 'Integrator', name: 'Integrator', forMenu: false, transformer: true, type: 'Math'},
        {module: 'Differentiator', name: 'Differentiator', forMenu: false, transformer: true, type: 'Math'},
        {module: 'SpectrumAnalyser', name: 'Spectrum analyser', forMenu: false, transformer: true, type: 'Math'},
        {module: 'SelfCorrelator', name: 'Self correlator', forMenu: false, transformer: true, type: 'Math'},
          // double transformers
        {module: 'Adder', name: 'Adder', forMenu: false, doubleTransformer: true, type: 'Amplifier'},
        {module: 'Correlator', name: 'Correlator', forMenu: false, doubleTransformer: true, type: 'Math'},
        {module: 'TwoSignalAmplitudeModulator', name: 'Two signal amplitude modulator', forMenu: false,
          doubleTransformer: true, type: 'Modulator', signal1: 'Carrier', signal2: 'Modulator'},
      ],
      waitingForAuthorization: null,
      signalHistory: {},
      recordedAudio: null,
      darkMode: localStorage.getItem('darkMode') === 'true',
      emailForPasswordRestore: null,
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
    addSignalToHistory(signal) {
      let historyKey = String(signal.id || 0)
      if (!this.signalHistory[historyKey]) {
        this.signalHistory[historyKey] = []
      }
      let numberOfSignalInHistory = this.signalHistory[historyKey].length
      this.signalHistory[historyKey].push(JSON.stringify(signal))
      return `${historyKey}-${numberOfSignalInHistory}`
    },
    getSignalFromHistory(signalKey) {
      let signalKeySplit = signalKey.split('-')
      let historyKey = signalKeySplit[0], numberOfSignalInHistory = signalKeySplit[1]
      let value = this.signalHistory[historyKey]?.[numberOfSignalInHistory]
      if (value) {
        return JSON.parse(value)
      }
    },
    clearSignalHistory() {
      this.signalHistory = {}
    },
    setRecordedAudio(recordedAudio) {
      this.recordedAudio = recordedAudio
    },
    setDarkMode(darkMode) {
      this.darkMode = darkMode
      localStorage.setItem('darkMode', darkMode)
    }
  },
})