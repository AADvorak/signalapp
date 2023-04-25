<script>
import formValidation from "../../mixins/form-validation";
import {dataStore} from "../../stores/data-store";
import formValuesSaving from "../../mixins/form-values-saving";
import formNumberValues from "../../mixins/form-number-values";
import ComponentBase from "../component-base";

export default {
  name: "TransformerBase",
  extends: ComponentBase,
  mixins: [formValidation, formValuesSaving, formNumberValues],
  props: {
    signal: Object,
    bus: Object
  },
  data: () => ({
    worker: null,
    transformFunctionName: '',
    form: {},
    validation: {},
    INPUT_PARAMS: {}
  }),
  computed: {
    numberInputs() {
      let inputs = []
      for (let key in this.form) {
        inputs.push(key)
      }
      return inputs
    }
  },
  mounted() {
    this.initWorker()
    this.form && this.restoreFormValues()
    this.bus.on('transform', () => {
      this.doTransform()
    })
    this.bus.on('cancel', () => {
      this.worker.terminate()
    })
  },
  beforeUnmount() {
    this.bus.off('transform')
    this.bus.off('cancel')
  },
  methods: {
    _trp(key, params) {
      return this.$t(`transformerParams.${this.$options.name}.${key}`, params)
    },
    _ton(key) {
      return this.$t(`operationNames.${key}`)
    },
    doTransform() {
      if (this.form && JSON.stringify(this.form) !== '{}') {
        this.clearValidation()
        this.parseFloatForm()
        if (!this.validateForm()) {
          this.bus.emit('validationFailed')
          return
        }
        this.saveFormValues()
      }
      if (this.transformFunctionName) {
        this.worker.postMessage(this.makeWorkerMessage())
      } else {
        this.addSignalToHistoryAndOpen(this.transformFunction())
      }
    },
    makeWorkerMessage() {
      return {
        transformFunctionName: this.transformFunctionName,
        signal: JSON.stringify(this.signal),
        params: JSON.stringify(this.form || {})
      }
    },
    changeSignalNameAndDescription(signal) {
      signal.description += '\n' + this.makeTransformedWithText() + ' ' + this.makeParamsText()
    },
    makeTransformedWithText() {
      return this._tc('messages.transformed') + ' ' + this._tr('with' + this.$options.name)
    },
    makeParamsText() {
      let paramsDescription = ''
      for (const key in this.form) {
        paramsDescription += (paramsDescription && ', ') + `${this._trp(key).toLowerCase()} = ${this.form[key]}`
      }
      return paramsDescription && `(${paramsDescription})`
    },
    addSignalToHistoryAndOpen(signal) {
      const route = useRoute()
      const signalId = route.params.id, currentHistoryKey = route.query.history
      const historyKey = dataStore().addSignalToHistory(signal, currentHistoryKey)
      useRouter().push(`/signal/${signalId || '0'}?history=${historyKey}`)
      this.bus.emit('transformed')
    },
    transformFunction() {
      return this.signal
    },
    validateForm() {
      let validated = true
      for (let field in this.form) {
        const validationMsg = this.getNumberValidationMsg(field)
        if (validationMsg) {
          this.validation[field].push(validationMsg)
          validated = false
        }
      }
      return validated
    },
    initWorker() {
      this.worker = new Worker('/transformers.js')
      this.worker.onmessage = e => {
        if (e.data.signal) {
          let signal = JSON.parse(e.data.signal)
          this.changeSignalNameAndDescription(signal)
          this.addSignalToHistoryAndOpen(signal)
        }
        if (e.data.progress) {
          this.bus.emit('progress', {
            progress: e.data.progress,
            operation: e.data.operation ? this._ton(e.data.operation) : '',
          })
        }
        if (e.data.error) {
          this.bus.emit('error', e.data.error)
        }
      }
    },
  },
}
</script>
