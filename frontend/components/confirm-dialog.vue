<template>
  <v-dialog
      :model-value="opened"
      :persistent="true"
      max-width="600px"
  >
    <v-card width="100%">
      <v-card-title>{{ _t('title') }}</v-card-title>
      <v-card-text>
        <div class="d-flex mb-4">
          {{ text }}
        </div>
        <div class="d-flex">
          <v-btn :color="okColor" class="mr-4" @click="ok">
            {{ _tc('buttons.ok') }}
          </v-btn>
          <v-btn @click="cancel">
            {{ _tc('buttons.cancel') }}
          </v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import ComponentBase from "./component-base";

export default {
  name: "confirm-dialog",
  extends: ComponentBase,
  props: {
    opened: {
      type: Boolean,
      default: false
    },
    text: {
      type: String,
      default: ''
    },
    okColor: {
      type: String,
      default: 'error'
    }
  },
  emits: ['ok', 'cancel'],
  methods: {
    ok() {
      this.$emit('ok')
    },
    cancel() {
      this.$emit('cancel')
    }
  }
}
</script>
