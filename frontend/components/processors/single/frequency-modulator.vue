<script>
import ProcessorBase from "../processor-base.vue";

export default {
  name: "FrequencyModulator",
  extends: ProcessorBase,
  data: () => ({
    form: {
      frequency: {
        value: 300,
        params: {
          min: 0,
          max: 20000,
          step: 1
        },
      },
      amplitude: {
        value: 1,
        params: {
          min: 0,
          max: 10,
          step: 0.01
        },
      },
      coefficient: {
        value: 2,
        params: {
          min: 0,
          max: 10,
          step: 0.01
        },
      },
    },
  })
}
</script>
