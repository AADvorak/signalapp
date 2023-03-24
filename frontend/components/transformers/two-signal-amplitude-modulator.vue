<template>
  <v-text-field
      v-model="form.depth"
      type="number"
      step="0.1"
      min="0"
      max="1"
      label="Modulation depth"
      :error="!!validation.depth.length"
      :error-messages="validation.depth"
      required/>
</template>

<script>
import TransformerDoubleBase from "./transformer-double-base";

export default {
  name: "TwoSignalAmplitudeModulator",
  extends: TransformerDoubleBase,
  data: () => ({
    transformFunctionName: 'twoSignalAmplitudeModulator',
    form: {
      depth: 0.5,
    },
    validation: {
      depth: [],
    },
  }),
  methods: {
    validateFunction() {
      let validated = []
      validated.push(this.validatePositiveNumber('depth'))
      if (this.form.depth > 1) {
        this.validation.depth.push('Should be not more than 1')
        validated.push(false)
      }
      return !validated.includes(false)
    },
  }
}
</script>
