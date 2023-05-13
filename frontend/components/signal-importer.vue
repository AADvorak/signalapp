<template>
  <v-card-title>
    {{ _t('import') }}
  </v-card-title>
  <v-card-text>
    <v-file-input
        v-model="file"
        accept=".txt,.wav"
        :label="_t('fromTxtOrWavFile')"/>
  </v-card-text>
  <select-dialog :items="selectItems" :opened="select.opened" :text="select.text" @select="select.select" @cancel="select.cancel"/>
  <message :opened="message.opened" :text="message.text" @hide="message.onHide"/>
</template>

<script>
import PageBase from "./page-base";
import FileUtils from "../utils/file-utils";
import WavCoder from "../audio/wav-coder";
import SignalUtils from "../utils/signal-utils";

export default {
  name: "signal-importer",
  extends: PageBase,
  data: () => ({
    file: [],
    selectItems: [
      {name: 'open', color: 'primary'},
      {name: 'save', color: 'success'},
    ],
  }),
  emits: ['signal'],
  watch: {
    file(newValue) {
      let file = newValue[0]
      switch (file.type) {
        case 'audio/wav':
          this.importFromFile(file, this.openWav, this.saveWav)
          break
        case 'text/plain':
          this.importFromFile(file, this.openTxt, this.saveTxt)
          break
      }
    },
  },
  methods: {
    async importFromFile(file, openFunc, saveFunc) {
      this.askSelect({
        text: this._t('selectAction'),
        select: async (action) => {
          if (action === 'open') {
            await openFunc(file)
          } else if (action === 'save') {
            await saveFunc(file)
          }
          // todo clear file input
        }
      })
    },
    async openWav(file) {
      try {
        const arrayBuffer = await FileUtils.readArrayBufferFromWavFile(file)
        const signal = await WavCoder.wavToSignal(arrayBuffer)
        if (!this.validateSignalLength(signal)) {
          return
        }
        this.makeSignalNameAndDescriptionFromFile(signal, file)
        this.$emit('signal', signal)
      } catch (e) {
        this.showMessage({
          text: `${this._tc('messages.error')}: ${e.message}`
        })
      }
    },
    async saveWav(file) {
      const arrayBuffer = await FileUtils.readArrayBufferFromWavFile(file)
      const response = await this.getApiProvider().post('/api/signals/wav/' + file.name,
          arrayBuffer, 'audio/wave')
      if (response.ok) {
        useRouter().push('/signal-manager')
      } else {
        this.showErrorsFromResponse(response, this._tc('messages.fileSaveError'))
      }
    },
    async openTxt(file) {
      const signal = await this.tryReadSignalFromTxtFile(file)
      signal && this.$emit('signal', signal)
    },
    async saveTxt(file) {
      const signal = await this.tryReadSignalFromTxtFile(file)
      if (!signal) {
        return
      }
      signal.maxAbsY = SignalUtils.calculateMaxAbsY(signal)
      const response = await this.getApiProvider().postJson('/api/signals/', signal)
      if (response.ok) {
        useRouter().push('/signal-manager')
      } else if (response.status === 400) {
        for (let error of response.errors) {
          if (error.field === 'data') {
            this.showMessage({
              text: this.getLocalizedErrorMessage(error)
            })
          }
        }
      } else {
        this.showErrorsFromResponse(response)
      }
    },
    async tryReadSignalFromTxtFile(file) {
      try {
        const signal = await FileUtils.readSignalFromTxtFile(file)
        if (!this.validateSignalLength(signal)) {
          return
        }
        this.makeSignalNameAndDescriptionFromFile(signal, file)
        return signal
      } catch (e) {
        this.showMessage({
          text: this._t(e.message)
        })
      }
    },
    makeSignalNameAndDescriptionFromFile(signal, file) {
      signal.name = file.name
      signal.description = this._t('importedFromFile', {name: file.name})
    },
    validateSignalLength(signal) {
      const maxSamplesNumber = 1024000, samplesNumber = signal.data.length
      if (samplesNumber > maxSamplesNumber) {
        this.showMessage({
          text: this._t('wrongSignalSamplesNumber', {samplesNumber, maxSamplesNumber})
        })
        return false
      }
      return true
    }
  }
}
</script>
