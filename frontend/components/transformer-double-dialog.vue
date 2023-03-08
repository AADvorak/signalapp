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
            <div class="mb-5">Transform signals with {{ selectedTransformer.name }}?</div>
            <v-row>
              <v-col :cols="5">
                <v-text-field
                    v-model="signal1.name"
                    :label="selectedTransformer.signal1 || 'Signal 1'"
                    readonly
                ></v-text-field>
              </v-col>
              <v-col :cols="2">
                <v-btn
                    color="secondary"
                    @click="changeSignals"
                >
                  <v-icon>
                    {{ mdiRotate360 }}
                  </v-icon>
                </v-btn>
              </v-col>
              <v-col :cols="5">
                <v-text-field
                    v-model="signal2.name"
                    :label="selectedTransformer.signal2 || 'Signal 2'"
                    readonly
                ></v-text-field>
              </v-col>
            </v-row>
            <component v-bind:is="selectedTransformer.module" :signal1="signal1" :signal2="signal2" :bus="bus"></component>
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
import {mdiRotate360} from "@mdi/js";
import TransformerDialogBase from "./transformer-dialog-base";
import Adder from "./transformers/adder";
import Correlator from "./transformers/correlator";
import TwoSignalAmplitudeModulator from "./transformers/two-signal-amplitude-modulator";

export default {
  name: "transformer-double-dialog",
  components: {Adder, Correlator, TwoSignalAmplitudeModulator},
  extends: TransformerDialogBase,
  props: {
    signals: Array
  },
  data: () => ({
    mdiRotate360,
    signal1: {},
    signal2: {},
    invertedSelection: false
  }),
  watch: {
    signals() {
      if (this.signals.length === 2) {
        this.selectSignals()
      }
    }
  },
  methods: {
    selectSignals() {
      this.signal1 = {...this.signals[this.invertedSelection ? 1 : 0]}
      this.signal2 = {...this.signals[this.invertedSelection ? 0 : 1]}
    },
    changeSignals() {
      this.invertedSelection = !this.invertedSelection
      this.selectSignals()
    }
  }
}
</script>
