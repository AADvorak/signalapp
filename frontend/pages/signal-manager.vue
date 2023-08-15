<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%">
        <v-card-text>
          <fixed-width-wrapper>
            <p>
              {{ _t('total', {pages, elements}) }}
            </p>
            <v-form class="mt-5">
              <v-row>
                <v-col>
                  <number-input
                      field="pageSize"
                      :label="_t('pageSize')"
                      :field-obj="form.pageSize"
                      @update="v => form.pageSize.value = v"/>
                </v-col>
                <v-col>
                  <text-input
                      field="search"
                      :field-obj="form.search"
                      @update="v => form.search.value = v"/>
                </v-col>
              </v-row>
              <v-row>
                <v-col>
                  <v-select
                      v-model="form.sampleRates.value"
                      item-title="reduced"
                      item-value="value"
                      :items="sampleRates"
                      :label="_t('sampleRates')"
                      multiple/>
                </v-col>
                <v-col>
                  <v-select
                      v-model="form.folderIds.value"
                      item-title="name"
                      item-value="id"
                      :items="folders"
                      :label="_t('folders')"
                      multiple/>
                </v-col>
              </v-row>
            </v-form>
          </fixed-width-wrapper>
          <fixed-width-wrapper v-if="signalsEmpty && !loadingOverlay">
            <h3 v-if="!formValues.search">{{ _t('youHaveNoStoredSignals') }}.
              <a href="/signal-generator">{{ _t('generate') }}</a> {{ _t('or') }}
              <a href="/signal-recorder">{{ _t('record') }}</a> {{ _t('newSignalsToStartWorking') }}.</h3>
            <h3 v-else>{{ _tc('messages.nothingIsFound') }}</h3>
          </fixed-width-wrapper>
          <div v-else>
            <div v-if="isMobile">
              <div v-for="signal in signals">
                <v-card >
                  <v-card-item>
                    <v-card-title>
                      <div style="height: 48px" class="d-flex justify-start">
                        <span><v-checkbox @click.stop v-model="signal.selected"/></span>
                        <div style="margin: 13px 0;">{{ restrictSignalNameLength(signal) }}</div>
                      </div>
                    </v-card-title>
                  </v-card-item>
                  <v-card-text v-if="signal.description">{{ signal.description }}</v-card-text>
                  <v-card-actions class="justify-center">
                    <v-btn @click="openSignal(signal)">
                      <v-icon color="primary">
                        {{ mdi.mdiFileEdit }}
                      </v-icon>
                    </v-btn>
                    <v-btn v-if="signal.sampleRate >= 3000" @click="playOrStopSignal(signal)">
                      <v-icon>
                        {{ isSignalPlayed(signal) ? mdi.mdiStop : mdi.mdiPlay }}
                      </v-icon>
                    </v-btn>
                    <v-btn v-if="folders.length">
                      <v-icon>
                        {{ mdi.mdiFolder }}
                      </v-icon>
                      <signal-folders-menu :signal-id="signal.id" @changed="onSignalFoldersChanged"/>
                    </v-btn>
                    <v-btn @click="askConfirmDeleteSignal(signal)">
                      <v-icon color="error">
                        {{ mdi.mdiDelete }}
                      </v-icon>
                    </v-btn>
                  </v-card-actions>
                </v-card>
                <v-divider/>
              </div>
            </div>
            <v-table v-else>
              <thead>
              <tr>
                <th class="text-left">
                  <!--                todo костыль-->
                  <div style="height: 58px">
                    <v-checkbox v-model="selectSignals"/>
                  </div>
                </th>
                <th @click="setSortingName" class="text-left">
                  {{ _tc('fields.name') }} {{sortingNameSign}}
                </th>
                <th @click="setSortingDescription" class="text-left">
                  {{ _tc('fields.description') }} {{sortingDescriptionSign}}
                </th>
                <th @click="setSortingSampleRate" class="text-left">
                  {{ _tc('fields.sampleRate') }} {{sortingSampleRateSign}}
                </th>
                <th class="text-left"></th>
                <th class="text-left"></th>
                <th v-if="folders.length" class="text-left"></th>
                <th class="text-left"></th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="signal in signals">
                <td>
                  <!--                todo костыль-->
                  <div style="height: 58px">
                    <v-checkbox @click.stop v-model="signal.selected"/>
                  </div>
                </td>
                <td>{{ signal.name }}</td>
                <td>{{ signal.description }}</td>
                <td>{{ this.reduceFractionDigitsByValue(signal.sampleRate) }}</td>
                <td class="text-right">
                  <btn-with-tooltip tooltip="open" @click="openSignal(signal)">
                    <v-icon color="primary">
                      {{ mdi.mdiFileEdit }}
                    </v-icon>
                  </btn-with-tooltip>
                </td>
                <td class="text-right">
                  <btn-with-tooltip v-if="signal.sampleRate >= 3000"
                                    :tooltip="isSignalPlayed(signal) ? 'stop' : 'play'"
                                    @click="playOrStopSignal(signal)">
                    <v-icon>
                      {{ isSignalPlayed(signal) ? mdi.mdiStop : mdi.mdiPlay }}
                    </v-icon>
                  </btn-with-tooltip>
                </td>
                <td v-if="folders.length" class="text-right">
                  <v-btn class="btn-with-icon" variant="text">
                    <v-icon>{{ mdi.mdiFolder }}</v-icon>
                    <signal-folders-menu :signal-id="signal.id" @changed="onSignalFoldersChanged"/>
                  </v-btn>
                </td>
                <td class="text-right">
                  <btn-with-tooltip tooltip="delete" @click="askConfirmDeleteSignal(signal)">
                    <v-icon color="error">
                      {{ mdi.mdiDelete }}
                    </v-icon>
                  </btn-with-tooltip>
                </td>
              </tr>
              </tbody>
            </v-table>
          </div>
          <fixed-width-wrapper>
            <v-pagination
                v-model="page"
                :length="pages"/>
            <div class="d-flex justify-center flex-wrap">
              <p class="mt-5">
                {{ _t('actionsWithSelectedSignals') }}
              </p>
            </div>
            <div class="d-flex justify-center flex-wrap">
              <v-dialog
                  v-model="transformDialog"
                  max-width="800px"
                  max-height="500px"
              >
                <template v-slot:activator="{ props }">
                  <v-btn
                      :disabled="!transformSignalsAvailable"
                      color="primary"
                      v-bind="props"
                      @click="loadSelectedSignalsData"
                  >
                    {{ _tc('buttons.transform') }}
                  </v-btn>
                </template>
                <select-transformer :bus="bus" :double="selectedSignals.length === 2" @close="transformDialog = false"/>
              </v-dialog>
              <v-dialog
                  v-model="viewDialog"
                  max-height="800px"
              >
                <template v-slot:activator="{ props }">
                  <v-btn
                      :disabled="!viewSignalsAvailable"
                      color="secondary"
                      v-bind="props"
                      @click="loadSelectedSignalsData"
                  >
                    {{ _t('view') }}
                  </v-btn>
                </template>
                <v-card width="100%">
                  <v-toolbar>
                    <v-toolbar-title>{{ _t('viewSignals') }}</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-btn
                        icon
                        @click="viewDialog = false"
                    >
                      <v-icon>{{ mdi.mdiClose }}</v-icon>
                    </v-btn>
                  </v-toolbar>
                  <v-card-text>
                    <chart-drawer :signals="viewDialog && selectedSignalsDataLoaded ? selectedSignals : []"/>
                  </v-card-text>
                </v-card>
              </v-dialog>
              <v-btn :disabled="!selectedSignals.length" color="error" @click="askConfirmDeleteSelectedSignals">
                {{ _tc('buttons.delete') }}
                <v-icon>{{ mdi.mdiDelete }}</v-icon>
              </v-btn>
            </div>
          </fixed-width-wrapper>
        </v-card-text>
      </v-card>
    </div>
    <transformer-double-dialog
        v-if="selectedSignals.length === 2"
        :bus="bus"
        :signals="transformSignalsAvailable && selectedSignalsDataLoaded ? selectedSignals : []"/>
    <transformer-dialog
        v-if="selectedSignals.length === 1"
        :bus="bus"
        :signal="transformSignalsAvailable && selectedSignalsDataLoaded ? selectedSignals[0] : null"/>
    <confirm-dialog :opened="confirm.opened" :text="confirm.text" ok-color="error"
                    @ok="confirm.ok" @cancel="confirm.cancel"/>
    <loading-overlay :show="loadingOverlay"/>
  </NuxtLayout>
