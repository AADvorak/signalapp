<template>
  <v-dialog
      :model-value="opened"
      max-width="800px"
      max-height="500px"
  >
    <v-card width="100%">
      <toolbar-with-close-btn :title="title" @close="close"/>
      <v-card-text>
        <v-form @submit.prevent>
          <v-text-field
              v-model="form.name.value"
              ref="nameInput"
              :label="_tc('fields.name')"
              :error="!!form.name.validation?.length"
              :error-messages="form.name.validation"
              required/>
          <v-textarea
              v-model="form.description.value"
              :label="_tc('fields.description')"
              :error="!!form.description.validation?.length"
              :error-messages="form.description.validation"/>
          <v-btn
              type="submit"
              color="success"
              :loading="saveFolderRequestSent"
              @click="saveFolder"
          >
            {{ _tc('buttons.save') }}
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import ToolbarWithCloseBtn from "~/components/toolbar-with-close-btn.vue";
import PageBase from "~/components/page-base.vue";
import FolderRequests from "~/api/folder-requests";
import formValues from "~/mixins/form-values";
import formValidation from "~/mixins/form-validation";

export default {
  name: "folder-editor",
  extends: PageBase,
  components: {ToolbarWithCloseBtn},
  mixins: [formValues, formValidation],
  props: {
    opened: Boolean,
    folder: {
      type: Object,
      default: {}
    }
  },
  emits: ['close', 'response'],
  data: () => ({
    form: {
      name: {value: ''},
      description: {value: ''},
    },
    saveFolderRequestSent: false
  }),
  computed: {
    title() {
      const actionKey = this.folder.id ? 'edit' : 'create'
      return `${this._t('title')} - ${this._tc('buttons.' + actionKey)}`
    }
  },
  watch: {
    folder() {
      this.formValue('name', this.folder.name || '')
      this.formValue('description', this.folder.description || '')
    }
  },
  methods: {
    async saveFolder() {
      this.clearValidation()
      await this.loadWithFlag(async () => {
        let response
        if (this.folder.id) {
          response = await FolderRequests.updateFolder(this.folder.id, this.formValues)
        } else {
          response = await FolderRequests.saveFolder(this.formValues)
        }
        if (response.status === 400) {
          this.parseValidation(response.errors)
        } else {
          this.close()
        }
        this.$emit('response', response)
      }, 'saveFolderRequestSent')
    },
    close() {
      this.$emit('close')
    }
  }
}
</script>
