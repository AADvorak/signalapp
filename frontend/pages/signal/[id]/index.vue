<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%">
        <v-card-text>
          <div class="mb-5">
            <chart-drawer :signals="signal.params ? [signal] : []"></chart-drawer>
          </div>
          <div v-if="signalParamsText" class="mb-5">
            {{ signalParamsText }}
          </div>
          <v-form @submit.prevent="saveSignal">
            <v-text-field
                v-model="signal.name"
                label="Name"
                :error="!!validation.name.length"
                :error-messages="validation.name"
                required
            ></v-text-field>
            <v-textarea
                v-model="signal.description"
                label="Description"
                :error="!!validation.description.length"
                :error-messages="validation.description"
            >
            </v-textarea>
            <div class="d-flex flex-wrap">
              <select-transformer-dialog :bus="bus"/>
              <v-btn color="success" @click="saveSignal">
                Save
              </v-btn>
              <v-btn color="success" v-if="signal.id" @click="saveSignalAsNew">
                Save as new
              </v-btn>
              <v-btn color="secondary" @click="exportSignalToTxt">
                Export txt
              </v-btn>
              <v-btn color="secondary" v-if="!signalIsStoredLocally" @click="exportSignalToWav">
                Export wav
              </v-btn>
            </div>
          </v-form>
        </v-card-text>
      </v-card>
    </div>
    <transformer-dialog :bus="bus" :signal="signal"/>
    <message :opened="message.opened" :text="message.text" @hide="message.onHide"></message>
    <loading-overlay :show="loadingOverlay"></loading-overlay>
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

export default {
  name: "index",
  components: {
    SelectTransformerDialog,
    TransformerDialog, ChartDrawer
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
      return `Sample rate = ${this.signal.sampleRate}`
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
    window.scrollTo(0,0)
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
