<template>
  <number-input
      v-for="numberInput in numberInputs"
      :field="numberInput"
      :label="_trp(numberInput)"
      :form="form"
      :validation="validation"
      :min="INPUT_PARAMS[numberInput].min"
      :max="INPUT_PARAMS[numberInput].max"
      :step="INPUT_PARAMS[numberInput].step"/>
</template>

<script>
import TransformerBase from "./transformer-base";
import NumberInput from "../number-input";

export default {
  name: "AmplitudeModulator",
  components: {NumberInput},
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'amplitudeModulator',
    form: {
      frequency: 300,
      amplitude: 1,
      depth: 0.5,
    },
    validation: {
      frequency: [],
      amplitude: [],
      depth: [],
    },
    INPUT_PARAMS: {
      frequency: {
        min: 0,
        max: 20000,
        step: 1
      },
      amplitude: {
        min: 0,
        max: 10,
        step: 0.01
      },
      depth: {
        min: 0,
        max: 1,
        step: 0.001
      },
    }
  })
}
</script>
