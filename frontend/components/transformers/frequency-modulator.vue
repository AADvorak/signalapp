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
  name: "FrequencyModulator",
  components: {NumberInput},
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'frequencyModulator',
    form: {
      frequency: 300,
      amplitude: 1,
      coefficient: 2,
    },
    validation: {
      frequency: [],
      amplitude: [],
      coefficient: [],
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
      coefficient: {
        min: 0,
        max: 10,
        step: 0.01
      },
    }
  })
}
</script>
