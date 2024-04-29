import {defineStore} from 'pinia'

export const roleStore = defineStore('roleStore', {
  state: () => ({
    roles: []
  })
})
