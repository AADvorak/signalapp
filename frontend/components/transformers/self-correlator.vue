<template></template>

<script>
import TransformerBase from "./transformer-base";

export default {
  name: "SelfCorrelator",
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'selfCorrelator'
  }),
}
</script>
