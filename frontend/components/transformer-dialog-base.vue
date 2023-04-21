<script>
import ComponentBase from "./component-base";

export default {
  name: "transformer-dialog-base",
  extends: ComponentBase,
  props: {
    bus: Object
  },
  data: () => ({
    dialog: false,
    selectedTransformer: null,
    processing: false,
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
    }
  },
  watch: {
    dialog(newValue) {
      if (newValue) {
        this.progress = 0
        this.operation = ''
      } else {
        this.bus.emit('cancel')
      }
    }
  },
  mounted() {
    this.bus.on('validationFailed', () => {
      this.processing = false
    })
    this.bus.on('progress', obj => {
      this.progress = obj.progress
      this.operation = obj.operation
    })
    this.bus.on('transformerSelected', transformer => this.selectTransformer(transformer))
    this.bus.on('transformed', () => {
      this.dialog = false
    })
  },
  beforeUnmount() {
    this.bus.off('validationFailed')
    this.bus.off('progress')
    this.bus.off('transformerSelected')
    this.bus.off('transformed')
  },
  methods: {
    ok() {
      this.processing = true
      this.bus.emit('transform')
    },
    cancel() {
      this.dialog = false
    },
    selectTransformer(transformer) {
      this.selectedTransformer = transformer
      this.dialog = true
      this.processing = false
      this.progress = 0
    },
    makeTransformWithQuestion(key) {
      return this._tc('messages.' + key, {transformerName: this._tr('with' + this.selectedTransformer.code)})
    }
  },
}
</script>
