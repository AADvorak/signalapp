<template>
  <v-text-field
      v-model="form.frequency"
      type="number"
      step="10"
      min="0"
      :label="_trp('frequency')"
      :error="!!validation.frequency.length"
      :error-messages="validation.frequency"
      required/>
  <v-text-field
      v-model="form.amplitude"
      type="number"
      step="0.1"
      min="0"
      :label="_trp('amplitude')"
      :error="!!validation.amplitude.length"
      :error-messages="validation.amplitude"
      required/>
  <v-text-field
      v-model="form.coefficient"
      type="number"
      step="1"
      min="0"
      :label="_trp('coefficient')"
      :error="!!validation.coefficient.length"
      :error-messages="validation.coefficient"
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
      coefficient: 2,
    },
    validation: {
      frequency: [],
      amplitude: [],
      coefficient: [],
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
