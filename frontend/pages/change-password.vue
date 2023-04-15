<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <v-form @submit.prevent="">
            <v-text-field
                v-model="form.oldPassword"
                :append-icon="showPassword ? mdiEyeOff : mdiEye"
                :type="showPassword ? 'text' : 'password'"
                :label="_t('oldPassword')"
                @click:append="showPassword = !showPassword"
                :error="!!validation.oldPassword.length"
                :error-messages="validation.oldPassword"
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
            <div class="d-flex">
              <v-btn color="success" :loading="changePasswordRequestSent" @click="changePasswordRequest">
                {{ _tc('buttons.changePassword') }}
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
import {mdiEye, mdiEyeOff} from "@mdi/js";
import PageBase from "../components/page-base";

export default {
  name: "change-password",
  extends: PageBase,
  mixins: [formValidation],
  data: () => ({
    mdiEye,
    mdiEyeOff,
    showPassword: false,
    form: {
      oldPassword: '',
      password: '',
      passwordRepeat: '',
    },
    validation: {
      oldPassword: [],
      password: [],
      passwordRepeat: [],
    },
    changePasswordRequestSent: false
  }),
  methods: {
    async changePasswordRequest() {
      this.clearValidation()
      if (this.form.passwordRepeat !== this.form.password) {
        const msg = this._tc('validation.same')
        this.validation.password.push(msg)
        this.validation.passwordRepeat.push(msg)
        return
      }
      await this.loadWithFlag(async () => {
        const response = await this.getApiProvider().putJson('/api/users/me/password', this.form)
        if (response.ok) {
          this.showMessage({
            text: this._t('passwordChangeSuccess')
          })
          this.form = {
            oldPassword: '',
            password: '',
            passwordRepeat: '',
          }
        } else if (response.status === 400) {
          this.parseValidation(response.errors)
        } else {
          this.showErrorsFromResponse(response, this._t('passwordChangeError'))
        }
      }, 'changePasswordRequestSent')
    }
  }
}
</script>
