import { createRouter, createWebHistory } from 'vue-router'

// vue路由：根据不同的url渲染不同的vue组件，所有组件只在单页面上渲染

// 定义路径和组件匹配
const routes = [

]

// 创建路由实例
const router = createRouter(
    {
        history: createWebHistory(import.meta.env.BASE_URL),
        routes
    }
)

// 配置导航守卫
// router.beforeEach((to, from) => {
// })

export default router