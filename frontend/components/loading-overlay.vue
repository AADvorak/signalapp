<template>
  <v-overlay
      :model-value="show"
      class="align-center justify-center"
  >
    <v-progress-circular
        indeterminate
        size="64"
    ></v-progress-circular>
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
