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
    _tr(key) {
      return this.$t('transformerNames.' + key)
    },
    _trt(key) {
      return this.$t('transformerTypes.' + key)
    },
    _tsn(key) {
      return this.$t('signalNames.' + key)
    }
  }
}
</script>
