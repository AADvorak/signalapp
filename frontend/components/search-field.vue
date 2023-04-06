<template>
  <v-form>
    <v-text-field
        v-model="search"
        v-on:keyup="onUpdate"
        label="Search"/>
  </v-form>
</template>

<script>
// todo review this component
export default {
  name: "search-field",
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
