<template>
  <div v-if="isMobile">
    <div v-for="item in items">
      <v-card >
        <v-card-item>
          <v-card-title>
            <div style="height: 48px" class="d-flex justify-start">
              <span v-if="select"><v-checkbox :model-value="item.selected" @click.stop/></span>
              <div style="margin: 13px 0;">{{ restrictCaptionLength(item) }}</div>
            </div>
          </v-card-title>
        </v-card-item>
        <v-card-text v-if="cardDescriptionExists(item)">{{ makeCardDescription(item) }}</v-card-text>
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
  <v-table v-else>
    <thead>
     <tr>
       <th v-if="select" class="text-left">
         <div style="height: 58px">
           <v-checkbox v-model="selectAllItems"/>
         </div>
       </th>
       <th class="text-left" @click="setSorting(caption)">
         {{ columnHeader(caption) }} {{ getSortDirSign(caption) }}
       </th>
       <th v-for="column in columns" class="text-left" @click="setSorting(column)">
         {{ columnHeader(column) }} {{ getSortDirSign(column) }}
       </th>
       <th v-for="_ in buttons" class="text-left"></th>
     </tr>
    </thead>
    <tbody>
      <tr v-for="item in items">
        <td v-if="select">
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

import DeviceUtils from "~/utils/device-utils";
import StringUtils from "~/utils/string-utils";
import ComponentBase from "~/components/component-base.vue";
import BtnWithTooltip from "~/components/btn-with-tooltip.vue";

const SORT_DIRS = {
  DESC: 'desc',
  ASC: 'asc'
}

export default {
  name: "table-or-list",
  extends: ComponentBase,
  components: {BtnWithTooltip},
  props: {
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
  }),
  computed: {
    isMobile() {
      return DeviceUtils.isMobile()
    },
    allItemsSelected() {
      return this.items.length && this.selectedIds.length === this.items.length
    }
  },
  watch: {
    selectedIds(newValue) {
      this.selectAllItems = this.allItemsSelected
      this.$emit('select', newValue)
    },
    selectAllItems(newValue) {
      if (newValue) {
        this.selectedIds = this.items.map(item => item.id)
      } else if (this.allItemsSelected) {
        this.selectedIds = []
      }
    },
    sortProp: {
      handler(newValue) {
        if (newValue.by && newValue.dir) {
          this.sort = newValue
        }
      },
      deep: true
    },
    sort: {
      handler(newValue) {
        this.$emit('sort', newValue)
      },
      deep: true
    },
  },
  methods: {
    restrictCaptionLength(item) {
      return StringUtils.restrictLength(item[this.caption], 22)
    },
    cardDescriptionExists(item) {
      for (const column of this.columns) {
        if (item[column]) {
          return true
        }
      }
      return false
    },
    makeCardDescription(item) {
      let description = ''
      for (const column of this.columns) {
        const value = this.columnValue(column, item)
        if (value) {
          if (description) {
            description += ', '
          }
          description += value
        }
      }
      return description
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
    getSortDirSign(column) {
      const columnName = this.columnName(column)
      if (!this.isSortable(columnName) || this.sort.by !== columnName) {
        return ''
      }
      if (this.sort.dir === SORT_DIRS.ASC) {
        return '(^)'
      }
      if (this.sort.dir === SORT_DIRS.DESC) {
        return '(v)'
      }
      return ''
    },
  }
}
</script>
