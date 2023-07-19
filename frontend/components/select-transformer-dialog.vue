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
        {{ _tc('buttons.transform') }}
      </v-btn>
    </template>
    <select-transformer :bus="bus" :double="double" @close="dialog = false"/>
  </v-dialog>
</template>

<script>
import ComponentBase from "./component-base";
import SelectTransformer from "./select-transformer";

export default {
  name: "select-transformer-dialog",
  components: {SelectTransformer},
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
