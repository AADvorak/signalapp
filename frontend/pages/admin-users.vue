<template>
  <card-with-layout full-width :confirm="confirm" :loading-overlay="loadingOverlay">
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
                        item-title="name"
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
            :select="true"
            :items="users"
            :columns="tableOrListConfig.columns"
            :buttons="tableOrListConfig.buttons"
            :reserved-height="reservedHeight"
            :sort-cols="['firstName', 'lastName', 'patronymic', 'createTime', 'email']"
            :sort-prop="sort"
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
import paginationUrlParams, {PaginationParamLocations} from "~/mixins/pagination-url-params";
import {roleStore} from "~/stores/role-store";
import {dataStore} from "~/stores/data-store";

const DATE_TIME_FORMATTER = value => new Date(value).toLocaleString()

const USER_ROLE_MENU_COMPONENT = 'user-role-menu'

export default {
  name: 'admin-users',
  components: {TableOrList, BtnWithTooltip, NumberInput, FixedWidthWrapper, TextInput, CardWithLayout},
  extends: PageBase,
  mixins: [formNumberValues, formValidation, formValuesSaving, actionWithTimeout, uiParamsSaving, paginationUrlParams],
  data: () => ({
    additionalPaginationParamsConfig: [
      {
        name: 'roleIds',
        location: PaginationParamLocations.FORM,
        readFunc: parseInt,
        isArray: true
      }
    ],
    mounted: false,
    form: {
      pageSize: {
        value: 10,
        params: {
          min: 5,
          max: 25,
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
        {name: 'createTime', formatter: DATE_TIME_FORMATTER},
        {name: 'lastActionTime', formatter: DATE_TIME_FORMATTER},
        'storedSignalsNumber',
        {name: 'role', valuePath: 'role.name', localeKeyGetter: value => `userRoles.${value}`},
      ],
      buttons: [
        {
          name: 'edit',
          icon: mdiFileEdit,
          color: 'primary',
          component: USER_ROLE_MENU_COMPONENT,
          condition: user => user.id !== dataStore().userInfo.id
        },
        {
          name: 'delete',
          icon: mdiDelete,
          color: 'error',
          condition: user => user.id !== dataStore().userInfo.id
        }
      ],
    },
    elements: 0,
    pages: 0,
    page: 1,
    users: [],
    usersEmpty: false,
    usersLastLoadFilter: '',
    sort: {
      by: '',
      dir: ''
    },
    uiParams: {
      openedPanels: []
    },
    mdi: {
      mdiFilterOff
    }
  }),
  computed: {
    filterIsEmpty() {
      return !this.formValues.search && !this.formValues.roleIds.length
    },
    reservedHeight() {
      return this.uiParams.openedPanels && this.uiParams.openedPanels.includes('loadParams') ? 410 : 254
    },
    roles() {
      return roleStore().roles
    }
  },
  watch: {
    page() {
      this.setUrlParams()
      this.loadUsers()
    },
    formValues() {
      this.actionWithTimeout(() => {
        // if (!this.validatePageSize()) {
        //   return
        // }
        this.page = 1
        this.saveFormValues()
        this.setUrlParams()
        this.loadUsers()
      })
    },
  },
  mounted() {
    this.mounted = true
    this.restoreFormValues()
    this.restoreUiParams()
    this.readUrlParams()
    this.setUrlParams()
    this.loadRoles()
    this.actionWithTimeout(this.loadUsers)
  },
  beforeUnmount() {
    this.mounted = false
  },
  methods: {
    async loadRoles() {
      const response = await this.getApiProvider().get('/api/admin/roles')
      if (response.ok) {
        roleStore().roles = response.data
      }
    },
    async loadUsers() {
      const filter = this.makeUserFilter()
      const filterJson = JSON.stringify(filter)
      if (!this.mounted || this.loadingOverlay
          || this.usersLastLoadFilter === filterJson
          /*|| !this.validatePageSize()*/) {
        return
      }
      await this.loadWithOverlay(async () => {
        const response = await this.getApiProvider().postJson('/api/admin/users/filter', filter)
        if (response.ok) {
          this.users = response.data.data
          this.elements = response.data.elements
          this.pages = response.data.pages
          this.usersEmpty = this.elements === 0
          this.usersLastLoadFilter = filterJson
        } else {
          this.showErrorsFromResponse(response)
        }
      })
    },
    setUrlParams() {
      if (!this.mounted) {
        return
      }
      useRouter().push(`/admin-users${this.makeUrlParams()}`)
    },
    makeUserFilter() {
      const filter = {
        page: this.page - 1,
        size: this.formValue('pageSize')
      }
      if (this.formValues.search) {
        filter.search = this.formValues.search
      }
      if (this.formValues.roleIds.length) {
        filter.roleIds = this.formValues.roleIds
      }
      if (this.sort.by) {
        filter.sortBy = this.sort.by
      }
      if (this.sort.dir) {
        filter.sortDir = this.sort.dir
      }
      return filter
    },
    clearFilter() {
      this.formValue('search', '')
      this.formValue('roleIds', [])
    },
    onTableButtonClick({button, item}) {
      if (button === 'delete') {
        this.askConfirmDeleteUser(item)
      }
    },
    onTableSort(sort) {
      this.sort = sort
      this.page = 1
      this.setUrlParams()
      this.loadUsers()
    },
    onTableChange(component) {
      if (component === USER_ROLE_MENU_COMPONENT) {
        this.usersLastLoadFilter = ''
        this.loadUsers()
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
        this.usersLastLoadFilter = ''
        await this.loadUsers()
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
