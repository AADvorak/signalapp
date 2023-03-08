<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <v-form>
            <v-text-field
                v-model="form.sampleRate"
                type="number"
                step="100"
                min="0"
                label="Sample rate (Hz)"
                :error="!!validation.sampleRate.length"
                :error-messages="validation.sampleRate"
                required
            ></v-text-field>
          </v-form>
          <div>{{ info }}</div>
          <div>{{ recordStatus }}</div>
          <v-btn color="error" :disabled="recordStatus !== RECORD_STATUSES.READY" @click="startRecord">
            REC
          </v-btn>
          <v-btn color="warning" :disabled="recordStatus === RECORD_STATUSES.READY" @click="pauseOrContinueRecord">
            Pause
          </v-btn>
          <v-btn color="gray" :disabled="recordStatus === RECORD_STATUSES.READY" @click="stopRecord">
            Stop
          </v-btn>
          <div>{{ recordedInfo }}</div>
          <v-btn color="success" :disabled="!recordedAudio" @click="saveRecorded">
            Save
          </v-btn>
          <v-btn color="secondary" :disabled="!recordedAudio" @click="exportRecordedToWav">
            Export wav
          </v-btn>
          <v-btn color="gray" :disabled="!recordedAudio" @click="clearRecorded">
            Clear
          </v-btn>
        </v-card-text>
      </v-card>
    </div>
    <message :opened="message.opened" :text="message.text" @hide="message.onHide"></message>
  </NuxtLayout>
</template>

<script>
import PageBase from "../components/page-base";
import formValidation from "../mixins/form-validation";
import formValuesSaving from "../mixins/form-values-saving";
import {dataStore} from "../stores/data-store";
import FileUtils from "../utils/file-utils";
import Recorder from "../audio/recorder";

export default {
  name: "signal-recorder",
  extends: PageBase,
  mixins: [formValidation, formValuesSaving],
  data: () => ({
    form: {
      sampleRate: 8000
    },
    validation: {
      sampleRate: []
    },
    RECORD_STATUSES: {
      READY: 'Ready to start record',
      RECORDING: 'Recording...',
      PAUSED: 'Recording paused',
    },
    recordStatus: '',
    info: '',
    gumStream: undefined,
    recorder: undefined,
    input: undefined,
    audioContext: undefined,
  }),
  computed: {
    recordedAudio() {
      return dataStore().getRecordedAudio
    },
    recordedInfo() {
      if (!this.recordedAudio) {
        return 'Nothing is recorded'
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
      if (!this.form.sampleRate || this.form.sampleRate < 3000 || this.form.sampleRate > 48000) {
        this.validation.sampleRate.push('Must be between 3000 and 48000')
        return false
      }
      return true
    },
    startRecord() {
      this.clearValidation()
      if (!this.validateForm()) {
        return
      }
      this.saveFormValues()
      let constraints = {audio: true, video: false}
      navigator.mediaDevices.getUserMedia(constraints).then(stream => {
        this.audioContext = new AudioContext({
          sampleRate: this.form.sampleRate
        })
        this.info = 'Format: 1 channel pcm @ ' + this.audioContext.sampleRate / 1000 + 'kHz'
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
          fileName: `Recorded ${date.toLocaleDateString()} ${date.toLocaleTimeString()}.wav`,
          blob
        })
      })
      this.recordStatus = this.RECORD_STATUSES.READY
    },
    async saveRecorded() {
      let response = await this.getApiProvider().post('/api/signals/wav/' + this.recordedAudio.fileName,
          await this.recordedAudio.blob.arrayBuffer(), 'audio/wave')
      if (response && response.ok) {
        useRouter().push('/signal-manager')
      } else {
        this.showErrorsFromResponse(response, 'Error saving record')
      }
    },
    exportRecordedToWav() {
      FileUtils.saveBlobToWavFile(this.recordedAudio)
    },
    clearRecorded() {
      dataStore().setRecordedAudio(null)
    },
  },
}
</script>
