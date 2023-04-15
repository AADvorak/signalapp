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
          <div v-else>{{ isSignedIn ? userButtonText : 'User' }}</div>
          <v-menu activator="parent">
            <v-list>
              <v-list-item v-if="isSignedIn" @click="signOut">
                <v-list-item-title>Sign out</v-list-item-title>
              </v-list-item>
              <v-list-item v-if="isSignedIn" to="/user-settings">
                <v-list-item-title>Settings</v-list-item-title>
              </v-list-item>
              <v-list-item v-if="!isSignedIn" to="/signin">
                <v-list-item-title>Sign in</v-list-item-title>
              </v-list-item>
              <v-list-item v-if="!isSignedIn" to="/signup">
                <v-list-item-title>Sign up</v-list-item-title>
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
              <v-card-title>Navigation</v-card-title>
              <v-card-text>
                <v-list>
                  <v-list-item @click="toMainPage">
                    <v-list-item-title>
                      <v-icon>{{ mdiHome }}</v-icon>
                      Start page
                    </v-list-item-title>
                  </v-list-item>
                  <v-list-item
                      v-for="item in modulesForMenu"
                      :key="item.module"
                      @click="toPage('/' + item.module.toLowerCase())"
                  >
                    <v-list-item-title>
                      <v-icon>{{ getModuleIcon(item) }}</v-icon>
                      {{ item.name }}
                    </v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-card-text>
            </v-card>
            <v-card width="100%">
              <v-card-title>Settings</v-card-title>
              <v-card-text>
                <v-switch hide-details v-model="darkMode" :label="`Dark mode: ${darkModeStr}`"/>
                <v-form>
                  <v-autocomplete
                      v-model="$i18n.locale"
                      :items="locales"
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
import i18n from "../plugins/i18n";

export default {
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
      isMobile: DeviceUtils.isMobile()
    }
  },
  computed: {
    theme() {
      return this.darkMode ? 'dark' : 'light'
    },
    darkModeStr() {
      return this.darkMode ? 'on' : 'off'
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
    locales() {
      return ['en', 'ru']
    }
  },
  watch: {
    darkMode(newValue) {
      dataStore().setDarkMode(newValue)
    },
    '$i18n.locale'(newValue) {
      dataStore().setLocale(newValue)
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
      for (let module of dataStore().getAllModules) {
        if (routeName.startsWith(module.module.toLowerCase())) {
          this.header = module.name
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
      if (this.locales.includes(locale)) {
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