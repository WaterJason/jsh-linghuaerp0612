import { UserLayout, TabLayout, RouteView, BlankLayout, PageView } from '@/components/layouts'

/**
 * 走菜单，走权限控制
 * @type {[null,null]}
 */
export const asyncRouterMap = [

  {
    path: '/',
    name: 'dashboard',
    component: TabLayout,
    meta: { title: '首页' },
    redirect: '/dashboard/analysis',
    children: [

    ]
  },
  {
    path: '*', redirect: '/404', hidden: true
  }
]

/**
 * 基础路由
 * @type { *[] }
 */
export const constantRouterMap = [
  {
    path: '/user',
    component: UserLayout,
    redirect: '/user/login',
    hidden: true,
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Login')
      },
      {
        path: 'register',
        name: 'register',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Register')
      }
    ]
  },
  {
    path: '/dashboard',
    component: TabLayout,
    redirect: '/dashboard/analysis',
    children: [
      {
        path: 'analysis',
        name: 'analysis',
        meta: { title: '首页' },
        component: () => import(/* webpackChunkName: "dashboard" */ '@/views/dashboard/Analysis')
      }
    ]
  },
  {
    path: '/404',
    component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404')
  },
  {
    path: '/demo',
    component: () => import(/* webpackChunkName: "demo" */ '@/views/production/CloisonneProduction'),
    meta: { title: '掐丝点蓝制作演示' }
  },
  {
    path: '/demo-accessory',
    component: () => import(/* webpackChunkName: "demo" */ '@/views/production/AccessoryProduction'),
    meta: { title: '配饰制作演示' }
  },
  {
    path: '/chongzuo-board',
    component: () => import(/* webpackChunkName: "production" */ '@/views/production/ChongzuoProductionBoard'),
    meta: { title: '崇左生产看板' }
  },
  {
    path: '/post-processing-tasks',
    component: () => import(/* webpackChunkName: "production" */ '@/views/production/PostProcessingTaskList'),
    meta: { title: '后工任务列表' }
  },

]
