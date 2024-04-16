<template>
  <v-select
      v-model="numberInputType"
      item-title="name"
      item-value="code"
      :items="numberInputTypes"
      :label="_t('numberInputType')"/>
</template>

<script>
import ComponentBase from "~/components/base/component-base.vue";
import {dataStore} from "~/stores/data-store";

export default {
  name: 'number-input-type-select',
  extends: ComponentBase,
  data: () => ({
    numberInputType: dataStore().numberInputType
  }),
  computed: {
    numberInputTypes() {
      let items = []
      for (const code of dataStore().numberInputTypes) {
        items.push({code, name: this._t('numberInputTypes.' + code)})
      }
      return items
    }
  },
  watch: {
    numberInputType(newValue) {
      dataStore().setNumberInputType(newValue)
    }
  }
}
</script>
