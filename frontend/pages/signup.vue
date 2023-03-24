<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <v-form @submit.prevent="signUpRequest">
            <v-text-field
                v-model="form.email"
                label="Email"
                :error="!!validation.email.length"
                :error-messages="validation.email"
                required/>
            <v-text-field
                v-model="form.password"
                :append-icon="showPassword ? mdiEyeOff : mdiEye"
                :type="showPassword ? 'text' : 'password'"
                label="Password"
                @click:append="showPassword = !showPassword"
                :error="!!validation.password.length"
                :error-messages="validation.password"
                required/>
            <v-text-field
                v-model="form.passwordRepeat"
                :append-icon="showPassword ? mdiEyeOff : mdiEye"
                :type="showPassword ? 'text' : 'password'"
                label="Password repeat"
                @click:append="showPassword = !showPassword"
                :error="!!validation.passwordRepeat.length"
                :error-messages="validation.passwordRepeat"
                required/>
            <v-text-field
                v-model="form.firstName"
                label="First name"
                :error="!!validation.firstName.length"
                :error-messages="validation.firstName"/>
            <v-text-field
                v-model="form.lastName"
                label="Last name"
                :error="!!validation.lastName.length"
                :error-messages="validation.lastName"/>
            <v-text-field
                v-model="form.patronymic"
                label="Patronymic"
                :error="!!validation.patronymic.length"
                :error-messages="validation.patronymic"/>
            <div class="d-flex">
              <v-btn color="primary" @click="signUpRequest">
                Sign up
              </v-btn>
              <v-btn color="secondary" @click="signIn">
                Sign in
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

export default {
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
  }),
  methods: {
    async signUpRequest() {
      this.clearValidation()
      if (this.form.passwordRepeat !== this.form.password) {
        const msg = 'Must be the same'
        this.validation.password.push(msg)
        this.validation.passwordRepeat.push(msg)
        return
      }
      let response = await ApiProvider.postJson('/api/users/', this.form)
      if (!response.ok) {
        if (response.status === 400) {
          this.parseValidation(response.errors)
        }
      } else {
        dataStore().setUserInfo(response.data)
        let waitingForAuthorization = dataStore().getWaitingForAuthorization
        useRouter().push(waitingForAuthorization ? waitingForAuthorization : '/')
        waitingForAuthorization && dataStore().clearWaitingForAuthorization()
      }
    },
    signIn() {
      useRouter().push('/signin')
    },
  },
}
</script>
