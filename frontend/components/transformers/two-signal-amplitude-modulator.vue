<script>
import TransformerDoubleBase from "./transformer-double-base";

export default {
  name: "TwoSignalAmplitudeModulator",
  extends: TransformerDoubleBase,
  data: () => ({
    form: {
      depth: {
        value: 0.5,
        params: {
          min: 0,
          max: 1,
          step: 0.001
        },
      },
    },
  })
}
</script>
