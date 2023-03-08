<template>
  <v-dialog
      v-model="opened"
      persistent
      max-width="600px"
  >
    <v-card width="100%">
      <v-card-title>Message</v-card-title>
      <v-card-text>
        <div class="d-flex">
          {{ text }}
        </div>
        <div class="d-flex mt-4">
          <v-btn @click="hide">
            OK
          </v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "message",
  props: {
    opened: Boolean,
    text: String
  },
  emits: ['hide'],
  methods: {
    hide() {
      this.$emit('hide')
    }
  }
}
</script>
