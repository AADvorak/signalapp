<template>
  <v-text-field
      v-model="form.frequency"
      type="number"
      step="1"
      min="0"
      label="Frequency"
      :error="!!validation.frequency.length"
      :error-messages="validation.frequency"
      required
  ></v-text-field>
  <v-text-field
      v-model="form.damping"
      type="number"
      step="0.1"
      min="0"
      label="Damping"
      :error="!!validation.damping.length"
      :error-messages="validation.damping"
      required
  ></v-text-field>
</template>

<script>
import TransformerBase from "./transformer-base";

export default {
  name: "LinearOscillator",
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'linearOscillator',
    form: {
      frequency: 100,
      damping: 0.1,
    },
    validation: {
      frequency: [],
      damping: [],
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
