import { createRouter, createWebHistory } from 'vue-router'
import {isAuthorized} from "@/net/auth.js";

// vue路由：根据不同的url渲染不同的vue组件，所有组件只在单页面上渲染

// 定义路径和组件匹配
const routes = [
    {
        path: '/welcome',
        name: 'welcome',
        component: () => import('@/views/welcome/WelcomeView.vue'),
        children: [
            {
                path: '',
                name: 'welcome-login',
                component: () => import('@/views/welcome/pages/LoginPage.vue')
            }, {
                path: 'register',
                name: 'welcome-register',
                component: () => import('@/views/welcome/pages/RegisterPage.vue')
            }, {
                path: 'forget',
                name: 'welcome-forget',
                component: () => import('@/views/welcome/pages/ForgetPage.vue')
            }
        ]
    }, {
        path: '/',
        name: 'index',
        component: () => import('@/views/index/IndexView.vue'),
        children: [
            {
                path: '',
                name: 'index-home',
                component: () => import('@/views/index/pages/HomePage.vue')
            },
            {
                path: 'search',
                name: 'index-search',
                component: () => import('@/views/index/pages/SearchPage.vue')
            },
            {
                path: 'article',
                redirect: '/'
            },
            {
                path: 'article/:id',
                name: 'index-article',
                component: () => import('@/views/index/pages/ArticlePage.vue')
            }
        ]
    }, {
        path: '/editor',
        name: 'editor',
        component: () => import('@/views/editor/EditView.vue'),
        children: [
            {
                path: '',
                name: 'editor-article',
                component: () => import('@/views/editor/pages/EditorPages.vue')
            }
        ]
    }, {
        path: '/writing',
        name: 'writing',
        component: () => import('@/views/writing/WritingView.vue'),
        children: [
            {
                path: '',
                name: 'writing-draft',
                component: () => import('@/views/writing/pages/DraftPage.vue')
            },
            {
                path: 'published',
                name: 'writing-published',
                component: () => import('@/views/writing/pages/PublishedPage.vue')
            }
        ]
    }, {
        path: '/:pathMatch(.*)*',
        name: 'notfound',
        component: () => import('@/views/NotFoundView.vue')
    }
]

// 创建路由实例
const router = createRouter(
    {
        history: createWebHistory(import.meta.env.BASE_URL),
        routes
    }
)

// 配置导航守卫
router.beforeEach((to, from) => {
    const isAuthenticated = isAuthorized()
    if(isAuthenticated && to.name.startsWith('welcome')) return { name: 'index-home' } // 已登录，却访问登录页面，返回主页面
    if(!isAuthenticated && !to.name.startsWith('welcome')) return { name: 'welcome-login' } // 未登录，却访问非登录页面，返回登录页面
    return true;
})

export default router