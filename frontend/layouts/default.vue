<template>
  <v-app :theme="theme">
    <v-app-bar elevation="1" app>
      <template v-slot:prepend>
        <v-app-bar-nav-icon @click="showOrHideMenu"/>
      </template>
      <v-img v-if="!isMobile"
             class="ml-2"
             src="../oscilloscope-logo.png"
             max-width="80"
             min-width="80"
             style="cursor: pointer;"
             @click="toMainPage"/>
      <v-spacer v-if="!isMobile"/>
      <v-app-bar-title>{{ header }}</v-app-bar-title>
      <v-spacer v-if="!isMobile"/>
      <template v-slot:append>
        <v-btn color="primary">
          <v-icon v-if="isMobile">{{ mdiAccount }}</v-icon>
          <div v-else>{{ isSignedIn ? userButtonText : _t('user') }}</div>
          <v-menu activator="parent">
            <v-list>
              <v-list-item v-if="isSignedIn" @click="signOut">
                <v-list-item-title>{{ _tc('buttons.signOut') }}</v-list-item-title>
              </v-list-item>
              <v-list-item v-if="isSignedIn" to="/user-settings">
                <v-list-item-title>{{ _t('settings') }}</v-list-item-title>
              </v-list-item>
              <v-list-item v-if="!isSignedIn" to="/signin">
                <v-list-item-title>{{ _tc('buttons.signIn') }}</v-list-item-title>
              </v-list-item>
              <v-list-item v-if="!isSignedIn" to="/signup">
                <v-list-item-title>{{ _tc('buttons.signUp') }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </v-btn>
      </template>
    </v-app-bar>
    <v-main>
      <v-container width="100%" style="padding: 12px; max-width: none;">
        <v-row>
          <v-col style="padding: 4px" v-if="showMainMenu" :cols="mainMenuCols" :sm="mainMenuColsSm" :md="mainMenuCols" :lg="mainMenuCols">
            <v-card width="100%">
              <v-card-title>{{ _t('navigation') }}</v-card-title>
              <v-card-text>
                <v-list>
                  <v-list-item @click="toMainPage">
                    <v-list-item-title>
                      <v-icon>{{ mdiHome }}</v-icon>
                      {{ _t('startPage') }}
                    </v-list-item-title>
                  </v-list-item>
                  <v-list-item
                      v-for="module in modulesForMenu"
                      :key="module.code"
                      @click="toPage('/' + module.code.toLowerCase())"
                  >
                    <v-list-item-title>
                      <v-icon>{{ getModuleIcon(module) }}</v-icon>
                      {{ $t(`${module.code}.name`) }}
                    </v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-card-text>
            </v-card>
            <v-card width="100%">
              <v-card-title>{{ _t('settings') }}</v-card-title>
              <v-card-text>
                <v-switch hide-details v-model="darkMode" :label="_t('darkMode', {darkModeState})"/>
                <v-form>
                  <v-select
                      v-model="$i18n.locale"
                      item-title="name"
                      item-value="code"
                      :items="localeItems"
                      :label="$t('language')"/>
                </v-form>
              </v-card-text>
            </v-card>
          </v-col>
          <v-col v-if="!isMobile || !showMainMenu" style="padding: 4px" :cols="pageCols" :sm="pageColsSm" :md="pageCols" :lg="pageCols">
            <slot/>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>

import {dataStore} from "../stores/data-store";
import ApiProvider from "../api/api-provider";
import {mdiAccount, mdiHome, mdiMicrophone, mdiSineWave, mdiServer, mdiCog} from "@mdi/js";
import DeviceUtils from "../utils/device-utils";
import en from '../locales/en'
import ru from '../locales/ru'
import ComponentBase from "../components/component-base";

export default {
  name: 'default',
  extends: ComponentBase,
  data() {
    return {
      mdiAccount, mdiHome,
      moduleIcons: {
        microphone: mdiMicrophone,
        sineWave: mdiSineWave,
        server: mdiServer
      },
      darkMode: dataStore().getDarkMode,
      header: '',
      showMainMenu: false,
      isMobile: DeviceUtils.isMobile(),
      locales: {en, ru}
    }
  },
  computed: {
    theme() {
      return this.darkMode ? 'dark' : 'light'
    },
    darkModeState() {
      return this._t(this.darkMode ? 'on' : 'off')
    },
    userButtonText() {
      return dataStore().userRepresentingString
    },
    isSignedIn() {
      return dataStore().isSignedIn
    },
    modulesForMenu() {
      return dataStore().getModulesForMenu
    },
    mainMenuCols() {
      return this.isMobile ? 12 : 3
    },
    mainMenuColsSm() {
      return this.isMobile ? 12 : 4
    },
    pageCols() {
      return this.showMainMenu ? 12 - this.mainMenuCols : 12
    },
    pageColsSm() {
      return this.showMainMenu ? 12 - this.mainMenuColsSm : 12
    },
    localeItems() {
      let items = []
      for (const code in this.locales) {
        items.push({code, name: this.locales[code].name})
      }
      return items
    }
  },
  watch: {
    darkMode(newValue) {
      dataStore().setDarkMode(newValue)
    },
    '$i18n.locale'(newValue) {
      dataStore().setLocale(newValue)
      this.setHeaderByRoute()
    }
  },
  mounted() {
    dataStore().loadUserInfo()
    this.setHeaderByRoute()
    this.detectLocale()
  },
  methods: {
    async signOut() {
      await ApiProvider.del('/api/sessions/')
      dataStore().clearUserInfo()
      this.toMainPage()
    },
    toMainPage() {
      this.toPage('/')
    },
    toPage(page) {
      useRouter().push(page)
    },
    setHeaderByRoute() {
      let routeName = useRoute().name
      if (routeName === 'index') {
        this.header = this._t('startPage')
        return
      }
      for (let module of dataStore().getAllModules) {
        if (routeName.startsWith(module.code.toLowerCase())) {
          this.header = this.$t(`${module.code}.name`)
          break
        }
      }
    },
    showOrHideMenu() {
      this.showMainMenu = !this.showMainMenu
      window.scrollTo(0,0)
    },
    getModuleIcon(module) {
      return this.moduleIcons[module.icon] || mdiCog
    },
    detectLocale() {
      if (dataStore().locale) {
        this.$i18n.locale = dataStore().locale
        return
      }
      if (navigator) {
        if (navigator.language) {
          if (this.trySetLocaleFromLanguage(navigator.language)) {
            return
          }
        }
        if (navigator.languages) {
          for (const language of navigator.languages) {
            if (this.trySetLocaleFromLanguage(language)) {
              return
            }
          }
        }
      }
      this.$i18n.locale = dataStore().defaultLocale
    },
    trySetLocaleFromLanguage(language) {
      const locale = this.localeFromLanguage(language)
      if (this.locales.hasOwnProperty(locale)) {
        this.$i18n.locale = locale
        return true
      }
      return false
    },
    localeFromLanguage(language) {
      return language.split('-')[0]
    }
  },
}
</script>

<style>
.v-toolbar-title__placeholder {
  text-align: center;
  position: absolute;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>