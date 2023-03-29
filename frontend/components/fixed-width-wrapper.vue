<template>
  <div style="display: flex; justify-content: center; align-items: center">
    <div style="width: 800px">
      <slot/>
    </div>
  </div>
</template>

<script>
export default {
  name: "fixed-width-wrapper"
}
</script>
