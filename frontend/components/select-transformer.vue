<template>
  <v-card width="100%">
    <v-toolbar>
      <v-toolbar-title>{{ _t('title') }}</v-toolbar-title>
      <v-spacer/>
      <v-btn
          icon
          @click="close"
      >
        <v-icon>{{mdiClose}}</v-icon>
      </v-btn>
    </v-toolbar>
    <v-card-text>
      <v-form>
        <v-row>
          <v-col>
            <v-select
                :model-value="form.types.value"
                item-title="name"
                item-value="code"
                :items="types"
                :label="_t('filterByTypes')"
                multiple/>
          </v-col>
          <v-col>
            <v-text-field
                :model-value="form.filter.value"
                :label="_tc('fields.search')"/>
          </v-col>
        </v-row>
      </v-form>
      <h3 style="height: 300px" v-if="!filteredTransformers.length">{{ _tc('messages.nothingIsFound') }}</h3>
      <v-table v-else fixed-header height="300px">
        <thead>
        <tr>
          <th>{{ _tc('fields.name') }}</th>
          <th>{{ _tc('fields.type') }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="transformer in filteredTransformers" @click="select(transformer)">
          <td style="cursor: pointer;">{{ _tr(transformer.code) }}</td>
          <td style="cursor: pointer;">{{ _trt(transformer.type) }}</td>
        </tr>
        </tbody>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script>
import ComponentBase from "./component-base";
import formValuesSaving from "../mixins/form-values-saving";
import {mdiClose} from "@mdi/js";
import {dataStore} from "../stores/data-store";

const TRANSFORMER_TYPES = ['amplifier', 'modulator', 'filter', 'oscillator', 'math']

export default {
  name: "select-transformer",
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
    mdiClose,
    form: {
      types: {value: []},
      filter: {value: ''}
    },
    selectedTypes: []
  }),
  computed: {
    transformers() {
      return this.double ? dataStore().getDoubleTransformers : dataStore().getTransformers
    },
    filteredTransformers() {
      return this.transformers.filter(transformer => {
        const {filter, types} = this.formValues
        let matchType = true, matchFilter = true
        if (filter.length) {
          matchFilter = this._tr(transformer.code).toLowerCase().includes(filter.toLowerCase())
        }
        if (types.length) {
          matchType = types.includes(transformer.type)
        }
        return matchFilter && matchType
      })
    },
    types() {
      return TRANSFORMER_TYPES
          .filter((value, index, self) => self.indexOf(value) === index)
          .map(code => ({code, name: this._trt(code)}))
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
    select(transformer) {
      this.close()
      this.bus.emit('transformerSelected', transformer)
    },
    close() {
      this.$emit('close')
    }
  },
}
</script>
