LinearOscillator = {

  params: {
    frequency: undefined,
    damping: undefined,
  },

  EQUATIONS: [
    (input, variables, params) => variables[1],
    (input, variables, params) => input - 2 * params.damping * variables[1] -
        Math.pow(2 * Math.PI * params.frequency, 2) * variables[0]
  ],

  /**
   * @param {Signal} param
   */
  init(param) {
    this.signal = param
    this.selectElements()
    this.initEvents()
    this.readParamsFromCookie()
  },

  selectElements() {
    this.ui = {}
    this.ui.frequencyInp = $('#LinearOscillatorFrequency')
    this.ui.frequencyInpValid = $('#LinearOscillatorFrequencyValid')
    this.ui.dampingInp = $('#LinearOscillatorDamping')
    this.ui.dampingInpValid = $('#LinearOscillatorDampingValid')
  },

  initEvents() {
    Workspace.ui.modalOkBtn.on('click', () => {
      this.doTransform()
    })
  },

  readParamsFromCookie() {
    let params = CookieManager.readObjectFromCookie('LinearOscillatorParams')
    if (params) {
      this.params = params
      this.setFormValues()
    }
  },

  getFormValues() {
    this.params = {
      frequency: parseFloat(this.ui.frequencyInp.val()),
      damping: parseFloat(this.ui.dampingInp.val())
    }
  },

  setFormValues() {
    this.ui.frequencyInp.val(this.params.frequency)
    this.ui.dampingInp.val(this.params.damping)
  },

  doTransform() {
    this.getFormValues()
    for (let key in this.params) {
      if (!this.validateParam(key)) return
    }
    CookieManager.writeObjectToCookie('LinearOscillatorParams', this.params)
    this.signal.description += `\nTransformed by linear oscillator with f = ${this.params.frequency}, d = ${this.params.damping}`
    this.signal.data = SignalUtils.solveDifEq({
      inData: this.signal.data,
      step: this.signal.params.step,
      equations: this.EQUATIONS,
      params: this.params,
      initial: [0.0, 0.0],
      outNumber: 0
    })
    Workspace.closeModule(this)
    Workspace.startModule({
      module: 'Cable',
      param: this.signal
    }).then()
  },

  validateParam(key) {
    let value = this.params[key]
    let invalidMsg = ''
    if (isNaN(value)) {
      invalidMsg = 'Should be a number'
    } else if (value < 0) {
      invalidMsg = 'Must have positive value'
    }
    if (invalidMsg) {
      this.ui[key + 'InpValid'].html(invalidMsg)
      this.ui[key + 'Inp'].addClass('is-invalid')
    }
    return !invalidMsg
  }

}
