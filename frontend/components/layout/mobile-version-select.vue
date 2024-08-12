<template>
  <v-select
      v-model="mobileVersionState"
      item-title="name"
      item-value="code"
      :items="mobileVersionStates"
      :label="_t('mobileVersion')"/>
</template>

<script>

import {mobileVersionStore} from "~/stores/mobile-version-store";
import ComponentBase from "~/components/base/component-base.vue";
import {MobileVersionStates} from "~/dictionary/mobile-version-states";

export default {
  name: "mobile-version-select",
  extends: ComponentBase,
  data: () => ({
    mobileVersionState: mobileVersionStore().mobileVersionState
  }),
  computed: {
    mobileVersionStates() {
      return Object.values(MobileVersionStates)
          .map(code => ({code, name: this._t('mobileVersionStates.' + code)}))
    }
  },
  watch: {
    mobileVersionState(newValue) {
      mobileVersionStore().setMobileVersionState(newValue)
    }
  }
}
</script>
