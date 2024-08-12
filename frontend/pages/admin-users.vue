<template>
  <card-with-layout full-width :message="message" :confirm="confirm" :loading-overlay="loadingOverlay">
    <template #default>
      <v-card-text>
        <fixed-width-wrapper>
          <v-expansion-panels v-model="uiParams.openedPanels" class="mb-1">
            <v-expansion-panel value="loadParams">
              <v-expansion-panel-title>
                {{ _tc('pagination.loadParams') }}
                <btn-with-tooltip
                    :disabled="filterIsEmpty"
                    tooltip="clear"
                    @click="clearFilter"
                >
                  <v-icon>
                    {{ mdi.mdiFilterOff }}
                  </v-icon>
                </btn-with-tooltip>
              </v-expansion-panel-title>
              <v-expansion-panel-text>
                <v-form>
                  <div class="d-flex justify-center flex-wrap">
                    <text-input
                        class="param-input w-full"
                        field="search"
                        :field-obj="form.search"
                        @update="v => form.search.value = v"/>
                    <v-select
                        class="param-input"
                        v-model="form.roleIds.value"
                        item-title="localeName"
                        item-value="id"
                        :items="roles"
                        :label="_t('roles')"
                        multiple
                    >
                      <template v-slot:no-data>
                        {{ _tc('messages.noData') }}
                      </template>
                    </v-select>
                  </div>
                </v-form>
              </v-expansion-panel-text>
            </v-expansion-panel>
          </v-expansion-panels>
        </fixed-width-wrapper>
        <data-viewer
            ref="dataViewer"
            data-name="users"
            data-url="/api/admin/users/page"
            caption="email"
            pagination
            :check-role="requiredRole"
            :filtering-params-config="filteringParamsConfig"
            :filters="filters"
            :columns="dataViewerConfig.columns"
            :buttons="dataViewerConfig.buttons"
            :reserved-height="reservedHeight"
            :sort-cols="['firstName', 'lastName', 'patronymic', 'createTime', 'email']"
            :bus="bus"
            @click="onDataViewerButtonClick"
            @update:url-params="setUrlParams"
            @update:loading-overlay="value => loadingOverlay = value"
            @update:filters="onDataViewerUpdateFilters">
          <template #dataEmpty>
            <h3 style="text-align: center;">{{ _tc('messages.nothingIsFound') }}</h3>
          </template>
        </data-viewer>
      </v-card-text>
    </template>
  </card-with-layout>
</template>

<script>
import PageBase from "~/components/base/page-base.vue";
import CardWithLayout from "~/components/common/card-with-layout.vue";
import TextInput from "~/components/common/text-input.vue";
import FixedWidthWrapper from "~/components/common/fixed-width-wrapper.vue";
import NumberInput from "~/components/common/number-input.vue";
import BtnWithTooltip from "~/components/common/btn-with-tooltip.vue";
import DataViewer from "~/components/common/data-viewer.vue";
import {mdiDelete, mdiFileEdit, mdiFilterOff} from "@mdi/js";
import formNumberValues from "~/mixins/form-number-values";
import formValidation from "~/mixins/form-validation";
import formValuesSaving from "~/mixins/form-values-saving";
import actionWithTimeout from "~/mixins/action-with-timeout";
import uiParamsSaving from "~/mixins/ui-params-saving";
import filtering from "~/mixins/filtering";
import {roleStore} from "~/stores/role-store";
import {userStore} from "~/stores/user-store";
import {DateTimeUtils} from "~/utils/date-time-utils";
import {Roles} from "~/dictionary/roles";
import requiredRoleMsg from "~/mixins/required-role-msg";
import mitt from "mitt";
import {DataViewerEvents} from "~/dictionary/data-viewer-events";

const isNotCurrentUser = user => user.id !== userStore().userInfo?.id

