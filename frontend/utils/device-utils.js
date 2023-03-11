const DeviceUtils = {

  isMobile() {
    return /Android|iPhone/i.test(navigator.userAgent)
  }

}

export default DeviceUtils