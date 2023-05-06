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
            <text-input
                v-for="field in filteredFormFields"
                :field="field"
                :field-obj="form[field]"/>
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
import TextInput from "../components/text-input";
import formValues from "../mixins/form-values";
import filterPatronymicField from "../mixins/filter-patronymic-field";

export default {
  name: "user-settings",
  components: {TextInput},
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
      this.formFields.forEach(field => {
        this.formValue(field, userInfo[field])
      })
    },
    async save() {
      this.clearValidation()
      await this.loadWithFlag(async () => {
        const response = await this.getApiProvider().putJson('/api/users/me/', this.formValues)
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
