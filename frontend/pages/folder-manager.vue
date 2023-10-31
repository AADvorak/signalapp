<template>
  <card-with-layout full-width :message="message" :loading-overlay="loadingOverlay">
    <template #default>
      <v-card-text>
        <fixed-width-wrapper v-if="!folders.length && !loadingOverlay">
          <h3 style="text-align: center">{{ _tc('messages.noFolders') }}</h3>
        </fixed-width-wrapper>
        <div v-else>
          <table-or-list
              data-name="folders"
              caption="name"
              :items="folders"
              :columns="tableOrListConfig.columns"
              :buttons="tableOrListConfig.buttons"
              @click="onTableButtonClick"/>
        </div>
        <fixed-width-wrapper>
          <div class="mt-5 d-flex justify-center flex-wrap">
            <v-btn @click="createFolder">
              {{ _tc('buttons.create') }}
            </v-btn>
          </div>
        </fixed-width-wrapper>
      </v-card-text>
    </template>
    <template #dialogs>
      <select-dialog
          :items="selectItems"
          :opened="select.opened"
          :text="select.text"
          @select="select.select"
          @cancel="select.cancel"/>
      <folder-editor
          :opened="folderEditorOpened"
          :folder="selectedFolder"
          @close="folderEditorOpened = false"
          @response="onEditorResponse"/>
    </template>
  </card-with-layout>
</template>

<script>
import {mdiDelete, mdiFileEdit} from "@mdi/js";
import PageBase from "~/components/page-base.vue";
import {dataStore} from "~/stores/data-store";
import FolderRequests from "~/api/folder-requests";

export default {
  name: "folder-manager",
  extends: PageBase,
  data: () => ({
    tableOrListConfig: {
      columns: ['description'],
      buttons: [
        {
          name: 'edit',
          icon: mdiFileEdit,
          color: 'primary'
        },
        {
          name: 'delete',
          icon: mdiDelete,
          color: 'error'
        }
      ],
    },
    selectedFolder: {},
    folderEditorOpened: false,
    selectItems: [
      {name: 'delete', color: 'error'},
      {name: 'deleteWithSignals', color: 'error'},
    ]
  }),
  computed: {
    folders() {
      return dataStore().folders
    }
  },
  mounted() {
    FolderRequests.setApiProvider(this.getApiProvider())
    this.loadFolders()
  },
  methods: {
    async loadFolders(force = false) {
      await this.loadWithOverlay(async () => {
        await FolderRequests.loadFolders(force, false)
      })
    },
    onTableButtonClick({button, item}) {
      if (button === 'delete') {
        this.askConfirmDeleteFolder(item)
      } else if (button === 'edit') {
        this.editFolder(item)
      }
    },
    onEditorResponse(response) {
      if (response.ok) {
        this.loadFolders(true)
      } else if (response.status !== 400) {
        this.showErrorsFromResponse(response)
      }
    },
    async askConfirmDeleteFolder(folder) {
      this.askSelect({
        text: this._t('confirmDeleteFolder', {name: folder.name}),
        select: async (action) => {
          const response = await FolderRequests.deleteFolder(folder.id, action === 'deleteWithSignals')
          if (response.ok) {
            await this.loadFolders(true)
          } else {
            this.showMessage({
              text: this._t('deleteError')
            })
          }
        }
      })
    },
    async editFolder(folder) {
      this.selectedFolder = folder
      this.folderEditorOpened = true
    },
    createFolder() {
      this.selectedFolder = {}
      this.folderEditorOpened = true
    }
  }
}
</script>
