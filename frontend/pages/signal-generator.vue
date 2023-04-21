<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-title>
          {{ _t('generate') }}
        </v-card-title>
        <v-card-text>
          <v-form @submit.prevent="generateAndOpenSignal">
            <v-text-field
                v-model="form.begin"
                type="number"
                step="0.01"
                :label="_t('begin') + ' (B)'"
                :error="!!validation.begin.length"
                :error-messages="validation.begin"
                required/>
            <v-text-field
                v-model="form.length"
                type="number"
                step="0.1"
                min="0"
                :label="_t('length') + ' (L)'"
                :error="!!validation.length.length"
                :error-messages="validation.length"
                required/>
            <v-text-field
                v-model="form.sampleRate"
                type="number"
                step="100"
                min="0"
                :label="_t('sampleRate') + ' (S)'"
                :error="!!validation.sampleRate.length"
                :error-messages="validation.sampleRate"
                required/>
            <v-text-field
                v-model="form.frequency"
                type="number"
                step="10"
                min="0"
                :label="_t('frequency') + ' (F)'"
                :error="!!validation.frequency.length"
                :error-messages="validation.frequency"
                required/>
            <v-text-field
                v-model="form.amplitude"
                type="number"
                step="1"
                min="0"
                :label="_t('amplitude') + ' (A)'"
                :error="!!validation.amplitude.length"
                :error-messages="validation.amplitude"
                required/>
            <v-text-field
                v-model="form.offset"
                type="number"
                step="1"
                :label="_t('offset') + ' (O)'"
                :error="!!validation.offset.length"
                :error-messages="validation.offset"
                required/>
            <v-select
                v-model="form.form"
                item-title="name"
                item-value="code"
                :items="signalForms"
                :label="_t('form')"/>
            <div class="d-flex">
              <v-btn color="primary" @click="generateAndOpenSignal">
                {{ _t('generate') }}
              </v-btn>
            </div>
          </v-form>
        </v-card-text>
        <v-card-title>
          {{ _t('import') }}
        </v-card-title>
        <v-card-text>
          <v-file-input
              v-model="file"
              accept=".txt,.wav"
              :label="_t('fromTxtOrWavFile')"/>
        </v-card-text>
      </v-card>
    </div>
    <message :opened="message.opened" :text="message.text" @hide="message.onHide"/>
  </NuxtLayout>
</template>

<script>
import formValidation from "../mixins/form-validation";
import formValuesSaving from "../mixins/form-values-saving";
import formNumberValues from "../mixins/form-number-values";
import {dataStore} from "../stores/data-store";
import FileUtils from "../utils/file-utils";
import PageBase from "../components/page-base";
import SignalUtils from "../utils/signal-utils";

