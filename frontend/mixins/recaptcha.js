import { useReCaptcha } from 'vue-recaptcha-v3';
import {appSettingsStore} from "~/stores/app-settings-store";

export default {
  data: () => ({
    recaptchaInstance: useReCaptcha()
  }),
  methods: {
    async recaptcha() {
      if (!appSettingsStore().settings?.verifyCaptcha) {
        return null
      }
      await this.recaptchaInstance?.recaptchaLoaded()
      return (await this.recaptchaInstance?.executeRecaptcha(this.$options.name))
    }
  }
}
