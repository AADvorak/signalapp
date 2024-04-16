<template>
  <card-with-layout>
    <v-card-text>
      <component :is="'description-' + $i18n.locale"/>
    </v-card-text>
  </card-with-layout>
</template>

<script>
import DescriptionEn from '../locales/description-en'
import DescriptionRu from '../locales/description-ru'
import DescriptionPl from '../locales/description-pl'
import CardWithLayout from "~/components/common/card-with-layout.vue";

export default {
  components: {
    CardWithLayout, DescriptionEn, DescriptionRu, DescriptionPl
  }
}
</script>
<style scoped>
:deep(.desc-div) {
  margin-top: 5px;
}
:deep(.desc-ul) {
  margin-left: 20px;
}
:deep(.desc-h2) {
  margin-top: 10px;
}
:deep(.desc-h1) {
  margin-bottom: 10px;
}
</style>
