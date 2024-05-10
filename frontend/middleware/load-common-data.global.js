import {userStore} from "~/stores/user-store";
import ApiProvider from "~/api/api-provider";
import {appSettingsStore} from "~/stores/app-settings-store";
import {dataStore} from "~/stores/data-store";

export default defineNuxtRouteMiddleware(async (to) => {
  const loadUserInfo = async () => {
    if (userStore().userInfo !== undefined) {
      return
    }
    const response = await ApiProvider.get('/api/users/me', true)
    userStore().setUserInfo(response.ok ? response.data : null)
  }

  const loadSettings = async () => {
    if (appSettingsStore().settings !== undefined) {
      return
    }
    const response = await ApiProvider.get('/api/application/settings', true)
    appSettingsStore().settings = response.ok ? response.data : null
  }

  const loadServerTimezoneOffset = async () => {
    if (dataStore().serverTimezoneOffset !== undefined) {
      return
    }
    const response = await ApiProvider.get('/api/application/timezone-offset', true)
    dataStore().serverTimezoneOffset = response.ok ? parseInt(response.data) : null
  }

  await Promise.all([loadUserInfo(), loadSettings(), loadServerTimezoneOffset()])
})
