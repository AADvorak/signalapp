<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <component v-bind:is="'description-' + $i18n.locale"/>
        </v-card-text>
      </v-card>
    </div>
  </NuxtLayout>
</template>

<script>
import DescriptionEn from '../components/description-en'
import DescriptionRu from '../components/description-ru'

export default {
  components: {
    DescriptionEn, DescriptionRu
  }
}
</script>
<style scoped>
/deep/ .desc-div {
  margin-top: 5px;
}
/deep/ .desc-ul {
  margin-left: 20px;
}
/deep/ .desc-h2 {
  margin-top: 10px;
}
/deep/ .desc-h1 {
  margin-bottom: 10px;
}
</style>
