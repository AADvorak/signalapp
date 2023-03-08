ApiProvider = {

  get(url, noHandleUnauthorized) {
    return fetch(url)
        .then(response => this.parseResponse(response, noHandleUnauthorized))
        .catch(error => error)
  },

  postJson(url, data, noHandleUnauthorized) {
    return this.post(url, JSON.stringify(data), 'application/json', noHandleUnauthorized)
  },

  post(url, body, contentType, noHandleUnauthorized) {
    return fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': contentType || 'text/plain'
      },
      body
    })
        .then(response => this.parseResponse(response, noHandleUnauthorized))
        .catch(error => error)
  },

  putJson(url, data) {
    return fetch(url, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })
        .then(response => this.parseResponse(response))
        .catch(error => error)
  },

  putText(url, data, extension) {
    return fetch(url, {
      method: 'PUT',
      headers: {
        'Content-Type': 'text/' + (extension === 'html' ? extension : 'plain')
      },
      body: data
    })
        .then(response => this.parseResponse(response))
        .catch(error => error)
  },

  del(url) {
    return fetch(url, {
      method: 'DELETE'
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
      await Workspace.startModule({module: 'SignIn'})
      EVENTS.USER_SIGNED_OUT.trigger()
    }
    return {...result, errors: data}
  }

}
