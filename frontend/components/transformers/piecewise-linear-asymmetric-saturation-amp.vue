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
  name: "PiecewiseLinearAsymmetricSaturationAmp",
  components: {NumberInput},
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'piecewiseLinearAsymmetricSaturationAmp',
    form: {
      coefficient: 2,
      maxPositiveOutput: 1,
      maxNegativeOutput: 0.5,
    },
    validation: {
      coefficient: [],
      maxPositiveOutput: [],
      maxNegativeOutput: [],
    },
    INPUT_PARAMS: {
      coefficient: {
        min: 0,
        max: 10,
        step: 0.01
      },
      maxPositiveOutput: {
        min: 0,
        max: 10,
        step: 0.01
      },
      maxNegativeOutput: {
        min: 0,
        max: 10,
        step: 0.01
      },
    }
  })
}
</script>
