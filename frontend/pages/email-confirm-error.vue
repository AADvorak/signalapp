<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <p style="color: #CF6679">{{ _t('name') }}</p>
          <v-btn color="secondary" to="/user-settings">{{ _t('goToUserSettings') }}</v-btn>
        </v-card-text>
      </v-card>
    </div>
  </NuxtLayout>
</template>

<script>
import ComponentBase from "../components/component-base";

export default {
  name: "email-confirm-error",
  extends: ComponentBase
}
</script>
