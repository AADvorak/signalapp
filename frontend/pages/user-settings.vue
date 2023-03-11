<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <div v-if="!emailConfirmed && !confirmEmailSent" class="mb-5">
            <p style="color: #CF6679">Email is not confirmed</p>
            <v-btn color="primary" :loading="confirmEmailRequestSent" @click="sendConfirmEmailRequest">
              Confirm
            </v-btn>
          </div>
          <div v-if="emailConfirmed" class="mb-5">
            <p style="color: #4CAF50">Email is confirmed</p>
          </div>
          <v-form>
            <v-text-field
                v-model="form.email"
                label="Email"
                :error="!!validation.email.length"
                :error-messages="validation.email"
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
            <div class="d-flex flex-wrap">
              <v-btn color="success" @click="save">
                Save
              </v-btn>
              <v-btn color="secondary" to="/change-password">
                Change password
              </v-btn>
              <v-btn color="error" @click="askConfirmDeleteAccount">
                Delete account
                <v-icon>{{ mdiDelete }}</v-icon>
              </v-btn>
            </div>
          </v-form>
        </v-card-text>
      </v-card>
    </div>
    <message :opened="message.opened" :text="message.text" @hide="message.onHide"/>
    <confirm-dialog :opened="confirm.opened" :text="confirm.text" ok-color="error"
                    @ok="confirm.ok" @cancel="confirm.cancel"/>
  </NuxtLayout>
</template>

<script>
import {mdiDelete} from "@mdi/js";
import PageBase from "../components/page-base";
import formValidation from "../mixins/form-validation";
import {dataStore} from "../stores/data-store";

export default {
  name: "user-settings",
  extends: PageBase,
  mixins: [formValidation],
  data: () => ({
    mdiDelete,
    form: {
      email: '',
      firstName: '',
      lastName: '',
      patronymic: '',
    },
    validation: {
      email: [],
      firstName: [],
      lastName: [],
      patronymic: [],
    },
    emailConfirmed: true,
    confirmEmailSent: false,
    confirmEmailRequestSent: false,
  }),
  mounted() {
    this.reloadUserInfo()
    dataStore().$subscribe((mutation, state) => {
      this.parseUserInfo(state.userInfo)
    })
  },
  methods: {
    async reloadUserInfo() {
      await this.loadWithOverlay(async () => {
        const response = await this.getApiProvider().get('/api/users/me/')
        if (response.ok) {
          dataStore().setUserInfo(response.data)
        }
      })
    },
    parseUserInfo(userInfo) {
      if (!userInfo || !userInfo.id) {
        return
      }
      this.emailConfirmed = userInfo.emailConfirmed
      this.form.email = userInfo.email
      this.form.firstName = userInfo.firstName
      this.form.lastName = userInfo.lastName
      this.form.patronymic = userInfo.patronymic
    },
    async save() {
      this.clearValidation()
      const response = await this.getApiProvider().putJson('/api/users/me/', this.form)
      if (response.ok) {
        dataStore().setUserInfo(response.data)
        this.showMessage({
          text: 'User info saved'
        })
      } else if (response.status === 400) {
        this.parseValidation(response.errors)
      }
    },
    askConfirmDeleteAccount() {
      this.askConfirm({
        text: 'Are you sure to delete your account? All your data will be removed immediately with no possibility of recover.',
        ok: () => {
          this.deleteAccount()
        }
      })
    },
    async deleteAccount() {
      const response = await this.getApiProvider().del('/api/users/me/')
      if (response.ok) {
        dataStore().clearUserInfo()
        useRouter().push('/')
      } else {
        this.showErrorsFromResponse(response, 'Error deleting account')
      }
    },
    async sendConfirmEmailRequest() {
      this.confirmEmailRequestSent = true
      try {
        const response = await this.getApiProvider().post('/api/users/confirm', window.location.origin)
        if (response.ok) {
          this.confirmEmailSent = true
          this.showMessage({
            text: `Confirm message is sent, check your email ${dataStore().userInfo.email}`
          })
        } else {
          this.showErrorsFromResponse(response, 'Error sending email')
        }
      } finally {
        this.confirmEmailRequestSent = false
      }
    }
  }
}
</script>
