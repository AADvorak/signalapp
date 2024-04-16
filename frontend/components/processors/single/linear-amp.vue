<script>
import ProcessorBase from "../processor-base.vue";

export default {
  name: "LinearAmp",
  extends: ProcessorBase,
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
