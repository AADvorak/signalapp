<script>
import ProcessorBase from "../processor-base.vue";

export default {
  name: "double-processor-base",
  extends: ProcessorBase,
  props: {
    signal1: Object,
    signal2: Object
  },
  methods: {
    makeWorkerMessage() {
      return {
        processorName: this.processorName,
        signal1: this.toRawDeep(this.signal1),
        signal2: this.toRawDeep(this.signal2),
        params: this.formValues
      }
    },
    changeSignalNameAndDescription(signal) {
      signal.name = this.makeProcessedWithText()
      signal.description = this.makeResultSignalDescription()
    },
    makeResultSignalDescription() {
      return this._tpp('description', {name1: this.signal1.name, name2: this.signal2.name, ...this.formValues})
    }
  }
}
</script>
