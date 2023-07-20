<template>
  <v-btn variant="text" @click="click">
    <slot/>
    <v-tooltip activator="parent"
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
.v-btn {
  width: 36px;
  min-width: 36px;
}
</style>
