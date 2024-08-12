const DeviceUtils = {

  isMobile() {
    return /Android|iPhone/i.test(navigator.userAgent)
  },

  scrollUp() {
    window.scrollTo(0,0)
  }

}

export default DeviceUtils
