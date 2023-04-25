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
  name: "LinearAmp",
  components: {NumberInput},
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'linearAmp',
    form: {
      coefficient: 1
    },
    validation: {
      coefficient: []
    },
    INPUT_PARAMS: {
      coefficient: {
        min: 0,
        max: 10,
        step: 0.01
      }
    }
  })
}
</script>
