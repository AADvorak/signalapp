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
  name: "LpRcFilter",
  components: {NumberInput},
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'lpRcFilter',
    form: {
      tau: 1
    },
    validation: {
      tau: []
    },
    INPUT_PARAMS: {
      tau: {
        min: 0,
        max: 1,
        step: 0.0005
      }
    }
  })
}
</script>
