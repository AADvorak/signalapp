<template>
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
                @update:model-value="setSortBy"/>
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
      <fixed-width-wrapper v-if="dataEmpty && !loadingOverlay">
        <slot name="dataEmpty"/>
      </fixed-width-wrapper>
      <div v-else v-for="item in items">
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
            <v-btn
                v-for="button in buttons"
                :disabled="!checkCondition(button, item)"
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
            </v-btn>
          </v-card-actions>
        </v-card>
        <v-divider/>
      </div>
      <fixed-width-wrapper v-if="pagination" class="mt-5">
        <v-form>
          <div class="d-flex justify-center flex-wrap">
            <v-select
                v-model="paginationParams.size"
                item-title="title"
                item-value="value"
                :items="sizeOptions"
                :label="_tc('pagination.pageSize')"
                style="max-width: 150px"
                density="compact"/>
            <v-pagination
                v-model="page"
                :length="pages"
                style="min-width: 400px"
                density="compact"/>
          </div>
        </v-form>
        <p style="text-align: center;">
          {{ _tc('pagination.total', {pages, elements}) }}
        </p>
      </fixed-width-wrapper>
    </div>
    <v-data-table-server
        ref="dataTable"
        v-else
        v-model="selectedIds"
        v-model:items-per-page="paginationParams.size"
        :headers="dataTableHeaders"
        :items="items"
        :items-length="elements"
        :height="elementHeight"
        item-value="id"
        :items-per-page-text="_tc('pagination.pageSize')"
        :items-per-page-options="sizeOptions"
        :show-select="multiSelect"
        :sort-asc-icon="mdi.mdiSortAscending"
        :sort-desc-icon="mdi.mdiSortDescending"
        :sort-by="dataTableSortBy"
        :hide-default-footer="!pagination"
        density="compact"
        fixed-header
        @update:options="onDataTableOptionsUpdate"
    >
      <template v-slot:no-data>
        <slot v-if="dataEmpty" name="dataEmpty"/>
      </template>
      <template v-slot:item.actions="{ item }">
        <div class="d-flex justify-center">
          <btn-with-tooltip
              v-for="button in buttons"
              :tooltip="makeTooltip(button, item)"
              :disabled="!checkCondition(button, item)"
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
        </div>
      </template>
    </v-data-table-server>
  </v-layout>
</template>

