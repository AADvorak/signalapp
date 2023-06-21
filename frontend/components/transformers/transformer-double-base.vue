<script>
import TransformerBase from "./transformer-base";

export default {
  name: "TransformerDoubleBase",
  extends: TransformerBase,
  props: {
    signal1: Object,
    signal2: Object
  },
  methods: {
    makeWorkerMessage() {
      return {
        transformFunctionName: this.transformFunctionName,
        signal1: this.toRawDeep(this.signal1),
        signal2: this.toRawDeep(this.signal2),
        params: this.formValues
      }
    },
    changeSignalNameAndDescription(signal) {
      signal.name = this.makeTransformedWithText()
      signal.description = this.makeResultSignalDescription()
    },
    makeResultSignalDescription() {
      return this._trp('description', {name1: this.signal1.name, name2: this.signal2.name, ...this.formValues})
    }
  }
}
</script>
