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
                  :error="!!form.name.validation?.length"
                  :error-messages="form.name.validation"
                  required/>
              <v-textarea
                  v-model="signal.description"
                  :label="_tc('fields.description')"
                  :error="!!form.description.validation?.length"
                  :error-messages="form.description.validation"/>
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
                <v-btn color="secondary" v-if="signalId !== '0' && historyKey === '0'" @click="exportSignalToWav">
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
    signalId: '',
    historyKey: '',
    signal: {},
    form: {
      name: {},
      description: {}
    },
    bus: new mitt()
  }),
  computed: {
    signalIsSaved() {
      return !!this.signal.id
    },
    signalParamsText() {
      if (!this.signal.sampleRate) {
        return ''
      }
      return this._t('sampleRate', {sampleRate: this.signal.sampleRate})
    }
  },
  watch: {
    '$route.query.history'() {
      window.scrollTo(0,0)
      this.getSignalFromHistoryOrLoad()
    }
  },
  mounted() {
    this.getSignalFromHistoryOrLoad()
    this.bus.on('error', error => {
      this.showMessage({
        text: `${this._t('transformSignalError')}: ${error.message}`
      })
    })
  },
  beforeUnmount() {
    this.bus.off('error')
  },
  methods: {
    getSignalFromHistoryOrLoad() {
      this.signal = {}
      const route = useRoute()
      this.signalId = route.params.id
      this.historyKey = route.query.history
      let signal = dataStore().getSignalFromHistory(this.signalId, this.historyKey)
      if (signal) {
        this.signal = signal
      } else if (!isNaN(this.signalId) && (!this.historyKey || this.historyKey === '0')) {
        this.loadWithOverlay(async () => {
          await this.loadSignal(this.signalId)
        })
      } else {
        this.signalNotFound()
      }
    },
    async loadSignal(id) {
      let response = await this.getApiProvider().get('/api/signals/' + id)
      if (response.ok) {
        let signal = response.data
        SignalUtils.calculateSignalParams(signal)
        const historyKey = dataStore().addSignalToHistory(signal)
        if (this.historyKey === '0') {
          this.signal = signal
        } else {
          useRouter().push(`/signal/${this.signalId}?history=${historyKey}`)
        }
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
          useRouter().push('/signal-manager')
        } else if (response.status === 400) {
          this.parseValidation(response.errors)
          for (let error of response.errors) {
            if (error.field === 'data') {
              this.showMessage({
                text: `${this._t('signalDataSaveError')}: ${this.getLocalizedErrorMessage(error)}`
              })
            }
          }
        } else {
          this.showErrorsFromResponse(response, this._t('signalSaveError'))
        }
      })
    },
    signalNotFound() {
      this.showMessage({
        text: this._t('signalNotFound'),
        onHide: () => {
          useRouter().push(dataStore().getUserInfo ? '/signal-manager' : '/signal-generator')
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
