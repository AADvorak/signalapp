<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <v-form @submit.prevent>
            <text-input
                v-for="field in filteredFormFields"
                ref="inputRefs"
                :field="field"
                :field-obj="form[field]"
                :show-password="showPassword"
                @update="v => form[field].value = v"
                @show="switchShowPassword"/>
            <div class="d-flex">
              <v-btn type="submit" color="success" :loading="signUpRequestSent" @click="signUpRequest">
                {{ _tc('buttons.signUp') }}
              </v-btn>
              <v-btn color="secondary" @click="signIn">
                {{ _tc('buttons.signIn') }}
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
import formValidation from "../mixins/form-validation";
import ApiProvider from "../api/api-provider";
import {dataStore} from "../stores/data-store";
import PageBase from "../components/page-base";
import TextInput from "../components/text-input";
import formValues from "../mixins/form-values";
import showPassword from "../mixins/show-password";
import filterPatronymicField from "../mixins/filter-patronymic-field";
import recaptcha from "../mixins/recaptcha";

export default {
  name: 'signup',
  components: {TextInput},
  extends: PageBase,
  mixins: [formValidation, formValues, showPassword, filterPatronymicField, recaptcha],
  data: () => ({
    form: {
      email: {value: ''},
      password: {value: ''},
      passwordRepeat: {value: ''},
      firstName: {value: ''},
      lastName: {value: ''},
      patronymic: {value: ''},
    },
    signUpRequestSent: false
  }),
  mounted() {
    this.focusFirstFormField()
  },
  methods: {
    async signUpRequest() {
      this.clearValidation()
      if (this.formValues.passwordRepeat !== this.formValues.password) {
        const msg = this._tc('validation.same')
        this.pushValidationMsg('password', msg)
        this.pushValidationMsg('passwordRepeat', msg)
        return
      }
      await this.loadWithFlag(async () => {
        const token = await this.recaptcha()
        let response = await ApiProvider.postJson('/api/users/', {...this.formValues, token})
        if (response.ok) {
          dataStore().setUserInfo(response.data)
          let waitingForAuthorization = dataStore().getWaitingForAuthorization
          useRouter().push(waitingForAuthorization ? waitingForAuthorization : '/')
          waitingForAuthorization && dataStore().clearWaitingForAuthorization()
        } else if (response.status === 400) {
          this.parseValidation(response.errors)
          this.showRecaptchaValidationError(response.errors)
        } else {
          this.showErrorsFromResponse(response, this._t('signUpError'))
        }
      }, 'signUpRequestSent')
    },
    signIn() {
      useRouter().push('/signin')
    },
  },
}
</script>
