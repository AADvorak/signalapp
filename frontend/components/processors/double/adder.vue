<script>
import DoubleProcessorBase from "./double-processor-base.vue";

export default {
  name: "Adder",
  extends: DoubleProcessorBase,
  data: () => ({
    form: {
      coefficient1: {
        value: 1,
        params: {
          min: -5,
          max: 5,
          step: 0.01
        }
      },
      coefficient2: {
        value: 1,
        params: {
          min: -5,
          max: 5,
          step: 0.01
        }
      },
    },
  })
}
</script>
