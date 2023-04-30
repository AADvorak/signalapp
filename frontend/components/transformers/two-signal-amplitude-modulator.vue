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
  name: "TwoSignalAmplitudeModulator",
  components: {NumberInput},
  extends: TransformerDoubleBase,
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
