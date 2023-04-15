<template>
  <v-text-field
      v-model="form.tau"
      type="number"
      step="0.1"
      min="0"
      :label="_trp('tau')"
      :error="!!validation.tau.length"
      :error-messages="validation.tau"
      required/>
</template>

<script>
import TransformerBase from "./transformer-base";

export default {
  name: "LpRcFilter",
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'lpRcFilter',
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
