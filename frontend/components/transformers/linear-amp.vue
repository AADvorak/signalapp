<script>
import TransformerBase from "./transformer-base";

export default {
  name: "LinearAmp",
  extends: TransformerBase,
  data: () => ({
    form: {
      coefficient: {
        value: 1,
        params: {
          min: 0,
          max: 10,
          step: 0.01
        }
      }
    },
  })
}
</script>
