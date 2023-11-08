<template>
  <v-card width="100%">
    <toolbar-with-close-btn :title="_t('title')" @close="close"/>
    <v-card-text>
      <v-form>
        <div class="d-flex justify-center flex-wrap">
          <v-select
              class="filter-input"
              v-model="form.types.value"
              item-title="name"
              item-value="code"
              :items="types"
              :label="_t('filterByTypes')"
              multiple/>
          <v-text-field
              class="filter-input"
              v-model="form.filter.value"
              :label="_tc('fields.search')"
          >
            <template v-slot:append-inner>
              <btn-with-tooltip
                  :disabled="filterIsEmpty"
                  tooltip="clear"
                  @click="clearFilter"
              >
                <v-icon>
                  {{ mdiFilterOff }}
                </v-icon>
              </btn-with-tooltip>
            </template>
          </v-text-field>
        </div>
      </v-form>
      <h3 style="height: 300px" v-if="!filteredProcessors.length">{{ _tc('messages.nothingIsFound') }}</h3>
      <v-table v-else fixed-header height="300px">
        <thead>
        <tr>
          <th>{{ _tc('fields.name') }}</th>
          <th>{{ _tc('fields.type') }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="processor in filteredProcessors" @click="select(processor)">
          <td style="cursor: pointer;">{{ _tpn(processor.code) }}</td>
          <td style="cursor: pointer;">{{ _tpt(processor.type) }}</td>
        </tr>
        </tbody>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script>
import {mdiFilterOff} from "@mdi/js";
import ComponentBase from "./component-base";
import formValuesSaving from "../mixins/form-values-saving";
import {moduleStore} from "~/stores/module-store";

const PROCESSOR_TYPES = ['amplifier', 'modulator', 'filter', 'oscillator', 'math']

export default {
  name: "select-processor",
  extends: ComponentBase,
  mixins: [formValuesSaving],
  props: {
    bus: Object,
    double: {
      type: Boolean,
      default: false
    }
  },
  emits: ['close'],
  data: () => ({
    form: {
      types: {value: []},
      filter: {value: ''}
    },
    selectedTypes: [],
    mdiFilterOff
  }),
  computed: {
    processors() {
      return this.double ? moduleStore().doubleProcessors : moduleStore().processors
    },
    filteredProcessors() {
      return this.processors.filter(processor => {
        const {filter, types} = this.formValues
        let matchType = true, matchFilter = true
        if (filter.length) {
          matchFilter = this._tpn(processor.code).toLowerCase().includes(filter.toLowerCase())
        }
        if (types.length) {
          matchType = types.includes(processor.type)
        }
        return matchFilter && matchType
      })
    },
    types() {
      return PROCESSOR_TYPES
          .filter((value, index, self) => self.indexOf(value) === index)
          .map(code => ({code, name: this._tpt(code)}))
    },
    filterIsEmpty() {
      return !this.formValues.filter && !this.formValues.types.length
    }
  },
  watch: {
    formValues: {
      handler() {
        this.saveFormValues()
      },
      deep: true
    },
  },
  mounted() {
    this.restoreFormValues()
  },
  methods: {
    select(processor) {
      this.close()
      this.bus.emit('processorSelected', processor)
    },
    close() {
      this.$emit('close')
    },
    clearFilter() {
      this.formValue('filter', '')
      this.formValue('types', [])
    }
  },
}
</script>

<style scoped>
.filter-input {
  min-width: 300px;
  max-width: 800px;
  margin-left: 5px;
  margin-right: 5px;
}
</style>
