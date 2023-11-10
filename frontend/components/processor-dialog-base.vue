<script>
import ComponentBase from "./component-base";
import StringUtils from "../utils/string-utils";

export default {
  name: "processor-dialog-base",
  extends: ComponentBase,
  props: {
    bus: Object
  },
  data: () => ({
    dialog: false,
    selectedProcessor: null,
    processing: false,
    processingDisabled: false,
    progress: {
      value: 0,
      operation: '',
    }
  }),
  computed: {
    progressBarText() {
      const {value, operation} = this.progress
      const formattedValue = value ? Math.ceil(value) + '%' : ''
      return operation ? this._ton(operation) + ': ' + formattedValue : formattedValue
    },
    okButtonText() {
      return this._tc('buttons.' + (this.processing ? 'working' : 'ok'))
    },
    titleWithRestrictedLength() {
      return StringUtils.restrictLength(this._tpn(this.selectedProcessor.code), 40)
    }
  },
  watch: {
    dialog(newValue) {
      if (newValue) {
        this.progress = {
          value: 0,
          operation: '',
        }
        this.processingDisabled = false
      } else {
        this.bus.emit('cancel')
      }
    }
  },
  mounted() {
    this.bus.on('validationFailed', () => {
      this.processingDisabled = true
    })
    this.bus.on('validationPassed', () => {
      this.processingDisabled = false
    })
    this.bus.on('progress', progress => {
      this.progress = progress
    })
    this.bus.on('processorSelected', processor => this.selectProcessor(processor))
    this.bus.on('processed', () => {
      this.dialog = false
    })
  },
  beforeUnmount() {
    this.bus.off('validationFailed')
    this.bus.off('validationPassed')
    this.bus.off('progress')
    this.bus.off('processorSelected')
    this.bus.off('processed')
  },
  methods: {
    _ton(key) {
      return this.$t(`operationNames.${key}`)
    },
    ok() {
      this.processing = true
      this.bus.emit('process')
    },
    cancel() {
      this.dialog = false
    },
    selectProcessor(processor) {
      this.selectedProcessor = processor
      this.dialog = true
      this.processing = false
      this.progress = 0
    },
    makeProcessWithQuestion(key) {
      return this._tc('messages.' + key, {processorName: this._tpn('with' + this.selectedProcessor.code)})
    }
  },
}
</script>
