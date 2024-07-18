import {userStore} from "~/stores/user-store";

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
    emptyValue: ''
  }
]

export default {
  data: () => ({
    elements: 0,
    pages: 0,
    page: 1,
    sort: {
      by: '',
      dir: ''
    },
    additionalPaginationParamsConfig: [],
    dataPageLastRequest: ''
  }),
  computed: {
    pageRequest() {
      const request = {}
      request.page = this.page - 1
      request.size = this.formValues.pageSize
      if (this.sort.by && this.sort.dir) {
        request.sort = this.sort
      }
      const filters = {}
      const search = this.formValues.search
      if (search) {
        filters.search = search
      }
      for (const paginationParamConfig of this.additionalPaginationParamsConfig) {
        const value = this.getPaginationParamValue(paginationParamConfig)
        if (value) {
          filters[paginationParamConfig.name] = value
        }
      }
      request.filters = filters
      return request
    },
    paginationParamsConfig() {
      return [...defaultPaginationParamsConfig, ...this.additionalPaginationParamsConfig]
    },
    filterFieldNames() {
      return [...this.paginationParamsConfig.map(item => item.name), 'sortBy', 'sortDir']
    },
    filterIsEmpty() {
      for (const paginationParamConfig of this.paginationParamsConfig) {
        const emptyValue = paginationParamConfig.emptyValue
        if (emptyValue === undefined) {
          continue
        }
        const formValue = this.formValue(paginationParamConfig.formField || paginationParamConfig.name)
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
        if (!this.validatePageSize()) {
          return
        }
        this.page = 1
        this.saveFormValues()
      })
    },
    pageRequest(newValue, oldValue) {
      const action = () => {
        this.setUrlParams()
        this.loadDataPage()
      }
      if (this.onlyPageChanged(newValue, oldValue)) {
        action()
      } else {
        this.actionWithTimeout(action, 'pageRequest')
      }
    }
  },
  methods: {
    async loadDataPageBase(dataName, dataUrl, checkRole) {
      const request = this.pageRequest
      const requestJson = JSON.stringify(request)
      if (!this.mounted || this.loadingOverlay
          || this.dataPageLastRequest === requestJson
          || !this.validatePageSize()
          || checkRole && !userStore().checkUserRole(checkRole)) {
        return
      }
      await this.loadWithOverlay(async () => {
        const response = await this.getApiProvider().postJson(dataUrl, request)
        if (response.ok) {
          this[dataName] = response.data.data
          this.elements = response.data.elements
          this.pages = response.data.pages
          this[dataName + 'Empty'] = this.elements === 0
          this.dataPageLastRequest = requestJson
        } else {
          this.showErrorsFromResponse(response)
        }
      })
    },
    validatePageSize() {
      this.clearValidation()
      const validationMsg = this.getNumberValidationMsg('pageSize')
      if (validationMsg) {
        this.pushValidationMsg('pageSize', validationMsg)
      }
      return !validationMsg
    },
    onlyPageChanged(newValue, oldValue) {
      let pageChanged = false, anotherValueChanged = false
      for (const field of this.filterFieldNames) {
        if (newValue[field]?.toString() !== oldValue[field]?.toString()) {
          if (field === 'page') {
            pageChanged = true
          } else {
            anotherValueChanged = true
          }
        }
      }
      return pageChanged && !anotherValueChanged
    },
    readUrlParams() {
      const route = useRoute()
      for (const paginationParamConfig of this.paginationParamsConfig) {
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
      for (const paginationParamConfig of this.paginationParamsConfig) {
        const value = this.getPaginationParamValue(paginationParamConfig)
        if (value) {
          const paramValue = paginationParamConfig.isArray ? this.makeUrlParamFromArr(value) : value
          params += `${params ? '&' : '?'}${paginationParamConfig.name}=${paramValue}`
        }
      }
      if (this.sort?.by) {
        params += `&sortBy=${this.sort.by}`
      }
      if (this.sort?.dir) {
        params += `&sortDir=${this.sort.dir}`
      }
      return params
    },
    getPaginationParamValue(paginationParamConfig) {
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
        return null
      }
      return value
    },
    makeUrlParamFromArr(arr) {
      let str = ''
      arr.forEach(item => {
        str += (str ? ',' : '') + item
      })
      return str
    },
    clearFilter() {
      for (const paginationParamConfig of this.paginationParamsConfig) {
        const emptyValue = paginationParamConfig.emptyValue
        if (emptyValue !== undefined) {
          this.formValue(paginationParamConfig.formField || paginationParamConfig.name, emptyValue)
        }
      }
    },
    onDataViewerSort(sort) {
      this.sort = sort
      this.page = 1
    }
  }
}
