export const PaginationParamLocations = {
  DATA: 'data',
  FORM: 'form'
}

const defaultPaginationParamsConfig = [
  {
    name: 'page',
    location: PaginationParamLocations.DATA,
    readFunc: parseInt
  },
  {
    name: 'size',
    location: PaginationParamLocations.FORM,
    formField: 'pageSize',
    readFunc: parseInt
  },
  {
    name: 'search',
    location: PaginationParamLocations.FORM,
  }
]

export default {
  data: () => ({
    additionalPaginationParamsConfig: []
  }),
  methods: {
    readUrlParams() {
      const route = useRoute()
      for (const paginationParamConfig of this.getPaginationParamsConfig()) {
        const rawValue = ref(route.query[paginationParamConfig.name]).value
        if (!rawValue) {
          continue
        }
        const readFunc = paginationParamConfig.readFunc || (value => value)
        const value = paginationParamConfig.isArray
            ? this.readNumberArrFromUrlParam(rawValue, readFunc)
            : readFunc(rawValue)
        switch (paginationParamConfig.location) {
          case PaginationParamLocations.DATA:
            this[paginationParamConfig.dataField || paginationParamConfig.name] = value
            break
          case PaginationParamLocations.FORM:
            this.formValue(paginationParamConfig.formField || paginationParamConfig.name, value)
            break
        }
      }
      const sortBy = ref(route.query.sortBy)
      if (sortBy.value && this.sort) {
        this.sort.by = sortBy.value
      }
      const sortDir = ref(route.query.sortDir)
      if (sortDir.value && this.sort) {
        this.sort.dir = sortDir.value
      }
    },
    readNumberArrFromUrlParam(str, parseFunc) {
      return str.split(',').map(v => parseFunc(v)).filter(number => !isNaN(number))
    },
    makeUrlParams() {
      let params = ''
      for (const paginationParamConfig of this.getPaginationParamsConfig()) {
        let value
        switch (paginationParamConfig.location) {
          case PaginationParamLocations.DATA:
            value = this[paginationParamConfig.dataField || paginationParamConfig.name]
            break
          case PaginationParamLocations.FORM:
            value = this.formValue(paginationParamConfig.formField || paginationParamConfig.name)
            break
        }
        if (!value || paginationParamConfig.isArray && !value.length) {
          continue
        }
        const paramValue = paginationParamConfig.isArray ? this.makeUrlParamFromArr(value) : value
        params += `${params ? '&' : '?'}${paginationParamConfig.name}=${paramValue}`
      }
      if (this.sort?.by) {
        params += `&sortBy=${this.sort.by}`
      }
      if (this.sort?.dir) {
        params += `&sortDir=${this.sort.dir}`
      }
      return params
    },
    makeUrlParamFromArr(arr) {
      let str = ''
      arr.forEach(item => {
        str += (str ? ',' : '') + item
      })
      return str
    },
    getPaginationParamsConfig() {
      return [...defaultPaginationParamsConfig, ...this.additionalPaginationParamsConfig]
    },
  }
}
