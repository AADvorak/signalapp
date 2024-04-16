<template>
  <v-select
      v-model="$i18n.locale"
      item-title="name"
      item-value="code"
      :items="localeItems"
      :label="$t('language')"/>
</template>

<script>
import ComponentBase from "~/components/component-base.vue";
import {dataStore} from "~/stores/data-store";

export default {
  name: 'locale-select',
  extends: ComponentBase,
  data: () => ({
    localeUtils: {}
  }),
  computed: {
    localeItems() {
      let items = []
      for (const code in this.localeUtils.locales) {
        items.push({code, name: this.localeUtils.locales[code].name})
      }
      return items
    },
  },
  watch: {
    '$i18n.locale'(newValue) {
      dataStore().setLocale(newValue)
      this.localeUtils.makeChartLang()
    },
  },
  mounted() {
    this.localeUtils = useLocaleUtils(this.$i18n)
    this.localeUtils.detectLocale()
    this.localeUtils.makeChartLang()
  }
}
</script>
