<template>
  <v-text-field
      v-model="form.coefficient1"
      type="number"
      step="0.1"
      :label="_trp('coefficient1')"
      :error="!!validation.coefficient1.length"
      :error-messages="validation.coefficient1"
      required/>
  <v-text-field
      v-model="form.coefficient2"
      type="number"
      step="0.1"
      :label="_trp('coefficient2')"
      :error="!!validation.coefficient2.length"
      :error-messages="validation.coefficient2"
      required/>
</template>

<script>
import TransformerDoubleBase from "./transformer-double-base";

export default {
  name: "Adder",
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
  })
}
</script>
