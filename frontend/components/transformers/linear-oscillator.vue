<template>
  <number-input
      v-for="field in formFields"
      :field="field"
      :label="_trp(field)"
      :field-obj="form[field]"/>
</template>

<script>
import TransformerBase from "./transformer-base";
import NumberInput from "../number-input";

export default {
  name: "LinearOscillator",
  components: {NumberInput},
  extends: TransformerBase,
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
