<template>
  <v-menu v-model="model" activator="parent" width="200px">
    <v-list>
      <v-list-item v-for="folder in folders" height="40px">
        <v-checkbox @click.stop="checkBoxStateChanged(folder)" v-model="folder.includes" :label="folder.name"/>
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
  data: () => ({
    model: false,
    folders: []
  }),
  watch: {
    model(newValue) {
      if (newValue) {
        this.loadSignalFolderIds()
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
      if (folder.includes) {
        await FolderRequests.deleteSignalFromFolder(this.signalId, folder.id)
      } else {
        await FolderRequests.addSignalToFolder(this.signalId, folder.id)
      }
    }
  }
}
</script>
