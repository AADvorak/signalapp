<script>
import DoubleProcessorBase from "./double-processor-base.vue";

export default {
  name: "TwoSignalAmplitudeModulator",
  extends: DoubleProcessorBase,
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
