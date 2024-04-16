<template>
  <card-with-layout :message="message">
    <v-card-text>
      <v-form>
        <number-input
            v-for="field in formFields"
            :field="field"
            :parent-name="$options.name"
            :field-obj="form[field]"
            @update="v => form[field].value = v"/>
      </v-form>
      <div>{{ info }}</div>
      <div>{{ _t('recordStatuses.' + recordStatus) }}</div>
      <div class="d-flex flex-wrap">
        <v-btn color="error" :disabled="recordStatus !== RECORD_STATUSES.READY" @click="startRecord">
          {{ _t('rec') }}
        </v-btn>
        <v-btn color="warning" :disabled="recordStatus === RECORD_STATUSES.READY" @click="pauseOrContinueRecord">
          {{ _t('pause') }}
        </v-btn>
        <v-btn color="gray" :disabled="recordStatus === RECORD_STATUSES.READY" @click="stopRecord">
          {{ _t('stop') }}
        </v-btn>
      </div>
      <div>{{ recordedInfo }}</div>
      <div class="d-flex flex-wrap">
        <v-btn
            color="primary"
            :disabled="!recordedAudio"
            @click="playOrStopSignal"
        >
          <v-icon>
            {{ isSignalPlayed ? mdiStop : mdiPlay }}
          </v-icon>
        </v-btn>
        <v-btn color="primary" :disabled="!recordedAudio" :loading="saveRequestSent" @click="openRecorded">
          {{ _tc('buttons.open') }}
        </v-btn>
        <v-btn color="success" :disabled="!recordedAudio" :loading="saveRequestSent" @click="saveRecorded">
          {{ _tc('buttons.save') }}
        </v-btn>
        <v-btn color="secondary" :disabled="!recordedAudio" @click="exportRecordedToWav">
          {{ _tc('buttons.exportWav') }}
        </v-btn>
        <v-btn color="gray" :disabled="!recordedAudio" @click="clearRecorded">
          {{ _t('clear') }}
        </v-btn>
      </div>
    </v-card-text>
  </card-with-layout>
</template>

<script>
import PageBase from "../components/base/page-base.vue";
import formValidation from "../mixins/form-validation";
import formValuesSaving from "../mixins/form-values-saving";
import {dataStore} from "~/stores/data-store";
import FileUtils from "../utils/file-utils";
import Recorder from "../audio/recorder";
import formNumberValues from "../mixins/form-number-values";
import {mdiPlay, mdiStop} from "@mdi/js";
import SignalPlayer from "../audio/signal-player";
import WavCoder from "../audio/wav-coder";
import SignalActions from "../mixins/signal-actions";
import {SignalRequests} from "~/api/signal-requests";
import CardWithLayout from "~/components/common/card-with-layout.vue";
import NumberInput from "~/components/common/number-input.vue";

export default {
  name: "signal-recorder",
  components: {NumberInput, CardWithLayout},
  extends: PageBase,
  mixins: [formValidation, formValuesSaving, formNumberValues, SignalActions],
  data: () => ({
    form: {
      sampleRate: {
        value: 8000,
        params: {
          min: 3000,
          max: 48000,
          step: 50
        }
      }
    },
    RECORD_STATUSES: {
      READY: 'ready',
      RECORDING: 'recording',
      PAUSED: 'paused',
    },
    recordStatus: '',
    info: '',
    gumStream: undefined,
    recorder: undefined,
    input: undefined,
    audioContext: undefined,
    saveRequestSent: false,
    isSignalPlayed: false,
    mdiPlay,
    mdiStop
  }),
  computed: {
    recordedAudio() {
      return dataStore().recordedAudio
    },
    recordedInfo() {
      if (!this.recordedAudio) {
        return this._t('nothingRecorded')
      }
      return this.recordedAudio.fileName
    }
  },
  mounted() {
    this.recordStatus = this.RECORD_STATUSES.READY
    this.restoreFormValues()
  },
  methods: {
    validateForm() {
      const field = 'sampleRate'
      const validationMsg = this.getNumberValidationMsg(field)
      if (validationMsg) {
        this.pushValidationMsg(field, validationMsg)
      }
      return !validationMsg
    },
    startRecord() {
      this.clearValidation()
      if (!this.validateForm()) {
        return
      }
      this.saveFormValues()
      const constraints = {audio: true, video: false},
          sampleRate = this.formValues.sampleRate
      navigator.mediaDevices.getUserMedia(constraints).then(stream => {
        this.audioContext = new AudioContext({sampleRate})
        this.info = this._t('audioFormat', {sampleRate})
        this.gumStream = stream
        this.input = this.audioContext.createMediaStreamSource(stream)
        this.rec = new Recorder(this.input,{numChannels:1})
        this.rec.record()
        this.recordStatus = this.RECORD_STATUSES.RECORDING
      }).catch(err => {
        this.info = err
        this.recordStatus = this.RECORD_STATUSES.READY
      })
    },
    pauseOrContinueRecord() {
      if (this.recordStatus === this.RECORD_STATUSES.PAUSED) {
        this.continueRecord()
      } else if (this.recordStatus === this.RECORD_STATUSES.RECORDING) {
        this.pauseRecord()
      }
    },
    pauseRecord() {
      this.rec.stop()
      this.recordStatus = this.RECORD_STATUSES.PAUSED
    },
    continueRecord() {
      this.rec.record()
      this.recordStatus = this.RECORD_STATUSES.RECORDING
    },
    stopRecord() {
      this.rec.stop()
      this.gumStream.getAudioTracks()[0].stop()
      this.rec.exportWAV(blob => {
        let date = new Date()
        dataStore().setRecordedAudio({
          fileName: this._t('fileName', {dateTime: `${date.toLocaleDateString()} ${date.toLocaleTimeString()}`}),
          blob
        })
      })
      this.recordStatus = this.RECORD_STATUSES.READY
    },
    async openRecorded() {
      const signal = (await WavCoder.wavToSignals(await this.recordedAudio.blob.arrayBuffer()))[0]
      if (!this.validateSignalLength(signal)) {
        return
      }
      signal.name = this.recordedAudio.fileName
      this.saveSignalToHistoryAndOpen(signal)
    },
    async saveRecorded() {
      await this.loadWithFlag(async () => {
        const response = await SignalRequests.saveNewSignalDtoWithData({
          name: this.recordedAudio.fileName,
          maxAbsY: 1,
          xMin: 0,
          sampleRate: this.formValues.sampleRate
        }, this.recordedAudio.blob)
        if (response.ok) {
          useRouter().push('/signal-manager')
        } else {
          this.showErrorsFromResponse(response, this._tc('messages.fileSaveError'))
        }
      }, 'saveRequestSent')
    },
    exportRecordedToWav() {
      FileUtils.saveBlobToWavFile(this.recordedAudio)
    },
    clearRecorded() {
      dataStore().setRecordedAudio(null)
    },
    async playOrStopSignal() {
      if (this.isSignalPlayed) {
        SignalPlayer.stop()
        this.isSignalPlayed = false
      } else {
        this.isSignalPlayed = true
        await SignalPlayer.setBlob(this.recordedAudio.blob).play(() => {
          this.isSignalPlayed = false
        })
      }
    },
  },
}
</script>
