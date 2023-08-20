<template>
  <v-toolbar>
    <v-toolbar-title>{{ title }}</v-toolbar-title>
    <v-spacer/>
    <v-btn
        icon
        @click="$emit('close')"
    >
      <v-icon>{{mdiClose}}</v-icon>
    </v-btn>
  </v-toolbar>
</template>

<script>
import {mdiClose} from "@mdi/js";

export default {
  name: "toolbar-with-close-btn",
  props: {
    title: {
      type: String,
      required: true
    }
  },
  emits: ['close'],
  data: () => ({
    mdiClose,
  })
}
</script>
