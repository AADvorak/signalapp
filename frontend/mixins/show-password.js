export default {
  data: () => ({
    showPassword: false
  }),
  methods: {
    switchShowPassword() {
      this.showPassword = !this.showPassword
    }
  }
}
