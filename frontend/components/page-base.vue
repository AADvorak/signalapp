<script>
import ApiProvider from "../api/api-provider";
import ConfirmDialog from "./confirm-dialog";
import Message from "./message";
import LoadingOverlay from "./loading-overlay";
import ComponentBase from "./component-base"
import SelectDialog from "./select-dialog";

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
    askSelect({text, select, cancel}) {
      this.select = {
        opened: true,
        text,
        select: (itemName) => {
          this.select.opened = false
          select && select(itemName)
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
          error.code && (errorMsg += (errorMsg && ', ') + this.getLocalizedErrorMessage(error))
        }
      }
      this.showMessage({
        text: (text || this._tc('messages.error')) + (errorMsg ? ': ' + errorMsg : '')
      })
    },
    getLocalizedErrorMessage(error) {
      return this._tsm(error.code, error.params)
    },
  },
}
</script>