export default {
  name: 'admin-users',
  components: {DataViewer, BtnWithTooltip, NumberInput, FixedWidthWrapper, TextInput, CardWithLayout},
  extends: PageBase,
  mixins: [
    formNumberValues, formValidation, formValuesSaving, actionWithTimeout, uiParamsSaving,
    filtering, requiredRoleMsg
  ],
  data: () => ({
    additionalFilteringParamsConfig: [
      {
        name: 'roleIds',
        readFunc: parseInt,
        isArray: true,
        emptyValue: []
      }
    ],
    mounted: false,
    form: {
      search: {value: ''},
      roleIds: {value: []}
    },
    dataViewerConfig: {
      columns: [
        {name: 'emailConfirmed', localeKeyGetter: value => value ? 'common.messages.yes' : 'common.messages.no'},
        'firstName', 'lastName', 'patronymic',
        {name: 'createTime', formatter: DateTimeUtils.getLocalDateLocaleString},
        {name: 'lastActionTime', formatter: DateTimeUtils.getLocalDateLocaleString},
        'storedSignalsNumber',
        {name: 'roles', valuePath: 'name', localeKeyGetter: value => `userRoles.${value}`, isArray: true},
      ],
      buttons: [
        {
          name: 'edit',
          icon: mdiFileEdit,
          color: 'primary',
          component: 'user-roles-menu',
          condition: isNotCurrentUser
        },
        {
          name: 'delete',
          icon: mdiDelete,
          color: 'error',
          condition: isNotCurrentUser
        }
      ],
    },
    uiParams: {
      openedPanels: []
    },
    mdi: {
      mdiFilterOff
    },
    bus: new mitt(),
    requiredRole: Roles.ADMIN
  }),
  computed: {
    reservedHeight() {
      return this.uiParams.openedPanels && this.uiParams.openedPanels.includes('loadParams') ? 354 : 254
    },
    roles() {
      return roleStore().roles.map(role => ({...role, localeName: this.$t(`userRoles.${role.name}`)}))
    }
  },
  mounted() {
    this.mounted = true
    this.restoreFormValues()
    this.restoreUiParams()
    this.loadRoles()
    setTimeout(() => this.requiredRoleMsg(this.requiredRole))
    this.bus.on(DataViewerEvents.NEW_USER_ROLES, this.onNewUserRoles)
    this.bus.on(DataViewerEvents.USER_ROLES_MENU_CLOSED_ROLES_CHANGED, this.onUserRolesMenuClosedRolesChanged)
  },
  beforeUnmount() {
    this.mounted = false
    this.bus.off(DataViewerEvents.NEW_USER_ROLES)
    this.bus.off(DataViewerEvents.USER_ROLES_MENU_CLOSED_ROLES_CHANGED)
  },
  methods: {
    async loadRoles() {
      if (!userStore().checkUserRole(this.requiredRole)) {
        return
      }
      const response = await this.getApiProvider().get('/api/admin/roles')
      if (response.ok) {
        roleStore().roles = response.data
      }
    },
    async loadDataPage() {
      await this.$refs.dataViewer?.loadDataPage()
    },
    setUrlParams(urlParams) {
      if (!this.mounted) {
        return
      }
      useRouter().push(`/admin-users${urlParams}`)
    },
    onDataViewerButtonClick({button, item}) {
      if (button === 'delete') {
        this.askConfirmDeleteUser(item)
      }
    },
    onUserRolesMenuClosedRolesChanged() {
      this.dataPageLastRequest = ''
      this.loadDataPage()
    },
    onNewUserRoles(event) {
      const users = this.$refs.dataViewer?.getData()?.items || []
      const user = users.filter(user => user.id === event.userId)[0]
      if (user) {
        user.roles = event.roles
      }
    },
    askConfirmDeleteUser(user) {
      this.askConfirm({
        text: this._t('confirmDeleteUser', {email: user.email}),
        ok: () => {
          this.deleteUser(user)
        }
      })
    },
    async deleteUser(user) {
      let response = await this.getApiProvider().del(`/api/admin/users/${user.id}`)
      if (response.ok) {
        this.dataPageLastRequest = ''
        await this.loadDataPage()
      }
    },
  }
}
</script>

<style scoped>
.param-input {
  min-width: 300px;
  max-width: 800px;
  margin-left: 5px;
  margin-right: 5px;
}
</style>
