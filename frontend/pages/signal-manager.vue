<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%">
        <v-card-text>
          <fixed-width-wrapper>
            <p>
              <span>Total: {{ pages }} pages, </span>
              <span>{{ elements }} entries</span>
            </p>
            <v-form>
              <v-row>
                <v-col>
                  <v-text-field
                      v-model="size"
                      :rules="sizeRules"
                      type="number"
                      step="1"
                      label="Page size"
                      required/>
                </v-col>
                <v-col>
                  <search-field
                      :init-search-value="filter"
                      @search="search => this.setFilter(search)"/>
                </v-col>
              </v-row>
            </v-form>
          </fixed-width-wrapper>
          <v-table>
            <thead>
            <tr>
              <th class="text-left">
<!--                todo костыль-->
                <div style="height: 58px">
                  <v-checkbox v-model="selectSignals"/>
                </div>
              </th>
              <th @click="setSortingName" class="text-left">
                Name {{sortingNameSign}}
              </th>
              <th @click="setSortingDescription" class="text-left">
                Description {{sortingDescriptionSign}}
              </th>
              <th class="text-left"></th>
              <th class="text-left"></th>
              <th v-if="!isMobile" class="text-left"></th>
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
              <td class="text-right">
                <v-icon color="primary" @click.stop="openSignal(signal)">
                  {{ mdi.mdiFileEdit }}
                </v-icon>
              </td>
              <td class="text-right">
                <v-icon @click.stop="saveSignalToWavFile(signal)">
                  {{ mdi.mdiFileImport }}
                </v-icon>
              </td>
              <td v-if="!isMobile" class="text-right">
                <v-icon @click.stop="playOrStopSignal(signal)">
                  {{ isSignalPlayed(signal) ? mdi.mdiStop : mdi.mdiPlay }}
                </v-icon>
              </td>
              <td class="text-right">
                <v-icon color="error" @click.stop="askConfirmDeleteSignal(signal)">
                  {{ mdi.mdiDelete }}
                </v-icon>
              </td>
            </tr>
            </tbody>
          </v-table>
          <fixed-width-wrapper>
            <v-pagination
                v-model="page"
                :length="pages"/>
            <div class="d-flex justify-center flex-wrap">
              <p class="mt-5">
                Actions with selected signals
              </p>
            </div>
            <div class="d-flex justify-center flex-wrap">
              <select-transformer-dialog :bus="bus" :double="true" :disabled="!transformSignalsAvailable"/>
              <v-dialog
                  v-model="viewDialog"
                  max-height="800px"
              >
                <template v-slot:activator="{ props }">
                  <v-btn
                      :disabled="!viewSignalsAvailable"
                      color="secondary"
                      v-bind="props"
                  >
                    View
                  </v-btn>
                </template>
                <v-card width="100%">
                  <v-toolbar>
                    <v-toolbar-title>View signals</v-toolbar-title>
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
                Delete
                <v-icon>{{ mdi.mdiDelete }}</v-icon>
              </v-btn>
            </div>
          </fixed-width-wrapper>
        </v-card-text>
      </v-card>
    </div>
    <transformer-double-dialog :bus="bus" :signals="transformSignalsAvailable && selectedSignalsDataLoaded ? selectedSignals : []"/>
    <confirm-dialog :opened="confirm.opened" :text="confirm.text" ok-color="error"
                    @ok="confirm.ok" @cancel="confirm.cancel"/>
    <loading-overlay :show="loadingOverlay"/>
  </NuxtLayout>
</template>

<script>
import {mdiDelete} from "@mdi/js";
import {mdiPlay} from "@mdi/js";
import {mdiStop} from "@mdi/js";
import {mdiFileImport} from "@mdi/js";
import {mdiClose} from "@mdi/js";
import {mdiFileEdit} from "@mdi/js";
import SignalPlayer from "../audio/signal-player";
import FileUtils from "../utils/file-utils";
import PageBase from "../components/page-base";
import ChartDrawer from "../components/chart-drawer";
import mitt from "mitt";
import SelectTransformerDialog from "../components/select-transformer-dialog";
import TransformerDoubleDialog from "../components/transformer-double-dialog";
import SignalUtils from "../utils/signal-utils";
import DeviceUtils from "../utils/device-utils";
import FixedWidthWrapper from "../components/fixed-width-wrapper";

