import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)
import Layout from '@/layout'

// 公共路由（登录、注册、首页）
export const constantRoutes = [
  { path: '/login', component: () => import('@/views/login/index'), hidden: true },
  { path: '/register', component: () => import('@/views/register/index'), hidden: true },
  { path: '/404', component: () => import('@/views/404'), hidden: true },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '首页', icon: 'el-icon-s-grid' }
    }]
  }
]

// 动态权限路由（精准隔离管理员与普通账号）
export const asyncRoutes = [


  {
    path: '/wish',
    component: Layout,
    redirect: '/wish/index',
    children: [
      {
        path: 'index',
        name: 'Wish',
        component: () => import('@/views/wish/index'),
        meta: { title: '求书心愿广场', icon: 'el-icon-star-off', noCache: true }
      }
    ]
  },

  {
    path: '/bookmanage',
    name: 'Bookmanage',
    component: Layout,
    redirect: '/bookmanage/bookinfo',
    alwaysShow: true,
    meta: { title: '图书管理', icon: 'el-icon-book' }, // 不写 roles，代表外层目录所有人可见
    children: [

      {
        path: 'bookinfo',
        name: 'Bookinfo',
        component: () => import('@/views/bookinfo/index'),
        meta: { title: '图书信息管理', icon: 'el-icon-notebook-1', noCache: true } // 不写 roles，大家都能用
      },
      {
        path: 'bookaudit',
        name: 'Bookaudit',
        component: () => import('@/views/bookaudit/index'),
        // 【核心隔离】审核专用，仅管理员可见！
        meta: { title: '图书审核管理', icon: 'el-icon-s-check', roles: ['admin'], noCache: true }
      },
      {
        path: 'booktype',
        name: 'Booktype',
        component: () => import('@/views/booktype/index'),
        // 【核心隔离】仅管理员可见！
        meta: { title: '图书类型管理', icon: 'el-icon-collection-tag', roles: ['admin'], noCache: true }
      },
      {
        path: 'borrow',
        name: 'Borrow',
        component: () => import('@/views/borrow/index'),
        meta: { title: '借阅信息管理', icon: 'el-icon-document', noCache: true } // 不写 roles，大家都能用
      }
    ]
  },
  {
    path: '/other',
    name: 'Other',
    component: Layout,
    redirect: '/other/user',
    alwaysShow: true,
    meta: { title: '其他管理', icon: 'el-icon-setting' }, // 不写 roles
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index'),
        // 【核心隔离】用户管理：严格限制仅管理员可见！学生绝对看不到！
        meta: { title: '用户管理', icon: 'el-icon-avatar', roles: ['admin'], noCache: true }
      },
      {
        path: 'password',
        name: 'Password',
        component: () => import('@/views/password/index'),
        meta: { title: '密码更改', icon: 'el-icon-unlock', noCache: true } // 不写 roles，大家都能用
      }
    ]
  },
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}

export default router
