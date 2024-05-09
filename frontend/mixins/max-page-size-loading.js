import {appSettingsStore} from "~/stores/app-settings-store";

export default {
  methods: {
    setMaxPageSize() {
      appSettingsStore().$subscribe((mutation, state) => {
        const settings = state.settings
        if (settings) {
          this.form.pageSize.params.max = settings.maxPageSize
        }
      })
    }
  }
}
