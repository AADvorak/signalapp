import ApiProvider from "~/api/api-provider";

const FileUtils = {

  /**
   * @param {Signal} signal
   */
  saveSignalToTxtFile(signal) {
    this.saveToFile('data:text/plain;charset=utf-8,' + encodeURIComponent(this.signalToTxt(signal)),
        this.getFileNameWithExtension(signal.name, '.txt'))
  },

  saveSignalToWavFile(signal) {
    this.saveToFile( ApiProvider.BASE_URL + '/api/signals/' + signal.id + '/wav',
        this.getFileNameWithExtension(signal.name, '.wav'))
  },

  saveBlobToWavFile({blob, fileName}) {
    this.saveToFile(URL.createObjectURL(blob), fileName)
  },

  saveToFile(href, fileName) {
    let element = document.createElement('a')
    element.setAttribute('href', href)
    element.setAttribute('download', fileName)
    element.style.display = 'none'
    document.body.appendChild(element)
    element.click()
    document.body.removeChild(element)
  },

  readSignalFromTxtFile(file) {
    return this.readFromFile(file).then(result => this.txtToSignal(result))
  },

  readArrayBufferFromWavFile(file) {
    return this.readFromFile(file, 'readAsArrayBuffer')
  },

  readFromFile(file, readFunc) {
    return new Promise((resolve, reject) => {
      let reader = new FileReader()
      reader[readFunc || 'readAsText'](file)
      reader.onload = () => {
        resolve(reader.result)
      }
      reader.onerror = (error) => {
        reject(error)
      }
    })
  },

  txtToSignal(txt) {
    try {
      let points = []
      let strArr = txt.split('\n')
      for (let str of strArr) {
        let values = str.split(' ')
        let item = {
          x: values[0],
          y: values[1]
        }
        if (item.x && item.y) {
          if (isNaN(item.x) || isNaN(item.y)) {
            throw new Error()
          }
          points.push({
            x: parseFloat(values[0]),
            y: parseFloat(values[1])
          })
        }
      }
      const xMin = points[0].x
      const step = points[1].x - xMin
      if (step <= 0) {
        throw new Error()
      }
      const sampleRate = 1 / step
      const maxStepError = step / 1000
      let data = []
      for (let i = 0; i < points.length; i++) {
        if (points[i + 1] && Math.abs(points[i + 1].x - points[i].x - step) > maxStepError) {
          throw new Error()
        }
        data.push(points[i].y)
      }
      return {xMin, sampleRate, data}
    } catch (e) {
      throw new Error('wrongTxtFormat')
    }
  },

  /**
   * @param {Signal} signal
   */
  signalToTxt(signal) {
    let txt = ''
    for (let i = 0; i < signal.data.length; i++) {
      txt += `${signal.xMin + i * signal.params.step} ${signal.data[i]}\n`
    }
    return txt
  },

  getFileNameWithExtension(name, extension) {
    return name + (name.endsWith(extension) ? '' : extension)
  }

}

export default FileUtils