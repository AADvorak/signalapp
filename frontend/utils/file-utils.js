import ApiProvider from "~/api/api-provider";

const FileUtils = {

  TYPES_BY_EXTENSION: {
    'txt': 'text/plain',
    'csv': 'text/csv',
    'json': 'application/json',
    'xml': 'application/xml'
  },

  EXTENSIONS_BY_TYPE: {
    'text/plain': 'txt',
    'text/csv': 'csv',
    'application/json': 'json',
    'application/xml': 'xml'
  },

  exportSignal(signal, extension) {
    const dataType = this.TYPES_BY_EXTENSION[extension],
        data = encodeURIComponent(this.stringify[extension](signal))
    this.saveToFile(`data:${dataType};charset=utf-8,${data}`,
        this.getFileNameWithExtension(signal.name, '.' + extension))
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

  readSignalFromTextFile(file) {
    const extension = this.EXTENSIONS_BY_TYPE[file.type]
    return this.readFromFile(file).then(result => this.parse[extension](result))
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

  getFileNameWithExtension(name, extension) {
    return name + (name.endsWith(extension) ? '' : extension)
  },

  stringify: {
    txt(signal) {
      return this.signalToDsv(signal, ' ')
    },
    csv(signal) {
      return this.signalToDsv(signal, ',')
    },
    json(signal) {
      return JSON.stringify({
        name: signal.name,
        description: signal.description,
        maxAbsY: signal.maxAbsY,
        xMin: signal.xMin,
        sampleRate: signal.sampleRate,
        data: signal.data
      })
    },
    xml(signal) {
      return ''
    },
    signalToDsv(signal, delimiter) {
      let text = ''
      for (let i = 0; i < signal.data.length; i++) {
        text += `${signal.xMin + i * signal.params.step}${delimiter}${signal.data[i]}\r\n`
      }
      return text
    },
  },

  parse: {
    txt(str) {
      return this.signalFromDsv(str, ' ')
    },
    csv(str) {
      return this.signalFromDsv(str, ',')
    },
    json(str) {
      return JSON.parse(str)
    },
    xml(str) {
      return {}
    },
    signalFromDsv(str, delimiter) {
      try {
        let points = []
        let strArr = str.split('\r\n')
        for (let str of strArr) {
          let values = str.split(delimiter)
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
        throw new Error('wrongFileFormat')
      }
    }
  }

}

export default FileUtils