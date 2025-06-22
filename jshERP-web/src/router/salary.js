import { RouteView } from '@/layouts'

/**
 * 薪酬管理模块路由配置
 */
export default {
  path: '/salary',
  name: 'salary',
  component: RouteView,
  redirect: '/salary/profile',
  meta: {
    title: '薪酬管理',
    icon: 'money-collect',
    permission: ['salary']
  },
  children: [
    {
      path: '/salary/profile',
      name: 'SalaryProfile',
      component: () => import('@/views/salary/SalaryProfileList'),
      meta: {
        title: '薪酬档案',
        permission: ['salary:profile:list']
      }
    },
    {
      path: '/salary/calculation',
      name: 'SalaryCalculation',
      component: () => import('@/views/salary/SalaryCalculation'),
      meta: {
        title: '薪酬计算',
        permission: ['salary:calculation:list']
      }
    },
    {
      path: '/salary/payment',
      name: 'SalaryPayment',
      component: () => import('@/views/salary/SalaryPayment'),
      meta: {
        title: '薪酬发放',
        permission: ['salary:payment:list']
      }
    },
    {
      path: '/salary/inquiry',
      name: 'SalaryInquiry',
      component: () => import('@/views/salary/SalaryInquiry'),
      meta: {
        title: '薪资查询',
        permission: ['salary:inquiry:list']
      }
    },
    {
      path: '/salary/config',
      name: 'SalaryConfig',
      component: () => import('@/views/salary/SalaryConfig'),
      meta: {
        title: '薪酬配置',
        permission: ['salary:config:list']
      }
    }
  ]
}
