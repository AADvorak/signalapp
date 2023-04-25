<template>
  <v-text-field
      v-if="type === 'text'"
      v-model="form[field]"
      type="number"
      :step="step"
      :label="labelComputed"
      :error="!!validation[field].length"
      :error-messages="validation[field]"
      required/>
  <div v-if="type === 'slider'">
    <div class="text-caption">{{ labelComputed }}</div>
    <v-slider
        v-model="form[field]"
        :min="min"
        :max="max"
        :step="step"
        :error="!!validation[field].length"
        :error-messages="validation[field]"
        thumb-label/>
  </div>
</template>

<script>
import {dataStore} from "../stores/data-store";

export default {
  name: "number-input",
  props: {
    parentName: {
      type: String,
      default: ''
    },
    label: {
      type: String,
      default: ''
    },
    field: {
      type: String,
      required: true
    },
    form: {
      type: Object,
      required: true
    },
    validation: {
      type: Object,
      required: true
    },
    min: {
      type: Number,
      required: true
    },
    max: {
      type: Number,
      required: true
    },
    step: {
      type: Number,
      required: true
    },
  },
  computed: {
    labelComputed() {
      return this.label || this.$t(`${this.parentName}.${this.field}`)
    },
    type() {
      return dataStore().numberInputType
    }
  }
}
</script>
