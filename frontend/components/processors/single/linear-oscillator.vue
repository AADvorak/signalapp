<script>
import ProcessorBase from "../processor-base.vue";

export default {
  name: "LinearOscillator",
  extends: ProcessorBase,
  data: () => ({
    form: {
      frequency: {
        value: 100,
        params: {
          min: 0,
          max: 20000,
          step: 20
        }
      },
      damping: {
        value: 0.1,
        params: {
          min: 0,
          max: 10,
          step: 0.005
        }
      },
    },
  })
}
</script>
