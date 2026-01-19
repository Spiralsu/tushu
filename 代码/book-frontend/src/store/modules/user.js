import { login, logout, getInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { resetRouter } from '@/router'

const getDefaultState = () => {
  return {
    token: getToken(),
    id: 0,
    name: '',
    avatar: '',
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
  SET_ID: (state, id) => {
    state.id = id
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  }
}

const actions = {
  // user login
  login({ commit }, userInfo) {
    const { username, password, isadmin } = userInfo
    return new Promise((resolve, reject) => {
      // 注意：前端字段 username 对应后端的 studentId
      login({ username: username.trim(), userpassword: password, isadmin: isadmin }).then(response => {
        // 【关键修改】适配新后端的返回结构 (code, msg)
        const { code, msg, data } = response

        // 兼容性判断：后端成功通常返回 code:0
        const resCode = code !== undefined ? code : response.status
        const resMsg = msg || response.message || '登录失败'

        // 只要不是成功状态
        if(resCode !== 0 && resCode !== 200) {
          reject(resMsg)
          return
        }

        // 校验 Token 是否存在
        // 如果后端直接返回扁平结构(R extends HashMap)，token可能直接在response里
        // 如果后端 put("data", ...)，token 可能在 response.data.token 里
        // 为了保险，做多重检查
        const token = data ? data.token : response.token;

        if (!token) {
          reject('Token获取失败: 后端未返回有效Token')
          return
        }

        commit('SET_TOKEN', token)
        setToken(token)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo(state.token).then(response => {
        const { data, code } = response

        if ((code !== 0 && code !== 200) || !data) {
          reject('验证失败，请重新登录')
          return
        }

        // 处理头像
        data['avatar'] = '/pic/02.jpg'
        // 根据 isAdmin 字段设置角色
        if (data.isadmin === 1){
          data['roles'] = ['admin']
          data['avatar'] = '/pic/05.jpg'
        }
        else {
          data['roles'] = ['reader']
        }

        const { userid, roles, username, avatar } = data

        if (!roles || roles.length <= 0) {
          reject('getInfo: roles 必须是非空数组!')
        }

        commit('SET_ID', userid)
        commit('SET_ROLES', roles)
        commit('SET_NAME', username)
        commit('SET_AVATAR', avatar)
        resolve(data)
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
