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
    },
    minimal: {
      type: Boolean,
      default: false
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
    bgColor() {
      return this.darkMode ? '#1F2227' : '#f1f1f1'
    },
    textColor() {
      return this.darkMode ? '#f1f1f1' : '#1F2227'
    },
    legendEnabled() {
      return this.signals.length > 1
    }
  },
  watch: {
    signals() {
      this.makeChartParams()
      this.makeChartStyles()
    },
  },
  mounted() {
    if (this.minimal) {
      this.chartOptions.chart.height = 150
    }
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
      let fractionDigits = this.getFractionDigitsByStep(this.signals[0].params.step)
      for (let x of commonGrid) {
        categories.push(x.toFixed(fractionDigits))
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
    },
    getFractionDigitsByStep(step) {
      if (step < 0.0001) {
        return 4
      } else if (step < 0.001) {
        return 3
      } else if (step < 0.01) {
        return 2
      } else if (step < 0.1) {
        return 1
      } else {
        return 0
      }
    },
    makeChartStyles() {
      // todo try to do this in data
      let textColor = {color: this.textColor}
      this.chartOptions.title.style = textColor
      this.chartOptions.xAxis.labels = {style: textColor}
      this.chartOptions.yAxis.labels = {style: textColor}
      this.chartOptions.legend = {
        itemStyle: textColor,
        enabled: this.legendEnabled
      }
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
