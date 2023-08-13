import ApiProvider from "~/api/api-provider";
import {dataStore} from "~/stores/data-store";

const FolderRequests = {
  async loadFolders(force = false) {
    if (dataStore().folders.length && !force) {
      return
    }
    const response = await ApiProvider.get('/api/folders', true)
    if (response.ok) {
      dataStore().folders = response.data
    }
  },
  async loadSignalFolderIds(signalId) {
    const response = await ApiProvider.get(`/api/signals/${signalId}/folders`, true)
    if (response.ok) {
      return response.data
    }
    return null
  },
  async addSignalToFolder(signalId, folderId) {
    await ApiProvider.postJson(`/api/signals/${signalId}/folders/${folderId}`, {}, true)
  },
  async deleteSignalFromFolder(signalId, folderId) {
    await ApiProvider.del(`/api/signals/${signalId}/folders/${folderId}`)
  }
}

export default FolderRequests
