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
  name: "LinearOscillator",
  components: {NumberInput},
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'linearOscillator',
    form: {
      frequency: 100,
      damping: 0.1,
    },
    validation: {
      frequency: [],
      damping: [],
    },
    INPUT_PARAMS: {
      frequency: {
        min: 0,
        max: 20000,
        step: 1
      },
      damping: {
        min: 0,
        max: 10,
        step: 0.005
      }
    }
  })
}
</script>
