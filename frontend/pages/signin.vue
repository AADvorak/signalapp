<script>
import {dataStore} from "~/stores/data-store";
import SignPageBase from "~/components/sign-page-base.vue";

export default {
  name: 'signin',
  extends: SignPageBase,
  data: () => ({
    form: {
      email: {value: ''},
      password: {value: ''},
    },
    buttonsConfig: [
      {
        key: 'signInRequest',
        isSubmit: true,
        color: 'success',
        text: {
          func: '_tc',
          key: 'buttons.signIn'
        }
      },
      {
        key: 'signUp',
        color: 'secondary',
        text: {
          func: '_tc',
          key: 'buttons.signUp'
        }
      },
      {
        key: 'restorePassword',
        color: 'secondary',
        text: {
          func: '_t',
          key: 'forgotPassword'
        }
      }
    ]
  }),
  mounted() {
    this.formValue('email', dataStore().emailForPasswordRestore || '')
    this.focusFirstFormField()
    this.redirectToStartPageIfSignedIn()
  },
  methods: {
    signInRequest() {
      this.clearValidation()
      this.request('/api/sessions', 'signInError')
    },
    signUp() {
      this.switchingSignForms = true
      useRouter().push('/signup')
    },
    restorePassword() {
      this.switchingSignForms = true
      dataStore().emailForPasswordRestore = this.formValues.email
      useRouter().push('/restore-password')
    },
  },
}
</script>
