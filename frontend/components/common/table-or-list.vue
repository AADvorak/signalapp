<template>
  <div v-if="isMobile">
    <div v-for="item in items">
      <v-card >
        <v-card-item>
          <v-card-title>
            <div style="height: 48px" class="d-flex justify-start">
              <span v-if="select"><v-checkbox v-model="selectedIds" :value="item.id" @click.stop/></span>
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
                  @changed="onSignalFoldersChanged"/>
            </v-btn>
          </template>
        </v-card-actions>
      </v-card>
      <v-divider/>
    </div>
  </div>
  <v-table v-else fixed-header>
    <thead>
     <tr>
       <th v-if="select" class="text-left no-padding">
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
        <td v-if="select" class="no-padding">
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
                @changed="onSignalFoldersChanged"/>
          </btn-with-tooltip>
        </td>
      </tr>
    </tbody>
  </v-table>
</template>

<script>
import {mdiSortAscending, mdiSortDescending} from "@mdi/js";
import DeviceUtils from "~/utils/device-utils";
import StringUtils from "~/utils/string-utils";
import ComponentBase from "~/components/base/component-base.vue";
import BtnWithTooltip from "~/components/common/btn-with-tooltip.vue";
import SignalFoldersMenu from "~/components/folders/signal-folders-menu.vue";

const SORT_DIRS = {
  DESC: 'desc',
  ASC: 'asc'
}

export default {
  name: "table-or-list",
  components: {SignalFoldersMenu, BtnWithTooltip},
  extends: ComponentBase,
  props: {
    dataName: {
      type: String,
      required: true
    },
    items: {
      type: Array,
      required: true
    },
    select: {
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
  },
  emits: ['click', 'change', 'select', 'sort'],
  data: () => ({
    selectAllItems: false,
    selectedIds: [],
    sort: {
      by: '',
      dir: ''
    },
    sortDirIcons: {}
  }),
  computed: {
    isMobile() {
      return DeviceUtils.isMobile()
    },
    allItemsSelected() {
      return this.items.length && this.selectedIds.length === this.items.length
    },
    sortingKey() {
      return this.dataName + 'Sorting'
    },
    allItemIds() {
      return this.items.map(item => item.id)
    }
  },
  watch: {
    selectedIds(newValue) {
      this.selectAllItems = this.allItemsSelected
      this.$emit('select', newValue)
    },
    selectAllItems(newValue) {
      if (newValue) {
        this.selectedIds = this.allItemIds
      } else if (this.allItemsSelected) {
        this.selectedIds = []
      }
    },
    sort: {
      handler(newValue) {
        this.recalculateSortDirIcons()
        this.saveSorting()
        this.$emit('sort', newValue)
      },
      deep: true
    },
    items() {
      this.selectedIds = this.selectedIds.filter(id => this.allItemIds.includes(id))
    }
  },
  mounted() {
    setTimeout(() => {
      if (this.validateSorting(this.sortProp)) {
        this.sort = this.sortProp
      } else {
        this.restoreSorting()
      }
    })
  },
  methods: {
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
      const value = item[this.columnName(column)]
      if (value === undefined || typeof column === 'string' || !column.formatter) {
        return value
      }
      return column.formatter(value)
    },
    columnHeader(column) {
      return this._tc('fields.' + this.columnName(column))
    },
    buttonClick(button, item) {
      this.$emit('click', {button, item})
    },
    onSignalFoldersChanged() {
      this.$emit('change', 'signal-folders-menu')
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
      const columnName = this.columnName(column)
      if (!this.isSortable(columnName)) {
        return
      }
      if (this.sort.by !== columnName) {
        this.sort.by = columnName
        this.sort.dir = SORT_DIRS.ASC
      } else {
        if (this.sort.dir === SORT_DIRS.ASC) {
          this.sort.dir = SORT_DIRS.DESC
        } else {
          this.sort.dir = ''
          this.sort.by = ''
        }
      }
    },
    recalculateSortDirIcons() {
      this.sortDirIcons = {}
      if (!this.sort.by) {
        return
      }
      this.sortDirIcons[this.sort.by] = this.getSortDirIcon()
    },
    getSortDirIcon() {
      if (this.sort.dir === SORT_DIRS.ASC) {
        return mdiSortAscending
      }
      if (this.sort.dir === SORT_DIRS.DESC) {
        return mdiSortDescending
      }
      return ''
    },
    saveSorting() {
      localStorage.setItem(this.sortingKey, JSON.stringify(this.sort))
    },
    restoreSorting() {
      const sortJson = localStorage.getItem(this.sortingKey)
      if (sortJson) {
        const sort = JSON.parse(sortJson)
        if (sort.by && sort.dir) {
          this.sort = sort
        }
      }
    },
    validateSorting(sort) {
      return sort.by && sort.dir
          && [SORT_DIRS.DESC, SORT_DIRS.ASC].includes(sort.dir)
          && this.sortCols.includes(sort.by)
    }
  }
}
</script>

<style scoped>
.no-padding {
  padding: 0 20px 0 0 !important;
}
</style>
