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
  name: "Adder",
  components: {NumberInput},
  extends: TransformerDoubleBase,
  data: () => ({
    transformFunctionName: 'adder',
    form: {
      coefficient1: 1,
      coefficient2: 1,
    },
    validation: {
      coefficient1: [],
      coefficient2: [],
    },
    INPUT_PARAMS: {
      coefficient1: {
        min: -5,
        max: 5,
        step: 0.01
      },
      coefficient2: {
        min: -5,
        max: 5,
        step: 0.01
      },
    },
  })
}
</script>
