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
  name: 'user-roles-menu',
  extends: ComponentBase,
  props: {
    user: {
      type: Object,
      required: true
    },
    bus: {
      type: Object,
      default: null
    }
  },
  data: () => ({
    model: false,
    roles: [],
    changed: false
  }),
  watch: {
    model(newValue) {
      if (newValue) {
        this.changed = false
        this.initRoles()
      } else {
        this.changed && this.bus && this.bus.emit('userRolesMenuClosedRolesChanged')
      }
    }
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
        this.changed = true
        this.bus && this.bus.emit('newUserRoles', {userId: this.user.id, roles: response.data})
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
