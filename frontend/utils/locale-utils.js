import en from "~/locales/en";
import ru from "~/locales/ru";
import pl from "~/locales/pl";
import {dataStore} from "~/stores/data-store";
import Highcharts from "highcharts";

export function useLocaleUtils(i18n) {

  const locales= {en, ru, pl}

  function detectLocale() {
    if (dataStore().locale) {
      i18n.locale = dataStore().locale
      return
    }
    if (navigator) {
      if (navigator.language) {
        if (trySetLocaleFromLanguage(navigator.language)) {
          return
        }
      }
      if (navigator.languages) {
        for (const language of navigator.languages) {
          if (trySetLocaleFromLanguage(language)) {
            return
          }
        }
      }
    }
    i18n.locale = dataStore().defaultLocale
  }

  function trySetLocaleFromLanguage(language) {
    const localeFromLanguage = extractLocaleFromLanguage(language)
    if (locales.hasOwnProperty(localeFromLanguage)) {
      i18n.locale = localeFromLanguage
      return true
    }
    return false
  }

  function extractLocaleFromLanguage(language) {
    return language.split('-')[0]
  }

  function makeChartLang() {
    Highcharts.setOptions({
      lang: locales[i18n.locale]?.chart
    })
  }

  return {locales, detectLocale, makeChartLang}
}
