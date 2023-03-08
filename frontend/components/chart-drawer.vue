<template>
  <Chart :options="chartOptions"></Chart>
</template>

<script>
import SignalUtils from "../utils/signal-utils";
import StringUtils from "../utils/string-utils";
import {Chart} from 'highcharts-vue'
import {dataStore} from "../stores/data-store";

export default {
  name: "chart-drawer",
  components: {
    Chart
  },
  props: {
    signals: {
      type: Array,
      required: true
    }
  },
  data: () => ({
    darkMode: dataStore().getDarkMode,
    chartOptions: {
      chart: {
        zoomType: 'xy',
        resetZoomButton: {}
      },
      title: {
        text: ''
      },
      series: [],
      xAxis: {
        categories: []
      },
      yAxis: {
        title: {
          text: null
        }
      },
      colors: [
        '#4CAF50',
        '#BB86FC',
        '#03DAC6',
        '#CF6679',
        '#f5c506',
      ]
    }
  }),
  computed: {
    chartTitle() {
      return this.signals.length > 1
          ? `Selected ${this.signals.length} signals`
          : StringUtils.restrictLength(this.signals[0].name, 50)
    },
    bgColor() {
      return this.darkMode ? '#1F2227' : '#f1f1f1'
    },
    textColor() {
      return this.darkMode ? '#f1f1f1' : '#1F2227'
    },
  },
  watch: {
    signals() {
      this.makeChartParams()
    },
  },
  mounted() {
    this.makeChartParams()
    this.makeChartStyles()
    dataStore().$subscribe((mutation, state) => {
      if (this.darkMode !== state.darkMode) {
        this.darkMode = state.darkMode
        this.makeChartStyles()
      }
    })
  },
  methods: {
    makeChartParams() {
      if (!this.signals || !this.signals.length) {
        return
      }
      for (let signal of this.signals) {
        if (!signal.data) {
          return
        }
      }
      // todo executes 2 times console.log('makeChartParams')
      let commonGrid = SignalUtils.makeCommonSignalsValueGrid(this.signals)
      let categories = []
      for (let x of commonGrid) {
        categories.push(x.toFixed(2))
      }
      let series = []
      for (let signal of this.signals) {
        let data = []
        for (let x of commonGrid) {
          data.push(SignalUtils.getSignalValue(signal, x))
        }
        series.push({
          name: StringUtils.restrictLength(signal.name, 50),
          data
        })
      }
      this.chartOptions.xAxis.categories = categories
      this.chartOptions.series = series
      this.chartOptions.title.text = this.chartTitle
    },
    makeChartStyles() {
      // todo try to do this in data
      let textColor = {color: this.textColor}
      this.chartOptions.title.style = textColor
      this.chartOptions.xAxis.labels = {style: textColor}
      this.chartOptions.yAxis.labels = {style: textColor}
      this.chartOptions.legend = {itemStyle: textColor}
      this.chartOptions.tooltip = {
        backgroundColor: this.bgColor,
        style: textColor
      }
      this.chartOptions.chart.resetZoomButton.theme = {style: textColor}
    }
  },
}
</script>

<style>
.highcharts-background {
  fill: none;
}
.highcharts-plot-background {
  fill: v-bind(bgColor);
}
.highcharts-button-box {
  fill: v-bind(bgColor);
}
</style>