</template>

<script>
import {mdiDelete, mdiPlay, mdiStop, mdiClose, mdiFileEdit, mdiFolder} from "@mdi/js";
import SignalPlayer from "../audio/signal-player";
import PageBase from "../components/page-base";
import ChartDrawer from "../components/chart-drawer";
import mitt from "mitt";
import TransformerDoubleDialog from "../components/transformer-double-dialog";
import SignalUtils from "../utils/signal-utils";
import FixedWidthWrapper from "../components/fixed-width-wrapper";
import TransformerDialog from "../components/transformer-dialog";
import SelectTransformer from "../components/select-transformer";
import NumberInput from "../components/number-input";
import formNumberValues from "../mixins/form-number-values";
import formValidation from "../mixins/form-validation";
import actionWithTimeout from "../mixins/action-with-timeout";
import formValuesSaving from "../mixins/form-values-saving";
import TextInput from "../components/text-input";
import DeviceUtils from "../utils/device-utils";
import StringUtils from "../utils/string-utils";
import BtnWithTooltip from "../components/btn-with-tooltip";
import {dataStore} from "~/stores/data-store";
import FolderRequests from "~/api/folder-requests";
import NumberUtils from "~/utils/number-utils";

export default {
  name: "signal-manager",
  components: {
    BtnWithTooltip,
    TextInput,
    NumberInput,
    SelectTransformer, TransformerDialog,
    ChartDrawer, TransformerDoubleDialog,
    FixedWidthWrapper
  },
  extends: PageBase,
  mixins: [formNumberValues, formValidation, formValuesSaving, actionWithTimeout],
  data: () => ({
    viewDialog: false,
    transformDialog: false,
    sampleRates: [],
    signals: [],
    signalsLastLoadFilter: '',
    signalsEmpty: false,
    elements: 0,
    pages: 0,
    page: 1,
    sortBy: '',
    sortDir: '',
    playedSignal: null,
    mdi: {
      mdiDelete,
      mdiPlay,
      mdiStop,
      mdiClose,
      mdiFileEdit,
      mdiFolder
    },
    form: {
      pageSize: {
        value: 10,
        params: {
          min: 5,
          max: 25,
          step: 1
        }
      },
      search: {value: ''},
      sampleRates: {value: []},
      folderIds: {value: []}
    },
    SORT_DIRS: {
      DESC: 'desc',
      ASC: 'asc'
    },
    SORT_COLS: {
      NAME: 'name',
      DESCRIPTION: 'description',
      SAMPLE_RATE: 'sampleRate'
    },
    bus: new mitt(),
    selectSignals: false,
    isMobile: DeviceUtils.isMobile()
  }),
  computed: {
    sortingNameSign() {
      if (this.sortBy === this.SORT_COLS.NAME) {
        return this.getSortDirSign()
      }
      return ''
    },
    sortingDescriptionSign() {
      if (this.sortBy === this.SORT_COLS.DESCRIPTION) {
        return this.getSortDirSign()
      }
      return ''
    },
    sortingSampleRateSign() {
      if (this.sortBy === this.SORT_COLS.SAMPLE_RATE) {
        return this.getSortDirSign()
      }
      return ''
    },
    selectedSignals() {
      return this.signals.filter(signal => signal.selected)
    },
    viewSignalsAvailable() {
      const selectedSignalsNumber = this.selectedSignals.length
      return selectedSignalsNumber > 0 && selectedSignalsNumber <= 5
    },
    transformSignalsAvailable() {
      return this.selectedSignals.length === 1
          || this.selectedSignals.length === 2 && SignalUtils.checkSignalsHaveSameValueGrid(this.selectedSignals)
    },
    selectedSignalsDataLoaded() {
      return this.selectedSignals.length === this.selectedSignals.filter(signal => signal.data).length
    },
    folders() {
      return dataStore().folders
    }
  },
  watch: {
    page() {
      this.setUrlParams()
      this.loadSignals()
    },
    formValues() {
      this.actionWithTimeout(() => {
        if (!this.validatePageSize()) {
          return
        }
        this.page = 1
        this.saveFormValues()
        this.setUrlParams()
        this.loadSignals()
      })
    },
    sortBy() {
      this.page = 1
      this.setUrlParams()
      this.loadSignals()
    },
    sortDir() {
      this.page = 1
      this.setUrlParams()
      this.loadSignals()
    },
    selectedSignals() {
      this.selectSignals = this.signals.length && this.selectedSignals.length === this.signals.length
    },
    selectSignals(newValue) {
      this.signals.forEach(signal => signal.selected = newValue)
    }
  },
  mounted() {
    this.restoreFormValues()
    this.readUrlParams()
    this.setUrlParams()
    this.loadSignals()
    this.loadSampleRates()
    FolderRequests.loadFolders()
  },
  methods: {
    validatePageSize() {
      this.clearValidation()
      const validationMsg = this.getNumberValidationMsg('pageSize')
      if (validationMsg) {
        this.pushValidationMsg('pageSize', validationMsg)
      }
      return !validationMsg
    },
    async loadSignals() {
      const filter = this.makeSignalFilter()
      const filterJson = JSON.stringify(filter)
      if (this.loadingOverlay || this.signalsLastLoadFilter === filterJson || !this.validatePageSize()) {
        return
      }
      await this.loadWithOverlay(async () => {
        const response = await this.getApiProvider().postJson('/api/signals/filter', filter)
        if (response.ok) {
          this.signals = response.data.data
          this.elements = response.data.elements
          this.pages = response.data.pages
          this.signalsEmpty = this.elements === 0
          this.signalsLastLoadFilter = filterJson
        } else {
          this.showErrorsFromResponse(response, this._t('loadSignalsError'))
        }
      })
    },
    async loadSignalData(signal) {
      if (signal.data) {
        return
      }
      let response = await this.getApiProvider().get(`/api/signals/${signal.id}/data`)
      if (response.ok) {
        signal.data = response.data
        SignalUtils.calculateSignalParams(signal)
      }
    },
    async loadSampleRates() {
      const response = await this.getApiProvider().get('/api/signals/sample-rates')
      if (response.ok) {
        this.sampleRates = response.data.map(value => ({value, reduced: this.reduceFractionDigitsByValue(value)}))
      }
    },
    setSortingName() {
      this.setSorting(this.SORT_COLS.NAME)
    },
    setSortingDescription() {
      this.setSorting(this.SORT_COLS.DESCRIPTION)
    },
    setSortingSampleRate() {
      this.setSorting(this.SORT_COLS.SAMPLE_RATE)
    },
    setSorting(col) {
      if (this.sortBy !== col) {
        this.sortBy = col
        this.sortDir = this.SORT_DIRS.DESC
      } else {
        if (this.sortDir === this.SORT_DIRS.DESC) {
          this.sortDir = this.SORT_DIRS.ASC
        } else {
          this.sortDir = ''
          this.sortBy = ''
        }
      }
    },
    getSortDirSign() {
      if (this.sortDir === this.SORT_DIRS.ASC) {
        return '(^)'
      }
      if (this.sortDir === this.SORT_DIRS.DESC) {
        return '(v)'
      }
      return ''
    },
    readUrlParams() {
      const route = useRoute()
      const page = ref(route.query.page)
      if (page.value) {
        this.page = parseInt(page.value)
      }
      const size = ref(route.query.size)
      if (size.value) {
        this.formValue('pageSize', parseInt(size.value))
      }
      const search = ref(route.query.search)
      if (search.value) {
        this.formValue('search', search.value)
      }
      const folderIds = ref(route.query.folderIds)
      if (folderIds.value) {
        this.formValue('folderIds', this.readNumberArrFromUrlParam(folderIds.value, parseInt))
      }
      const sampleRates = ref(route.query.sampleRates)
      if (sampleRates.value) {
        this.formValue('sampleRates', this.readNumberArrFromUrlParam(sampleRates.value, parseFloat))
      }
      const sortBy = ref(route.query.sortBy)
      if (sortBy.value) {
        this.sortBy = sortBy.value
      }
      const sortDir = ref(route.query.sortDir)
      if (sortDir.value) {
        this.sortDir = sortDir.value
      }
    },
    readNumberArrFromUrlParam(str, parseFunc) {
      return str.split(',').map(v => parseFunc(v)).filter(number => !isNaN(number))
    },
    setUrlParams() {
      let url = `/signal-manager${this.makeUrlParams()}`
      useRouter().push(url)
    },
    makeUrlParams() {
      let params = `?page=${this.page}&size=${this.formValue('pageSize')}`
      if (this.formValues.search) {
        params += `&search=${this.formValues.search}`
      }
      if (this.formValues.folderIds.length) {
        params += `&folderIds=${this.makeUrlParamFromArr(this.formValues.folderIds)}`
      }
      if (this.formValues.sampleRates.length) {
        params += `&sampleRates=${this.makeUrlParamFromArr(this.formValues.sampleRates)}`
      }
      if (this.sortBy) {
        params += `&sortBy=${this.sortBy}`
      }
      if (this.sortDir) {
        params += `&sortDir=${this.sortDir}`
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
    makeSignalFilter() {
      const filter = {
        page: this.page - 1,
        size: this.formValue('pageSize')
      }
      if (this.formValues.search) {
        filter.search = this.formValues.search
      }
      if (this.formValues.folderIds.length) {
        filter.folderIds = this.formValues.folderIds
      }
      if (this.formValues.sampleRates.length) {
        filter.folderIds = this.formValues.sampleRates
      }
      if (this.sortBy) {
        filter.sortBy = this.sortBy
      }
      if (this.sortDir) {
        filter.sortDir = this.sortDir
      }
      return filter
    },
    openSignal(signal) {
      useRouter().push(`/signal/${signal.id}?history=0`)
    },
    async playOrStopSignal(signal) {
      if (this.isSignalPlayed(signal)) {
        SignalPlayer.stop()
        this.playedSignal = null
      } else {
        this.playedSignal = signal
        await SignalPlayer.setSignal(signal).play(() => {
          if (this.isSignalPlayed(signal)) {
            this.playedSignal = null
          }
        })
      }
    },
    isSignalPlayed(signal) {
      return this.playedSignal === signal
    },
    askConfirmDeleteSignal(signal) {
      this.askConfirm({
        text: this._t('confirmDeleteSignal', {name: signal.name}),
        ok: () => {
          this.deleteSignal(signal)
        }
      })
    },
    async deleteSignal(signal) {
      let response = await this.deleteSignalRequest(signal)
      if (response.ok) {
        this.signalsLastLoadFilter = ''
        await this.loadSignals()
      }
    },
    askConfirmDeleteSelectedSignals() {
      this.askConfirm({
        text: this._t('confirmDeleteSignals', {length: this.selectedSignals.length}),
        ok: () => {
          this.deleteSelectedSignals()
        }
      })
    },
    async deleteSelectedSignals() {
      let promiseArr = []
      this.selectedSignals.forEach(signal => promiseArr.push(this.deleteSignalRequest(signal)))
      await Promise.all(promiseArr)
      this.signalsLastLoadFilter = ''
      await this.loadSignals()
    },
    deleteSignalRequest(signal) {
      return this.getApiProvider().del('/api/signals/' + signal.id)
    },
    loadSelectedSignalsData() {
      this.loadWithOverlay(async () => {
        let promiseArr = []
        this.selectedSignals.forEach(signal => promiseArr.push(this.loadSignalData(signal)))
        await Promise.all(promiseArr)
      })
    },
    onSignalFoldersChanged() {
      if (this.formValues.folderIds.length) {
        this.signalsLastLoadFilter = ''
        this.loadSignals()
      }
    },
    restrictSignalNameLength(signal) {
      return StringUtils.restrictLength(signal.name, 22)
    },
    reduceFractionDigitsByValue(value) {
      return NumberUtils.reduceFractionDigitsByValue(value)
    }
  },
}
</script>

<style scoped>
.btn-with-icon {
  width: 36px;
  min-width: 36px;
}
.v-col {
  padding-bottom: 0;
  padding-top: 0;
}
</style>