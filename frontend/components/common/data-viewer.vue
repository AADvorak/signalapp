<template>
  <fixed-width-wrapper v-if="dataEmpty && !loadingOverlay">
    <slot name="dataEmpty"/>
  </fixed-width-wrapper>
  <div v-else>
    <v-layout fluid>
      <div v-if="isMobile" class="element-full-width">
        <fixed-width-wrapper v-if="pagination">
          <v-form>
            <div class="d-flex justify-center flex-wrap">
              <v-select
                  :model-value="paginationParams.sort.by"
                  item-title="header"
                  item-value="key"
                  density="compact"
                  :label="_tc('fields.sortBy')"
                  :items="sortColsWithHeaders"
                  clearable
                  @update:model-value="setSorting"/>
              <v-btn v-if="sortDirIcons[paginationParams.sort.by]"
                     variant="plain"
                     @click="switchSortDir">
                <v-icon style="width: 22px; height: 22px;">
                  {{ sortDirIcons[paginationParams.sort.by] }}
                </v-icon>
              </v-btn>
            </div>
          </v-form>
        </fixed-width-wrapper>
        <div v-for="item in items">
          <v-card>
            <v-card-item>
              <v-card-title>
                <div style="height: 48px" class="d-flex justify-start">
                  <span v-if="multiSelect"><v-checkbox v-model="selectedIds" :value="item.id" @click.stop/></span>
                  <div style="margin: 13px 0;">{{ restrictCaptionLength(item) }}</div>
                </div>
              </v-card-title>
            </v-card-item>
            <v-card-text v-if="cardDescriptionExists(item)">
              <template v-for="column in columns">
                <div v-if="columnValue(column, item)">
                  {{ columnHeader(column) }}: {{ columnValue(column, item) }}
                </div>
              </template>
            </v-card-text>
            <v-card-actions v-if="buttons.length" class="justify-center">
              <template v-for="button in buttons">
                <v-btn v-if="checkCondition(button, item)" @click="buttonClick(button.name, item)">
                  <v-icon :color="button.color">
                    {{ makeIcon(button, item) }}
                  </v-icon>
                  <signal-folders-menu
                      v-if="button.component === 'signal-folders-menu'"
                      :signal-id="item.id"
                      :bus="bus"/>
                  <user-roles-menu
                      v-if="button.component === 'user-roles-menu'"
                      :user="item"
                      :bus="bus"/>
                </v-btn>
              </template>
            </v-card-actions>
          </v-card>
          <v-divider/>
        </div>
      </div>
      <v-table :height="elementHeight" class="element-full-width" v-else fixed-header>
        <thead>
        <tr>
          <th v-if="multiSelect" class="text-left no-padding">
            <div style="height: 58px">
              <v-checkbox v-model="selectAllItems"/>
            </div>
          </th>
          <th class="text-left" @click="setSorting(caption)">
            <span v-if="sortDirIcons[caption]">
              <v-icon>
                {{ sortDirIcons[caption] }}
              </v-icon>
            </span>
            {{ columnHeader(caption) }}
          </th>
          <th v-for="column in columns" @click="setSorting(column)">
            <span v-if="sortDirIcons[columnName(column)]">
              <v-icon>
               {{ sortDirIcons[columnName(column)] }}
              </v-icon>
            </span>
            {{ columnHeader(column) }}
          </th>
          <th v-for="_ in buttons" class="text-left"></th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="item in items">
          <td v-if="multiSelect" class="no-padding">
            <div style="height: 58px">
              <v-checkbox v-model="selectedIds" :value="item.id" @click.stop/>
            </div>
          </td>
          <td>{{ item[caption] }}</td>
          <td v-for="column in columns">{{ columnValue(column, item) }}</td>
          <td v-for="button in buttons">
            <btn-with-tooltip
                v-if="checkCondition(button, item)"
                :tooltip="makeTooltip(button, item)"
                @click="buttonClick(button.name, item)"
            >
              <v-icon :color="button.color">
                {{ makeIcon(button, item) }}
              </v-icon>
              <signal-folders-menu
                  v-if="button.component === 'signal-folders-menu'"
                  :signal-id="item.id"
                  :bus="bus"/>
              <user-roles-menu
                  v-if="button.component === 'user-roles-menu'"
                  :user="item"
                  :bus="bus"/>
            </btn-with-tooltip>
          </td>
        </tr>
        </tbody>
      </v-table>
    </v-layout>
    <fixed-width-wrapper v-if="pagination">
      <v-form>
        <div class="d-flex justify-center flex-wrap">
          <v-text-field
              v-model="paginationParams.size"
              :label="_tc('pagination.pageSize')"
              :step="1"
              :error="!!pageSizeValidation.length"
              :error-messages="pageSizeValidation"
              type="number"
              style="max-width: 300px"
              density="compact"
              required/>
          <v-pagination
              v-model="paginationParams.page"
              :length="pages"
              density="compact"/>
        </div>
      </v-form>
      <p style="text-align: center;">
        {{ _tc('pagination.total', {pages, elements}) }}
      </p>
    </fixed-width-wrapper>
  </div>
