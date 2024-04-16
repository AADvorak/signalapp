<template>
  <v-select
      v-for="selectWithSaving in selectsWithSavingArray"
      v-model="model[selectWithSaving.key]"
      item-title="name"
      item-value="code"
      :items="selectWithSaving.values"
      :label="_t(selectWithSaving.key)"/>
</template>

<script>
import ComponentBase from "~/components/base/component-base.vue";
import {SelectsWithSaving, SelectUtils} from "~/utils/select-utils";

const ASK_SELECT_VALUE = 'askSelect'

export default {
  name: 'selects-settings',
  extends: ComponentBase,
  data: () => ({
    selectsWithSaving: SelectsWithSaving,
    model: {}
  }),
  computed: {
    selectsWithSavingArray() {
      return Object.keys(this.selectsWithSaving).map(key => ({
        key,
        values: [...this.selectsWithSaving[key].values, ASK_SELECT_VALUE].map(code => ({
          code,
          name: this._tc('buttons.' + code)
        }))
      }))
    }
  },
  watch: {
    model: {
      handler(newValue) {
        Object.keys(newValue).forEach(key => {
          const storedSelected = SelectUtils.getSelected(key)
          const newSelected = newValue[key]
          if (newSelected !== storedSelected) {
            SelectUtils.setSelected(key, newSelected)
          }
        })
      },
      deep: true
    }
  },
  mounted() {
    this.selectsWithSavingArray.forEach(item => {
      this.model[item.key] = SelectUtils.getSelected(item.key) || ASK_SELECT_VALUE
    })
  }
}
</script>
