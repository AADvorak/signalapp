<template>
  <card-with-layout full-width :message="message" :loading-overlay="loadingOverlay">
    <template #default>
      <v-card-text>
        <div class="mb-5">
          <chart-drawer :signals="signal.params ? [signal] : []"/>
        </div>
        <fixed-width-wrapper>
          <v-row>
            <v-col cols="9">
              <div v-if="signalParamsText" class="mb-5">
                {{ signalParamsText }}
              </div>
            </v-col>
            <v-col cols="3" class="text-right">
              <v-btn
                  v-if="signal.sampleRate >= 3000"
                  color="primary"
                  @click="playOrStopSignal"
              >
                <v-icon>
                  {{ isSignalPlayed ? mdiStop : mdiPlay }}
                </v-icon>
              </v-btn>
            </v-col>
          </v-row>
          <v-form @submit.prevent>
            <v-text-field
                v-model="signal.name"
                ref="nameInput"
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
              <select-processor-dialog :bus="bus"/>
              <v-btn color="success" @click="saveSignal">
                {{ _tc('buttons.save') }}
              </v-btn>
              <v-btn color="success" v-if="signal.id" @click="saveSignalAsNew">
                {{ _t('saveAsNew') }}
              </v-btn>
              <v-btn color="secondary">
                {{ _tc('buttons.exportSignal') }}
                <v-menu activator="parent">
                  <v-list>
                    <v-list-item @click="exportSignalToJson">
                      <v-list-item-title>JSON</v-list-item-title>
                    </v-list-item>
                    <v-list-item @click="exportSignalToXml">
                      <v-list-item-title>XML</v-list-item-title>
                    </v-list-item>
                  </v-list>
                </v-menu>
              </v-btn>
              <v-btn color="secondary">
                {{ _tc('buttons.exportData') }}
                <v-menu activator="parent">
                  <v-list>
                    <v-list-item @click="exportSignalToTxt">
                      <v-list-item-title>TXT</v-list-item-title>
                    </v-list-item>
                    <v-list-item @click="exportSignalToCsv">
                      <v-list-item-title>CSV</v-list-item-title>
                    </v-list-item>
                  </v-list>
                </v-menu>
              </v-btn>
              <v-btn color="secondary" @click="exportSignalToWav">
                {{ _tc('buttons.exportWav') }}
              </v-btn>
            </div>
          </v-form>
        </fixed-width-wrapper>
      </v-card-text>
    </template>
    <template #dialogs>
      <single-processor-dialog :bus="bus" :signal="signal"/>
    </template>
  </card-with-layout>
</template>

<script>
import {dataStore} from "~/stores/data-store";
import mitt from 'mitt'
import formValidation from "../../../mixins/form-validation";
import PageBase from "../../../components/page-base";
import FileUtils from "../../../utils/file-utils";
import SignalUtils from "../../../utils/signal-utils";
import WavCoder from "../../../audio/wav-coder";
import SignalPlayer from "../../../audio/signal-player";
import {mdiPlay, mdiStop} from "@mdi/js";
import NumberUtils from "~/utils/number-utils";
import {SignalRequests} from "~/api/signal-requests";

export default {
  name: "signal",
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
    bus: new mitt(),
    isSignalPlayed: false,
    mdiPlay,
    mdiStop
  }),
  computed: {
    signalIsSaved() {
      return !!this.signal.id
    },
    signalParamsText() {
      let text = ''
      if (this.signal.sampleRate) {
        text += this._t('sampleRate',
            {sampleRate: NumberUtils.reduceFractionDigitsByValue(this.signal.sampleRate)})
      }
      const samplesNumber = this.signal.data?.length
      if (samplesNumber) {
        text += ', ' + this._t('samplesNumber', {samplesNumber})
      }
      return text
    }
  },
  watch: {
    '$route.query.history'() {
      window.scrollTo(0,0)
      this.getSignalFromHistoryOrLoad()
    },
    signal() {
      this.signal.name && this.$refs.nameInput.focus()
    }
  },
  mounted() {
    SignalRequests.setApiProvider(this.getApiProvider())
    this.getSignalFromHistoryOrLoad()
    this.bus.on('error', error => {
      this.showMessage({
        text: `${this._t('processSignalError')}: ${error.message}`
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
      const signal = dataStore().getSignalFromHistory(this.signalId, this.historyKey)
      if (signal) {
        this.signal = signal
      } else if (!isNaN(this.signalId) && this.signalId !== '0' && (!this.historyKey || this.historyKey === '0')) {
        this.loadWithOverlay(async () => {
          await this.loadSignal()
        })
      } else {
        this.signalNotFound()
      }
    },
    async loadSignal() {
      try {
        const signal = await SignalRequests.loadSignalWithData(this.signalId)
        SignalUtils.calculateSignalParams(signal)
        const historyKey = dataStore().addSignalToHistory(signal)
        if (this.historyKey === '0') {
          this.signal = signal
        } else {
          await useRouter().push(`/signal/${this.signalId}?history=${historyKey}`)
        }
      } catch (e) {
        if (e.status === 404) {
          this.signalNotFound()
        }
      }
    },
    async saveSignalAsNew() {
      delete this.signal.id
      await this.saveSignal()
    },
    async saveSignal() {
      this.clearValidation()
      await this.loadWithOverlay(async () => {
        const response = this.signalIsSaved
            ? await SignalRequests.saveExistingSignal(this.signal)
            : await SignalRequests.saveNewSignal(this.signal)
        if (response.ok) {
          this.signalIsSaved && dataStore().updateSignalInHistory(this.signal, this.historyKey)
          await useRouter().push('/signal-manager')
        } else {
          this.parseValidation(response.errors)
          this.showErrorsFromResponse(response, this._t('signalSaveError'))
        }
      })
    },
    signalNotFound() {
      this.showMessage({
        text: this._t('signalNotFound'),
        onHide: () => {
          useRouter().push(dataStore().isSignedIn ? '/signal-manager' : '/signal-generator')
        }
      })
    },
    exportSignalToTxt() {
      FileUtils.exportSignal(this.signal, 'txt')
    },
    exportSignalToCsv() {
      FileUtils.exportSignal(this.signal, 'csv')
    },
    exportSignalToJson() {
      FileUtils.exportSignal(this.signal, 'json')
    },
    exportSignalToXml() {
      FileUtils.exportSignal(this.signal, 'xml')
    },
    exportSignalToWav() {
      if (this.signalId !== '0' && this.historyKey === '0') {
        FileUtils.saveSignalToWavFile(this.signal)
      } else {
        FileUtils.saveBlobToWavFile({
          fileName: FileUtils.getFileNameWithExtension(this.signal.name, '.wav'),
          blob: WavCoder.signalToWav(this.signal)
        })
      }
    },
    async playOrStopSignal() {
      if (this.isSignalPlayed) {
        SignalPlayer.stop()
        this.isSignalPlayed = false
      } else {
        this.isSignalPlayed = true
        await SignalPlayer.setSignal(this.signal).play(() => {
          this.isSignalPlayed = false
        })
      }
    },
  },
}
</script>
