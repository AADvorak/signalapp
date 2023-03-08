import ApiProvider from "~/api/api-provider";

const UserUtils = {
  async loadUserInfo() {
    const userInfo = await this.fetchUserInfo()
    if (userInfo) {
      return userInfo
    }
    return null
  },
  async fetchUserInfo() {
    let response = await ApiProvider.get('/api/users/me/', true)
    if (response.ok) {
      return response.data
    }
    return null
  },
}

export default UserUtils