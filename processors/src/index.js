import {Common} from "./common";
import {LinearAmp} from "./single/linear-amp";
import {PiecewiseLinearSymmetricSaturationAmp} from "./single/piecewise-linear-symmetric-saturation-amp";
import {PiecewiseLinearAsymmetricSaturationAmp} from "./single/piecewise-linear-asymmetric-saturation-amp";
import {Inverter} from "./single/inverter";
import {AmplitudeModulator} from "./single/amplitude-modulator";
import {FrequencyModulator} from "./single/frequency-modulator";
import {LpRcFilter} from "./single/lp-rc-filter";
import {HpRcFilter} from "./single/hp-rc-filter";
import {LinearOscillator} from "./single/linear-oscillator";
import {SpectrumAnalyserDct} from "./single/spectrum-analyser-dct";
import {SpectrumAnalyserFft} from "./single/spectrum-analyser-fft";
import {Adder} from "./double/adder";
import {TwoSignalAmplitudeModulator} from "./double/two-signal-amplitude-modulator";

const SingleProcessors = {
  /**
   * amplifiers
   */
  linearAmp: LinearAmp,
  piecewiseLinearSymmetricSaturationAmp: PiecewiseLinearSymmetricSaturationAmp,
  piecewiseLinearAsymmetricSaturationAmp: PiecewiseLinearAsymmetricSaturationAmp,
  inverter: Inverter,
  /**
   * modulators
   */
  amplitudeModulator: AmplitudeModulator,
  frequencyModulator: FrequencyModulator,
  /**
   * filters
   */
  lpRcFilter: LpRcFilter,
  hpRcFilter: HpRcFilter,
  /**
   * oscillator chains
   */
  linearOscillator: LinearOscillator,
  /**
   * math
   */
  integrator: {
    process(signal) {
      signal.data = Common.integrate(signal.data, signal.params.step)
      return signal
    }
  },
  differentiator: {
    process(signal) {
      signal.data = Common.differentiate(signal.data, signal.params.step)
      return signal
    }
  },
  spectrumAnalyserDct: SpectrumAnalyserDct,
  spectrumAnalyserFft: SpectrumAnalyserFft,
  selfCorrelator: {
    process(signal) {
      const {data, xMin} = Common.calculateCorrelationFunction(signal, signal)
      signal.data = data
      signal.xMin = xMin
      return signal
    }
  },
}

const DoubleProcessors = {
  adder: Adder,
  correlator: {
    process(signal1, signal2) {
      return {
        sampleRate: signal1.sampleRate,
        ...Common.calculateCorrelationFunction(signal1, signal2)
      }
    }
  },
  twoSignalAmplitudeModulator: TwoSignalAmplitudeModulator
}

onmessage = msg => {
  let {signal, signal1, signal2, params, processorName} = msg.data
  if (signal) {
    signal = SingleProcessors[processorName].process(signal, params)
  } else if (signal1 && signal2) {
    signal = DoubleProcessors[processorName].process(signal1, signal2, params)
  }
  Common.calculateMaxAbsYAndParams(signal)
  postMessage({signal})
}
