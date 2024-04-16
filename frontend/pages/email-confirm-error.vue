<template>
  <card-with-layout>
    <v-card-text>
      <p style="color: #CF6679">{{ _t('name') }}</p>
      <v-btn color="secondary" to="/user-settings">{{ _t('goToUserSettings') }}</v-btn>
    </v-card-text>
  </card-with-layout>
</template>

<script>
import ComponentBase from "../components/base/component-base.vue";
import CardWithLayout from "~/components/common/card-with-layout.vue";

export default {
  name: "email-confirm-error",
  components: {CardWithLayout},
  extends: ComponentBase
}
</script>
