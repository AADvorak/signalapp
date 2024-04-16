<template>
  <card-with-layout :message="message">
    <v-card-text>
      <v-form @submit.prevent>
        <text-input
            v-for="field in formFields"
            ref="inputRefs"
            :field="field"
            :field-obj="form[field]"
            :show-password="showPassword"
            @update="v => form[field].value = v"
            @show="switchShowPassword"/>
        <div class="d-flex">
          <v-btn type="submit" color="success" :loading="changePasswordRequestSent" @click="changePasswordRequest">
            {{ _tc('buttons.changePassword') }}
          </v-btn>
        </div>
      </v-form>
    </v-card-text>
  </card-with-layout>
</template>

<script>
import formValidation from "../mixins/form-validation";
import PageBase from "../components/base/page-base.vue";
import formValues from "../mixins/form-values";
import showPassword from "../mixins/show-password";
import CardWithLayout from "~/components/common/card-with-layout.vue";
import TextInput from "~/components/common/text-input.vue";

export default {
  name: "change-password",
  components: {TextInput, CardWithLayout},
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
  mounted() {
    this.focusFirstFormField()
  },
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
            text: this._t('passwordChangeSuccess'),
            onHide: () => useRouter().push('/user-settings')
          })
          this.clearForm()
        } else {
          this.parseValidation(response.errors)
          this.showErrorsFromResponse(response, this._t('passwordChangeError'))
        }
      }, 'changePasswordRequestSent')
    }
  }
}
</script>
