<template>
  <v-text-field
      v-model="form.coefficient"
      type="number"
      step="0.1"
      min="0"
      label="Coefficient"
      :error="!!validation.coefficient.length"
      :error-messages="validation.coefficient"
      required
  ></v-text-field>
</template>

<script>
import TransformerBase from "./transformer-base";

export default {
  name: "LinearAmp",
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'linearAmp',
    form: {
      coefficient: 1
    },
    validation: {
      coefficient: []
    }
  }),
  methods: {
    validateFunction() {
      return this.validatePositiveNumber('coefficient')
    }
  }
}
</script>
