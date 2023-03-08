<script>
import formValidation from "../../mixins/form-validation";
import {dataStore} from "../../stores/data-store";
import formValuesSaving from "../../mixins/form-values-saving";
import formNumberValues from "../../mixins/form-number-values";

export default {
  name: "TransformerBase",
  mixins: [formValidation, formValuesSaving, formNumberValues],
  props: {
    signal: Object,
    bus: Object
  },
  data: () => ({
    worker: null,
    transformFunctionName: ''
  }),
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
    doTransform() {
      if (this.form) {
        this.clearValidation()
        this.parseFloatForm()
        if (!this.validateFunction()) {
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
    addSignalToHistoryAndOpen(signal) {
      let signalKey = dataStore().addSignalToHistory(signal)
      useRouter().push('/signal/' + signalKey)
    },
    transformFunction() {
      return this.signal
    },
    validateFunction() {
    },
    validatePositiveNumber(key) {
      let value = this.form[key]
      let invalidMsg = ''
      if (isNaN(value)) {
        invalidMsg = 'Should be a number'
      } else if (value < 0) {
        invalidMsg = 'Must have positive value'
      }
      if (invalidMsg) {
        this.validation[key].push(invalidMsg)
      }
      return !invalidMsg
    },
    initWorker() {
      this.worker = new Worker('/transformers.js')
      this.worker.onmessage = e => {
        if (e.data.signal) {
          this.addSignalToHistoryAndOpen(JSON.parse(e.data.signal))
        }
        if (e.data.progress) {
          this.bus.emit('progress', {
            progress: e.data.progress,
            operation: e.data.operation,
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
