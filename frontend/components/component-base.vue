<script>
export default {
  name: "component-base",
  methods: {
    _t(key, params) {
      return this.$t(`${this.$options.name}.${key}`, params)
    },
    _tc(key, params) {
      return this.$t(`common.${key}`, params)
    },
    _tpn(key) {
      return this.$t('processorNames.' + key)
    },
    _tpt(key) {
      return this.$t('processorTypes.' + key)
    },
    _tsn(key) {
      return this.$t('signalNames.' + key)
    },
    _tsm(key, params) {
      return this.$t('serverMessages.' + key, params)
    }
  }
}
</script>
