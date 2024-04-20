<template>
  <span>{{ version }}</span>
</template>

<script>
import ApiProvider from "~/api/api-provider";

export default {
  name: "app-version",
  data: () => ({
    version: ''
  }),
  mounted() {
    this.loadAppVersion()
  },
  methods: {
    async loadAppVersion() {
      const response = await ApiProvider.get('/api/application/version', true)
      if (response.ok) {
        this.version = response.data
      }
    }
  }
}
</script>
