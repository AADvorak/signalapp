import {userStore} from "~/stores/user-store";

export default {
  methods: {
    requiredRoleMsg(role) {
      if (!userStore().checkUserRole(role)) {
        this.showMessage({
          text: `${this._tc('messages.requiredRole')}: ${this.$t('userRoles.' + role)}`,
          onHide: () => {
            useRouter().push('/').then()
          }
        })
      }
    }
  }
}
