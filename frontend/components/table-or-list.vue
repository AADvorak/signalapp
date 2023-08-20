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
          <v-btn v-for="button in buttons" @click="buttonClick(button.name, item)">
            <v-icon :color="button.color">
              {{ button.icon }}
            </v-icon>
          </v-btn>
        </v-card-actions>
      </v-card>
      <v-divider/>
    </div>
  </div>
  <table v-else>
    <thead>
     <tr>
       <th v-if="select" class="text-left">
         <div style="height: 58px">
           <v-checkbox :model-value="allItemsSelected"/>
         </div>
       </th>
       <th class="text-left"> {{ columnHeader(caption) }} </th>
       <th v-for="column in columns" class="text-left">
         {{ columnHeader(column) }}
       </th>
       <th v-for="_ in buttons" class="text-left"></th>
     </tr>
    </thead>
    <tbody>
      <tr v-for="item in items">
        <td v-if="select">
          <div style="height: 58px">
            <v-checkbox :model-value="item.selected" @click.stop/>
          </div>
        </td>
        <td>{{ item[caption] }}</td>
        <td v-for="column in columns">{{ item[column] }}</td>
        <td v-for="button in buttons">
          <btn-with-tooltip :tooltip="button.name" @click="buttonClick(button.name, item)">
            <v-icon :color="button.color">
              {{ button.icon }}
            </v-icon>
          </btn-with-tooltip>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script>

import DeviceUtils from "~/utils/device-utils";
import StringUtils from "~/utils/string-utils";
import ComponentBase from "~/components/component-base.vue";
import BtnWithTooltip from "~/components/btn-with-tooltip.vue";

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
    allItemsSelected: {
      type: Boolean,
      default: false
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
    }
  },
  emits: ['click'],
  computed: {
    isMobile() {
      return DeviceUtils.isMobile()
    }
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
        if (item[column]) {
          if (description) {
            description += ', '
          }
          description += item[column]
        }
      }
      return description
    },
    columnHeader(column) {
      return this._tc('fields.' + column)
    },
    buttonClick(button, item) {
      this.$emit('click', {button, item})
    }
  }
}
</script>
