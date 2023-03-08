import ApiProvider from "~/api/api-provider";
import SignalUtils from "~/utils/signal-utils";

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

  readSignalFromWavFile(file) {
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
          points.push({
            x: parseFloat(values[0]),
            y: parseFloat(values[1])
          })
        }
      }
      const xMin = points[0].x
      // todo check steps
      const sampleRate = 1 / (points[1].x - xMin)
      const data = points.map(point => point.y)
      const maxAbsY = SignalUtils.calculateMaxAbsY({data})
      return {xMin, sampleRate, data, maxAbsY}
    } catch (e) {
      throw new Error('Wrong txt file format')
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