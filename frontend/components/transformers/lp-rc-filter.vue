<script>
import TransformerBase from "./transformer-base";

export default {
  name: "LpRcFilter",
  extends: TransformerBase,
  data: () => ({
    form: {
      tau: {
        value: 1,
        params: {
          min: 0,
          max: 1,
          step: 0.0005
        }
      }
    },
  })
}
</script>
