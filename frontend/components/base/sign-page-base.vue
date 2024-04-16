<template>
  <card-with-layout :message="message">
    <v-card-text>
      <p v-if="$options.name === 'signin' && waitingForAuthorization" class="mb-5">{{ _t('signInToContinue') }}</p>
      <v-form @submit.prevent>
        <text-input
            v-for="field in formFields"
            ref="inputRefs"
            :field="field"
            :field-obj="form[field]"
            :show-password="showPassword"
            @update="v => form[field].value = v"
            @show="switchShowPassword"/>
        <div class="d-flex flex-wrap">
          <v-btn
              v-for="buttonConfig in buttonsConfig"
              :type="buttonConfig.isSubmit ? 'submit' : ''"
              :color="buttonConfig.color"
              :loading="buttonConfig.isSubmit && requestSent"
              @click="onClick(buttonConfig.key)"
          >
            {{ getButtonText(buttonConfig.text) }}
          </v-btn>
        </div>
      </v-form>
    </v-card-text>
  </card-with-layout>
</template>

<script>
import PageBase from "~/components/base/page-base.vue";
import formValidation from "~/mixins/form-validation";
import formValues from "~/mixins/form-values";
import showPassword from "~/mixins/show-password";
import recaptcha from "~/mixins/recaptcha";
import {dataStore} from "~/stores/data-store";
import ApiProvider from "~/api/api-provider";
import CardWithLayout from "~/components/common/card-with-layout.vue";
import TextInput from "~/components/common/text-input.vue";

export default {
  name: 'sign-page-base',
  components: {TextInput, CardWithLayout},
  extends: PageBase,
  mixins: [formValidation, formValues, showPassword, recaptcha],
  data: () => ({
    requestSent: false,
    requestSuccess: false,
    switchingSignForms: false,
    form: {},
    buttonsConfig: []
  }),
  computed: {
    waitingForAuthorization() {
      return dataStore().waitingForAuthorization
    },
    isSignedIn() {
      return dataStore().isSignedIn
    }
  },
  watch: {
    isSignedIn() {
      this.redirectToStartPageIfSignedIn()
    }
  },
  beforeUnmount() {
    !this.switchingSignForms && dataStore().clearWaitingForAuthorization()
  },
  methods: {
    request(url, errorKey) {
      this.loadWithFlag(async () => {
        const token = await this.recaptcha()
        const response = await ApiProvider.postJson(url, {...this.formValues, token})
        if (response.ok) {
          this.requestSuccess = true
          dataStore().setUserInfo(response.data)
          await useRouter().push(this.waitingForAuthorization ? this.waitingForAuthorization : '/')
        } else {
          this.parseValidation(response.errors)
          this.showErrorsFromResponse(response, this._t(errorKey))
        }
      }, 'requestSent').then()
    },
    redirectToStartPageIfSignedIn() {
      if (this.isSignedIn && !this.requestSuccess) {
        this.showMessage({
          text: this._tc('messages.alreadySignedIn'),
          onHide: () => {
            useRouter().push('/').then()
          }
        })
      }
    },
    getButtonText(config) {
      return this[config.func](config.key)
    },
    onClick(key) {
      this[key]()
    },
  },
}
</script>
