<template>
  <v-dialog
      v-model="opened"
      persistent
      max-width="600px"
  >
    <v-card width="100%">
      <v-card-title>Confirmation</v-card-title>
      <v-card-text>
        <div class="d-flex mb-4">
          {{ text }}
        </div>
        <div class="d-flex">
          <v-btn :color="okColor" class="mr-4" @click="ok">
            OK
          </v-btn>
          <v-btn @click="cancel">
            Cancel
          </v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "confirm-dialog",
  props: {
    opened: Boolean,
    text: String,
    okColor: {
      type: String,
      default: 'success'
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
