<template>
  <v-form>
    <v-text-field
        v-model="search.value"
        v-on:keyup="onUpdate"
        :label="_tc('search')"/>
  </v-form>
</template>

<script>
import ComponentBase from "./component-base";
import actionWithTimeout from "../mixins/action-with-timeout";

export default {
  name: "search-field",
  extends: ComponentBase,
  mixins: [actionWithTimeout],
  props: {
    initSearchValue: String
  },
  emits: ['search'],
  data() {
    return {
      search: {value: ''},
    }
  },
  watch: {
    initSearchValue(newValue) {
      this.search.value = newValue
    }
  },
  methods: {
    onUpdate() {
      this.actionWithTimeout('search', () => this.$emit('search', this.search.value))
    }
  }
}
</script>
