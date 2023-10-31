<template>
  <card-with-layout :message="message" :confirm="confirm" :loading-overlay="loadingOverlay">
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
      <v-form @submit.prevent>
        <text-input
            v-for="field in filteredFormFields"
            ref="inputRefs"
            :field="field"
            :field-obj="form[field]"
            @update="v => form[field].value = v"/>
        <div class="d-flex flex-wrap">
          <v-btn type="submit" color="success" :loading="saveRequestSent" @click="save">
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
  </card-with-layout>
</template>

<script>
import {mdiDelete} from "@mdi/js";
import PageBase from "../components/page-base";
import formValidation from "../mixins/form-validation";
import {dataStore} from "~/stores/data-store";
import formValues from "../mixins/form-values";
import filterPatronymicField from "../mixins/filter-patronymic-field";

const ME_URL = '/api/users/me'

export default {
  name: "user-settings",
  extends: PageBase,
  mixins: [formValidation, formValues, filterPatronymicField],
  data: () => ({
    mdiDelete,
    form: {
      email: {value: ''},
      firstName: {value: ''},
      lastName: {value: ''},
      patronymic: {value: ''},
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
    this.focusFirstFormField()
  },
  methods: {
    async reloadUserInfo() {
      await this.loadWithOverlay(async () => {
        const response = await this.getApiProvider().get(ME_URL)
        if (response.ok) {
          dataStore().setUserInfo(response.data)
        } else {
          this.showErrorsFromResponse(response)
        }
      })
    },
    parseUserInfo(userInfo) {
      if (!userInfo || !userInfo.id) {
        return
      }
      this.emailConfirmed = userInfo.emailConfirmed
      this.formFields.forEach(field => {
        this.formValue(field, userInfo[field])
      })
    },
    async save() {
      this.clearValidation()
      await this.loadWithFlag(async () => {
        const response = await this.getApiProvider().putJson(ME_URL, this.formValues)
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
      const response = await this.getApiProvider().del(ME_URL)
      if (response.ok) {
        dataStore().clearUserInfo()
        await useRouter().push('/')
      } else {
        this.showErrorsFromResponse(response, this._t('deleteError'))
      }
    },
    async sendConfirmEmailRequest() {
      await this.loadWithFlag(async () => {
        const response = await this.getApiProvider().postJson('/api/users/confirm', {
          origin: window.location.origin,
          localeTitle: this._t('confirmEmailMailTitle'),
          localeMsg: this._t('confirmEmailMailMsg'),
        })
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
