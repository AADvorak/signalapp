<template></template>

<script>
import TransformerBase from "./transformer-base";

export default {
  name: "Inverter",
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'inverter'
  }),
}
</script>
