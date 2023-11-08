<script>
import ProcessorBase from "./processor-base";

export default {
  name: "PiecewiseLinearSymmetricSaturationAmp",
  extends: ProcessorBase,
  data: () => ({
    form: {
      coefficient: {
        value: 2,
        params: {
          min: 0,
          max: 10,
          step: 0.01
        }
      },
      maxOutput: {
        value: 1,
        params: {
          min: 0,
          max: 10,
          step: 0.01
        }
      },
    },
  })
}
</script>
