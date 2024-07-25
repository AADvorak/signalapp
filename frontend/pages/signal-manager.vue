<template>
  <card-with-layout full-width :confirm="confirm" :loading-overlay="loadingOverlay">
    <template #default>
      <v-card-text>
        <fixed-width-wrapper>
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
        <data-viewer
            ref="dataViewer"
            data-name="signals"
            data-url="/api/signals/page"
            caption="name"
            select
            pagination
            :filtering-params-config="filteringParamsConfig"
            :filters="filters"
            :columns="dataViewerConfig.columns"
            :buttons="dataViewerConfig.buttons"
            :reserved-height="reservedHeight"
            :sort-cols="['name', 'description', 'sampleRate']"
            :bus="bus"
            @click="onDataViewerButtonClick"
            @select="onDataViewerSelect"
            @update:url-params="setUrlParams"
            @update:loading-overlay="value => loadingOverlay = value"
            @update:filters="onDataViewerUpdateFilters">
          <template #dataEmpty>
            <h3 v-if="filterIsEmpty" style="text-align: center;">{{ _t('youHaveNoStoredSignals') }}.
              <a href="/signal-generator">{{ _t('generate') }}</a> {{ _t('or') }}
              <a href="/signal-recorder">{{ _t('record') }}</a> {{ _t('newSignalsToStartWorking') }}.</h3>
            <h3 v-else style="text-align: center;">{{ _tc('messages.nothingIsFound') }}</h3>
          </template>
        </data-viewer>
        <fixed-width-wrapper>
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
import DataViewer from "~/components/common/data-viewer.vue";
import SelectProcessor from "~/components/processor-selection/select-processor.vue";
import ToolbarWithCloseBtn from "~/components/common/toolbar-with-close-btn.vue";
import DoubleProcessorDialog from "~/components/processor-selection/double-processor-dialog.vue";
import SingleProcessorDialog from "~/components/processor-selection/single-processor-dialog.vue";
import filtering from "~/mixins/filtering";
import {userStore} from "~/stores/user-store";
import {DataViewerEvents} from "~/dictionary/data-viewer-events";

export default {
  name: "signal-manager",
  components: {
    SingleProcessorDialog, DoubleProcessorDialog, ToolbarWithCloseBtn,
    SelectProcessor, DataViewer, TextInput, NumberInput, BtnWithTooltip,
    FixedWidthWrapper, CardWithLayout
  },
  extends: PageBase,
  mixins: [
    formNumberValues, formValidation, formValuesSaving, actionWithTimeout, uiParamsSaving, filtering
  ],
  data: () => ({
    additionalFilteringParamsConfig: [
      {
        name: 'folderIds',
        readFunc: parseInt,
        isArray: true,
        emptyValue: []
      },
      {
        name: 'sampleRates',
        readFunc: parseFloat,
        isArray: true,
        emptyValue: []
      }
    ],
    mounted: false,
    viewDialog: false,
    processDialog: false,
    sampleRates: [],
    signals: [],
    selectedIds: [],
    selectedSignals: [],
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
    dataViewerConfig() {
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
    }
  },
  mounted() {
    this.mounted = true
    SignalRequests.setApiProvider(this.getApiProvider())
    this.restoreFormValues()
    this.restoreUiParams()
    this.loadSampleRates()
    this.loadFolders()
    this.actionWithTimeout(this.loadDataPage)
    this.bus.on(DataViewerEvents.SIGNAL_FOLDERS_MENU_CLOSED_FOLDERS_CHANGED,
        this.onSignalFoldersMenuClosedFoldersChanged)
  },
  beforeUnmount() {
    this.mounted = false
    this.bus.off(DataViewerEvents.SIGNAL_FOLDERS_MENU_CLOSED_FOLDERS_CHANGED)
  },
  methods: {
    async loadDataPage() {
      await this.$refs.dataViewer?.loadDataPage()
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
    setUrlParams(urlParams) {
      if (!this.mounted) {
        return
      }
      useRouter().push(`/signal-manager${urlParams}`)
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
        this.dataPageLastRequest = ''
        await this.loadDataPage()
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
      this.dataPageLastRequest = ''
      await this.loadDataPage()
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
    onSignalFoldersMenuClosedFoldersChanged() {
      if (this.formValues.folderIds.length) {
        this.dataPageLastRequest = ''
        this.loadDataPage()
      }
    },
    onDataViewerButtonClick({button, item}) {
      if (button === 'open') {
        this.openSignal(item)
      } else if (button === 'play') {
        this.playOrStopSignal(item)
      } else if (button === 'delete') {
        this.askConfirmDeleteSignal(item)
      }
    },
    onDataViewerSelect(selectedSignals) {
      this.selectedSignals = selectedSignals
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
