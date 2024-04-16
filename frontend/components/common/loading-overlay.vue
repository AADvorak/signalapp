<template>
  <v-overlay
      :model-value="show"
      :persistent="true"
      class="align-center justify-center"
  >
    <v-progress-circular
        indeterminate
        size="64"/>
  </v-overlay>
</template>

<script>
export default {
  name: "loading-overlay",
  props: {
    show: Boolean
  }
}
</script>
