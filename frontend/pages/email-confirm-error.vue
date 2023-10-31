<template>
  <card-with-layout>
    <v-card-text>
      <p style="color: #CF6679">{{ _t('name') }}</p>
      <v-btn color="secondary" to="/user-settings">{{ _t('goToUserSettings') }}</v-btn>
    </v-card-text>
  </card-with-layout>
</template>

<script>
import ComponentBase from "../components/component-base";

export default {
  name: "email-confirm-error",
  extends: ComponentBase
}
</script>
