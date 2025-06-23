/**
 * 掐丝珐琅馆模块路由配置
 * 
 * @author jshERP
 * @since 2025-01-22
 */

import { BasicLayout } from '@/components/layouts'

const CloisonneRouter = {
  path: '/cloisonne',
  name: 'cloisonne',
  component: BasicLayout,
  redirect: '/cloisonne/dashboard',
  meta: {
    title: '掐丝珐琅馆',
    icon: 'shop',
    permission: ['1001']
  },
  children: [
    {
      path: '/cloisonne/dashboard',
      name: 'CloisonneDashboard',
      component: () => import('@/views/cloisonne/Dashboard'),
      meta: {
        title: '总览仪表板',
        icon: 'dashboard',
        permission: ['1001'],
        keepAlive: true
      }
    },
    {
      path: '/cloisonne/schedule',
      name: 'CloisonneSchedule',
      component: () => import('@/views/cloisonne/Schedule'),
      meta: {
        title: '排班管理',
        icon: 'calendar',
        permission: ['1002'],
        keepAlive: true
      }
    },
    {
      path: '/cloisonne/coffee',
      name: 'CloisonneCoffee',
      component: () => import('@/views/cloisonne/Coffee'),
      meta: {
        title: '咖啡店管理',
        icon: 'coffee',
        permission: ['1003'],
        keepAlive: true
      }
    },
    {
      path: '/cloisonne/pos',
      name: 'CloisonnePOS',
      component: () => import('@/views/cloisonne/POS'),
      meta: {
        title: 'POS销售',
        icon: 'shopping-cart',
        permission: ['1004'],
        keepAlive: true
      }
    },
    {
      path: '/cloisonne/task',
      name: 'CloisonneTask',
      component: () => import('@/views/cloisonne/Task'),
      meta: {
        title: '任务管理',
        icon: 'check-square',
        permission: ['1005'],
        keepAlive: true
      }
    },
    {
      path: '/cloisonne/config',
      name: 'CloisonneConfig',
      component: () => import('@/views/cloisonne/Config'),
      meta: {
        title: '模块配置',
        icon: 'setting',
        permission: ['1006'],
        keepAlive: false
      }
    }
  ]
}

export default CloisonneRouter
