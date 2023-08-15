<template>
  <v-menu v-model="model" activator="parent" width="200px">
    <v-list>
      <v-list-item v-for="folder in folders" height="40px">
        <v-checkbox
            v-model="folder.includes"
            :label="folder.name"
            @click.stop="checkBoxStateChanged(folder)"/>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script>

import ComponentBase from "~/components/component-base.vue";
import {dataStore} from "~/stores/data-store";
import FolderRequests from "~/api/folder-requests";

export default {
  name: "signal-folders-menu",
  extends: ComponentBase,
  props: {
    signalId: {
      type: Number,
      required: true
    }
  },
  emits: ['changed'],
  data: () => ({
    model: false,
    folders: [],
    changed: false
  }),
  watch: {
    model(newValue) {
      if (newValue) {
        this.changed = false
        this.loadSignalFolderIds()
      } else {
        this.changed && this.$emit('changed')
      }
    }
  },
  methods: {
    async loadSignalFolderIds() {
      const signalFolderIds = await FolderRequests.loadSignalFolderIds(this.signalId)
      if (signalFolderIds) {
        this.folders = dataStore().folders.map(folder => ({...folder, includes: signalFolderIds.includes(folder.id)}))
      }
    },
    async checkBoxStateChanged(folder) {
      this.changed = true
      if (folder.includes) {
        await FolderRequests.deleteSignalFromFolder(this.signalId, folder.id)
      } else {
        await FolderRequests.addSignalToFolder(this.signalId, folder.id)
      }
    }
  }
}
</script>
