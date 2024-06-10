<template>
  <v-menu v-model="model" activator="parent" width="200px">
    <v-list>
      <v-list-item v-for="folder in folders" height="40px">
        <v-checkbox
            v-model="folder.includes"
            @click.stop="checkBoxStateChanged(folder)"
        >
          <template v-slot:label>
            <div @click.stop>
              {{ folder.name }}
            </div>
          </template>
        </v-checkbox>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script>

import ComponentBase from "~/components/base/component-base.vue";
import FolderRequests from "~/api/folder-requests";
import {userStore} from "~/stores/user-store";
import {TableOrListEvents} from "~/dictionary/table-or-list-events";

export default {
  name: "signal-folders-menu",
  extends: ComponentBase,
  props: {
    signalId: {
      type: Number,
      required: true
    },
    bus: {
      type: Object,
      default: null
    }
  },
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
        this.changed && this.bus && this.bus.emit(TableOrListEvents.SIGNAL_FOLDERS_MENU_CLOSED_FOLDERS_CHANGED)
      }
    }
  },
  methods: {
    async loadSignalFolderIds() {
      const signalFolderIds = await FolderRequests.loadSignalFolderIds(this.signalId)
      if (signalFolderIds) {
        this.folders = userStore().folders.map(folder => ({...folder, includes: signalFolderIds.includes(folder.id)}))
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
