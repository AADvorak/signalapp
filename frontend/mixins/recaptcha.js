import { useReCaptcha } from 'vue-recaptcha-v3';
import {dataStore} from "~/stores/data-store";

export default {
  data: () => ({
    recaptchaInstance: useReCaptcha()
  }),
  methods: {
    async recaptcha() {
      if (!dataStore().settings?.verifyCaptcha) {
        return null
      }
      await this.recaptchaInstance?.recaptchaLoaded()
      return (await this.recaptchaInstance?.executeRecaptcha(this.$options.name))
    }
  }
}
