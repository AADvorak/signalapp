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
        signal1: JSON.stringify(this.signal1),
        signal2: JSON.stringify(this.signal2),
        params: JSON.stringify(this.form || {})
      }
    }
  }
}
</script>
