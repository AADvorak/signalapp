<template></template>

<script>
import TransformerDoubleBase from "./transformer-double-base";

export default {
  name: "Correlator",
  extends: TransformerDoubleBase,
  data: () => ({
    transformFunctionName: 'correlator'
  }),
}
</script>
