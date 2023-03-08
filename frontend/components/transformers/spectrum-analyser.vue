<template></template>

<script>
import TransformerBase from "./transformer-base";

export default {
  name: "SpectrumAnalyser",
  extends: TransformerBase,
  data: () => ({
    transformFunctionName: 'spectrumAnalyser'
  }),
}
</script>
