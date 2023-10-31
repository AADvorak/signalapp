<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card v-if="fullWidth" width="100%">
        <slot/>
      </v-card>
      <v-card v-else width="100%" min-width="400" max-width="800">
        <slot/>
      </v-card>
    </div>
    <slot name="dialogs"/>
    <message v-if="message" :opened="message.opened" :text="message.text" @hide="message.onHide"/>
    <confirm-dialog v-if="confirm" :opened="confirm.opened" :text="confirm.text" @ok="confirm.ok" @cancel="confirm.cancel"/>
    <loading-overlay :show="loadingOverlay"/>
  </NuxtLayout>
</template>

<script>
export default {
  name: "card-with-layout",
  props: {
    fullWidth: {
      type: Boolean,
      default: false
    },
    message: {
      type: Object,
      default: null
    },
    confirm: {
      type: Object,
      default: null
    },
    loadingOverlay: {
      type: Boolean,
      default: false
    },
  }
}
</script>
