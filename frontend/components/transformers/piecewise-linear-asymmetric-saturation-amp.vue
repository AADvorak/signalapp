<template>
  <v-text-field
      v-model="form.coefficient"
      type="number"
      step="0.1"
      min="0"
      :label="_trp('coefficient')"
      :error="!!validation.coefficient.length"
      :error-messages="validation.coefficient"
      required/>
  <v-text-field
      v-model="form.maxPositiveOutput"
      type="number"
      step="0.1"
      min="0"
      :label="_trp('maxPositiveOutput')"
      :error="!!validation.maxPositiveOutput.length"
      :error-messages="validation.maxPositiveOutput"
      required/>
  <v-text-field
      v-model="form.maxNegativeOutput"
      type="number"
      step="0.1"
      min="0"
      :label="_trp('maxNegativeOutput')"
      :error="!!validation.maxNegativeOutput.length"
      :error-messages="validation.maxNegativeOutput"
      required/>
</template>

<script>
import TransformerBase from "./transformer-base";

export default {
  name: "PiecewiseLinearAsymmetricSaturationAmp",
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

  }),
  methods: {
    validateFunction() {
      let validated = []
      for (let key in this.form) {
        validated.push(this.validatePositiveNumber(key))
      }
      return !validated.includes(false)
    },
  }
}
</script>
