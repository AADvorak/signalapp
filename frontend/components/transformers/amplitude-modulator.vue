<template>
  <v-text-field
      v-model="form.frequency"
      type="number"
      step="10"
      min="0"
      label="Carrier frequency"
      :error="!!validation.frequency.length"
      :error-messages="validation.frequency"
      required
  ></v-text-field>
  <v-text-field
      v-model="form.amplitude"
      type="number"
      step="0.1"
      min="0"
      label="Carrier amplitude"
      :error="!!validation.amplitude.length"
      :error-messages="validation.amplitude"
      required
  ></v-text-field>
  <v-text-field
      v-model="form.depth"
      type="number"
      step="0.1"
      min="0"
      max="1"
      label="Modulation depth"
      :error="!!validation.depth.length"
      :error-messages="validation.depth"
      required
  ></v-text-field>
</template>

<script>
import TransformerBase from "./transformer-base";

export default {
  name: "AmplitudeModulator",
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'amplitudeModulator',
    form: {
      frequency: 300,
      amplitude: 1,
      depth: 0.5,
    },
    validation: {
      frequency: [],
      amplitude: [],
      depth: [],
    },
  }),
  methods: {
    validateFunction() {
      let validated = []
      for (let key in this.form) {
        validated.push(this.validatePositiveNumber(key))
      }
      if (this.form.depth > 1) {
        this.validation.depth.push('Should be not more than 1')
        validated.push(false)
      }
      return !validated.includes(false)
    },
  }
}
</script>
