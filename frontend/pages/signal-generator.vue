<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-title>
          {{ _t('generate') }}
        </v-card-title>
        <v-card-text>
          <v-form @submit.prevent="generateAndOpenSignal">
            <number-input
                v-for="numberInput in numberInputs"
                :field="numberInput"
                :parent-name="$options.name"
                :field-obj="form[numberInput]"
                @update="v => form[numberInput].value = v"/>
            <v-select
                v-model="form.form.value"
                item-title="name"
                item-value="code"
                :items="signalForms"
                :label="_t('form')"/>
            <div class="d-flex">
              <v-btn color="primary" :disabled="!signal" @click="generateAndOpenSignal">
                {{ _t('generate') }}
              </v-btn>
              <v-btn
                  color="secondary"
                  @click="preview = !preview"
              >
                {{ _t('preview') }}
                <v-icon>
                  {{ preview ? mdiEye : mdiEyeOff }}
                </v-icon>
              </v-btn>
            </div>
          </v-form>
          <chart-drawer class="mt-4" v-if="preview && signal" :signals="signal ? [signal] : []" :minimal="true"/>
        </v-card-text>
        <signal-importer @signal="s => saveSignalToHistoryAndOpen(s)"/>
      </v-card>
    </div>
    <message :opened="message.opened" :text="message.text" @hide="message.onHide"/>
    <loading-overlay :show="loadingOverlay"/>
  </NuxtLayout>
</template>

<script>
import formValidation from "../mixins/form-validation";
import formValuesSaving from "../mixins/form-values-saving";
import formNumberValues from "../mixins/form-number-values";
import PageBase from "../components/page-base";
import SignalUtils from "../utils/signal-utils";
import ChartDrawer from "../components/chart-drawer";
import {mdiEye, mdiEyeOff} from "@mdi/js";
import NumberInput from "../components/number-input";
import actionWithTimeout from "../mixins/action-with-timeout";
import SignalImporter from "../components/signal-importer";
import SignalActions from "../mixins/signal-actions";

const PREVIEW_KEY = 'SignalGeneratorPreview'
const SIGNAL_FORMS = {
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
  noise: ({amplitude, offset}) => {
    return offset + amplitude * (Math.random() * 2 - 1)
  }
}
const VALIDATION_FUNCTIONS = {
  length(ctx) {
    if (!ctx.isAllFieldsValidated(['length', 'sampleRate'])) {
      return
    }
    let pointsNumber = ctx.formValues.length * ctx.formValues.sampleRate
    if (pointsNumber < 2 || pointsNumber > 512000) {
      return ctx._t('wrongPointsNumber', {pointsNumber: Math.floor(pointsNumber)})
    }
  },
  frequency(ctx) {
    if (!ctx.isAllFieldsValidated(['frequency', 'sampleRate'])) {
      return
    }
    if (2 * ctx.formValues.frequency > ctx.formValues.sampleRate) {
      return ctx._t('lessThanHalfSampleRate')
    }
  }
}

export default {
  name: "signal-generator",
  components: {SignalImporter, NumberInput, ChartDrawer},
  extends: PageBase,
  mixins: [formValidation, formValuesSaving, formNumberValues, actionWithTimeout, SignalActions],
  data: () => ({
    mdiEye,
    mdiEyeOff,
    preview: localStorage.getItem(PREVIEW_KEY) === 'true',
    signal: null,
    form: {
      begin: {
        value: 0,
        params: {
          min: -10,
          max: 10,
          step: 0.01
        }
      },
      length: {
        value: 0.1,
        params: {
          min: 0,
          max: 20,
          step: 0.01
        }
      },
      sampleRate: {
        value: 3000,
        params: {
          min: 0,
          max: 48000,
          step: 50
        }
      },
      frequency: {
        value: 100,
        params: {
          min: 0,
          max: 20000,
          step: 20
        }
      },
      amplitude: {
        value: 1,
        params: {
          min: 0,
          max: 10,
          step: 0.01
        }
      },
      offset: {
        value: 0,
        params: {
          min: -5,
          max: 5,
          step: 0.01
        }
      },
      form: {value: 'sine'}
    },
  }),
  computed: {
    signalForms() {
      return Object.keys(SIGNAL_FORMS).map(code => ({code, name: this._t('forms.' + code)}))
    },
    numberInputs() {
      return this.formFields.filter(field => field !== 'form')
    }
  },
  watch: {
    preview(newValue) {
      localStorage.setItem(PREVIEW_KEY, newValue)
    },
    formValues: {
      handler(newValue, oldValue) {
        if (!this.onlyTypesChanged(newValue, oldValue)) {
          this.actionWithTimeout(() => this.precalculateSignal())
        }
      },
      deep: true
    }
  },
  mounted() {
    this.restoreFormValues()
    this.precalculateSignal()
  },
  methods: {
    precalculateSignal() {
      this.clearValidation()
      this.parseFloatForm({exclude: ['form']})
      if (!this.validateForm()) {
        this.signal = null
        return
      }
      this.saveFormValues()
      let data = []
      let step = 1 / this.formValues.sampleRate
      for (let x = this.formValues.begin; x < this.formValues.begin + this.formValues.length; x += step) {
        data.push(SIGNAL_FORMS[this.formValues.form]({x, ...this.formValues}))
      }
      const form = this._t('forms.' + this.formValues.form)
      let signal = {
        id: 0,
        name: this._t('signalName', {form}),
        description: this._t('description', {form, frequency: this.formValues.frequency, length: data.length}),
        sampleRate: this.formValues.sampleRate,
        xMin: this.formValues.begin,
        data
      }
      SignalUtils.calculateSignalParams(signal)
      this.signal = signal
    },
    generateAndOpenSignal() {
      this.signal && this.saveSignalToHistoryAndOpen(this.signal)
    },
    validateForm() {
      let validationObj = {}
      for (const field of this.numberInputs) {
        const validationMsg = this.getNumberValidationMsg(field)
        if (validationMsg) {
          validationObj[field] = validationMsg
        }
      }
      this.pushValidationMsgFromObject(validationObj)
      validationObj = {}
      for (const field in VALIDATION_FUNCTIONS) {
        const validationMsg = VALIDATION_FUNCTIONS[field](this)
        if (validationMsg) {
          validationObj[field] = validationMsg
        }
      }
      this.pushValidationMsgFromObject(validationObj)
      return this.isAllFieldsValidated(this.numberInputs)
    },
    pushValidationMsgFromObject(validationObj) {
      for (const field in validationObj) {
        this.pushValidationMsg(field, validationObj[field])
      }
    },
    onlyTypesChanged(oldForm, newForm) {
      for (let field in newForm) {
        if (oldForm[field].toString() !== newForm[field].toString()) {
          return false
        }
      }
      return true
    }
  },
}
</script>
