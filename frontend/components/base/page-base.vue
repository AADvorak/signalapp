<script>
import ApiProvider from "../../api/api-provider";
import ConfirmDialog from "../common/confirm-dialog.vue";
import Message from "../common/message.vue";
import LoadingOverlay from "../common/loading-overlay.vue";
import ComponentBase from "./component-base.vue"
import SelectDialog from "../common/select-dialog.vue";
import {SelectUtils} from "~/utils/select-utils";

export default {
  name: "page-base",
  extends: ComponentBase,
  components: {ConfirmDialog, SelectDialog, Message, LoadingOverlay},
  data: () => ({
    message: {
      opened: false,
      text: '',
      onHide: () => {}
    },
    confirm: {
      opened: false,
      text: '',
      ok: () => {
      },
      cancel: () => {
      }
    },
    select: {
      opened: false,
      text: '',
      select: () => {
      },
      cancel: () => {
      }
    },
    selectItems: [],
    loadingOverlay: false
  }),
  mounted() {
    window.scrollTo(0,0)
  },
  methods: {
    getApiProvider() {
      return ApiProvider.setRouter(useRouter()).setRoute(useRoute())
    },
    showMessage({text, onHide}) {
      this.message = {
        opened: true,
        text,
        onHide: () => {
          this.message.opened = false
          onHide && onHide()
        }
      }
    },
    askConfirm({text, ok, cancel}) {
      this.confirm = {
        opened: true,
        text,
        ok: () => {
          this.confirm.opened = false
          ok && ok()
        },
        cancel: () => {
          this.confirm.opened = false
          cancel && cancel()
        }
      }
    },
    askSelect({key, text, select, cancel}) {
      const selectedItemName = SelectUtils.getSelected(key)
      if (selectedItemName) {
        select && select(selectedItemName)
        return
      }
      this.select = {
        opened: true,
        text,
        select: ({rememberSelection, selectedItemName}) => {
          rememberSelection && SelectUtils.setSelected(key, selectedItemName)
          this.select.opened = false
          select && select(selectedItemName)
        },
        cancel: () => {
          this.select.opened = false
          cancel && cancel()
        }
      }
    },
    async loadWithOverlay(loadFunction) {
      this.loadingOverlay = true
      try {
        await loadFunction()
      } finally {
        this.loadingOverlay = false
      }
    },
    async loadWithFlag(loadFunction, flagName) {
      this[flagName] = true
      try {
        await loadFunction()
      } finally {
        this[flagName] = false
      }
    },
    showErrorsFromResponse(response, text) {
      if (response.status === 401) {
        return
      }
      let errorMsg = ''
      if (Array.isArray(response.errors)) {
        for (let error of response.errors) {
          error.code && !error.field && (errorMsg += (errorMsg && ', ') + this.getLocalizedErrorMessage(error))
        }
      }
      errorMsg && this.showMessage({
        text: (text || this._tc('messages.error')) + ': ' + errorMsg
      })
    },
    getLocalizedErrorMessage(error) {
      const keyRoot = 'serverMessages.'
      const key = keyRoot + error.code
      let msg = this.$t(key, error.params)
      if (msg === key) {
        msg = this.$t(keyRoot + 'UNKNOWN_ERROR')
      }
      return msg
    },
  },
}
</script>
