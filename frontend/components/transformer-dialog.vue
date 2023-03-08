<template>
  <v-dialog
      v-model="dialog"
      max-width="600px"
  >
    <v-card width="100%">
      <v-toolbar>
        <v-toolbar-title>{{ selectedTransformer.name }}</v-toolbar-title>
      </v-toolbar>
      <v-card-text>
        <v-form>
          <div :hidden="processing">
            <div class="mb-5">Transform signal with {{ selectedTransformer.name }}?</div>
            <component v-bind:is="selectedTransformer.module" :signal="signal" :bus="bus"></component>
          </div>
          <div class="d-flex">
          <v-btn :disabled="processing" color="primary" class="mr-4" @click="ok">
            {{ processing ? 'Working...' : 'OK' }}
          </v-btn>
          <v-btn @click="cancel">
            Cancel
          </v-btn>
          </div>
        </v-form>
        <v-progress-linear v-if="processing" v-model="progress" color="blue" height="25" class="mt-5">
          <strong>{{ progressBarText }}</strong>
        </v-progress-linear>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import LinearAmp from "./transformers/linear-amp";
import PiecewiseLinearSymmetricSaturationAmp from "./transformers/piecewise-linear-symmetric-saturation-amp";
import PiecewiseLinearAsymmetricSaturationAmp from "./transformers/piecewise-linear-asymmetric-saturation-amp";
import Inverter from "./transformers/inverter";
import AmplitudeModulator from "./transformers/amplitude-modulator";
import FrequencyModulator from "./transformers/frequency-modulator";
import LpRcFilter from "./transformers/lp-rc-filter";
import HpRcFilter from "./transformers/hp-rc-filter";
import LinearOscillator from "./transformers/linear-oscillator";
import Integrator from "./transformers/integrator";
import Differentiator from "./transformers/differentiator";
import SelfCorrelator from "./transformers/self-correlator";
import SpectrumAnalyser from "./transformers/spectrum-analyser";
import TransformerDialogBase from "./transformer-dialog-base";

export default {
  name: "transformer-dialog",
  components: {
    LinearAmp, PiecewiseLinearSymmetricSaturationAmp, PiecewiseLinearAsymmetricSaturationAmp, Inverter,
    AmplitudeModulator, FrequencyModulator,
    LpRcFilter, HpRcFilter,
    LinearOscillator,
    Integrator, Differentiator, SelfCorrelator, SpectrumAnalyser
  },
  extends: TransformerDialogBase,
  props: {
    signal: Object
  }
}
</script>
