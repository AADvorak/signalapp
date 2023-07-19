<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <v-form @submit.prevent>
            <text-input
                v-for="field in formFields"
                :field="field"
                :field-obj="form[field]"
                @update="v => form[field].value = v"/>
            <div class="d-flex">
              <v-btn type="submit" color="primary" :loading="restorePasswordRequestSent" @click="restorePasswordRequest">
                {{ _t('name') }}
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
import {dataStore} from "../stores/data-store";
import ApiProvider from "../api/api-provider";
import PageBase from "../components/page-base";
import formValues from "../mixins/form-values";
import TextInput from "../components/text-input";

export default {
  name: "restore-password",
  components: {TextInput},
  extends: PageBase,
  mixins: [formValidation, formValues],
  data: () => ({
    form: {
      email: {value: ''},
    },
    restorePasswordRequestSent: false,
  }),
  mounted() {
    this.formValue('email', dataStore().emailForPasswordRestore || '')
  },
  methods: {
    async restorePasswordRequest() {
      this.clearValidation()
      await this.loadWithFlag(async () => {
        const response = await ApiProvider.postJson('/api/users/restore/', {
          email: this.formValues.email,
          localeTitle: this._t('restorePasswordMailTitle'),
          localeMsg: this._t('restorePasswordMailMsg'),
        })
        if (response.ok) {
          this.showMessage({
            text: this._t('newPasswordSentByEmail'),
            onHide: () => useRouter().push('/signin')
          })
        } else if (response.status === 400) {
          this.parseValidation(response.errors)
        } else {
          this.showErrorsFromResponse(response, this._t('passwordSendError'))
        }
      }, 'restorePasswordRequestSent')
    }
  }
}
</script>
