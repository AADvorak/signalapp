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
      v-model="form.maxOutput"
      type="number"
      step="0.1"
      min="0"
      label="Max output amplitude"
      :error="!!validation.maxOutput.length"
      :error-messages="validation.maxOutput"
      required
  ></v-text-field>
</template>

<script>
import TransformerBase from "./transformer-base";

export default {
  name: "PiecewiseLinearSymmetricSaturationAmp",
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'piecewiseLinearSymmetricSaturationAmp',
    form: {
      coefficient: 2,
      maxOutput: 1,
    },
    validation: {
      coefficient: [],
      maxOutput: [],
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