</template>

<script>
import {mdiSortAscending, mdiSortDescending} from "@mdi/js";
import DeviceUtils from "~/utils/device-utils";
import StringUtils from "~/utils/string-utils";
import BtnWithTooltip from "~/components/common/btn-with-tooltip.vue";
import SignalFoldersMenu from "~/components/folders/signal-folders-menu.vue";
import UserRolesMenu from "~/components/admin/user-roles-menu.vue";
import FixedWidthWrapper from "~/components/common/fixed-width-wrapper.vue";
import NumberInput from "~/components/common/number-input.vue";
import {appSettingsStore} from "~/stores/app-settings-store";
import {userStore} from "~/stores/user-store";
import actionWithTimeout from "~/mixins/action-with-timeout";
import PageBase from "~/components/base/page-base.vue";
import formNumberValues from "~/mixins/form-number-values";

const SORT_DIRS = {
  DESC: 'desc',
  ASC: 'asc'
}

export default {
  name: "data-viewer",
  components: {NumberInput, FixedWidthWrapper, UserRolesMenu, SignalFoldersMenu, BtnWithTooltip},
  extends: PageBase,
  mixins: [actionWithTimeout, formNumberValues],
  props: {
    dataName: {
      type: String,
      required: true
    },
    dataUrl: {
      type: String,
      default: ''
    },
    checkRole: {
      type: String,
      default: ''
    },
    multiSelect: {
      type: Boolean,
      default: false
    },
    pagination: {
      type: Boolean,
      default: false
    },
    sortCols: {
      type: Array,
      default: []
    },
    caption: {
      type: String,
      required: true
    },
    columns: {
      type: Array,
      default: []
    },
    buttons: {
      type: Array,
      default: []
    },
    sortProp: {
      type: Object,
      default: {}
    },
    reservedHeight: {
      type: Number,
      default: 0
    },
    filteringParamsConfig: {
      type: Array,
      default: () => ([])
    },
    filters: {
      type: Object,
      default: () => ({})
    },
    bus: {
      type: Object,
      default: null
    }
  },
  emits: ['click', 'update:selected-items', 'update:url-params', 'update:loading-overlay', 'update:filters'],
  data: () => ({
    selectAllItems: false,
    selectedIds: [],
    elements: 0,
    pages: 0,
    items: [],
    dataEmpty: false,
    paginationParams: {
      page: 1,
      size: 10,
      sort: {
        by: '',
        dir: ''
      },
    },
    sortDirIcons: {},
    windowHeight: window.innerHeight,
    dataPageLastRequest: '',
    pageSizeValidation: []
  }),
  computed: {
    pageRequest() {
      const request = {}
      request.page = this.paginationParams.page - 1
      request.size = this.paginationParams.size
      if (this.paginationParams.sort?.by && this.paginationParams.sort?.dir) {
        request.sort = this.paginationParams.sort
      }
      if (Object.keys(this.filters).length > 0) {
        request.filters = this.filters
      }
      return request
    },
    filterFieldNames() {
      return [...this.filteringParamsConfig.map(item => item.name)]
    },
    isMobile() {
      return DeviceUtils.isMobile()
    },
    allItemsSelected() {
      return this.items.length && this.selectedIds.length === this.items.length
    },
    paginationParamsKey() {
      return this.dataName + 'PaginationParams'
    },
    allItemIds() {
      return this.items.map(item => item.id)
    },
    elementHeight() {
      return this.windowHeight - this.reservedHeight
    },
    sortColsWithHeaders() {
      return this.sortCols.map(sortCol => ({key: sortCol, header: this.columnHeader(sortCol)}))
    },
  },
  watch: {
    filters() {
      this.paginationParams.page = 1
    },
    pageRequest(newValue, oldValue) {
      if (!this.pagination) {
        return
      }
      const action = () => {
        this.setUrlParams()
        this.loadDataPage()
      }
      if (this.onlyPageChanged(newValue, oldValue)) {
        action()
      } else {
        this.actionWithTimeout(action, 'pageRequest')
      }
    },
    loadingOverlay(newValue) {
      this.$emit('update:loading-overlay', newValue)
    },
    selectedIds(newValue) {
      this.selectAllItems = this.allItemsSelected
      this.$emit('update:selected-items', this.items.filter(item => newValue.includes(item.id)))
    },
    selectAllItems(newValue) {
      if (newValue) {
        this.selectedIds = this.allItemIds
      } else if (this.allItemsSelected) {
        this.selectedIds = []
      }
    },
    paginationParams: {
      handler() {
        if (this.validatePaginationParams()) {
          this.recalculateSortDirIcons()
          this.savePaginationParams()
        }
      },
      deep: true
    },
    items() {
      this.selectedIds = this.selectedIds.filter(id => this.allItemIds.includes(id))
    }
  },
  mounted() {
    if (this.pagination) {
      this.restorePaginationParams()
      this.readUrlParams()
      this.setUrlParams()
    }
  },
  methods: {
    async loadDataPage() {
      const request = this.pageRequest
      const requestJson = JSON.stringify(request)
      if (this.loadingOverlay || this.dataPageLastRequest === requestJson
          || this.checkRole && !userStore().checkUserRole(this.checkRole)
          || !this.validatePaginationParams()) {
        return
      }
      await this.loadWithOverlay(async () => {
        const response = await this.getApiProvider().postJson(this.dataUrl, request)
        if (response.ok) {
          this.items = response.data.data
          this.elements = response.data.elements
          this.pages = response.data.pages
          this.dataEmpty = this.elements === 0
          this.dataPageLastRequest = requestJson
        } else {
          this.showErrorsFromResponse(response)
        }
      })
    },
    setData({items, elements, pages}) {
      if (items) {
        this.items = items
      }
      if (elements) {
        this.elements = elements
      }
      if (pages) {
        this.pages = pages
      }
      this.dataEmpty = this.elements === 0 && this.items.length === 0
    },
    onlyPageChanged(newValue, oldValue) {
      let filtersValueChanged = false
      if (newValue.filters && !oldValue.filters || !newValue.filters && oldValue.filters) {
        filtersValueChanged = true
      } else if (newValue.filters && oldValue.filters) {
        for (const field of this.filterFieldNames) {
          if (newValue.filters[field]?.toString() !== oldValue.filters[field]?.toString()) {
            filtersValueChanged = true
          }
        }
      }
      const anotherPaginationParamChanged = newValue.size !== oldValue.size
          || newValue.sort?.by !== oldValue.sort?.by
          || newValue.sort?.dir !== oldValue.sort?.dir
      return newValue.page.toString() !== oldValue.page.toString()
          && !filtersValueChanged && !anotherPaginationParamChanged
    },
    readUrlParams() {
      const route = useRoute()
      const filters = {}
      for (const filteringParamConfig of this.filteringParamsConfig) {
        const rawValue = ref(route.query[filteringParamConfig.name]).value
        if (!rawValue) {
          continue
        }
        const readFunc = filteringParamConfig.readFunc || (value => value)
        filters[filteringParamConfig.formField || filteringParamConfig.name] = filteringParamConfig.isArray
            ? this.readNumberArrFromUrlParam(rawValue, readFunc)
            : readFunc(rawValue)
      }
      if (Object.keys(filters).length > 0) {
        this.$emit('update:filters', filters)
      }
      const sortBy = ref(route.query.sortBy)
      if (sortBy.value && this.paginationParams.sort && this.validateSortBy(sortBy.value)) {
        this.paginationParams.sort.by = sortBy.value
      }
      const sortDir = ref(route.query.sortDir)
      if (sortDir.value && this.paginationParams.sort && this.validateSortDir(sortDir.value)) {
        this.paginationParams.sort.dir = sortDir.value
      }
      const page = ref(route.query.page)
      if (page.value && !isNaN(page.value)) {
        this.paginationParams.page = parseInt(page.value)
      }
      const size = ref(route.query.size)
      if (size.value && !isNaN(size.value)) {
        this.paginationParams.size = parseInt(size.value)
      }
    },
    readNumberArrFromUrlParam(str, parseFunc) {
      return str.split(',').map(v => parseFunc(v)).filter(number => !isNaN(number))
    },
    setUrlParams() {
      this.$emit('update:url-params', this.makeUrlParams())
    },
    makeUrlParams() {
      let params = `?page=${this.paginationParams.page}&size=${this.paginationParams.size}`
      for (const paginationParamConfig of this.filteringParamsConfig) {
        const value = this.filters[paginationParamConfig.name]
        if (value) {
          const paramValue = paginationParamConfig.isArray ? this.makeUrlParamFromArr(value) : value
          params += `&${paginationParamConfig.name}=${paramValue}`
        }
      }
      const sortBy = this.paginationParams.sort?.by
      if (sortBy) {
        params += `&sortBy=${sortBy}`
      }
      const sortDir = this.paginationParams.sort?.dir
      if (sortDir) {
        params += `&sortDir=${sortDir}`
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
    restrictCaptionLength(item) {
      return StringUtils.restrictLength(item[this.caption], 22)
    },
    cardDescriptionExists(item) {
      for (const column of this.columns) {
        if (item[this.columnName(column)]) {
          return true
        }
      }
      return false
    },
    columnName(column) {
      if (typeof column === 'object') {
        return column.name
      }
      return column
    },
    columnValue(column, item) {
      if (column.isArray) {
        let allValues = ''
        for (const arrayItem of item[this.columnName(column)]) {
          const value = this.getValueFromObject(column, arrayItem)
          allValues += (allValues ? ', ' : '') + value
        }
        return allValues
      }
      return this.getValueFromObject(column, item)
    },
    getValueFromObject(column, item) {
      const value = this.extractColumnValue(column, item)
      if (value !== undefined && value !== null && typeof column !== 'string') {
        if (column.localeKeyGetter) {
          return this.$t(column.localeKeyGetter(value))
        }
        if (column.formatter) {
          return column.formatter(value)
        }
      }
      return value
    },
    extractColumnValue(column, item) {
      if (typeof column === 'string' || !column.valuePath) {
        return item[this.columnName(column)]
      }
      const valuePathArr = column.valuePath.split('.')
      let obj = item[valuePathArr[0]]
      for (let i = 1; i < valuePathArr.length; i++) {
        if (!obj) {
          return obj
        }
        obj = obj[valuePathArr[i]]
      }
      return obj
    },
    columnHeader(column) {
      return this._tc('fields.' + this.columnName(column))
    },
    buttonClick(button, item) {
      this.$emit('click', {button, item})
    },
    checkCondition(button, item) {
      if (!button.condition) {
        return true
      }
      return button.condition(item)
    },
    makeTooltip(button, item) {
      if (!button.tooltip) {
        return button.name
      }
      return button.tooltip(item)
    },
    makeIcon(button, item) {
      if (typeof button.icon === 'function') {
        return button.icon(item)
      }
      return button.icon
    },
    isSortable(columnName) {
      return this.sortCols.includes(columnName)
    },
    setSorting(column) {
      if (!column) {
        this.clearSorting()
        return
      }
      const columnName = this.columnName(column)
      if (!this.isSortable(columnName)) {
        return
      }
      if (this.paginationParams.sort.by !== columnName) {
        this.paginationParams.sort.by = columnName
        this.paginationParams.sort.dir = SORT_DIRS.ASC
      } else {
        if (this.paginationParams.sort.dir === SORT_DIRS.ASC) {
          this.paginationParams.sort.dir = SORT_DIRS.DESC
        } else {
          this.clearSorting()
        }
      }
    },
    clearSorting() {
      this.paginationParams.sort.dir = ''
      this.paginationParams.sort.by = ''
    },
    switchSortDir() {
      if (this.paginationParams.sort.dir === SORT_DIRS.ASC) {
        this.paginationParams.sort.dir = SORT_DIRS.DESC
      } else {
        this.paginationParams.sort.dir = SORT_DIRS.ASC
      }
    },
    recalculateSortDirIcons() {
      this.sortDirIcons = {}
      if (!this.paginationParams.sort.by) {
        return
      }
      this.sortDirIcons[this.paginationParams.sort.by] = this.getSortDirIcon()
    },
    getSortDirIcon() {
      if (this.paginationParams.sort.dir === SORT_DIRS.ASC) {
        return mdiSortAscending
      }
      if (this.paginationParams.sort.dir === SORT_DIRS.DESC) {
        return mdiSortDescending
      }
      return ''
    },
    savePaginationParams() {
      localStorage.setItem(this.paginationParamsKey, JSON.stringify(this.paginationParams))
    },
    restorePaginationParams() {
      const paramsJson = localStorage.getItem(this.paginationParamsKey)
      if (paramsJson) {
        const params = JSON.parse(paramsJson)
        if (params.page && params.size && params.sort) {
          this.paginationParams = params
        }
      }
    },
    validatePaginationParams() {
      const {page, size, sort} = this.paginationParams
      return page && size
          && this.validatePageSize(size)
          && this.validateSortBy(sort.by)
          && this.validateSortDir(sort.dir)
    },
    validatePageSize(size) {
      this.pageSizeValidation = []
      const value = size,
          minValue = 5,
          maxValue = appSettingsStore().settings?.maxPageSize
      let validationMsg = ''
      if (isNaN(value)) {
        validationMsg = this._tc('validation.number')
      } else if (value < minValue || value > maxValue) {
        validationMsg = this._tc('validation.between', {minValue, maxValue})
      }
      if (validationMsg) {
        this.pageSizeValidation.push(validationMsg)
      }
      return !validationMsg
    },
    validateSortDir(sortDir) {
      return !sortDir || [SORT_DIRS.DESC, SORT_DIRS.ASC].includes(sortDir)
    },
    validateSortBy(sortBy) {
      return !sortBy || this.sortCols.includes(sortBy)
    },
    // todo
    onResize() {
      this.windowHeight = window.innerHeight
    },
  }
}
</script>

<style scoped>
.no-padding {
  padding: 0 20px 0 0 !important;
}

.element-full-width {
  width: 100%;
}
</style>
