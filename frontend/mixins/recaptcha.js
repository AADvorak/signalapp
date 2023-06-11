import { useReCaptcha } from 'vue-recaptcha-v3';

export default {
  data: () => ({
    recaptchaInstance: useReCaptcha()
  }),
  methods: {
    async recaptcha() {
      await this.recaptchaInstance?.recaptchaLoaded()
      return (await this.recaptchaInstance?.executeRecaptcha(this.$options.name))
    },
    showRecaptchaValidationError(errors) {
      for (let error of errors) {
        if (error.field === 'token') {
          this.showMessage({
            text: this.getLocalizedErrorMessage(error)
          })
        }
      }
    },
  }
}
