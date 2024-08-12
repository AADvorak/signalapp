<template>
  <v-text-field
      ref="input"
      :model-value="fieldObj.value"
      :append-icon="appendIcon"
      :type="(!isPasswordField || showPassword) ? 'text' : 'password'"
      :label="$t('common.fields.' + field)"
      @click:append="onClickAppend"
      :error="!!fieldObj.validation?.length"
      :error-messages="fieldObj.validation"
      @input="$emit('update', $event.target.value)"
      required/>
</template>

<script>
import {mdiEye, mdiEyeOff} from "@mdi/js";

const PASSWORD_FIELDS = ['password', 'passwordRepeat', 'oldPassword']

export default {
  name: "text-input",
  props: {
    field: {
      type: String,
      required: true
    },
    fieldObj: {
      type: Object,
      required: true
    },
    showPassword: {
      type: Boolean,
      default: false
    }
  },
  emits: ['show', 'update'],
  computed: {
    isPasswordField() {
      return PASSWORD_FIELDS.includes(this.field)
    },
    appendIcon() {
      if (!this.isPasswordField) {
        return null
      }
      return this.showPassword ? mdiEyeOff : mdiEye
    },
  },
  methods: {
    onClickAppend() {
      if (!this.isPasswordField) {
        return
      }
      this.$emit('show')
    },
    focus() {
      this.$refs.input?.focus()
    }
  }
}
</script>
