import {defineStore} from "pinia";
import {ProcessorTypes} from "~/dictionary/processor-types";
import {Roles} from "~/dictionary/roles";

export const moduleStore = defineStore('moduleStore', {
  state: () => ({
    modules: [
      {code: 'signal-generator', forMenu: true, icon: 'sineWave'},
      {code: 'signal-recorder', forMenu: true, icon: 'microphone'},
      {code: 'signal-manager', forMenu: true, icon: 'server'},
      {code: 'admin-users', forMenu: true, icon: 'accountMultiple', role: Roles.ADMIN},
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
      {code: 'LinearAmp', forMenu: false, processor: true, type: ProcessorTypes.AMPLIFIER},
      {code: 'PiecewiseLinearSymmetricSaturationAmp', forMenu: false, processor: true, type: ProcessorTypes.AMPLIFIER},
      {code: 'PiecewiseLinearAsymmetricSaturationAmp', forMenu: false, processor: true, type: ProcessorTypes.AMPLIFIER},
      {code: 'Inverter', forMenu: false, processor: true, type: ProcessorTypes.AMPLIFIER},
      // modulators
      {code: 'AmplitudeModulator', forMenu: false, processor: true, type: ProcessorTypes.MODULATOR},
      {code: 'FrequencyModulator', forMenu: false, processor: true, type: ProcessorTypes.MODULATOR},
      // filters
      {code: 'LpRcFilter', forMenu: false, processor: true, type: ProcessorTypes.FILTER},
      {code: 'HpRcFilter', forMenu: false, processor: true, type: ProcessorTypes.FILTER},
      // oscillator chains
      {code: 'LinearOscillator', forMenu: false, processor: true, type: ProcessorTypes.OSCILLATOR},
      // math
      {code: 'Integrator', forMenu: false, processor: true, type: ProcessorTypes.MATH},
      {code: 'Differentiator', forMenu: false, processor: true, type: ProcessorTypes.MATH},
      {code: 'SpectrumAnalyserDct', forMenu: false, processor: true, type: ProcessorTypes.MATH},
      {code: 'SpectrumAnalyserFft', forMenu: false, processor: true, type: ProcessorTypes.MATH},
      {code: 'SelfCorrelator', forMenu: false, processor: true, type: ProcessorTypes.MATH},
      // double processors
      {code: 'Adder', forMenu: false, doubleProcessor: true, type: ProcessorTypes.AMPLIFIER},
      {code: 'Correlator', forMenu: false, doubleProcessor: true, type: ProcessorTypes.MATH},
      {
        code: 'TwoSignalAmplitudeModulator', forMenu: false, doubleProcessor: true, type: ProcessorTypes.MODULATOR,
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
