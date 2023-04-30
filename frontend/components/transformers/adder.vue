<template>
  <number-input
      v-for="field in formFields"
      :field="field"
      :label="_trp(field)"
      :field-obj="form[field]"/>
</template>

<script>
import TransformerDoubleBase from "./transformer-double-base";
import NumberInput from "../number-input";

export default {
  name: "Adder",
  components: {NumberInput},
  extends: TransformerDoubleBase,
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
