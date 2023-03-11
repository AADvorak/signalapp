<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <p v-if="waitingForAuthorization" class="mb-5">Please sign in to continue</p>
          <v-form @submit.prevent="signInRequest">
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
            <div class="d-flex">
              <v-btn color="primary" @click="signInRequest">
                Sign in
              </v-btn>
              <v-btn color="secondary" to="/signup">
                Sign up
              </v-btn>
              <v-btn color="secondary" @click="restorePassword">
                Forgot password
              </v-btn>
            </div>
          </v-form>
        </v-card-text>
      </v-card>
    </div>
  </NuxtLayout>
</template>

<script>
import {mdiEye, mdiEyeOff} from "@mdi/js";
import ApiProvider from "../api/api-provider";
import {dataStore} from "../stores/data-store";
import formValidation from "../mixins/form-validation"

export default {
  mixins: [formValidation],
  data: () => ({
    mdiEye,
    mdiEyeOff,
    showPassword: false,
    form: {
      email: '',
      password: '',
    },
    validation: {
      email: [],
      password: []
    },
  }),
  computed: {
    waitingForAuthorization() {
      return dataStore().getWaitingForAuthorization
    }
  },
  mounted() {
    this.form.email = dataStore().emailForPasswordRestore || ''
  },
  methods: {
    async signInRequest() {
      this.clearValidation()
      let response = await ApiProvider.postJson('/api/sessions/', this.form)
      if (response.ok) {
        dataStore().setUserInfo(response.data)
        useRouter().push(this.waitingForAuthorization ? this.waitingForAuthorization : '/')
        this.waitingForAuthorization && dataStore().clearWaitingForAuthorization()
      } else if (response.status === 400) {
        this.parseValidation(response.errors)
      }
    },
    restorePassword() {
      dataStore().emailForPasswordRestore = this.form.email
      useRouter().push('/restore-password')
    }
  },
}
</script>
