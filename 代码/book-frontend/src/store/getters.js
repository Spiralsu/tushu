const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  // 新增这一行，让页面能拿到学号
  username: state => state.user.username,
  id: state => state.user.id,
  roles: state => state.user.roles
}
export default getters
