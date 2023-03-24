<template>
  <v-form @submit.prevent="emitSearch">
    <div class="d-flex">
      <v-text-field
          v-model="search"
          label="Search"/>
    </div>
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
      search: ''
    }
  },
  watch: {
    initSearchValue(newValue) {
      this.search = newValue
    }
  },
  methods: {
    emitSearch() {
      this.$emit('search', this.search)
    }
  }
}
</script>
