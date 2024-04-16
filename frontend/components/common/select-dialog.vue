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
        <div class="d-flex flex-wrap">
          <v-btn v-for="item in items" :color="item.color" class="mr-4" @click="() => select(item.name)">
            {{ getButtonText(item) }}
          </v-btn>
          <v-btn v-if="!noCancel" @click="cancel">
            {{ _tc('buttons.cancel') }}
          </v-btn>
        </div>
        <div v-if="askRemember">
          <v-checkbox v-model="rememberSelection" :label="_t('rememberSelection')"/>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import ComponentBase from "../base/component-base.vue";

export default {
  name: "select-dialog",
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
    items: {
      type: Array,
      required: true
    },
    noCancel: {
      type: Boolean,
      default: false
    },
    askRemember: {
      type: Boolean,
      default: false
    }
  },
  emits: ['select', 'cancel'],
  data: () => ({
    rememberSelection: false
  }),
  methods: {
    select(selectedItemName) {
      this.$emit('select', {
        selectedItemName,
        rememberSelection: this.rememberSelection
      })
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
