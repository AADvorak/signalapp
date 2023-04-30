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
  name: "HpRcFilter",
  components: {NumberInput},
  extends: TransformerBase,
  data: () => ({
    form: {
      tau: {
        value: 1,
        params: {
          min: 0,
          max: 1,
          step: 0.0005
        }
      }
    },
  })
}
</script>
