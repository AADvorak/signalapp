export default {
  data: () => ({
    uiParams: {}
  }),
  computed: {
    uiParamsKey() {
      return this.$options.name + 'UiParams'
    }
  },
  watch: {
    uiParams: {
      handler() {
        this.saveUiParams()
      },
      deep: true
    }
  },
  methods: {
    saveUiParams() {
      localStorage.setItem(this.uiParamsKey, JSON.stringify(this.uiParams))
    },
    restoreUiParams() {
      const uiParamsJson = localStorage.getItem(this.uiParamsKey)
      if (uiParamsJson) {
        const uiParams = JSON.parse(uiParamsJson)
        for (let field in uiParams) {
          if (uiParams.hasOwnProperty(field) && this.uiParams.hasOwnProperty(field)) {
            this.uiParams[field] = uiParams[field]
          }
        }
      }
    }
  }
}
