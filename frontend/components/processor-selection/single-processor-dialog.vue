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
import LinearAmp from "~/components/processors/single/linear-amp.vue";
import PiecewiseLinearSymmetricSaturationAmp from "~/components/processors/single/piecewise-linear-symmetric-saturation-amp.vue";
import PiecewiseLinearAsymmetricSaturationAmp from "~/components/processors/single/piecewise-linear-asymmetric-saturation-amp.vue";
import Inverter from "~/components/processors/single/inverter.vue";
import AmplitudeModulator from "~/components/processors/single/amplitude-modulator.vue";
import FrequencyModulator from "~/components/processors/single/frequency-modulator.vue";
import LpRcFilter from "~/components/processors/single/lp-rc-filter.vue";
import HpRcFilter from "~/components/processors/single/hp-rc-filter.vue";
import LinearOscillator from "~/components/processors/single/linear-oscillator.vue";
import Integrator from "~/components/processors/single/integrator.vue";
import Differentiator from "~/components/processors/single/differentiator.vue";
import SelfCorrelator from "~/components/processors/single/self-correlator.vue";
import SpectrumAnalyserDct from "~/components/processors/single/spectrum-analyser-dct.vue";
import SpectrumAnalyserFft from "~/components/processors/single/spectrum-analyser-fft.vue";
import ProcessorDialogBase from "./processor-dialog-base.vue";

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
