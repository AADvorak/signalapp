<template>
  <v-card-title>
    {{ _t('import') }}
  </v-card-title>
  <v-card-text>
    <v-file-input
        v-model="file"
        accept=".txt,.csv,.json,.xml,.wav"
        :label="_t('fromTextOrWavFile')"/>
  </v-card-text>
  <select-dialog
      :items="selectItems"
      :opened="select.opened"
      :text="select.text"
      :ask-remember="true"
      @select="select.select"
      @cancel="select.cancel"/>
  <message
      :opened="message.opened"
      :text="message.text"
      @hide="message.onHide"/>
</template>

<script>
import PageBase from "../base/page-base.vue";
import FileUtils from "../../utils/file-utils";
import WavCoder from "../../audio/wav-coder";
import SignalUtils from "../../utils/signal-utils";
import SignalActions from "../../mixins/signal-actions";
import {SignalRequests} from "~/api/signal-requests";
import {ImportSignalActions, SelectsWithSaving} from "~/utils/select-utils";

const ACTION_SELECT_ITEMS = [
  {name: ImportSignalActions.open, color: 'primary'},
  {name: ImportSignalActions.save, color: 'success'},
]

export default {
  name: "signal-importer",
  // todo project structure violation, this is not a page
  extends: PageBase,
  mixins: [SignalActions],
  data: () => ({
    file: [],
    selectItems: ACTION_SELECT_ITEMS,
  }),
  emits: ['signal'],
  watch: {
    file(newValue) {
      let file = newValue[0]
      if (file.type === 'audio/wav') {
        this.importFromFile(file, this.openWav, this.saveWav)
      } else if (Object.keys(FileUtils.EXTENSIONS_BY_TYPE).includes(file.type)) {
        this.importFromFile(file, this.openText, this.saveText)
      }
    },
  },
  methods: {
    async importFromFile(file, openFunc, saveFunc) {
      this.selectItems = ACTION_SELECT_ITEMS
      this.askSelect({
        key: SelectsWithSaving.importSignalActions.key,
        text: this._t('selectAction'),
        select: async (action) => {
          if (action === ImportSignalActions.open) {
            await openFunc(file)
          } else if (action === ImportSignalActions.save) {
            await saveFunc(file)
          }
          // todo clear file input
        }
      })
    },
    async openWav(file) {
      try {
        const arrayBuffer = await FileUtils.readArrayBufferFromWavFile(file)
        const signals = await WavCoder.wavToSignals(arrayBuffer)
        if (!this.validateSignalLength(signals[0])) {
          return
        }
        if (signals.length === 1) {
          this.makeSignalNameAndDescriptionFromFile(signals[0], file)
          this.$emit('signal', signals[0])
        } else {
          this.selectItems = []
          for (let i = 0; i < signals.length; i++) {
            this.selectItems.push({name: i + 1, color: 'secondary', noLocale: true})
          }
          this.askSelect({
            text: this._t('selectChannel'),
            select: async (channel) => {
              const channelNumber = parseInt(channel) - 1
              this.makeSignalNameAndDescriptionFromFile(signals[channelNumber], file)
              this.$emit('signal', signals[channelNumber])
            }
          })
        }
      } catch (e) {
        this.showMessage({
          text: `${this._tc('messages.error')}: ${e.message}`
        })
      }
    },
    async saveWav(file) {
      const arrayBuffer = await FileUtils.readArrayBufferFromWavFile(file)
      const audioData = await WavCoder.decodeWAV(arrayBuffer)
      if (!this.validateAudioDataLength(audioData)) {
        return
      }
      const blobs = WavCoder.splitChannels(audioData)
      const responses = []
      const promises = blobs.map((blob, i) => (async () => {
        const signalDto = {
          maxAbsY: 1,
          xMin: 0,
          sampleRate: audioData.sampleRate
        }
        this.makeSignalNameAndDescriptionFromFile(signalDto, file)
        if (blobs.length > 1) {
          signalDto.name += ' (' + i + ')'
        }
        const response = await SignalRequests.saveNewSignalDtoWithData(signalDto, blob)
        responses.push(response)
      })())
      await Promise.all(promises)
      let noErrors = true, errorResponse = null
      responses.forEach(response => {
        if (!response.ok) {
          noErrors = false
          errorResponse = response
        }
      })
      noErrors && await useRouter().push('/signal-manager')
      errorResponse && this.showErrorsFromResponse(errorResponse, this._tc('messages.fileSaveError'))
    },
    async openText(file) {
      const signal = await this.tryReadSignalFromTextFile(file)
      signal && this.$emit('signal', signal)
    },
    async saveText(file) {
      const signal = await this.tryReadSignalFromTextFile(file)
      if (!signal) {
        return
      }
      SignalUtils.calculateMaxAbsY(signal)
      const response = await SignalRequests.saveNewSignal(signal)
      if (response.ok) {
        await useRouter().push('/signal-manager')
      } else {
        this.showErrorsFromResponse(response)
      }
    },
    async tryReadSignalFromTextFile(file) {
      try {
        const signal = await FileUtils.readSignalFromTextFile(file)
        if (!this.validateSignalLength(signal)) {
          return
        }
        if (!signal.name) {
          this.makeSignalNameAndDescriptionFromFile(signal, file)
        }
        return signal
      } catch (e) {
        this.showMessage({
          text: this._t(e.message)
        })
      }
    },
    makeSignalNameAndDescriptionFromFile(signal, file) {
      signal.name = file.name
          .replaceAll('.wav', '')
          .replaceAll('.txt', '')
          .replaceAll('.csv', '')
      signal.description = this._t('importedFromFile', {name: file.name})
    }
  }
}
</script>
