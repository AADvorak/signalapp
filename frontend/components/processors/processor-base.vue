<template>
  <number-input
      v-for="field in formFields"
      ref="inputRefs"
      :field="field"
      :label="_tpp(field)"
      :field-obj="form[field]"
      @update="v => form[field].value = v"/>
</template>

<script>
import formValidation from "../../mixins/form-validation";
import formValuesSaving from "../../mixins/form-values-saving";
import formNumberValues from "../../mixins/form-number-values";
import ComponentBase from "../base/component-base.vue";
import actionWithTimeout from "../../mixins/action-with-timeout";
import {toRaw, isProxy} from "vue";
import NumberInput from "~/components/common/number-input.vue";
import {ProcessingEvents} from "~/dictionary/processing-events";

export default {
  name: "processor-base",
  components: {NumberInput},
  extends: ComponentBase,
  mixins: [formValidation, formValuesSaving, formNumberValues, actionWithTimeout],
  props: {
    signal: Object,
    bus: Object
  },
  data: () => ({
    worker: null,
    form: {},
  }),
  computed: {
    processorName() {
      const name = this.$options.name
      return name.charAt(0).toLowerCase() + name.slice(1)
    }
  },
  watch: {
    formValues() {
      this.bus.emit(ProcessingEvents.VALIDATION_FAILED)
      this.actionWithTimeout(() => {
        this.clearValidation()
        this.parseFloatForm()
        if (!this.validateForm()) {
          this.bus.emit(ProcessingEvents.VALIDATION_FAILED)
          return
        }
        this.bus.emit(ProcessingEvents.VALIDATION_PASSED)
        this.saveFormValues()
      })
    }
  },
  mounted() {
    this.form && this.restoreFormValues()
    this.bus.on(ProcessingEvents.PROCESS, () => {
      this.doProcess()
    })
    this.bus.on(ProcessingEvents.CANCEL, () => {
      this.worker && this.worker.terminate()
    })
    setTimeout(() => this.focusFirstFormField())
    if (!this.formFields.length) {
      this.bus.emit(ProcessingEvents.PROCESS)
    }
  },
  beforeUnmount() {
    this.bus.off(ProcessingEvents.PROCESS)
    this.bus.off(ProcessingEvents.CANCEL)
  },
  methods: {
    _tpp(key, params) {
      return this.$t(`processorParams.${this.$options.name}.${key}`, params)
    },
    doProcess() {
      this.worker = new Worker('/worker/processors.js')
      this.worker.onmessage = msg => {
        if (msg.data.signal) {
          const signal = msg.data.signal
          this.changeSignalNameAndDescription(signal)
          this.addSignalToHistoryAndOpen(signal)
        }
        if (msg.data.progress) {
          this.bus.emit(ProcessingEvents.PROGRESS_CHANGED, msg.data.progress)
        }
      }
      this.worker.onerror = e => {
        this.bus.emit(ProcessingEvents.PROCESSING_FINISHED)
        this.bus.emit(ProcessingEvents.PROCESSING_ERROR, e)
      }
      this.worker.postMessage(this.makeWorkerMessage())
      this.bus.emit(ProcessingEvents.PROCESSING_STARTED)
    },
    makeWorkerMessage() {
      return {
        processorName: this.processorName,
        signal: this.toRawDeep(this.signal),
        params: this.formValues
      }
    },
    toRawDeep(proxy) {
      const obj = toRaw(proxy)
      for (const key in obj) {
        if (isProxy(obj[key])) {
          obj[key] = toRaw(obj[key])
        }
      }
      return obj
    },
    changeSignalNameAndDescription(signal) {
      if (!signal.description) {
        signal.description = ''
      }
      if (signal.description) {
        signal.description += '\n'
      }
      signal.description += this.makeProcessedWithText() + ' ' + this.makeParamsText()
    },
    makeProcessedWithText() {
      return this._tc('messages.processed') + ' ' + this._tpn('with' + this.$options.name)
    },
    makeParamsText() {
      let paramsDescription = ''
      for (const field in this.formValues) {
        paramsDescription += (paramsDescription && ', ') + `${this._tpp(field).toLowerCase()} = ${this.formValues[field]}`
      }
      return paramsDescription && `(${paramsDescription})`
    },
    addSignalToHistoryAndOpen(signal) {
      const route = useRoute()
      const signalId = route.params.id || signal.id || '0', currentHistoryKey = route.query.history || '0'
      const historyKey = signalStore().addSignalToHistory(signal, currentHistoryKey)
      useRouter().push(`/signal/${signalId}?history=${historyKey}`)
      this.bus.emit(ProcessingEvents.PROCESSING_FINISHED)
    },
    validateForm() {
      let validated = true
      for (let field in this.form) {
        const validationMsg = this.getNumberValidationMsg(field)
        if (validationMsg) {
          this.pushValidationMsg(field, validationMsg)
          validated = false
        }
      }
      return validated
    },
  },
}
</script>
