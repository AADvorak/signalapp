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
import TransformerDoubleBase from "./transformer-double-base";
import NumberInput from "../number-input";

export default {
  name: "TwoSignalAmplitudeModulator",
  components: {NumberInput},
  extends: TransformerDoubleBase,
  data: () => ({
    transformFunctionName: 'twoSignalAmplitudeModulator',
    form: {
      depth: 0.5,
    },
    validation: {
      depth: [],
    },
    INPUT_PARAMS: {
      depth: {
        min: 0,
        max: 1,
        step: 0.001
      },
    }
  })
}
</script>
