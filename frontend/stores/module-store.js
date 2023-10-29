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
      {
        code: 'TwoSignalAmplitudeModulator', forMenu: false, doubleTransformer: true, type: 'modulator',
        signal1: 'carrierSignal', signal2: 'modulatingSignal'
      },
    ]
  }),
  getters: {
    modulesForMenu: state => state.modules.filter(module => module.forMenu),
    transformers: state => state.modules.filter(module => module.transformer),
    doubleTransformers: state => state.modules.filter(module => module.doubleTransformer)
  }
})
