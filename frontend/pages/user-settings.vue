<template>
  <NuxtLayout name="default">
    <div class="d-flex align-center flex-column">
      <v-card width="100%" min-width="400" max-width="800">
        <v-card-text>
          <div v-if="!emailConfirmed && !confirmEmailSent" class="mb-5">
            <p style="color: #CF6679">{{ _t('emailNotConfirmed') }}</p>
            <v-btn color="primary" :loading="confirmEmailRequestSent" @click="sendConfirmEmailRequest">
              {{ _t('confirm') }}
            </v-btn>
          </div>
          <div v-if="emailConfirmed" class="mb-5">
            <p style="color: #4CAF50">{{ _t('emailConfirmed') }}</p>
          </div>
          <v-form>
            <v-text-field
                v-model="form.email"
                :label="_tc('fields.email')"
                :error="!!validation.email.length"
                :error-messages="validation.email"
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
                v-if="$i18n.locale === 'ru'"
                v-model="form.patronymic"
                :label="_tc('fields.patronymic')"
                :error="!!validation.patronymic.length"
                :error-messages="validation.patronymic"/>
            <div class="d-flex flex-wrap">
              <v-btn color="success" :loading="saveRequestSent" @click="save">
                {{ _tc('buttons.save') }}
              </v-btn>
              <v-btn color="secondary" to="/change-password">
                {{ _tc('buttons.changePassword') }}
              </v-btn>
              <v-btn color="error" @click="askConfirmDeleteAccount">
                {{ _t('deleteAccount') }}
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
    <loading-overlay :show="loadingOverlay"/>
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
    saveRequestSent: false
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
      await this.loadWithFlag(async () => {
        const response = await this.getApiProvider().putJson('/api/users/me/', this.form)
        if (response.ok) {
          dataStore().setUserInfo(response.data)
          this.showMessage({
            text: this._t('saveSuccess')
          })
        } else if (response.status === 400) {
          this.parseValidation(response.errors)
        } else {
          this.showErrorsFromResponse(response, this._t('saveError'))
        }
      }, 'saveRequestSent')
    },
    askConfirmDeleteAccount() {
      this.askConfirm({
        text: this._t('deleteConfirm'),
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
        this.showErrorsFromResponse(response, this._t('deleteError'))
      }
    },
    async sendConfirmEmailRequest() {
      await this.loadWithFlag(async () => {
        const response = await this.getApiProvider().post('/api/users/confirm', window.location.origin)
        if (response.ok) {
          this.confirmEmailSent = true
          this.showMessage({
            text: this._t('confirmSentCheckEmail', {email: dataStore().userInfo.email})
          })
        } else {
          this.showErrorsFromResponse(response, this._t('sendEmailError'))
        }
      }, 'confirmEmailRequestSent')
    }
  }
}
</script>
