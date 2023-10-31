<template>
  <v-dialog
      :model-value="opened"
      :persistent="true"
      max-width="600px"
  >
    <v-card width="100%">
      <v-card-title>{{ _t('title') }}</v-card-title>
      <v-card-text>
        <div class="d-flex">
          {{ text }}
        </div>
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hide">
            {{ _tc('buttons.ok') }}
          </v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import ComponentBase from "./component-base";

export default {
  name: "message",
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
  },
  emits: ['hide'],
  methods: {
    hide() {
      this.$emit('hide')
    }
  }
}
</script>
