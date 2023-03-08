import {dataStore} from "~/stores/data-store";

const ApiProvider = {

  BASE_URL: window.location.port === '3000' ? 'http://localhost:8080' : '',

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
    return fetch(this.BASE_URL + url, {
      credentials: 'include'
    })
        .then(response => this.parseResponse(response, noHandleUnauthorized))
        .catch(error => error)
  },

  postJson(url, data, noHandleUnauthorized) {
    return this.post(url, JSON.stringify(data), 'application/json', noHandleUnauthorized)
  },

  post(url, body, contentType, noHandleUnauthorized) {
    return fetch(this.BASE_URL + url, {
      method: 'POST',
      headers: {
        'Content-Type': contentType || 'text/plain'
      },
      body,
      credentials: 'include'
    })
        .then(response => this.parseResponse(response, noHandleUnauthorized))
        .catch(error => error)
  },

  putJson(url, data) {
    return fetch(this.BASE_URL + url, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data),
      credentials: 'include'
    })
        .then(response => this.parseResponse(response))
        .catch(error => error)
  },

  putText(url, data, extension) {
    return fetch(this.BASE_URL + url, {
      method: 'PUT',
      headers: {
        'Content-Type': 'text/' + (extension === 'html' ? extension : 'plain')
      },
      body: data,
      credentials: 'include'
    })
        .then(response => this.parseResponse(response))
        .catch(error => error)
  },

  del(url) {
    return fetch(this.BASE_URL + url, {
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
      dataStore().clearUserInfo()
      this.route && dataStore().setWaitingForAuthorization({
        path: this.route.fullPath
      })
      this.router && this.router.push('/signin')
    }
    return {...result, errors: data}
  }

}

export default ApiProvider