export default {
  name: "signal-generator",
  extends: PageBase,
  mixins: [formValidation, formValuesSaving, formNumberValues],
  data: () => ({
    file: [],
    form: {
      begin: 0,
      length: 0.1,
      sampleRate: 3000,
      frequency: 100,
      amplitude: 1,
      offset: 0,
      form: 'sine'
    },
    validation: {
      begin: [],
      length: [],
      sampleRate: [],
      frequency: [],
      amplitude: [],
      offset: []
    },
    SIGNAL_FORMS: {
      sine: ({x, frequency, amplitude, offset}) => {
        return offset + amplitude * Math.sin(x * 2 * Math.PI * frequency)
      },
      square: ({x, frequency, amplitude, offset}) => {
        return Math.sin(x * 2 * Math.PI * frequency) >= 0 ? offset + amplitude : offset - amplitude
      },
      triangle: ({x, frequency, amplitude, offset}) => {
        return offset + (2 * amplitude / Math.PI) * Math.asin(Math.sin(x * 2 * Math.PI * frequency))
      },
      sawtooth: ({x, frequency, amplitude, offset}) => {
        return offset + (2 * amplitude / Math.PI) * Math.atan(Math.tan(x * Math.PI * frequency))
      },
      noise: ({x, frequency, amplitude, offset}) => {
        return offset + amplitude * (Math.random() * 2 - 1)
      }
    },
    VALIDATION_FUNCTIONS: {
      length(values, ctx) {
        if (values.length <= 0) return ctx._tc('validation.greaterThanZero')
        let pointsNumber = values.length * values.sampleRate
        if (!ctx.VALIDATION_FUNCTIONS.sampleRate(values, ctx) && (pointsNumber < 2 || pointsNumber > 512000)) {
          return ctx._t('wrongPointsNumber', {pointsNumber: Math.floor(pointsNumber)})
        }
      },
      sampleRate(values, ctx) {
        const maxValue = 48000
        if (values.sampleRate <= 0) return ctx._tc('validation.greaterThanZero')
        if (values.sampleRate > maxValue) return ctx._t('notGreaterThan', {maxValue})
      },
      frequency(values, ctx) {
        if (values.frequency <= 0) return ctx._tc('validation.greaterThanZero')
        if (!ctx.VALIDATION_FUNCTIONS.sampleRate(values, ctx) && 2 * values.frequency > values.sampleRate) {
          return ctx._t('lessThanHalfSampleRate')
        }
      },
      amplitude(values, ctx) {
        if (values.amplitude < 0) return ctx._tc('validation.greaterThanZero')
      },
    }
  }),
  computed: {
    signalForms() {
      let forms = []
      for (let code in this.SIGNAL_FORMS) {
        forms.push({code, name: this._t('forms.' + code)})
      }
      return forms
    }
  },
  watch: {
    file(newValue) {
      let file = newValue[0]
      switch (file.type) {
        case 'audio/wav':
          this.importFromWavFile(file)
          break
        case 'text/plain':
          this.importFromTxtFile(file)
          break
      }
    }
  },
  mounted() {
    this.restoreFormValues()
  },
  methods: {
    generateAndOpenSignal() {
      this.clearValidation()
      this.parseFloatForm({exclude: ['form']})
      if (!this.validateForm()) {
        return
      }
      this.saveFormValues()
      let data = []
      let step = 1 / this.form.sampleRate
      for (let x = this.form.begin; x < this.form.begin + this.form.length; x += step) {
        data.push(this.SIGNAL_FORMS[this.form.form]({x, ...this.form}))
      }
      const form = this._t('forms.' + this.form.form)
      this.saveSignalToHistoryAndOpen({
        id: 0,
        name: this._t('signalName', {form}),
        description: this._t('description', {form, frequency: this.form.frequency, length: data.length}),
        sampleRate: this.form.sampleRate,
        xMin: this.form.begin,
        data
      })
    },
    validateForm() {
      let validated = true
      for (let fieldName in this.form) {
        if (fieldName !== 'form') {
          let value = this.form[fieldName]
          let validationMsg = ''
          if (isNaN(value)) {
            validationMsg = this._tc('validation.mustBeNumber')
          } else {
            let validationFunction = this.VALIDATION_FUNCTIONS[fieldName]
            if (validationFunction) {
              validationMsg = validationFunction(this.form, this)
            }
          }
          if (validationMsg) {
            this.validation[fieldName].push(validationMsg)
            validated = false
          }
        }
      }
      return validated
    },
    async importFromTxtFile(file) {
      try {
        let signal = await FileUtils.readSignalFromTxtFile(file)
        signal.name = file.name
        signal.description = this._t('importedFromFile', {name: file.name})
        this.saveSignalToHistoryAndOpen(signal)
      } catch (e) {
        this.showMessage({
          text: e.message
        })
      }
    },
    async importFromWavFile(file) {
      let data = await FileUtils.readSignalFromWavFile(file)
      let response = await this.getApiProvider().post('/api/signals/wav/' + file.name,
          data, 'audio/wave')
      if (response && response.ok) {
        useRouter().push('/signal-manager')
      } else {
        this.showErrorsFromResponse(response, this._tc('messages.fileSaveError'))
        // todo clear file input
      }
    },
    saveSignalToHistoryAndOpen(signal) {
      signal.maxAbsY = SignalUtils.calculateMaxAbsY(signal)
      SignalUtils.calculateSignalParams(signal)
      useRouter().push('/signal/0?history=' + dataStore().addSignalToHistory(signal))
    }
  },
}
</script>
