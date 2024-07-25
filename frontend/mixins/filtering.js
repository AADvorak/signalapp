const defaultFilteringParamsConfig = [
  {
    name: 'search',
    emptyValue: ''
  }
]

export default {
  data: () => ({
    additionalFilteringParamsConfig: []
  }),
  computed: {
    filters() {
      const filters = {}
      const search = this.formValues.search
      if (search) {
        filters.search = search
      }
      for (const filteringParamConfig of this.additionalFilteringParamsConfig) {
        const value = this.getFilteringParamValue(filteringParamConfig)
        if (value) {
          filters[filteringParamConfig.name] = value
        }
      }
      return filters
    },
    filteringParamsConfig() {
      return [...defaultFilteringParamsConfig, ...this.additionalFilteringParamsConfig]
    },
    filterIsEmpty() {
      for (const filteringParamConfig of this.filteringParamsConfig) {
        const emptyValue = filteringParamConfig.emptyValue
        if (emptyValue === undefined) {
          continue
        }
        const formValue = this.formValue(filteringParamConfig.formField || filteringParamConfig.name)
        if (JSON.stringify(emptyValue) !== JSON.stringify(formValue)) {
          return false
        }
      }
      return true
    }
  },
  watch: {
    formValues() {
      this.actionWithTimeout(() => {
        this.saveFormValues()
      })
    }
  },
  methods: {
    getFilteringParamValue(filteringParamConfig) {
      const value = this.formValue(filteringParamConfig.formField || filteringParamConfig.name)
      if (!value || filteringParamConfig.isArray && !value.length) {
        return null
      }
      return value
    },
    clearFilter() {
      for (const filteringParamConfig of this.filteringParamsConfig) {
        const emptyValue = filteringParamConfig.emptyValue
        if (emptyValue !== undefined) {
          this.formValue(filteringParamConfig.formField || filteringParamConfig.name, emptyValue)
        }
      }
    },
    onDataViewerUpdateFilters(filters) {
      Object.keys(filters).forEach(key => this.formValue(key, filters[key]))
    },
  }
}
