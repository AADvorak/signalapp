<template>
  <v-menu v-model="model" activator="parent" width="200px">
    <v-list>
      <v-list-item v-for="role in roles" height="40px">
        <v-checkbox
            v-model="role.selected"
            @click.stop="checkBoxStateChanged(role)"
        >
          <template v-slot:label>
            <div @click.stop>
              {{ $t(`userRoles.${role.name}`) }}
            </div>
          </template>
        </v-checkbox>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script>
import ComponentBase from "~/components/base/component-base.vue";
import {roleStore} from "~/stores/role-store";
import ApiProvider from "~/api/api-provider";

export default {
  name: 'user-role-menu',
  extends: ComponentBase,
  props: {
    user: {
      type: Object,
      required: true
    }
  },
  emits: ['changed'],
  data: () => ({
    model: false,
    roles: []
  }),
  watch: {
    user() {
      this.initRoles()
    }
  },
  mounted() {
    this.initRoles()
  },
  methods: {
    initRoles() {
      this.roles = roleStore().roles.map(role => ({...role, selected: this.checkUserHasRole(role.id)}))
    },
    async checkBoxStateChanged(role) {
      const response = role.selected
          ? await ApiProvider.del(`api/admin/users/${this.user.id}/roles/${role.id}`)
          : await ApiProvider.putJson(`api/admin/users/${this.user.id}/roles/${role.id}`)
      if (response.ok) {
        this.$emit('changed')
        this.model = false
      }
    },
    checkUserHasRole(roleId) {
      for (const role of this.user.roles) {
        if (role.id === roleId) {
          return true
        }
      }
      return false
    }
  }
}
</script>