<script>
import {mdiSortAscending, mdiSortDescending} from "@mdi/js";
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
import {mobileVersionStore} from "~/stores/mobile-version-store";
import DeviceUtils from "~/utils/device-utils";

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
    selectedIds: [],
    elements: 0,
    pages: 0,
    items: [],
    dataEmpty: false,
    page: 1,
    paginationParams: {
      size: 10,
      sort: {
        by: '',
        dir: ''
      },
    },
    sortDirIcons: {},
    windowHeight: window.innerHeight,
    dataPageLastRequest: '',
    mdi: {
      mdiSortAscending,
      mdiSortDescending
    }
  }),
  computed: {
    pageRequest() {
      const request = {}
      request.page = this.page - 1
      request.size = this.paginationParams.size
      if (this.paginationParams.sort?.by && this.paginationParams.sort?.dir) {
        request.sort = this.paginationParams.sort
      }
      if (Object.keys(this.filters).length > 0) {
        request.filters = this.filters
      }
      return request
    },
    isMobile() {
      return mobileVersionStore().isMobile
    },
    dataTableHeaders() {
      const headers = [{
        title: this.columnHeader(this.caption),
        key: this.caption,
        align: 'start',
        sortable: this.isSortable(this.caption)
      }]
      for (const column of this.columns) {
        const columnName = this.columnName(column)
        headers.push({
          title: this.columnHeader(column),
          key: columnName,
          align: 'start',
          sortable: this.isSortable(columnName),
          value: item => this.columnValue(column, item)
        })
      }
      headers.push({key: 'actions', sortable: false})
      return headers
    },
    sizeOptions() {
      const minValue = 5,
          step = 5,
          maxValue = appSettingsStore().settings?.maxPageSize
      const options = []
      const push = function (value) {
        options.push({value, title: String(value)})
      }
      for (let value = minValue; value < maxValue; value += step) {
        push(value)
      }
      push(maxValue)
      return options
    },
    dataTableSortBy() {
      if (!this.paginationParams.sort.by || !this.paginationParams.sort.dir) {
        return []
      }
      return [{
        key: this.paginationParams.sort.by,
        order: this.paginationParams.sort.dir
      }]
    },
    paginationParamsKey() {
      return this.dataName + 'PaginationParams'
    },
    pageKey() {
      return this.dataName + 'Page'
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
      this.page = 1
      this.dataEmpty = false
      this.actionWithTimeout(this.setUrlParamsAndLoadDataPage, 'filters')
    },
    loadingOverlay(newValue) {
      this.$emit('update:loading-overlay', newValue)
    },
    selectedIds(newValue) {
      this.$emit('update:selected-items', this.items.filter(item => newValue.includes(item.id)))
    },
    paginationParams: {
      handler() {
        if (this.validatePaginationParams()) {
          this.recalculateSortDirIcons()
          this.savePaginationParams()
          this.page = 1
          this.dataEmpty = false
          this.actionWithTimeout(this.setUrlParamsAndLoadDataPage, 'paginationParams')
        }
      },
      deep: true
    },
    page() {
      this.savePage()
      this.setUrlParamsAndLoadDataPage()
    },
    items() {
      this.selectedIds = this.selectedIds.filter(id => this.allItemIds.includes(id))
    }
  },
  mounted() {
    if (this.pagination) {
      this.restorePaginationParams()
      this.restorePage()
      this.readUrlParams()
      setTimeout(this.setUrlParamsAndLoadDataPage)
    }
  },
  methods: {
    setUrlParamsAndLoadDataPage() {
      this.setUrlParams()
      this.loadDataPage()
    },
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
          this.isMobile && setTimeout(DeviceUtils.scrollUp)
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
    getData() {
      return {
        items: this.items,
        elements: this.elements,
        pages: this.pages
      }
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
      const page = parseInt(ref(route.query.page).value)
      if (page && !isNaN(page)) {
        this.page = page
      }
      const size = parseInt(ref(route.query.size).value)
      if (size && !isNaN(size) && this.validatePageSize(size)) {
        this.paginationParams.size = size
      }
    },
    readNumberArrFromUrlParam(str, parseFunc) {
      return str.split(',').map(v => parseFunc(v)).filter(number => !isNaN(number))
    },
    setUrlParams() {
      this.$emit('update:url-params', this.makeUrlParams())
    },
    makeUrlParams() {
      let params = `?page=${this.page}&size=${this.paginationParams.size}`
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
    setSortBy(columnName) {
      if (!columnName) {
        this.clearSorting()
        return
      }
      this.paginationParams.sort.by = columnName
      if (!this.paginationParams.sort.dir) {
        this.paginationParams.sort.dir = SORT_DIRS.ASC
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
        if (params.size && params.sort) {
          this.paginationParams = params
        }
      }
    },
    savePage() {
      localStorage.setItem(this.pageKey, JSON.stringify({page: this.page}))
    },
    restorePage() {
      const paramsJson = localStorage.getItem(this.pageKey)
      if (paramsJson) {
        const params = JSON.parse(paramsJson)
        if (params.page) {
          this.page = params.page
        }
      }
    },
    validatePaginationParams() {
      const {size, sort} = this.paginationParams
      return size
          && this.validatePageSize(size)
          && this.validateSortBy(sort.by)
          && this.validateSortDir(sort.dir)
    },
    validatePageSize(size) {
      return this.sizeOptions.map(option => option.value).includes(size)
    },
    validateSortDir(sortDir) {
      return !sortDir || [SORT_DIRS.DESC, SORT_DIRS.ASC].includes(sortDir)
    },
    validateSortBy(sortBy) {
      return !sortBy || this.sortCols.includes(sortBy)
    },
    onDataTableOptionsUpdate(options) {
      this.page = options.page
      this.paginationParams.size = options.itemsPerPage
      const sort = options.sortBy[0]
      if (sort) {
        this.paginationParams.sort.by = sort.key
        this.paginationParams.sort.dir = sort.order
      } else {
        this.clearSorting()
      }
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
