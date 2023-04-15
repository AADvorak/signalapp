<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <v-form @submit.prevent="signUpRequest">
            <v-text-field
                v-model="form.email"
                :label="_tc('fields.email')"
                :error="!!validation.email.length"
                :error-messages="validation.email"
                required/>
            <v-text-field
                v-model="form.password"
                :append-icon="showPassword ? mdiEyeOff : mdiEye"
                :type="showPassword ? 'text' : 'password'"
                :label="_tc('fields.password')"
                @click:append="showPassword = !showPassword"
                :error="!!validation.password.length"
                :error-messages="validation.password"
                required/>
            <v-text-field
                v-model="form.passwordRepeat"
                :append-icon="showPassword ? mdiEyeOff : mdiEye"
                :type="showPassword ? 'text' : 'password'"
                :label="_tc('fields.passwordRepeat')"
                @click:append="showPassword = !showPassword"
                :error="!!validation.passwordRepeat.length"
                :error-messages="validation.passwordRepeat"
                required/>
            <v-text-field
                v-model="form.firstName"
                :label="_tc('fields.firstName')"
                :error="!!validation.firstName.length"
                :error-messages="validation.firstName"/>
            <v-text-field
                v-model="form.lastName"
                :label="_tc('fields.lastName')"
                :error="!!validation.lastName.length"
                :error-messages="validation.lastName"/>
            <v-text-field
                v-model="form.patronymic"
                :label="_tc('fields.patronymic')"
                :error="!!validation.patronymic.length"
                :error-messages="validation.patronymic"/>
            <div class="d-flex">
              <v-btn color="success" :loading="signUpRequestSent" @click="signUpRequest">
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
  </NuxtLayout>
</template>

<script>
import formValidation from "../mixins/form-validation";
import {mdiEye, mdiEyeOff} from "@mdi/js";
import ApiProvider from "../api/api-provider";
import {dataStore} from "../stores/data-store";
import PageBase from "../components/page-base";

export default {
  name: 'signup',
  extends: PageBase,
  mixins: [formValidation],
  data: () => ({
    mdiEye,
    mdiEyeOff,
    showPassword: false,
    form: {
      email: '',
      password: '',
      passwordRepeat: '',
      firstName: '',
      lastName: '',
      patronymic: '',
    },
    validation: {
      email: [],
      password: [],
      passwordRepeat: [],
      firstName: [],
      lastName: [],
      patronymic: [],
    },
    signUpRequestSent: false
  }),
  methods: {
    async signUpRequest() {
      this.clearValidation()
      if (this.form.passwordRepeat !== this.form.password) {
        const msg = this._tc('validation.same')
        this.validation.password.push(msg)
        this.validation.passwordRepeat.push(msg)
        return
      }
      await this.loadWithFlag(async () => {
        let response = await ApiProvider.postJson('/api/users/', this.form)
        if (response.ok) {
          dataStore().setUserInfo(response.data)
          let waitingForAuthorization = dataStore().getWaitingForAuthorization
          useRouter().push(waitingForAuthorization ? waitingForAuthorization : '/')
          waitingForAuthorization && dataStore().clearWaitingForAuthorization()
        } else if (response.status === 400) {
          this.parseValidation(response.errors)
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
