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
            <div class="mb-5">{{ makeProcessWithQuestion('processSignalsWith') }}?</div>
            <div class="d-flex justify-center flex-wrap">
              <v-text-field
                  class="signal-name-input"
                  v-model="signal1.name"
                  :label="_tsn(selectedProcessor.signal1 || 'signal1')"
                  readonly/>
              <btn-with-tooltip tooltip="change" :small="false" @click="changeSignals">
                <v-icon>
                  {{ mdiRotate360 }}
                </v-icon>
              </btn-with-tooltip>
              <v-text-field
                  class="signal-name-input"
                  v-model="signal2.name"
                  :label="_tsn(selectedProcessor.signal2 || 'signal2')"
                  readonly/>
            </div>
            <component :is="selectedProcessor.code" :signal1="signal1" :signal2="signal2" :bus="bus"/>
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
import {mdiRotate360} from "@mdi/js";
import ProcessorDialogBase from "./processor-dialog-base.vue";
import Adder from "~/components/processors/double/adder.vue";
import Correlator from "~/components/processors/double/correlator.vue";
import TwoSignalAmplitudeModulator from "~/components/processors/double/two-signal-amplitude-modulator.vue";
import BtnWithTooltip from "~/components/common/btn-with-tooltip.vue";

export default {
  name: "double-processor-dialog",
  components: {BtnWithTooltip, Adder, Correlator, TwoSignalAmplitudeModulator},
  extends: ProcessorDialogBase,
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

<style scoped>
.signal-name-input {
  min-width: 300px;
  max-width: 800px;
  margin-left: 5px;
  margin-right: 5px;
}
</style>
