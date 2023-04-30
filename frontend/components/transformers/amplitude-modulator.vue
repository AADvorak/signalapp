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
  name: "AmplitudeModulator",
  components: {NumberInput},
  extends: TransformerBase,
  data: () => ({
    form: {
      frequency: {
        value: 300,
        params: {
          min: 0,
          max: 20000,
          step: 1
        }
      },
      amplitude: {
        value: 1,
        params: {
          min: 0,
          max: 10,
          step: 0.01
        }
      },
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
