<script>
import ProcessorBase from "../processor-base.vue";

export default {
  name: "HpRcFilter",
  extends: ProcessorBase,
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
