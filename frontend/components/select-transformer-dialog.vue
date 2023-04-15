<template>
  <v-dialog
      v-model="dialog"
      max-width="600px"
      max-height="500px"
  >
    <template v-slot:activator="{ props }">
      <v-btn
          color="primary"
          v-bind="props"
          :disabled="disabled"
      >
        {{ _tc('buttons.transform') }}
      </v-btn>
    </template>
    <v-card width="100%">
      <v-toolbar>
        <v-toolbar-title>{{ _t('title') }}</v-toolbar-title>
        <v-spacer/>
        <v-btn
            icon
            @click="dialog = false"
        >
          <v-icon>{{mdiClose}}</v-icon>
        </v-btn>
      </v-toolbar>
      <v-card-text>
        <v-form>
          <v-row>
            <v-col>
              <v-select
                  v-model="form.types"
                  item-title="name"
                  item-value="code"
                  :items="types"
                  :label="_t('filterByTypes')"
                  multiple/>
            </v-col>
            <v-col>
              <v-text-field
                  v-model="form.filter"
                  :label="_tc('search')"
                  autofocus/>
            </v-col>
          </v-row>
        </v-form>
        <v-table fixed-header height="300px">
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
  </v-dialog>
</template>

<script>
import {dataStore} from "../stores/data-store";
import formValuesSaving from "../mixins/form-values-saving";
import {mdiClose} from "@mdi/js";
import ComponentBase from "./component-base";

export default {
  name: "select-transformer-dialog",
  extends: ComponentBase,
  mixins: [formValuesSaving],
  props: {
    bus: Object,
    double: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
  },
  data: () => ({
    dialog: false,
    mdiClose,
    transformers: [],
    form: {
      types: [],
      filter: ''
    },
    selectedTypes: []
  }),
  computed: {
    filteredTransformers() {
      return this.transformers.filter(transformer => {
        let matchType = true, matchFilter = true
        if (this.form.filter.length) {
          matchFilter = this._tr(transformer.code).toLowerCase().includes(this.form.filter.toLowerCase())
        }
        if (this.form.types.length) {
          matchType = this.form.types.includes(transformer.type)
        }
        return matchFilter && matchType
      })
    },
    types() {
      return this.transformers.map(t => t.type)
          .filter((value, index, self) => self.indexOf(value) === index)
          .map(code => ({code, name: this._trt(code)}))
    }
  },
  watch: {
    'form.types'() {
      this.saveFormValues()
    },
    'form.filter'() {
      this.saveFormValues()
    },
  },
  mounted() {
    this.restoreFormValues()
    this.transformers = this.double ? dataStore().getDoubleTransformers : dataStore().getTransformers
  },
  methods: {
    select(transformer) {
      this.dialog = false
      this.bus.emit('transformerSelected', transformer)
    },
  },
}
</script>
