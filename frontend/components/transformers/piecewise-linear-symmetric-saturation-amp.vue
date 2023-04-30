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
  name: "PiecewiseLinearSymmetricSaturationAmp",
  components: {NumberInput},
  extends: TransformerBase,
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
