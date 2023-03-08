ModuleLoader = {

  async loadModule({id, module, container, isUserModule}) {
    await this.loadModuleScript({id, module, isUserModule})
    if (container) await this.loadHtmlToContainer({id, module, container, isUserModule})
    return window[module]
  },

  loadModuleScript({id, module, isUserModule}) {
    let elementId = 'Script' + module
    let oldScript = document.getElementById(elementId)
    if (oldScript) oldScript.remove()
    return this.loadScript({
      src: this.getModuleResourceLink({id, module, isUserModule, extension: 'js'}),
      elementId
    })
  },

  loadScript({src, elementId}) {
    return new Promise((resolve, reject) => {
      let newScript = document.createElement('script')
      newScript.setAttribute('src', src)
      newScript.setAttribute('id', elementId)
      newScript.onload = () => {
        resolve()
      }
      document.head.appendChild(newScript)
    })
  },

  async loadHtmlToContainer({id, module, container, isUserModule}) {
    let result = await ApiProvider.get(this.getModuleResourceLink({id, module, isUserModule, extension: 'html'}))
    if (result.ok) container.html(result.data)
  },

  getModuleResourceLink({id, module, isUserModule, extension}) {
    let path = isUserModule ? '/api/modules/' + id + '/' + extension : '/' + extension + '/'
    return path + (!isUserModule ? module.toLowerCase() + '.' + extension : '')
  }

}