import {dataStore} from "~/stores/data-store";

export const DateTimeUtils = {

  getLocalDateLocaleString(dateStr) {
    const date = new Date(dateStr)
    const clientOffset = date.getTimezoneOffset() * 60
    const serverOffset = dataStore().serverTimezoneOffset || 0
    date.setTime(date.getTime() - serverOffset - clientOffset)
    return date.toLocaleString()
  }
}
