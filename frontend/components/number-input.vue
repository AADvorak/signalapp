<template>
  <v-text-field
      v-if="type === 'text'"
      v-model="fieldObj.value"
      type="number"
      :step="fieldObj.params.step"
      :label="labelComputed"
      :error="!!fieldObj.validation?.length"
      :error-messages="fieldObj.validation"
      required/>
  <div v-if="type === 'slider'">
    <div class="text-caption">{{ labelComputed }}</div>
    <v-slider
        v-model="fieldObj.value"
        :min="fieldObj.params.min"
        :max="fieldObj.params.max"
        :step="fieldObj.params.step"
        :error="!!fieldObj.validation?.length"
        :error-messages="fieldObj.validation"
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
    fieldObj: {
      type: Object,
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
