<script>
import filterPatronymicField from "../mixins/filter-patronymic-field";
import SignPageBase from "~/components/base/sign-page-base.vue";

export default {
  name: 'signup',
  extends: SignPageBase,
  mixins: [filterPatronymicField],
  data: () => ({
    form: {
      email: {value: ''},
      password: {value: ''},
      passwordRepeat: {value: ''},
      firstName: {value: ''},
      lastName: {value: ''},
      patronymic: {value: ''},
    },
    buttonsConfig: [
      {
        key: 'signUpRequest',
        isSubmit: true,
        color: 'success',
        text: {
          func: '_tc',
          key: 'buttons.signUp'
        }
      },
      {
        key: 'signIn',
        color: 'secondary',
        text: {
          func: '_tc',
          key: 'buttons.signIn'
        }
      },
    ]
  }),
  mounted() {
    this.focusFirstFormField()
    this.redirectToStartPageIfSignedIn()
  },
  methods: {
    signUpRequest() {
      this.clearValidation()
      if (this.formValues.passwordRepeat !== this.formValues.password) {
        const msg = this._tc('validation.same')
        this.pushValidationMsg('password', msg)
        this.pushValidationMsg('passwordRepeat', msg)
        return
      }
      this.request('/api/users', 'signUpError')
    },
    signIn() {
      this.switchingSignForms = true
      useRouter().push('/signin')
    },
  },
}
</script>
