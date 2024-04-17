<script>
import ComponentBase from "../base/component-base.vue";
import StringUtils from "../../utils/string-utils";
import {ProcessingEvents} from "~/dictionary/processing-events";

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
        this.bus.emit(ProcessingEvents.CANCEL)
      }
    }
  },
  mounted() {
    this.bus.on(ProcessingEvents.VALIDATION_FAILED, () => {
      this.processingDisabled = true
    })
    this.bus.on(ProcessingEvents.VALIDATION_PASSED, () => {
      this.processingDisabled = false
    })
    this.bus.on(ProcessingEvents.PROGRESS_CHANGED, progress => {
      this.progress = progress
    })
    this.bus.on(ProcessingEvents.PROCESSOR_SELECTED, processor => this.selectProcessor(processor))
    this.bus.on(ProcessingEvents.PROCESSING_FINISHED, () => {
      this.dialog = false
    })
    this.bus.on(ProcessingEvents.PROCESSING_STARTED, () => {
      this.processing = true
    })
  },
  beforeUnmount() {
    this.bus.off(ProcessingEvents.VALIDATION_FAILED)
    this.bus.off(ProcessingEvents.VALIDATION_PASSED)
    this.bus.off(ProcessingEvents.PROGRESS_CHANGED)
    this.bus.off(ProcessingEvents.PROCESSOR_SELECTED)
    this.bus.off(ProcessingEvents.PROCESSING_FINISHED)
    this.bus.off(ProcessingEvents.PROCESSING_STARTED)
  },
  methods: {
    _ton(key) {
      return this.$t(`operationNames.${key}`)
    },
    ok() {
      this.bus.emit(ProcessingEvents.PROCESS)
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
