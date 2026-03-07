import router from './router'
import store from './store'
import { Message } from 'element-ui'
import "@/api/initialize"
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { getToken } from '@/utils/auth' // get token from cookie
import getPageTitle from '@/utils/get-page-title'

NProgress.configure({ showSpinner: false }) // NProgress Configuration

const whiteList = ['/login', '/register'] // 免登录白名单

router.beforeEach(async(to, from, next) => {
  NProgress.start()

  // 设置页面标题
  document.title = getPageTitle(to.meta.title)

  const hasToken = getToken()

  if (hasToken) {
    if (to.path === '/login') {
      // 已登录，跳转到首页
      next({ path: '/' })
      NProgress.done()
    } else {
      // 检查是否已获取过角色信息
      const hasRoles = store.getters.roles && store.getters.roles.length > 0
      if (hasRoles) {
        next()
      } else {
        try {
          // 【核心点】尝试获取用户信息
          const { roles } = await store.dispatch('user/getInfo')

          // 根据角色生成动态路由
          const accessRoutes = await store.dispatch('permission/generateRoutes', roles)

          // 挂载动态路由
          router.addRoutes(accessRoutes)

          // 确保路由挂载完成后再跳转
          next({ ...to, replace: true })
        } catch (error) {
          // 【关键修复】如果获取用户信息失败（如 Token 过期），静默重置并跳转，不弹 Message.error
          await store.dispatch('user/resetToken')
          console.error('Token失效或系统错误，自动跳转登录页:', error)
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    /* 没有 token */
    if (whiteList.indexOf(to.path) !== -1) {
      // 在白名单中，直接进入
      next()
    } else {
      // 否则全部重定向到登录页
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
