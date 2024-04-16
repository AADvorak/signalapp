<template>
  <v-text-field
      v-if="type === 'text'"
      ref="input"
      :model-value="fieldObj.value"
      :step="fieldObj.params.step"
      :label="labelComputed"
      :error="!!fieldObj.validation?.length"
      :error-messages="fieldObj.validation"
      @input="$emit('update', $event.target.value)"
      type="number"
      required/>
  <div v-else>
    <div class="text-caption">{{ labelComputed }}</div>
    <v-slider
        :model-value="fieldObj.value"
        :min="fieldObj.params.min"
        :max="fieldObj.params.max"
        :step="fieldObj.params.step"
        :error="!!fieldObj.validation?.length"
        :error-messages="fieldObj.validation"
        @update:model-value="v => $emit('update', v)"
        thumb-label/>
  </div>
</template>

<script>
import {dataStore} from "~/stores/data-store";

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
  emits: ['update'],
  computed: {
    labelComputed() {
      return this.label || this.$t(`${this.parentName}.${this.field}`)
    },
    type() {
      return dataStore().numberInputType
    }
  },
  methods: {
    focus() {
      this.$refs.input?.focus()
    }
  }
}
</script>
