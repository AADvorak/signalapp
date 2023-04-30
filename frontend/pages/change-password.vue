<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <v-form @submit.prevent="">
            <text-input
                v-for="field in formFields"
                :field="field"
                :field-obj="form[field]"
                :show-password="showPassword"
                @show="switchShowPassword"/>
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
import PageBase from "../components/page-base";
import formValues from "../mixins/form-values";
import showPassword from "../mixins/show-password";
import TextInput from "../components/text-input";

export default {
  name: "change-password",
  components: {TextInput},
  extends: PageBase,
  mixins: [formValidation, formValues, showPassword],
  data: () => ({
    form: {
      oldPassword: {value: ''},
      password: {value: ''},
      passwordRepeat: {value: ''},
    },
    changePasswordRequestSent: false
  }),
  methods: {
    async changePasswordRequest() {
      this.clearValidation()
      if (this.formValues.passwordRepeat !== this.formValues.password) {
        const msg = this._tc('validation.same')
        this.pushValidationMsg('password', msg)
        this.pushValidationMsg('passwordRepeat', msg)
        return
      }
      await this.loadWithFlag(async () => {
        const response = await this.getApiProvider().putJson('/api/users/me/password', this.formValues)
        if (response.ok) {
          this.showMessage({
            text: this._t('passwordChangeSuccess')
          })
          this.clearForm()
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
