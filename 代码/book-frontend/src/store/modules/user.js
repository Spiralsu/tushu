import { login, logout, getInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { resetRouter } from '@/router'

const getDefaultState = () => {
  return {
    token: getToken(),
    name: '',
    username: '', // 专门用来存学号
    avatar: '',
    id: null,
    roles: []
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_USERNAME: (state, username) => {
    state.username = username
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_ID: (state, id) => {
    state.id = id
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  }
}

const actions = {
  // user login
  login({ commit }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {

      // 1. 构造后端需要的参数对象
      // 后端 User.java 中密码字段叫 userpassword，必须对应上！
      const loginParams = {
        username: username.trim(),
        userpassword: password
      }

      console.log('正在发起登录请求，参数：', loginParams)

      login(loginParams).then(response => {
        console.log('后端返回的原始结果：', response)

        // 2. 解析返回结果
        // 兼容两种常见的后端返回格式：
        // 格式A: { code: 0, msg: "success", data: { token: "xxx" } }
        // 格式B: { code: 0, msg: "success", data: "xxx-token-string" }
        const { code, msg, data } = response

        // 3. 校验业务状态码 (假设 0 或 200 为成功)
        if (code !== 0 && code !== 200) {
          console.error('登录失败：后端返回错误码', code, msg)
          reject(msg || '登录失败，请检查账号密码')
          return
        }

        // 4. 提取 Token
        // 优先尝试从 data.token 取，如果 data 本身就是字符串，则直接用 data
        let token = null
        if (data && typeof data === 'object' && data.token) {
          token = data.token
        } else if (data && typeof data === 'string') {
          token = data
        } else if (response.token) {
          // 极少数情况，token 直接在最外层
          token = response.token
        }

        if (!token) {
          console.error('登录失败：未找到有效Token', data)
          reject('系统错误：后端未返回有效Token')
          return
        }

        console.log('登录成功，获取到Token：', token)
        commit('SET_TOKEN', token)
        setToken(token)
        resolve()
      }).catch(error => {
        console.error('登录请求发生网络或代码异常：', error)
        reject(error)
      })
    })
  },

  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo(state.token).then(response => {
        const { data } = response

        if (!data) {
          return reject('验证失败，请重新登录')
        }

        // 解构后端字段
        // 注意：后端可能返回 studentid 作为学号，也可能没返回
        const { roles, username, studentid, avatar, userid, isadmin } = data

        // 构造角色数组
        let computedRoles = roles
        if (!computedRoles || computedRoles.length <= 0) {
          // 根据 isadmin (0/1) 判断角色
          computedRoles = (isadmin === 1) ? ['admin'] : ['reader']
        }

        commit('SET_ROLES', computedRoles)
        commit('SET_NAME', username) // 姓名
        commit('SET_USERNAME', studentid || username) // 学号 (优先取 studentid)
        commit('SET_AVATAR', avatar)
        commit('SET_ID', userid)

        resolve({ ...data, roles: computedRoles })
      }).catch(error => {
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      logout(state.token).then(() => {
        removeToken()
        resetRouter()
        commit('RESET_STATE')
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      removeToken()
      commit('RESET_STATE')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
