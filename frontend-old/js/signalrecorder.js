SignalRecorder = {

  gumStream: undefined,
  rec: undefined,
  input: undefined,
  audioContext: undefined,

  RECORD_STATUSES: {
    READY: 'Ready to start record',
    RECORDING: 'Recording...',
    PAUSED: 'Recording paused',
  },

  RECORD_PARAM_VALUES: 'RecordParamValues',

  recordStatus: '',
  recorded: null,

  init(recorded) {
    this.selectElements()
    this.initEvents()
    this.setRecordParamValuesFromCookie()
    this.initRecorder(recorded)
  },

  selectElements() {
    this.ui = {}
    this.ui.recBtn = $('#SignalRecorderRec')
    this.ui.pauseBtn = $('#SignalRecorderPause')
    this.ui.stopBtn = $('#SignalRecorderStop')
    this.ui.exportToWavBtn = $('#SignalRecorderExportToWav')
    this.ui.saveBtn = $('#SignalRecorderSave')
    this.ui.clearBtn = $('#SignalRecorderClear')
    this.ui.recordStatusDiv = $('#SignalRecorderRecordStatus')
    this.ui.recordedDiv = $('#SignalRecorderRecorded')
    this.ui.infoDiv = $('#SignalRecorderInfo')
    this.ui.recordParamForm = {}
    this.ui.recordParamForm.sampleRate = $('#SignalRecorderSampleRate')
    this.ui.recordParamForm.sampleRateValid = $('#SignalRecorderSampleRateValid')
  },

  initEvents() {
    this.ui.recBtn.on('click', () => {
      this.startRecord()
    })
    this.ui.pauseBtn.on('click', () => {
      this.recordStatus === this.RECORD_STATUSES.RECORDING ? this.pauseRecord() : this.continueRecord()
    })
    this.ui.stopBtn.on('click', () => {
      this.stopRecord()
    })
    this.ui.saveBtn.on('click', () => {
      this.saveRecorded().then()
    })
    this.ui.exportToWavBtn.on('click', () => {
      this.exportRecordedToWav()
    })
    this.ui.clearBtn.on('click', () => {
      this.clearRecorded()
    })
  },

  initRecorder(recorded) {
    this.setRecordStatus(this.RECORD_STATUSES.READY)
    this.setRecorded(recorded)
  },

  setRecordParamValuesFromCookie() {
    let values = CookieManager.readObjectFromCookie(this.RECORD_PARAM_VALUES)
    if (values) {
      let form = this.ui.recordParamForm
      form.sampleRate.val(values.sampleRate)
    }
  },

  getRecordParamValues() {
    let form = this.ui.recordParamForm
    return {
      sampleRate: parseInt(form.sampleRate.val())
    }
  },

  validateRecordParamValues(values) {
    this.removeInvalidIndication('sampleRate')
    if (!values.sampleRate || values.sampleRate < 3000 || values.sampleRate > 48000) {
      this.markInvalid('sampleRate', 'Must be between 3000 and 48000')
      return false
    }
    return true
  },

  startRecord() {
    let values = this.getRecordParamValues()
    if (!this.validateRecordParamValues(values)) {
      return
    }
    CookieManager.writeObjectToCookie(this.RECORD_PARAM_VALUES, values)
    let constraints = {audio: true, video: false}
    navigator.mediaDevices.getUserMedia(constraints).then(stream => {
      this.audioContext = new AudioContext({
        sampleRate: values.sampleRate
      })
      this.ui.infoDiv.text('Format: 1 channel pcm @ ' + this.audioContext.sampleRate / 1000 + 'kHz')
      this.gumStream = stream
      this.input = this.audioContext.createMediaStreamSource(stream)
      this.rec = new Recorder(this.input,{numChannels:1})
      this.rec.record()
      this.setRecordStatus(this.RECORD_STATUSES.RECORDING)
    }).catch(err => {
      this.ui.infoDiv.text(err)
      this.setRecordStatus(this.RECORD_STATUSES.READY)
    })
  },

  pauseRecord() {
    this.rec.stop()
    this.setRecordStatus(this.RECORD_STATUSES.PAUSED)
  },

  continueRecord() {
    this.rec.record()
    this.setRecordStatus(this.RECORD_STATUSES.RECORDING)
  },

  stopRecord() {
    this.rec.stop()
    this.gumStream.getAudioTracks()[0].stop()
    this.rec.exportWAV(blob => {
      let date = new Date()
      this.setRecorded({
        fileName: `Recorded ${date.toLocaleDateString()} ${date.toLocaleTimeString()}.wav`,
        blob
      })
    })
    this.setRecordStatus(this.RECORD_STATUSES.READY)
  },

  async saveRecorded() {
    let response = await ApiProvider.post('/api/signals/wav/' + this.recorded.fileName,
        await this.recorded.blob.arrayBuffer(), 'audio/wave')
    if (response && response.ok) {
      Workspace.startModule({
        module: 'SignalManager'
      }).then()
    } else if (response.status === 401) {
      Workspace.setWaitingForLoginModule('SignalRecorder', this.recorded)
    } else {
      let errorMsg = ''
      for (let error of response.errors) {
        error.message && (errorMsg += errorMsg ? ', ' + error.message : error.message)
      }
      Workspace.showAlert('Error saving record' + (errorMsg ? ': ' + errorMsg : ''))
    }
  },

  exportRecordedToWav() {
    FileManager.saveBlobToWavFile(this.recorded)
  },

  clearRecorded() {
    this.setRecorded(null)
  },

  setRecordStatus(recordStatus) {
    this.recordStatus = recordStatus
    this.ui.recordStatusDiv.text(this.recordStatus)
    switch (this.recordStatus) {
      case this.RECORD_STATUSES.READY:
        this.ui.recBtn.prop('disabled', false)
        this.ui.pauseBtn.prop('disabled', true)
        this.ui.stopBtn.prop('disabled', true)
        break;
      case this.RECORD_STATUSES.RECORDING:
        this.ui.recBtn.prop('disabled', true)
        this.ui.pauseBtn.prop('disabled', false)
        this.ui.stopBtn.prop('disabled', false)
        break;
      case this.RECORD_STATUSES.PAUSED:
        this.ui.recBtn.prop('disabled', true)
        this.ui.pauseBtn.prop('disabled', false)
        this.ui.stopBtn.prop('disabled', false)
        break;
    }
  },

  setRecorded(recorded) {
    this.recorded = recorded
    this.ui.recordedDiv.text(this.recorded ? this.recorded.fileName : 'Nothing is recorded')
    if (this.recorded) {
      this.ui.exportToWavBtn.prop('disabled', false)
      this.ui.saveBtn.prop('disabled', false)
      this.ui.clearBtn.prop('disabled', false)
    } else {
      this.ui.exportToWavBtn.prop('disabled', true)
      this.ui.saveBtn.prop('disabled', true)
      this.ui.clearBtn.prop('disabled', true)
    }
  },

  markInvalid(fieldName, msg) {
    let form = this.ui.recordParamForm
    form[fieldName].addClass('is-invalid')
    form[fieldName + 'Valid'].html(msg)
  },

  removeInvalidIndication(fieldName) {
    let form = this.ui.recordParamForm
    form[fieldName].removeClass('is-invalid')
  },

}
