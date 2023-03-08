<script>
import ApiProvider from "../api/api-provider";
import ConfirmDialog from "./confirm-dialog";
import Message from "./message";
import LoadingOverlay from "./loading-overlay";

export default {
  name: "page-base",
  components: {ConfirmDialog, Message, LoadingOverlay},
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
    loadingOverlay: false
  }),
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
    async loadWithOverlay(loadFunction) {
      this.loadingOverlay = true
      try {
        await loadFunction()
      } finally {
        this.loadingOverlay = false
      }
    },
    showErrorsFromResponse(response, text = 'Error') {
      if (response.status === 401) {
        return
      }
      let errorMsg = ''
      for (let error of response.errors) {
        error.message && (errorMsg += errorMsg ? ', ' + error.message : error.message)
      }
      this.showMessage({
        text: text + (errorMsg ? ': ' + errorMsg : '')
      })
    }
  },
}
</script>
