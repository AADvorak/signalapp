SignalGenerator = {

  VALIDATION_MSG: {
    number: 'Must be a number',
    greaterThanZero: 'Must be greater than zero'
  },

  FORM_VALIDATION: {
    length: (values) => {
      if (values.length <= 0) return SignalGenerator.VALIDATION_MSG.greaterThanZero
      let pointsNumber = values.length * values.sampleRate
      if (!SignalGenerator.FORM_VALIDATION.sampleRate(values) && (pointsNumber < 2 || pointsNumber > 512000)) {
        return 'Number of signal points (S * L) must be in range from 2 to 512000. Now it is ' + Math.floor(pointsNumber)
      }
    },
    sampleRate: (values) => {
      if (values.sampleRate <= 0) return SignalGenerator.VALIDATION_MSG.greaterThanZero
      if (values.sampleRate > 48000) return 'Must be not greater than 48000'
    },
    frequency: (values) => {
      if (values.frequency <= 0) return SignalGenerator.VALIDATION_MSG.greaterThanZero
      if (!SignalGenerator.FORM_VALIDATION.sampleRate(values) && 2 * values.frequency > values.sampleRate) {
        return 'Must be less than a half of sample rate'
      }
    },
    amplitude: (values) => {
      if (values.amplitude < 0) return SignalGenerator.VALIDATION_MSG.greaterThanZero
    },
  },

  SIGNAL_FORMS: {
    sine: ({x, frequency, amplitude, offset}) => {
      return offset + amplitude * Math.sin(x * 2 * Math.PI * frequency)
    },
    square: ({x, frequency, amplitude, offset}) => {
      return Math.sin(x * 2 * Math.PI * frequency) >= 0 ? offset + amplitude : offset - amplitude
    },
    triangle: ({x, frequency, amplitude, offset}) => {
      return offset + (2 * amplitude / Math.PI) * Math.asin(Math.sin(x * 2 * Math.PI * frequency))
    },
    sawtooth: ({x, frequency, amplitude, offset}) => {
      return offset + (2 * amplitude / Math.PI) * Math.atan(Math.tan(x * Math.PI * frequency))
    },
    noise: ({x, frequency, amplitude, offset}) => {
      return offset + amplitude * (Math.random() * 2 - 1)
    }
  },

  init() {
    this.selectElements()
    this.fillFormSelector()
    this.initEvents()
    this.setSignalParamValuesFromCookie()
  },

  selectElements() {
    this.ui = {}
    this.ui.signalParamForm = {}
    this.ui.signalParamForm.begin = $('#SignalGeneratorBegin')
    this.ui.signalParamForm.beginValid = $('#SignalGeneratorBeginValid')
    this.ui.signalParamForm.length = $('#SignalGeneratorLength')
    this.ui.signalParamForm.lengthValid = $('#SignalGeneratorLengthValid')
    this.ui.signalParamForm.sampleRate = $('#SignalGeneratorSampleRate')
    this.ui.signalParamForm.sampleRateValid = $('#SignalGeneratorSampleRateValid')
    this.ui.signalParamForm.frequency = $('#SignalGeneratorFrequency')
    this.ui.signalParamForm.frequencyValid = $('#SignalGeneratorFrequencyValid')
    this.ui.signalParamForm.amplitude = $('#SignalGeneratorAmplitude')
    this.ui.signalParamForm.amplitudeValid = $('#SignalGeneratorAmplitudeValid')
    this.ui.signalParamForm.offset = $('#SignalGeneratorOffset')
    this.ui.signalParamForm.offsetValid = $('#SignalGeneratorOffsetValid')
    this.ui.signalParamForm.form = $('#SignalGeneratorForm')
    this.ui.saveBtn = $('#SignalGeneratorSave')
    this.ui.txtFileInp = $('#SignalGeneratorTxtFile')
    this.ui.wavFileInput = $('#SignalGeneratorWavFile')
  },

  fillFormSelector() {
    for (let form in this.SIGNAL_FORMS) {
      this.ui.signalParamForm.form.append(`<option>${form}</option>`)
    }
  },

  initEvents() {
    this.ui.saveBtn.on('click', () => {
      let values = this.getSignalParamValues()
      if (this.validateSignalParamValues(values)) {
        CookieManager.writeObjectToCookie('SignalParamValues', values)
        this.generateAndOpenSignal(values)
      }
    })
    this.ui.txtFileInp.on('change', () => {
      this.importFromTxtFile().then()
    })
    this.ui.wavFileInput.on('change', () => {
      this.importFromWavFile().then()
    })
  },

  getSignalParamValues() {
    let form = this.ui.signalParamForm
    return {
      begin: parseFloat(form.begin.val()),
      length: parseFloat(form.length.val()),
      sampleRate: parseFloat(form.sampleRate.val()),
      frequency: parseFloat(form.frequency.val()),
      amplitude: parseFloat(form.amplitude.val()),
      offset: parseFloat(form.offset.val()),
      form: form.form.val()
    }
  },

  setSignalParamValuesFromCookie() {
    let values = CookieManager.readObjectFromCookie('SignalParamValues')
    if (values) {
      let form = this.ui.signalParamForm
      values.begin && form.begin.val(values.begin)
      values.length && form.length.val(values.length)
      values.sampleRate && form.sampleRate.val(values.sampleRate)
      values.frequency && form.frequency.val(values.frequency)
      values.amplitude && form.amplitude.val(values.amplitude)
      values.offset && form.offset.val(values.offset)
      values.form && form.form.val(values.form)
    }
  },

  validateSignalParamValues(values) {
    let validated = true
    for (let fieldName in values) {
      if (fieldName !== 'form') {
        let validationMsg = this.getFieldValidationMsg(fieldName, values)
        if (validationMsg) {
          this.markInvalid(fieldName, validationMsg)
          validated = false
        } else {
          this.removeInvalidIndication(fieldName)
        }
      }
    }
    return validated
  },

  getFieldValidationMsg(fieldName, values) {
    let value = values[fieldName]
    if (isNaN(value)) {
      return this.VALIDATION_MSG.number
    } else {
      let customFieldValidation = this.FORM_VALIDATION[fieldName]
      return customFieldValidation ? customFieldValidation(values) : ''
    }
  },

  markInvalid(fieldName, msg) {
    let form = this.ui.signalParamForm
    form[fieldName].addClass('is-invalid')
    form[fieldName + 'Valid'].html(msg)
  },

  removeInvalidIndication(fieldName) {
    let form = this.ui.signalParamForm
    form[fieldName].removeClass('is-invalid')
  },

  getFileName(input) {
    return input.val().replace(/C:\\fakepath\\/, '')
  },

  generateAndOpenSignal({begin, length, sampleRate, frequency, amplitude, offset, form}) {
    let data = []
    let step = 1 / sampleRate
    for (let x = begin; x < begin + length; x += step) {
      data.push(this.SIGNAL_FORMS[form]({x, frequency, amplitude, offset}))
    }
    this.sendToCable({
      name: `Generated ${form} signal`,
      description: `Generated ${form} signal with F = ${frequency} (${data.length} points)`,
      maxAbsY: amplitude,
      sampleRate: sampleRate,
      xMin: begin,
      data
    })
  },

  async importFromTxtFile() {
    try {
      let signal = await FileManager.readSignalFromTxtFile(this.ui.txtFileInp)
      let fileName = this.getFileName(this.ui.txtFileInp)
      signal.name = fileName
      signal.description = `Imported from file ${fileName}`
      this.sendToCable(signal)
    } catch (e) {
      Workspace.showAlert(e.message)
    }
  },

  async importFromWavFile() {
    let data = await FileManager.readSignalFromWavFile(this.ui.wavFileInput)
    let response = await ApiProvider.post('/api/signals/wav/' + this.getFileName(this.ui.wavFileInput),
        data, 'audio/wave')
    if (response && response.ok) {
      Workspace.startModule({
        module: 'SignalManager'
      }).then()
    } else {
      let errorMsg = ''
      for (let error of response.errors) {
        error.message && (errorMsg += errorMsg ? ', ' + error.message : error.message)
      }
      Workspace.showAlert('Error uploading file' + (errorMsg ? ': ' + errorMsg : ''))
    }
  },

  sendToCable(signal) {
    EVENTS.INIT_SIGNAL_STACK.trigger('SignalGenerator')
    Workspace.startModule({
      module: 'Cable',
      param: signal
    }).then()
  }

}