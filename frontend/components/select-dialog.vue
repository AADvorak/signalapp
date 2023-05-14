<template>
  <v-dialog
      v-model="opened"
      persistent
      max-width="600px"
  >
    <v-card width="100%">
      <v-card-title>{{ _t('title') }}</v-card-title>
      <v-card-text>
        <div class="d-flex mb-4">
          {{ text }}
        </div>
        <div class="d-flex">
          <v-btn v-for="item in items" :color="item.color" class="mr-4" @click="() => select(item.name)">
            {{ getButtonText(item) }}
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
  name: "select-dialog",
  extends: ComponentBase,
  props: {
    opened: Boolean,
    text: String,
    items: {
      type: Array,
      required: true
    }
  },
  emits: ['select', 'cancel'],
  methods: {
    select(itemName) {
      this.$emit('select', itemName)
    },
    cancel() {
      this.$emit('cancel')
    },
    getButtonText(item) {
      return item.noLocale ? item.name : this._tc('buttons.' + item.name)
    }
  }
}
</script>
