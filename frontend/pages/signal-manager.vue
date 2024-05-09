<template>
  <card-with-layout full-width :confirm="confirm" :loading-overlay="loadingOverlay">
    <template #default>
      <v-card-text>
        <fixed-width-wrapper>
          <p>
            {{ _tc('pagination.total', {pages, elements}) }}
          </p>
          <v-expansion-panels v-model="uiParams.openedPanels" class="mb-1">
            <v-expansion-panel value="loadParams">
              <v-expansion-panel-title>
                {{ _tc('pagination.loadParams') }}
                <btn-with-tooltip
                    :disabled="filterIsEmpty"
                    tooltip="clear"
                    @click="clearFilter"
                >
                  <v-icon>
                    {{ mdi.mdiFilterOff }}
                  </v-icon>
                </btn-with-tooltip>
              </v-expansion-panel-title>
              <v-expansion-panel-text>
                <v-form>
                  <div class="d-flex justify-center flex-wrap">
                    <number-input
                        class="param-input"
                        field="pageSize"
                        :label="_tc('pagination.pageSize')"
                        :field-obj="form.pageSize"
                        @update="v => form.pageSize.value = v"/>
                    <text-input
                        class="param-input"
                        field="search"
                        :field-obj="form.search"
                        @update="v => form.search.value = v"/>
                    <v-select
                        class="param-input"
                        v-model="form.sampleRates.value"
                        item-title="reduced"
                        item-value="value"
                        :items="sampleRates"
                        :label="_t('sampleRates')"
                        multiple
                    >
                      <template v-slot:no-data>
                        {{ _tc('messages.noData') }}
                      </template>
                    </v-select>
                    <v-select
                        class="param-input"
                        v-model="form.folderIds.value"
                        item-title="name"
                        item-value="id"
                        :items="folders"
                        :label="_t('folders')"
                        multiple
                    >
                      <template v-slot:append-inner>
                        <btn-with-tooltip
                            :tooltip="folders.length ? 'edit' : 'create'"
                            :small="false"
                            @click="openFolderManager"
                        >
                          <v-icon style="width: 22px; height: 22px;">
                            {{ folders.length ? mdi.mdiFolderEdit : mdi.mdiFolderPlus }}
                          </v-icon>
                        </btn-with-tooltip>
                      </template>
                      <template v-slot:no-data>
                        {{ _tc('messages.noFolders') }}
                      </template>
                    </v-select>
                  </div>
                </v-form>
              </v-expansion-panel-text>
            </v-expansion-panel>
          </v-expansion-panels>
        </fixed-width-wrapper>
        <fixed-width-wrapper v-if="signalsEmpty && !loadingOverlay">
          <h3 v-if="filterIsEmpty" style="text-align: center;">{{ _t('youHaveNoStoredSignals') }}.
            <a href="/signal-generator">{{ _t('generate') }}</a> {{ _t('or') }}
            <a href="/signal-recorder">{{ _t('record') }}</a> {{ _t('newSignalsToStartWorking') }}.</h3>
          <h3 v-else style="text-align: center;">{{ _tc('messages.nothingIsFound') }}</h3>
        </fixed-width-wrapper>
        <div v-else>
          <table-or-list
              data-name="signals"
              caption="name"
              :select="true"
              :items="signals"
              :columns="tableOrListConfig.columns"
              :buttons="tableOrListConfig.buttons"
              :reserved-height="reservedHeight"
              :sort-cols="['name', 'description', 'sampleRate']"
              :sort-prop="this.sort"
              @click="onTableButtonClick"
              @change="onTableChange"
              @select="onTableSelect"
              @sort="onTableSort"/>
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
                v-model="processDialog"
                max-width="800px"
                max-height="500px"
            >
              <template v-slot:activator="{ props }">
                <v-btn
                    :disabled="!processSignalsAvailable"
                    color="primary"
                    v-bind="props"
                    @click="loadSelectedSignalsData"
                >
                  {{ _tc('buttons.process') }}
                </v-btn>
              </template>
              <select-processor :bus="bus" :double="selectedSignals.length === 2" @close="processDialog = false"/>
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
                <toolbar-with-close-btn :title="_t('viewSignals')" @close="viewDialog = false"/>
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
    </template>
    <template #dialogs>
      <double-processor-dialog
          v-if="selectedSignals.length === 2"
          :bus="bus"
          :signals="processSignalsAvailable && selectedSignalsDataLoaded ? selectedSignals : []"/>
      <single-processor-dialog
          v-if="selectedSignals.length === 1"
          :bus="bus"
          :signal="processSignalsAvailable && selectedSignalsDataLoaded ? selectedSignals[0] : null"/>
    </template>
  </card-with-layout>
