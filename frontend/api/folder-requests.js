import ApiProvider from "~/api/api-provider";
import {dataStore} from "~/stores/data-store";

const FolderRequests = {
  apiProvider: ApiProvider,
  setApiProvider(apiProvider) {
    this.apiProvider = apiProvider
  },
  async loadFolders(force = false, noHandleUnauthorized = true) {
    if (dataStore().folders.length && !force) {
      return
    }
    const response = await this.apiProvider.get('/api/folders', noHandleUnauthorized)
    if (response.ok) {
      dataStore().folders = response.data
    } else {
      throw response
    }
  },
  async saveFolder(folder) {
    return await this.apiProvider.postJson('/api/folders/', folder)
  },
  async updateFolder(folderId, folder) {
    return await this.apiProvider.putJson(`/api/folders/${folderId}`, folder)
  },
  async deleteFolder(folderId, deleteSignals = false) {
    return await this.apiProvider.del(`/api/folders/${folderId}?deleteSignals=${deleteSignals}`)
  },
  async loadSignalFolderIds(signalId) {
    const response = await this.apiProvider.get(`/api/signals/${signalId}/folders`, true)
    if (response.ok) {
      return response.data
    }
    return null
  },
  async addSignalToFolder(signalId, folderId) {
    return await this.apiProvider.postJson(`/api/signals/${signalId}/folders/${folderId}`, {}, true)
  },
  async deleteSignalFromFolder(signalId, folderId) {
    return await this.apiProvider.del(`/api/signals/${signalId}/folders/${folderId}`)
  }
}

export default FolderRequests
