import {defineStore} from 'pinia'

export const appSettingsStore = defineStore('appSettingsStore', {
  state: () => ({
    settings: undefined
  })
})
