<template>
  <v-form>
    <v-text-field
        v-model="search"
        v-on:keyup="onUpdate"
        :label="_tc('search')"/>
  </v-form>
</template>

<script>
// todo review this component
import ComponentBase from "./component-base";

export default {
  name: "search-field",
  extends: ComponentBase,
  props: {
    initSearchValue: String
  },
  emits: ['search'],
  data() {
    return {
      search: '',
    }
  },
  watch: {
    initSearchValue(newValue) {
      this.search = newValue
    }
  },
  methods: {
    onUpdate() {
      this.waitToFinishUserInput(this.search)
    },
    waitToFinishUserInput(search) {
      setTimeout(() => {
        if (search === this.search) {
          this.emitSearch()
        }
      }, 600)
    },
    emitSearch() {
      this.$emit('search', this.search)
    }
  }
}
</script>
