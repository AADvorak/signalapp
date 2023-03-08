class AppEvent {

  constructor() {
    this._subscribers = []
  }

  subscribe(obj, funcName) {
    this.unsubscribe(obj)
    this._subscribers.push({obj, funcName})
  }

  unsubscribe(obj) {
    for (let i = 0; i < this._subscribers.length; i++) {
      if (obj === this._subscribers[i].obj) this._subscribers.splice(i, 1)
    }
  }

  trigger(param) {
    for (let subscriber of this._subscribers) {
      subscriber.obj[subscriber.funcName](param)
    }
  }

}

EVENTS = {

  MODULES_LOADED: new AppEvent(),
  MODULES_CHANGED: new AppEvent(),
  INIT_SIGNAL_STACK: new AppEvent(),
  USER_SIGNED_IN: new AppEvent(),
  USER_SIGNED_OUT: new AppEvent(),
  USER_INFO_CHANGED: new AppEvent(),

}