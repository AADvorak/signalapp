<template>
  <v-btn
      :class="small ? 'v-btn-small' : 'v-btn-large'"
      :disabled="disabled"
      variant="text"
      @click.stop="click">
    <slot/>
    <v-tooltip
        activator="parent"
        location="top"
        open-delay="1000">
      {{ $t('common.buttons.' + tooltip) }}
    </v-tooltip>
  </v-btn>
</template>

<script>
export default {
  name: "btn-with-tooltip",
  props: {
    tooltip: {
      type: String,
      required: true
    },
    small: {
      type: Boolean,
      default: true
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  emits: ['click'],
  methods: {
    click() {
      this.$emit('click')
    }
  }
}
</script>

<style scoped>
.v-btn-small {
  width: 36px;
  min-width: 36px;
}
.v-btn-large {
  height: 55px;
}
</style>
