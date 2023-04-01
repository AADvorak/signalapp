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
      updateCount: 0
    }
  },
  watch: {
    initSearchValue(newValue) {
      this.search = newValue
    }
  },
  methods: {
    onUpdate() {
      this.updateCount++
      this.waitToFinishUserInput(this.updateCount)
    },
    waitToFinishUserInput(updateCount) {
      setTimeout(() => {
        if (updateCount === this.updateCount) {
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
