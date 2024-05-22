import {defineStore} from 'pinia'

export const userStore = defineStore('userStore', {
  state: () => {
    return {
      userInfo: undefined,
      folders: []
    }
  },
  getters: {
    userDescription: state => {
      const userInfo = state.userInfo
      if (!userInfo) {
        return ''
      }
      if (userInfo.firstName && userInfo.lastName && userInfo.patronymic) {
        return userInfo.firstName.substring(0, 1) + '.' + userInfo.patronymic.substring(0, 1) + '. ' + userInfo.lastName
      }
      if (userInfo.firstName && userInfo.lastName) {
        return userInfo.firstName.substring(0, 1) + '. ' + userInfo.lastName
      }
      if (userInfo.firstName) {
        return userInfo.firstName
      }
      if (userInfo.lastName) {
        return userInfo.lastName
      }
      return userInfo.email
    },
    isSignedIn: state => !!state.userInfo?.id,
  },
  actions: {
    setUserInfo(userInfo) {
      this.userInfo = userInfo
    },
    clearPersonalData() {
      this.userInfo = undefined
      this.folders = []
    },
    checkUserRole(roleName) {
      return this.userInfo?.roles?.map(role => role.name).includes(roleName)
    }
  },
})