</template>

<script>
import {mdiDelete, mdiPlay, mdiStop, mdiFileEdit, mdiFolder, mdiFolderEdit, mdiFolderPlus, mdiFilterOff} from "@mdi/js";
import SignalPlayer from "../audio/signal-player";
import PageBase from "../components/base/page-base.vue";
import mitt from "mitt";
import SignalUtils from "../utils/signal-utils";
import formNumberValues from "../mixins/form-number-values";
import formValidation from "../mixins/form-validation";
import actionWithTimeout from "../mixins/action-with-timeout";
import formValuesSaving from "../mixins/form-values-saving";
import DeviceUtils from "../utils/device-utils";
import FolderRequests from "~/api/folder-requests";
import NumberUtils from "~/utils/number-utils";
import uiParamsSaving from "~/mixins/ui-params-saving";
import {SignalRequests} from "~/api/signal-requests";
import CardWithLayout from "~/components/common/card-with-layout.vue";
import FixedWidthWrapper from "~/components/common/fixed-width-wrapper.vue";
import BtnWithTooltip from "~/components/common/btn-with-tooltip.vue";
import NumberInput from "~/components/common/number-input.vue";
import TextInput from "~/components/common/text-input.vue";
import TableOrList from "~/components/common/table-or-list.vue";
import SelectProcessor from "~/components/processor-selection/select-processor.vue";
import ToolbarWithCloseBtn from "~/components/common/toolbar-with-close-btn.vue";
import DoubleProcessorDialog from "~/components/processor-selection/double-processor-dialog.vue";
import SingleProcessorDialog from "~/components/processor-selection/single-processor-dialog.vue";
import paginationUrlParams, {PaginationParamLocations} from "~/mixins/pagination-url-params";
import {userStore} from "~/stores/user-store";
import maxPageSizeLoading from "~/mixins/max-page-size-loading";

