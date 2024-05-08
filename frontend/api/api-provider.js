import {dataStore} from "~/stores/data-store";
import {userStore} from "~/stores/user-store";

const ApiProvider = {

  router: null,
  route: null,

  setRouter(router) {
    this.router = router
    return this
  },

  setRoute(route) {
    this.route = route
    return this
  },

  get(url, noHandleUnauthorized) {
    return fetch(url, {
      credentials: 'include'
    })
        .then(response => this.parseResponse(response, noHandleUnauthorized))
        .catch(error => error)
  },

  postJson(url, data, noHandleUnauthorized) {
    return this.post(url, JSON.stringify(data), 'application/json', noHandleUnauthorized)
  },

  postMultipart(url, object, data, noHandleUnauthorized) {
    return this.post(url, this.createFormData(object, data), '', noHandleUnauthorized)
  },

  post(url, body, contentType, noHandleUnauthorized) {
    return this.request(url, 'POST', body, contentType, noHandleUnauthorized)
  },

  putJson(url, data, noHandleUnauthorized) {
    return this.request(url, 'PUT', data, 'application/json', noHandleUnauthorized)
  },

  putMultipart(url, object, data, noHandleUnauthorized) {
    return this.request(url, 'PUT', this.createFormData(object, data), '', noHandleUnauthorized)
  },

  request(url, method, body, contentType, noHandleUnauthorized) {
    const init = {
      method,
      body,
      credentials: 'include'
    }
    if (contentType) {
      init.headers = {
        'Content-Type': contentType
      }
    }
    return fetch(url, init)
        .then(response => this.parseResponse(response, noHandleUnauthorized))
        .catch(error => error)
  },

  del(url) {
    return fetch(url, {
      method: 'DELETE',
      credentials: 'include'
    })
        .then(response => this.parseResponse(response))
        .catch(error => error)
  },

  async parseResponse(response, noHandleUnauthorized) {
    const isJson = response.headers.get('content-type')?.includes('application/json')
    const isWav = response.headers.get('content-type')?.includes('audio/wave')
    let data
    if (isJson) {
      data = await response.json()
    } else if (isWav) {
      data = await response.arrayBuffer()
    } else {
      data = await response.text()
    }
    const result = {ok: response.ok, status: response.status}
    if (response.ok) {
      return {...result, data}
    }
    if (response.status === 401 && !noHandleUnauthorized) {
      userStore().clearPersonalData()
      this.route && dataStore().setWaitingForAuthorization(this.route.fullPath)
      this.router && this.router.push('/signin')
    }
    return {...result, errors: data}
  },

  createFormData(object, data) {
    const formData = new FormData()
    formData.append('json', new Blob([JSON.stringify(object)],  {type: 'application/json'}) )
    formData.append('data', data)
    return formData
  }

}

export default ApiProvider