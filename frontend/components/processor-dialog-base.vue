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
    progress: 0,
    operation: '',
  }),
  computed: {
    progressBarText() {
      const progress = Math.ceil(this.progress) + '%'
      return this.operation ? this.operation + ': ' + progress : progress
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
        this.progress = 0
        this.operation = ''
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
    this.bus.on('progress', obj => {
      this.progress = obj.progress
      this.operation = obj.operation
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
