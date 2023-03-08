<template>
  <v-text-field
      v-model="form.tau"
      type="number"
      step="0.1"
      min="0"
      label="Time constant"
      :error="!!validation.tau.length"
      :error-messages="validation.tau"
      required
  ></v-text-field>
</template>

<script>
import TransformerBase from "./transformer-base";

export default {
  name: "HpRcFilter",
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'hpRcFilter',
    form: {
      tau: 1
    },
    validation: {
      tau: []
    }
  }),
  methods: {
    validateFunction() {
      return this.validatePositiveNumber('tau')
    }
  }
}
</script>
