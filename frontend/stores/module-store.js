import {defineStore} from 'pinia'

export const moduleStore = defineStore('moduleStore', {
  state: () => ({
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
      {code: 'folder-manager', forMenu: false},
      // processors
      // amplifiers
      {code: 'LinearAmp', forMenu: false, processor: true, type: 'amplifier'},
      {code: 'PiecewiseLinearSymmetricSaturationAmp', forMenu: false, processor: true, type: 'amplifier'},
      {code: 'PiecewiseLinearAsymmetricSaturationAmp', forMenu: false, processor: true, type: 'amplifier'},
      {code: 'Inverter', forMenu: false, processor: true, type: 'amplifier'},
      // modulators
      {code: 'AmplitudeModulator', forMenu: false, processor: true, type: 'modulator'},
      {code: 'FrequencyModulator', forMenu: false, processor: true, type: 'modulator'},
      // filters
      {code: 'LpRcFilter', forMenu: false, processor: true, type: 'filter'},
      {code: 'HpRcFilter', forMenu: false, processor: true, type: 'filter'},
      // oscillator chains
      {code: 'LinearOscillator', forMenu: false, processor: true, type: 'oscillator'},
      // math
      {code: 'Integrator', forMenu: false, processor: true, type: 'math'},
      {code: 'Differentiator', forMenu: false, processor: true, type: 'math'},
      {code: 'SpectrumAnalyser', forMenu: false, processor: true, type: 'math'},
      {code: 'SelfCorrelator', forMenu: false, processor: true, type: 'math'},
      // double processors
      {code: 'Adder', forMenu: false, doubleProcessor: true, type: 'amplifier'},
      {code: 'Correlator', forMenu: false, doubleProcessor: true, type: 'math'},
      {
        code: 'TwoSignalAmplitudeModulator', forMenu: false, doubleProcessor: true, type: 'modulator',
        signal1: 'carrierSignal', signal2: 'modulatingSignal'
      },
    ]
  }),
  getters: {
    modulesForMenu: state => state.modules.filter(module => module.forMenu),
    processors: state => state.modules.filter(module => module.processor),
    doubleProcessors: state => state.modules.filter(module => module.doubleProcessor)
  }
})
