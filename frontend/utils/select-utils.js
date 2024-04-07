export const AfterSaveSignalActions = {
  toSignalManager: 'toSignalManager',
  toSignalGenerator: 'toSignalGenerator',
  continueWorkingWithSignal: 'continueWorkingWithSignal'
}

export const ImportSignalActions = {
  save: 'save',
  open: 'open'
}

export const SelectsWithSaving = {

  afterSaveSignalActions: {
    key: 'afterSaveSignalActions',
    values: Object.keys(AfterSaveSignalActions)
  },

  importSignalActions: {
    key: 'importSignalActions',
    values: Object.keys(ImportSignalActions)
  }
}

export const SelectUtils = {

  validateKey(key) {
    return key && Object.keys(SelectsWithSaving).includes(key)
  },

  validateValue(key, value) {
    const validValues = SelectsWithSaving[key]?.values
    return validValues && validValues.includes(value)
  },

  getSelected(key) {
    if (!this.validateKey(key)) {
      return ''
    }
    return localStorage.getItem(this.getLocalStorageKey(key))
  },

  setSelected(key, value) {
    if (!this.validateKey(key)) {
      return
    }
    if (!this.validateValue(key, value)) {
      localStorage.removeItem(this.getLocalStorageKey(key))
    } else {
      localStorage.setItem(this.getLocalStorageKey(key), value)
    }
  },

  getLocalStorageKey(key) {
    return key + 'Selected'
  }
}
