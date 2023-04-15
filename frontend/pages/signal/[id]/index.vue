<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%">
        <v-card-text>
          <div class="mb-5">
            <chart-drawer :signals="signal.params ? [signal] : []"/>
          </div>
          <fixed-width-wrapper>
            <div v-if="signalParamsText" class="mb-5">
              {{ signalParamsText }}
            </div>
            <v-form @submit.prevent="saveSignal">
              <v-text-field
                  v-model="signal.name"
                  :label="_tc('fields.name')"
                  :error="!!validation.name.length"
                  :error-messages="validation.name"
                  required/>
              <v-textarea
                  v-model="signal.description"
                  :label="_tc('fields.description')"
                  :error="!!validation.description.length"
                  :error-messages="validation.description"/>
              <div class="d-flex flex-wrap">
                <select-transformer-dialog :bus="bus"/>
                <v-btn color="success" @click="saveSignal">
                  {{ _tc('buttons.save') }}
                </v-btn>
                <v-btn color="success" v-if="signal.id" @click="saveSignalAsNew">
                  {{ _t('saveAsNew') }}
                </v-btn>
                <v-btn color="secondary" @click="exportSignalToTxt">
                  {{ _tc('buttons.exportTxt') }}
                </v-btn>
                <v-btn color="secondary" v-if="!signalIsStoredLocally" @click="exportSignalToWav">
                  {{ _tc('buttons.exportWav') }}
                </v-btn>
              </div>
            </v-form>
          </fixed-width-wrapper>
        </v-card-text>
      </v-card>
    </div>
    <transformer-dialog :bus="bus" :signal="signal"/>
    <message :opened="message.opened" :text="message.text" @hide="message.onHide"/>
    <loading-overlay :show="loadingOverlay"/>
  </NuxtLayout>
</template>

<script>
import ChartDrawer from "../../../components/chart-drawer";
import {dataStore} from "../../../stores/data-store";
import TransformerDialog from "../../../components/transformer-dialog";
import mitt from 'mitt'
import formValidation from "../../../mixins/form-validation";
import PageBase from "../../../components/page-base";
import FileUtils from "../../../utils/file-utils";
import SelectTransformerDialog from "../../../components/select-transformer-dialog";
import SignalUtils from "../../../utils/signal-utils";
import FixedWidthWrapper from "../../../components/fixed-width-wrapper";

export default {
  name: "signal",
  components: {
    SelectTransformerDialog,
    TransformerDialog, ChartDrawer,
    FixedWidthWrapper
  },
  extends: PageBase,
  mixins: [formValidation],
  data: () => ({
    signalKey: '',
    signal: {},
    validation: {
      name: [],
      description: []
    },
    bus: new mitt()
  }),
  computed: {
    transformers() {
      return dataStore().getTransformers
    },
    signalIsSaved() {
      return !!this.signal.id
    },
    signalIsStoredLocally() {
      return this.signalKey.includes('-')
    },
    signalParamsText() {
      if (!this.signal.sampleRate) {
        return ''
      }
      return this._t('sampleRate', {sampleRate: this.signal.sampleRate})
    }
  },
  mounted() {
    this.signalKey = this.$route.params.id
    if (this.signalIsStoredLocally) {
      let signal = dataStore().getSignalFromHistory(this.signalKey)
      if (signal) {
        this.signal = signal
      } else {
        this.signalNotFound()
      }
    } else {
      let signalId = parseInt(this.signalKey)
      if (!isNaN(signalId)) {
        this.loadWithOverlay(async () => {
          await this.loadSignal(signalId)
        })
      } else {
        this.signalNotFound()
      }
    }
    this.bus.on('error', error => {
      this.showMessage({
        text: 'Error transforming signal: ' + error.message
      })
    })
  },
  beforeUnmount() {
    this.bus.off('error')
  },
  methods: {
    async loadSignal(id) {
      let response = await this.getApiProvider().get('/api/signals/' + id)
      if (response.ok) {
        let signal = response.data
        SignalUtils.calculateSignalParams(signal)
        this.signal = signal
      } else if (response.status === 404) {
        this.signalNotFound()
      }
    },
    async saveSignalAsNew() {
      delete this.signal.id
      await this.saveSignal()
    },
    async saveSignal() {
      this.clearValidation()
      await this.loadWithOverlay(async () => {
        let response
        if (this.signalIsSaved) {
          response = await this.getApiProvider().putJson('/api/signals/' + this.signal.id, this.signal)
        } else {
          response = await this.getApiProvider().postJson('/api/signals/', this.signal)
        }
        if (response.ok) {
          dataStore().clearSignalHistory()
          useRouter().push('/signal-manager')
        } else if (response.status === 400) {
          this.parseValidation(response.errors)
          for (let error of response.errors) {
            if (error.field === 'data') {
              this.showMessage({
                text: 'Error saving signal data: ' + error.message
              })
            }
          }
        } else {
          this.showErrorsFromResponse(response, 'Error saving signal')
        }
      })
    },
    signalNotFound() {
      this.showMessage({
        text: 'Signal is not found',
        onHide: () => {
          useRouter().push('/signal-manager')
        }
      })
    },
    exportSignalToTxt() {
      FileUtils.saveSignalToTxtFile(this.signal)
    },
    exportSignalToWav() {
      FileUtils.saveSignalToWavFile(this.signal)
    },
  },
}
</script>
