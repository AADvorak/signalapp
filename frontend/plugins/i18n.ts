import { defineNuxtPlugin } from '#app'
import { createI18n } from 'vue-i18n'
import en from '../locales/en'
import ru from '../locales/ru'
import pl from '../locales/pl'

export default defineNuxtPlugin((nuxtApp) => {
    const i18n = createI18n({
        legacy: false,
        globalInjection: true,
        locale: 'en',
        messages: {
            en,
            ru,
            pl
        }
    })
    nuxtApp.vueApp.use(i18n)
})