export default {
  name: "signal-manager",
  components: {
    ChartDrawer, SelectTransformerDialog,
    TransformerDoubleDialog, FixedWidthWrapper
  },
  extends: PageBase,
  data: () => ({
    viewDialog: false,
    signals: [],
    filter: '',
    elements: 0,
    pages: 0,
    page: 1,
    size: 10,
    sortBy: '',
    sortDir: '',
    playedSignal: null,
    mdi: {
      mdiDelete,
      mdiPlay,
      mdiStop,
      mdiFileImport,
      mdiClose,
      mdiFileEdit
    },
    sizeRules: [
      v => !!v || 'This field is required',
      v => ( v && v <= 25 && v >= 5 ) || 'Size should be between 5 and 25'
    ],
    SORT_DIRS: {
      DESC: 'desc',
      ASC: 'asc'
    },
    SORT_COLS: {
      NAME: 'name',
      DESCRIPTION: 'description'
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
    selectedSignals() {
      return this.signals.filter(signal => signal.selected)
    },
    viewSignalsAvailable() {
      const selectedSignalsNumber = this.selectedSignals.length
      return selectedSignalsNumber > 0 && selectedSignalsNumber <= 5
    },
    transformSignalsAvailable() {
      return this.selectedSignals.length === 2 && SignalUtils.checkSignalsHaveSameValueGrid(this.selectedSignals)
    },
    selectedSignalsDataLoaded() {
      return this.selectedSignals.length === this.selectedSignals.filter(signal => signal.data).length
    }
  },
  watch: {
    filter() {
      this.setUrlParams()
      this.loadSignals()
    },
    page() {
      this.setUrlParams()
      this.loadSignals()
    },
    size(newValue) {
      if (!newValue || newValue < 5 || newValue > 25) {
        return
      }
      this.setUrlParams()
      this.loadSignals()
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
      // todo load only if needed
      this.selectedSignals.forEach(signal => this.loadSignalData(signal))
    },
    selectSignals(newValue) {
      this.signals.forEach(signal => signal.selected = newValue)
    }
  },
  mounted() {
    if (!this.readUrlParams()) {
      this.setUrlParams()
    }
    this.loadSignals()
    this.bus.on('transformerSelected', () => this.selectedSignals.forEach(signal => this.loadSignalData(signal)))
  },
  beforeUnmount() {
    this.bus.off('transformerSelected')
  },
  methods: {
    async loadSignals() {
      if (this.loadingOverlay) {
        return
      }
      await this.loadWithOverlay(async () => {
        const response = await this.getApiProvider().get(`/api/signals${this.makeUrlParams(true)}`)
        if (response.ok) {
          this.signals = response.data.data
          this.elements = response.data.elements
          this.pages = response.data.pages
        } else {
          this.showErrorsFromResponse(response, 'Error loading signals')
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
    setFilter(filter) {
      this.filter = filter
      this.page = 1
    },
    setSortingName() {
      this.setSorting(this.SORT_COLS.NAME)
    },
    setSortingDescription() {
      this.setSorting(this.SORT_COLS.DESCRIPTION)
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
        this.size = parseInt(size.value)
      }
      const filter = ref(route.query.filter)
      if (filter.value) {
        this.filter = filter.value
      }
      const sortBy = ref(route.query.sortBy)
      if (sortBy.value) {
        this.sortBy = sortBy.value
      }
      const sortDir = ref(route.query.sortDir)
      if (sortDir.value) {
        this.sortDir = sortDir.value
      }
      return page.value || size.value || filter.value || sortBy.value || sortDir.value
    },
    setUrlParams() {
      let url = `/signal-manager${this.makeUrlParams()}`
      useRouter().push(url)
    },
    makeUrlParams(pageMinus1) {
      let params = `?page=${pageMinus1 ? this.page - 1 : this.page}&size=${this.size}`
      if (this.filter) {
        params += `&filter=${this.filter}`
      }
      if (this.sortBy) {
        params += `&sortBy=${this.sortBy}`
      }
      if (this.sortDir) {
        params += `&sortDir=${this.sortDir}`
      }
      return params
    },
    openSignal(signal) {
      useRouter().push('/signal/' + signal.id)
    },
    saveSignalToWavFile(signal) {
      FileUtils.saveSignalToWavFile(signal)
    },
    async playOrStopSignal(signal) {
      if (this.isSignalPlayed(signal)) {
        SignalPlayer.stop()
        this.playedSignal = null
      } else {
        this.playedSignal = signal
        await SignalPlayer.setSignalId(signal.id).play(() => {
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
        text: `Are you sure to delete ${signal.name}?`,
        ok: () => {
          this.deleteSignal(signal)
        }
      })
    },
    async deleteSignal(signal) {
      let response = await this.deleteSignalRequest(signal)
      if (response.ok) {
        await this.loadSignals()
      }
    },
    askConfirmDeleteSelectedSignals() {
      this.askConfirm({
        text: `Are you sure to delete selected ${this.selectedSignals.length} signals?`,
        ok: () => {
          this.deleteSelectedSignals()
        }
      })
    },
    async deleteSelectedSignals() {
      let promiseArr = []
      this.selectedSignals.forEach(signal => promiseArr.push(this.deleteSignalRequest(signal)))
      await Promise.all(promiseArr)
      await this.loadSignals()
    },
    deleteSignalRequest(signal) {
      return this.getApiProvider().del('/api/signals/' + signal.id)
    }
  },
}
</script>