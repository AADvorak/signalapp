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
import {NumberInputTypes} from "~/dictionary/number-input-types";

export default {
  name: 'number-input-type-select',
  extends: ComponentBase,
  data: () => ({
    numberInputType: dataStore().numberInputType
  }),
  computed: {
    numberInputTypes() {
      return Object.values(NumberInputTypes)
          .map(code => ({code, name: this._t('numberInputTypes.' + code)}))
    }
  },
  watch: {
    numberInputType(newValue) {
      dataStore().setNumberInputType(newValue)
    }
  }
}
</script>
