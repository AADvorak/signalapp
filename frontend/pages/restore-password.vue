<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <v-form @submit.prevent="restorePasswordRequest">
            <v-text-field
                v-model="form.email"
                label="Email"
                :error="!!validation.email.length"
                :error-messages="validation.email"
                required/>
            <div class="d-flex">
              <v-btn color="primary" @click="restorePasswordRequest">
                Restore password
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

export default {
  name: "restore-password",
  extends: PageBase,
  mixins: [formValidation],
  data: () => ({
    form: {
      email: '',
    },
    validation: {
      email: [],
    },
  }),
  mounted() {
    this.form.email = dataStore().emailForPasswordRestore || ''
  },
  methods: {
    async restorePasswordRequest() {
      this.clearValidation()
      const response = await ApiProvider.postJson('/api/users/restore/' + this.form.email, {})
      if (response.ok) {
        this.showMessage({
          text: 'New password is sent by email',
          onHide: () => useRouter().push('/signin')
        })
      } else if (response.status === 400) {
        this.parseValidation(response.errors)
      } else {
        this.showErrorsFromResponse(response, 'Error sending new password')
      }
    }
  }
}
</script>
