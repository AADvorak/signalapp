import {defineStore} from 'pinia'
import {MobileVersionStates} from "~/dictionary/mobile-version-states";
import DeviceUtils from "~/utils/device-utils";

export const mobileVersionStore = defineStore('mobileVersionStore', {
  state: () => ({
    mobileVersionState: localStorage.getItem('mobileVersionState') || MobileVersionStates.AUTO
  }),
  getters: {
    isMobile: state => {
      switch (state.mobileVersionState) {
        case MobileVersionStates.ON:
          return true
        case MobileVersionStates.OFF:
          return false
        default:
          return DeviceUtils.isMobile()
      }
    }
  },
  actions: {
    setMobileVersionState(mobileVersionState) {
      this.mobileVersionState = mobileVersionState
      localStorage.setItem('mobileVersionState', mobileVersionState)
    }
  }
})
