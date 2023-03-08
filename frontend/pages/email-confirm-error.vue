<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400">
        <v-card-text>
          <p style="color: #CF6679">Email confirm error</p>
          <v-btn color="secondary" to="/user-settings">Go to user settings</v-btn>
        </v-card-text>
      </v-card>
    </div>
  </NuxtLayout>
</template>

<script>
export default {
  name: "email-confirm-error"
}
</script>
