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
  <v-text-field
      v-model="form.maxPositiveOutput"
      type="number"
      step="0.1"
      min="0"
      label="Max positive output amplitude"
      :error="!!validation.maxPositiveOutput.length"
      :error-messages="validation.maxPositiveOutput"
      required
  ></v-text-field>
  <v-text-field
      v-model="form.maxNegativeOutput"
      type="number"
      step="0.1"
      min="0"
      label="Max negative output amplitude"
      :error="!!validation.maxNegativeOutput.length"
      :error-messages="validation.maxNegativeOutput"
      required
  ></v-text-field>
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
