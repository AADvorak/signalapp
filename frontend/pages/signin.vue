<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <p v-if="waitingForAuthorization" class="mb-5">{{ _t('signInToContinue') }}</p>
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
              <v-btn type="submit" color="success" :loading="signInRequestSent" @click="signInRequest">
                {{ _tc('buttons.signIn') }}
              </v-btn>
              <v-btn color="secondary" to="/signup">
                {{ _tc('buttons.signUp') }}
              </v-btn>
              <v-btn color="secondary" @click="restorePassword">
                {{ _t('forgotPassword') }}
              </v-btn>
            </div>
          </v-form>
        </v-card-text>
      </v-card>
    </div>
    <message :opened="message.opened" :text="message.text" @hide="message.onHide"/>
  </NuxtLayout>
</template>

<script>
import ApiProvider from "../api/api-provider";
import {dataStore} from "../stores/data-store";
import formValidation from "../mixins/form-validation"
import PageBase from "../components/page-base";
import TextInput from "../components/text-input";
import formValues from "../mixins/form-values";
import showPassword from "../mixins/show-password";
import recaptcha from "../mixins/recaptcha";

export default {
  name: 'signin',
  components: {TextInput},
  extends: PageBase,
  mixins: [formValidation, formValues, showPassword, recaptcha],
  data: () => ({
    form: {
      email: {value: ''},
      password: {value: ''},
    },
    signInRequestSent: false,
    signInRequestSuccess: false
  }),
  computed: {
    waitingForAuthorization() {
      return dataStore().getWaitingForAuthorization
    },
    userInfo() {
      return dataStore().userInfo
    }
  },
  watch: {
    userInfo() {
      this.redirectToStartPageIfSignedIn()
    }
  },
  mounted() {
    this.formValue('email', dataStore().emailForPasswordRestore || '')
    this.focusFirstFormField()
    this.redirectToStartPageIfSignedIn()
  },
  beforeUnmount() {
    dataStore().clearWaitingForAuthorization()
  },
  methods: {
    async signInRequest() {
      this.clearValidation()
      await this.loadWithFlag(async () => {
        const token = await this.recaptcha()
        const response = await ApiProvider.postJson('/api/sessions/', {...this.formValues, token})
        if (response.ok) {
          this.signInRequestSuccess = true
          dataStore().setUserInfo(response.data)
          await useRouter().push(this.waitingForAuthorization ? this.waitingForAuthorization : '/')
        } else if (response.status === 400) {
          this.parseValidation(response.errors)
          this.showRecaptchaValidationError(response.errors)
        } else {
          this.showErrorsFromResponse(response, this._t('signInError'))
        }
      }, 'signInRequestSent')
    },
    restorePassword() {
      dataStore().emailForPasswordRestore = this.formValues.email
      useRouter().push('/restore-password')
    },
    redirectToStartPageIfSignedIn() {
      if (this.userInfo && !this.signInRequestSuccess) {
        this.showMessage({
          text: this._t('alreadySignedIn'),
          onHide: () => {
            useRouter().push('/')
          }
        })
      }
    }
  },
}
</script>
