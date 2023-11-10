<template>
  <v-dialog
      v-model="dialog"
      max-width="800px"
  >
    <v-card width="100%">
      <v-toolbar>
        <v-toolbar-title>{{ titleWithRestrictedLength }}</v-toolbar-title>
      </v-toolbar>
      <v-card-text>
        <v-form @submit.prevent>
          <div :hidden="processing">
            <div class="mb-5">{{ makeProcessWithQuestion('processSignalWith') }}?</div>
            <component :is="selectedProcessor.code" :signal="signal" :bus="bus"/>
          </div>
          <div class="d-flex">
          <v-btn type="submit" color="primary" class="mr-4"
                 :disabled="processing || processingDisabled" @click="ok">
            {{ okButtonText }}
          </v-btn>
          <v-btn @click="cancel">
            {{ _tc('buttons.cancel') }}
          </v-btn>
          </div>
        </v-form>
        <v-progress-linear
            v-if="processing"
            v-model="progress.value"
            :indeterminate="!progress.value"
            color="blue" height="25" class="mt-5">
          <strong>{{ progressBarText }}</strong>
        </v-progress-linear>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import LinearAmp from "~/components/processors/linear-amp";
import PiecewiseLinearSymmetricSaturationAmp from "~/components/processors/piecewise-linear-symmetric-saturation-amp";
import PiecewiseLinearAsymmetricSaturationAmp from "~/components/processors/piecewise-linear-asymmetric-saturation-amp";
import Inverter from "~/components/processors/inverter";
import AmplitudeModulator from "~/components/processors/amplitude-modulator";
import FrequencyModulator from "~/components/processors/frequency-modulator";
import LpRcFilter from "~/components/processors/lp-rc-filter";
import HpRcFilter from "~/components/processors/hp-rc-filter";
import LinearOscillator from "~/components/processors/linear-oscillator";
import Integrator from "~/components/processors/integrator";
import Differentiator from "~/components/processors/differentiator";
import SelfCorrelator from "~/components/processors/self-correlator";
import SpectrumAnalyserDct from "~/components/processors/spectrum-analyser-dct";
import SpectrumAnalyserFft from "~/components/processors/spectrum-analyser-fft";
import ProcessorDialogBase from "./processor-dialog-base";

export default {
  name: "single-processor-dialog",
  components: {
    LinearAmp,
    PiecewiseLinearSymmetricSaturationAmp,
    PiecewiseLinearAsymmetricSaturationAmp,
    Inverter,
    AmplitudeModulator,
    FrequencyModulator,
    LpRcFilter,
    HpRcFilter,
    LinearOscillator,
    Integrator,
    Differentiator,
    SelfCorrelator,
    SpectrumAnalyserDct,
    SpectrumAnalyserFft
  },
  extends: ProcessorDialogBase,
  props: {
    signal: Object
  }
}
</script>
