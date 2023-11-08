<script>
import ProcessorBase from "./processor-base";

export default {
  name: "PiecewiseLinearAsymmetricSaturationAmp",
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
      maxPositiveOutput: {
        value: 1,
        params: {
          min: 0,
          max: 10,
          step: 0.01
        }
      },
      maxNegativeOutput: {
        value: 0.5,
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
