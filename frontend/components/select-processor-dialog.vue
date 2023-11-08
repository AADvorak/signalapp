<template>
  <v-dialog
      v-model="dialog"
      max-width="800px"
      max-height="500px"
  >
    <template v-slot:activator="{ props }">
      <v-btn
          type="submit"
          color="primary"
          v-bind="props"
          :disabled="disabled"
      >
        {{ _tc('buttons.process') }}
      </v-btn>
    </template>
    <select-processor :bus="bus" :double="double" @close="dialog = false"/>
  </v-dialog>
</template>

<script>
import ComponentBase from "./component-base";

export default {
  name: "select-processor-dialog",
  extends: ComponentBase,
  props: {
    bus: Object,
    double: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
  },
  data: () => ({
    dialog: false
  }),
}
</script>
