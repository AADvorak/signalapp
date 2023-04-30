export default {
  computed: {
    filteredFormFields() {
      return this.formFields.filter(field => field !== 'patronymic' || this.$i18n.locale === 'ru')
    }
  },
}
