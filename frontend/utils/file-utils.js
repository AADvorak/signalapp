import ApiProvider from "~/api/api-provider";

const FileUtils = {

  TYPES_BY_EXTENSION: {
    'txt': 'text/plain',
    'csv': 'text/csv',
    'json': 'application/json',
    'xml': 'text/xml'
  },

  EXTENSIONS_BY_TYPE: {
    'text/plain': 'txt',
    'text/csv': 'csv',
    'application/json': 'json',
    'text/xml': 'xml'
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
        xMin: signal.xMin,
        sampleRate: signal.sampleRate,
        data: signal.data
      })
    },
    xml(signal) {
      const xml = document.implementation.createDocument('', '', null)
      const createAndAppendChild = (parent, name, html) => {
        const el = xml.createElement(name)
        if (html !== undefined) {
          el.innerHTML = html
        }
        parent.appendChild(el)
        return el
      }
      const root = createAndAppendChild(xml,'Signal')
      createAndAppendChild(root, 'name', signal.name)
      createAndAppendChild(root, 'description', signal.description)
      createAndAppendChild(root, 'xMin', signal.xMin)
      createAndAppendChild(root, 'sampleRate', signal.sampleRate)
      const data = createAndAppendChild(root, 'data')
      for (const sample of signal.data) {
        createAndAppendChild(data, 'sample', sample)
      }
      return new XMLSerializer().serializeToString(xml)
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
      const validateNumber = (value, positive) => {
        const number = parseFloat(value)
        if (isNaN(number)) {
          return false
        }
        return !(positive && number <= 0)
      }
      try {
        const signal = JSON.parse(str)
        if (!signal.name || !validateNumber(signal.xMin) || !validateNumber(signal.sampleRate, true)
            || !signal.data || !signal.data.length) {
          throw new Error()
        }
        for (const sample of signal.data) {
          if (!validateNumber(sample)) {
            throw new Error()
          }
        }
        return signal
      } catch (e) {
        throw new Error('wrongFileFormat')
      }
    },
    xml(str) {
      try {
        const parser = new DOMParser()
        const signalXml = parser.parseFromString(str, 'application/xml')
        const signalNode = signalXml.querySelector('Signal')
        const signal = {
          data: []
        }
        for (const field of ['name', 'description']) {
          const node = signalNode.querySelector(field)
          if (node) {
            signal[field] = node.innerHTML
          }
        }
        if (!signal.name) {
          throw new Error()
        }
        for (const field of ['xMin', 'sampleRate']) {
          const value = parseFloat(signalNode.querySelector(field).innerHTML)
          if (isNaN(value)) {
            throw new Error()
          }
          signal[field] = value
        }
        if (signal.sampleRate <= 0) {
          throw new Error()
        }
        const sampleNodes = signalNode.querySelector('data').childNodes
        if (!sampleNodes || !sampleNodes.length) {
          throw new Error()
        }
        for (const sampleNode of sampleNodes) {
          const sample = parseFloat(sampleNode.innerHTML || '0')
          if (isNaN(sample)) {
            throw new Error()
          }
          signal.data.push(sample)
        }
        return signal
      } catch (e) {
        throw new Error('wrongFileFormat')
      }
    },
    signalFromDsv(str, delimiter) {
      try {
        let points = []
        let strArr = str.split('\r\n')
        for (let str of strArr) {
          const values = str.split(delimiter)
          if (values.length < 2) {
            continue
          }
          const item = {
            x: parseFloat(values[0]),
            y: parseFloat(values[1])
          }
          if (isNaN(item.x) || isNaN(item.y)) {
            throw new Error()
          }
          points.push(item)
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