export default {
  name: "signal-manager",
  components: {
    SingleProcessorDialog, DoubleProcessorDialog, ToolbarWithCloseBtn,
    SelectProcessor, TableOrList, TextInput, NumberInput, BtnWithTooltip,
    FixedWidthWrapper, CardWithLayout
  },
  extends: PageBase,
  mixins: [
    formNumberValues, formValidation, formValuesSaving, actionWithTimeout, uiParamsSaving,
    paginationUrlParams, maxPageSizeLoading
  ],
  data: () => ({
    additionalPaginationParamsConfig: [
      {
        name: 'folderIds',
        location: PaginationParamLocations.FORM,
        readFunc: parseInt,
        isArray: true
      },
      {
        name: 'sampleRates',
        location: PaginationParamLocations.FORM,
        readFunc: parseFloat,
        isArray: true
      }
    ],
    mounted: false,
    viewDialog: false,
    processDialog: false,
    sampleRates: [],
    signals: [],
    selectedIds: [],
    signalsLastLoadFilter: '',
    signalsEmpty: false,
    elements: 0,
    pages: 0,
    page: 1,
    sort: {
      by: '',
      dir: ''
    },
    playedSignal: null,
    mdi: {
      mdiDelete,
      mdiPlay,
      mdiStop,
      mdiFileEdit,
      mdiFolder,
      mdiFolderEdit,
      mdiFolderPlus,
      mdiFilterOff
    },
    form: {
      pageSize: {
        value: 10,
        params: {
          min: 5,
          step: 1
        }
      },
      search: {value: ''},
      sampleRates: {value: []},
      folderIds: {value: []}
    },
    bus: new mitt(),
    isMobile: DeviceUtils.isMobile(),
    uiParams: {
      openedPanels: []
    }
  }),
  computed: {
    tableOrListConfig() {
      const buttons = [
        {
          name: 'open',
          icon: mdiFileEdit,
          color: 'primary'
        },
        {
          name: 'play',
          icon: signal => this.isSignalPlayed(signal) ? mdiStop : mdiPlay,
          tooltip: signal => this.isSignalPlayed(signal) ? 'stop' : 'play',
          condition: signal => signal.sampleRate >= 3000
        }
      ]
      if (this.folders.length) {
        buttons.push({
          name: 'folders',
          icon: mdiFolder,
          component: 'signal-folders-menu'
        })
      }
      buttons.push({
        name: 'delete',
        icon: mdiDelete,
        color: 'error'
      })
      return {
        columns: ['description', {
          name: 'sampleRate',
          formatter: value => this.reduceFractionDigitsByValue(value)
        }],
        buttons
      }
    },
    reservedHeight() {
      return this.uiParams.openedPanels && this.uiParams.openedPanels.includes('loadParams') ? 486 : 330
    },
    selectedSignals() {
      return this.signals.filter(signal => this.selectedIds.includes(signal.id))
    },
    viewSignalsAvailable() {
      const selectedSignalsNumber = this.selectedSignals.length
      return selectedSignalsNumber > 0 && selectedSignalsNumber <= 5
    },
    processSignalsAvailable() {
      return this.selectedSignals.length === 1
          || this.selectedSignals.length === 2 && SignalUtils.checkSignalsHaveSameValueGrid(this.selectedSignals)
    },
    selectedSignalsDataLoaded() {
      return this.selectedSignals.length === this.selectedSignals.filter(signal => signal.data).length
    },
    folders() {
      return userStore().folders
    },
    filterIsEmpty() {
      return !this.formValues.search && !this.formValues.sampleRates.length && !this.formValues.folderIds.length
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
  },
  mounted() {
    this.mounted = true
    SignalRequests.setApiProvider(this.getApiProvider())
    this.restoreFormValues()
    this.restoreUiParams()
    this.readUrlParams()
    this.setUrlParams()
    this.setMaxPageSize()
    this.loadSampleRates()
    this.loadFolders()
    this.actionWithTimeout(() => this.loadSignals())
  },
  beforeUnmount() {
    this.mounted = false
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
      if (!this.mounted || this.loadingOverlay
          || this.signalsLastLoadFilter === filterJson
          || !this.validatePageSize()) {
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
      try {
        signal.data = await SignalRequests.loadData(signal.id)
        SignalUtils.calculateSignalParams(signal)
      } catch (e) {
        console.log(e)
      }
    },
    async loadSampleRates() {
      const response = await this.getApiProvider().get('/api/signals/sample-rates')
      if (response.ok) {
        const loadedSampleRateValues = response.data
        this.sampleRates = loadedSampleRateValues.map(value =>
            ({value, reduced: this.reduceFractionDigitsByValue(value)}))
        this.formValue('sampleRates', this.formValues.sampleRates.filter(sampleRate =>
            loadedSampleRateValues.includes(sampleRate)))
      }
    },
    async loadFolders() {
      await FolderRequests.loadFolders()
      const loadedFolderIds = this.folders.map(folder => folder.id)
      this.formValue('folderIds', this.formValues.folderIds.filter(folderId =>
          loadedFolderIds.includes(folderId)))
    },
    setUrlParams() {
      if (!this.mounted) {
        return
      }
      useRouter().push(`/signal-manager${this.makeUrlParams()}`)
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
        filter.sampleRates = this.formValues.sampleRates
      }
      if (this.sort.by) {
        filter.sortBy = this.sort.by
      }
      if (this.sort.dir) {
        filter.sortDir = this.sort.dir
      }
      return filter
    },
    clearFilter() {
      this.formValue('search', '')
      this.formValue('folderIds', [])
      this.formValue('sampleRates', [])
    },
    openSignal(signal) {
      signalStore().clearHistoryForSignal(signal.id)
      useRouter().push(`/signal/${signal.id}?history=0`)
    },
    openFolderManager() {
      useRouter().push('/folder-manager')
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
    onTableButtonClick({button, item}) {
      if (button === 'open') {
        this.openSignal(item)
      } else if (button === 'play') {
        this.playOrStopSignal(item)
      } else if (button === 'delete') {
        this.askConfirmDeleteSignal(item)
      }
    },
    onTableChange(component) {
      if (component === 'signal-folders-menu') {
        this.onSignalFoldersChanged()
      }
    },
    onTableSelect(selectedIds) {
      this.selectedIds = selectedIds
    },
    onTableSort(sort) {
      this.sort = sort
      this.page = 1
      this.setUrlParams()
      this.loadSignals()
    },
    reduceFractionDigitsByValue(value) {
      return NumberUtils.reduceFractionDigitsByValue(value)
    }
  },
}
</script>

<style>
.v-expansion-panel-text__wrapper {
  padding: 0 0 0 0;
}
</style>

<style scoped>
.param-input {
  min-width: 300px;
  max-width: 800px;
  margin-left: 5px;
  margin-right: 5px;
}
</style>
