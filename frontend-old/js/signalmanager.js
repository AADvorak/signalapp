/**
 * @typedef {Object} Signal
 * @property {number} [id]
 * @property {string} name
 * @property {string} description
 * @property {number} maxAbsY
 * @property {number} sampleRate
 * @property {number} xMin
 * @property {number[]} [data]
 * @property {SignalParams} params
 */

/**
 * @typedef {Object} SignalParams
 * @property {number} xMin
 * @property {number} xMax
 * @property {number} step
 * @property {number} length
 */

SignalManager = {

  init() {
    this.selectElements()
    this.createTable()
    this.initEvents()
    this.fillTable().then()
  },

  selectElements() {
    this.ui = {}
    this.ui.addSignalsBtn = $('#SignalManagerAdd')
    this.ui.viewSignalsBtn = $('#SignalManagerView')
    this.ui.correlateSignalsBtn = $('#SignalManagerCorrelate')
  },

  initEvents() {
    this.ui.addSignalsBtn.on('click', () => {
      this.sendSelectedSignals('Adder').then()
    })
    this.ui.viewSignalsBtn.on('click', () => {
      this.sendSelectedSignals('SignalViewer').then()
    })
    this.ui.correlateSignalsBtn.on('click', () => {
      this.sendSelectedSignals('Correlator').then()
    })
    EVENTS.USER_SIGNED_OUT.subscribe(this, 'clearTable')
    EVENTS.USER_SIGNED_IN.subscribe(this, 'fillTable')
  },

  createTable() {
    this.ui.table = new Table({
      id: 'SignalManagerTable',
      container: $('#SignalManagerMain'),
      selectors: true,
      fields: {
        name: {
          name: 'Name',
          format: (value, object) => {
            let formattedValue = StringManager.restrictLength(value, 50)
            return `<a href="#">${formattedValue}</a>`
          },
          click: signal => this.sendSignalToCable(signal).then()
        },
        description: {
          name: 'Description',
          format: value => {
            let formattedValue = StringManager.onlyBeforeNewLine(value)
            formattedValue = StringManager.restrictLength(formattedValue, 200)
            return formattedValue
          }
        },
        wave: {
          format: () => `<a href="#"><i title="Download wave file" class="fa fa-file-audio-o" aria-hidden="true"></i></a>`,
          click: signal => FileManager.saveSignalToWavFile(signal)
        },
        play: {
          format: () => `<a href="#"><i title="Play signal" class="fa fa-play" aria-hidden="true"></i></a>`,
          click: signal => SignalPlayer.setSignalId(signal.id).play().then()
        },
        stop: {
          format: () => `<a href="#"><i title="Stop playing" class="fa fa-stop" aria-hidden="true"></i></a>`,
          click: () => SignalPlayer.stop()
        },
        del: {
          format: () => `<a href="#"><i title="Delete signal" class="fa fa-trash" aria-hidden="true"></i></a>`,
          click: signal => this.deleteSignal(signal).then()
        },
      }
    })
  },

  clearTable() {
    this.ui.table.clearAll()
  },

  async fillTable() {
    let result = await ApiProvider.get('/api/signals')
    if (!result.ok) {
      if (result.status === 401) {
        Workspace.setWaitingForLoginModule('SignalManager')
      }
      return
    }
    this.ui.table.makeTableRows(result.data.data)
  },

  /**
   * @param {Signal} signal
   * @returns {Promise<void>}
   */
  async deleteSignal(signal) {
    let result = await ApiProvider.del('/api/signals/' + signal.id)
    if (!result.ok) {
      if (result.status === 401) {
        Workspace.setWaitingForLoginModule('SignalManager')
      }
      return
    }
    this.clearTable()
    await this.fillTable()
  },

  /**
   * @param {Signal} signal
   * @returns {Promise<SignalData>}
   */
  async getSignalData(signal) {
    let result = await ApiProvider.get('/api/signals/' + signal.id + '/data')
    if (result.ok) {
      signal.data = result.data
      SignalUtils.calculateSignalParams(signal)
    } else if (result.status === 401) {
      Workspace.setWaitingForLoginModule('SignalManager')
    }
  },

  /**
   * @param {Signal} signal
   */
  async sendSignalToCable(signal) {
    await this.getSignalData(signal)
    if (!signal.data) {
      return
    }
    EVENTS.INIT_SIGNAL_STACK.trigger('SignalManager')
    await Workspace.startModule({
      module: 'Cable',
      param: signal
    })
  },

  /**
   * @param {Signal[]} titles
   * @param {string} module
   */
  async sendSignals({signals, module}) {
    for (let signal of signals) {
      await this.getSignalData(signal)
    }
    SignalUtils.calculateSignalsParams(signals)
    if (!SignalUtils.checkSignalsValueGrid(signals)) {
      Workspace.showAlert('Selected signal X values must lie on the same grid')
      return
    }
    await Workspace.startModule({
      module,
      param: signals
    })
  },

  /**
   * @param {string} module
   */
  async sendSelectedSignals(module) {
    let signals = this.ui.table.getSelectedData()
    if (!signals.length) {
      Workspace.showAlert('No signals chosen')
      return
    }
    await this.sendSignals({signals, module})
  },

}
