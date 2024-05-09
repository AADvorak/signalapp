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
                  <template v-for="module in modulesForMenu">
                    <v-list-item v-if="checkUserRoleForModule(module)"
                        :key="module.code"
                        @click="toPage('/' + module.code.toLowerCase())"
                    >
                      <v-list-item-title>
                        <v-icon>{{ getModuleIcon(module) }}</v-icon>
                        {{ $t(`${module.code}.name`) }}
                      </v-list-item-title>
                    </v-list-item>
                  </template>
                </v-list>
              </v-card-text>
            </v-card>
            <v-card width="100%">
              <v-card-title>{{ _t('settings') }}</v-card-title>
              <v-card-text>
                <v-switch hide-details v-model="darkMode" :label="_t('darkMode', {darkModeState})"/>
                <v-form>
                  <locale-select/>
                  <number-input-type-select/>
                  <selects-settings/>
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

import {dataStore} from "~/stores/data-store";
import {moduleStore} from "~/stores/module-store";
import ApiProvider from "../api/api-provider";
import {mdiAccount, mdiHome, mdiMicrophone, mdiSineWave, mdiServer, mdiCog, mdiAccountMultiple} from "@mdi/js";
import DeviceUtils from "../utils/device-utils";
import ComponentBase from "../components/base/component-base.vue";
import LocaleSelect from "~/components/layout/locale-select.vue";
import SelectsSettings from "~/components/layout/selects-settings.vue";
import NumberInputTypeSelect from "~/components/layout/number-input-type-select.vue";
import {userStore} from "~/stores/user-store";
import {appSettingsStore} from "~/stores/app-settings-store";

export default {
  name: 'default',
  components: {NumberInputTypeSelect, SelectsSettings, LocaleSelect},
  extends: ComponentBase,
  data() {
    return {
      mdiAccount, mdiHome,
      moduleIcons: {
        microphone: mdiMicrophone,
        sineWave: mdiSineWave,
        server: mdiServer,
        accountMultiple: mdiAccountMultiple
      },
      darkMode: dataStore().darkMode,
      header: '',
      showMainMenu: false,
      isMobile: DeviceUtils.isMobile()
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
      return userStore().userDescription
    },
    isSignedIn() {
      return userStore().isSignedIn
    },
    modulesForMenu() {
      return moduleStore().modulesForMenu
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
    }
  },
  watch: {
    darkMode(newValue) {
      dataStore().setDarkMode(newValue)
    },
    '$i18n.locale'() {
      this.setHeaderByRoute()
    }
  },
  mounted() {
    window.history.scrollRestoration = 'manual'
    this.setHeaderByRoute()
    this.loadUserInfo()
    this.loadSettings()
    this.loadServerTimezoneOffset()
    useLocaleUtils(this.$i18n).detectLocale()
  },
  methods: {
    async signOut() {
      await ApiProvider.del('/api/sessions')
      userStore().clearPersonalData()
      this.toMainPage()
    },
    async loadUserInfo() {
      if (userStore().userInfo !== undefined) {
        return
      }
      const response = await ApiProvider.get('/api/users/me', true)
      userStore().setUserInfo(response.ok ? response.data : null)
    },
    async loadSettings() {
      if (appSettingsStore().settings !== undefined) {
        return
      }
      const response = await ApiProvider.get('/api/application/settings', true)
      appSettingsStore().settings = response.ok ? response.data : null
    },
    async loadServerTimezoneOffset() {
      if (dataStore().serverTimezoneOffset !== undefined) {
        return
      }
      const response = await ApiProvider.get('/api/application/timezone-offset', true)
      dataStore().serverTimezoneOffset = response.ok ? parseInt(response.data) : null
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
      for (let module of moduleStore().modules) {
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
    checkUserRoleForModule(module) {
      return !module.role || userStore().checkUserRole(module.role)
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
