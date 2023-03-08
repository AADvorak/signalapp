<template></template>

<script>
import TransformerBase from "./transformer-base";

export default {
  name: "Integrator",
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'integrator'
  }),
}
</script>
