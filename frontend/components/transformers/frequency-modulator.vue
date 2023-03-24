<template>
  <v-text-field
      v-model="form.frequency"
      type="number"
      step="10"
      min="0"
      label="Carrier frequency"
      :error="!!validation.frequency.length"
      :error-messages="validation.frequency"
      required/>
  <v-text-field
      v-model="form.amplitude"
      type="number"
      step="0.1"
      min="0"
      label="Carrier amplitude"
      :error="!!validation.amplitude.length"
      :error-messages="validation.amplitude"
      required/>
  <v-text-field
      v-model="form.deviation"
      type="number"
      step="1"
      min="0"
      label="Frequency deviation"
      :error="!!validation.deviation.length"
      :error-messages="validation.deviation"
      required/>
</template>

<script>
import TransformerBase from "./transformer-base";

export default {
  name: "FrequencyModulator",
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'frequencyModulator',
    form: {
      frequency: 300,
      amplitude: 1,
      deviation: 2,
    },
    validation: {
      frequency: [],
      amplitude: [],
      deviation: [],
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
