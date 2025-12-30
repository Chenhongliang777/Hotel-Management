import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'room-type',
        name: 'RoomType',
        component: () => import('@/views/room/RoomType.vue'),
        meta: { title: '房型管理', icon: 'House' }
      },
      {
        path: 'room',
        name: 'Room',
        component: () => import('@/views/room/Room.vue'),
        meta: { title: '房间管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'room-status',
        name: 'RoomStatus',
        component: () => import('@/views/room/RoomStatus.vue'),
        meta: { title: '房态管理', icon: 'Grid' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/order/Order.vue'),
        meta: { title: '订单管理', icon: 'Document' }
      },
      {
        path: 'checkin',
        name: 'CheckIn',
        component: () => import('@/views/order/CheckIn.vue'),
        meta: { title: '入住办理', icon: 'Promotion' }
      },
      {
        path: 'checkout',
        name: 'CheckOut',
        component: () => import('@/views/order/CheckOut.vue'),
        meta: { title: '退房结算', icon: 'SwitchButton' }
      },
      {
        path: 'pos',
        name: 'Pos',
        component: () => import('@/views/pos/Pos.vue'),
        meta: { title: 'POS消费', icon: 'ShoppingCart' }
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('@/views/inventory/Inventory.vue'),
        meta: { title: '库存管理', icon: 'Box' }
      },
      {
        path: 'cleaning',
        name: 'Cleaning',
        component: () => import('@/views/cleaning/Cleaning.vue'),
        meta: { title: '清洁任务', icon: 'Brush' }
      },
      {
        path: 'employee',
        name: 'Employee',
        component: () => import('@/views/system/Employee.vue'),
        meta: { title: '员工管理', icon: 'User', roles: ['owner'] }
      },
      {
        path: 'guest',
        name: 'Guest',
        component: () => import('@/views/system/Guest.vue'),
        meta: { title: '宾客管理', icon: 'UserFilled' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/Statistics.vue'),
        meta: { title: '经营分析', icon: 'DataAnalysis', roles: ['owner'] }
      },
      {
        path: 'config',
        name: 'Config',
        component: () => import('@/views/system/Config.vue'),
        meta: { title: '系统设置', icon: 'Setting', roles: ['owner'] }
      },
      {
        path: 'log',
        name: 'Log',
        component: () => import('@/views/system/Log.vue'),
        meta: { title: '操作日志', icon: 'Tickets', roles: ['owner'] }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/Role.vue'),
        meta: { title: '角色管理', icon: 'User', roles: ['owner'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 悦居民宿管理系统` : '悦居民宿管理系统'
  
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
