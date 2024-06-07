<template>
  <card-with-layout full-width :message="message" :confirm="confirm" :loading-overlay="loadingOverlay">
    <template #default>
      <v-card-text>
        <fixed-width-wrapper>
          <p>
            {{ _tc('pagination.total', {pages, elements}) }}
          </p>
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
                    <number-input
                        class="param-input"
                        field="pageSize"
                        :label="_tc('pagination.pageSize')"
                        :field-obj="form.pageSize"
                        @update="v => form.pageSize.value = v"/>
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
        <fixed-width-wrapper v-if="usersEmpty && !loadingOverlay">
          <h3 style="text-align: center;">{{ _tc('messages.nothingIsFound') }}</h3>
        </fixed-width-wrapper>
        <table-or-list v-else
            data-name="users"
            caption="email"
            :items="users"
            :columns="tableOrListConfig.columns"
            :buttons="tableOrListConfig.buttons"
            :reserved-height="reservedHeight"
            :sort-cols="['firstName', 'lastName', 'patronymic', 'createTime', 'email']"
            :sort-prop="sort"
            :bus="bus"
            @click="onTableButtonClick"
            @sort="onTableSort"
            @change="onTableChange"/>
        <fixed-width-wrapper>
          <v-pagination
              v-model="page"
              :length="pages"/>
        </fixed-width-wrapper>
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
import TableOrList from "~/components/common/table-or-list.vue";
import {mdiDelete, mdiFileEdit, mdiFilterOff} from "@mdi/js";
import formNumberValues from "~/mixins/form-number-values";
import formValidation from "~/mixins/form-validation";
import formValuesSaving from "~/mixins/form-values-saving";
import actionWithTimeout from "~/mixins/action-with-timeout";
import uiParamsSaving from "~/mixins/ui-params-saving";
import pagination, {PaginationParamLocations} from "~/mixins/pagination";
import {roleStore} from "~/stores/role-store";
import {userStore} from "~/stores/user-store";
import {DateTimeUtils} from "~/utils/date-time-utils";
import {Roles} from "~/dictionary/roles";
import requiredRoleMsg from "~/mixins/required-role-msg";
import {appSettingsStore} from "~/stores/app-settings-store";
import mitt from "mitt";

const USER_ROLE_MENU_COMPONENT = 'user-role-menu'

const isNotCurrentUser = user => user.id !== userStore().userInfo?.id

export default {
  name: 'admin-users',
  components: {TableOrList, BtnWithTooltip, NumberInput, FixedWidthWrapper, TextInput, CardWithLayout},
  extends: PageBase,
  mixins: [
    formNumberValues, formValidation, formValuesSaving, actionWithTimeout, uiParamsSaving,
    pagination, requiredRoleMsg
  ],
  data: () => ({
    additionalPaginationParamsConfig: [
      {
        name: 'roleIds',
        location: PaginationParamLocations.FORM,
        readFunc: parseInt,
        isArray: true,
        emptyValue: []
      }
    ],
    mounted: false,
    form: {
      pageSize: {
        value: 10,
        params: {
          min: 5,
          max: appSettingsStore().settings?.maxPageSize,
          step: 1
        }
      },
      search: {value: ''},
      roleIds: {value: []}
    },
    tableOrListConfig: {
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
          component: USER_ROLE_MENU_COMPONENT,
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
    users: [],
    usersEmpty: false,
    uiParams: {
      openedPanels: []
    },
    mdi: {
      mdiFilterOff
    },
    bus: new mitt(),
  }),
  computed: {
    reservedHeight() {
      return this.uiParams.openedPanels && this.uiParams.openedPanels.includes('loadParams') ? 410 : 254
    },
    roles() {
      return roleStore().roles.map(role => ({...role, localeName: this.$t(`userRoles.${role.name}`)}))
    }
  },
  mounted() {
    this.mounted = true
    this.restoreFormValues()
    this.restoreUiParams()
    this.readUrlParams()
    this.setUrlParams()
    this.loadRoles()
    this.actionWithTimeout(() => {
      this.requiredRoleMsg(Roles.ADMIN)
      this.loadDataPage()
    })
    this.bus.on('newUserRoles', this.onNewUserRoles)
  },
  beforeUnmount() {
    this.mounted = false
    this.bus.off('newUserRoles')
  },
  methods: {
    async loadRoles() {
      if (!userStore().checkUserRole(Roles.ADMIN)) {
        return
      }
      const response = await this.getApiProvider().get('/api/admin/roles')
      if (response.ok) {
        roleStore().roles = response.data
      }
    },
    async loadDataPage() {
      await this.loadDataPageBase('users', '/api/admin/users/filter', Roles.ADMIN)
    },
    setUrlParams() {
      if (!this.mounted) {
        return
      }
      useRouter().push(`/admin-users${this.makeUrlParams()}`)
    },
    onTableButtonClick({button, item}) {
      if (button === 'delete') {
        this.askConfirmDeleteUser(item)
      }
    },
    onTableChange(component) {
      if (component === USER_ROLE_MENU_COMPONENT) {
        this.dataPageLastLoadFilter = ''
        this.loadDataPage()
      }
    },
    onNewUserRoles(event) {
      const user = this.users.filter(user => user.id === event.userId)[0]
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
        this.dataPageLastLoadFilter = ''
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
