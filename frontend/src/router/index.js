import { createRouter, createWebHistory } from 'vue-router'
import {getAuthRole, isAuthorized} from "@/net/auth.js";
import {ElMessage} from "element-plus";

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
                path: 'home',
                name: 'index-home',
                component: () => import('@/views/index/pages/HomePage.vue')
            },
            {
                path: '',
                redirect: '/home'
            },
            {
                path: 'search',
                name: 'index-search',
                component: () => import('@/views/index/pages/SearchPage.vue')
            },
            {
                path: 'article/:type(draft|approved)/:id',
                name: 'index-article',
                component: () => import('@/views/index/pages/ArticlePage.vue')
            },
            {
                path: 'user/:id',
                name: 'index-user',
                component: () => import('@/views/index/pages/UserPage.vue')
            }
        ]
    }, {
        path: '/editor',
        name: 'editor',
        component: () => import('@/views/editor/EditView.vue'),
        children: [
            {
              path: '',
              name: 'editor-index',
              redirect: '/editor/create',
            },
            {
                path: 'create',
                name: 'editor-create',
                component: () => import('@/views/editor/pages/CreatePage.vue')
            },
            {
                path: 'update/:type(draft|article)/:id',
                name: 'editor-update',
                component: () => import('@/views/editor/pages/UpdatePage.vue')
            }
        ]
    }, {
        path: '/writing',
        name: 'writing',
        component: () => import('@/views/writing/WritingView.vue'),
        children: [
            {
                path: '',
                name: 'writing-index',
                redirect: '/writing/draft'
            },
            {
                path: 'draft',
                name: 'writing-draft',
                component: () => import('@/views/writing/pages/DraftPage.vue')
            },
            {
                path: 'published',
                name: 'writing-published',
                component: () => import('@/views/writing/pages/PublishedPage.vue')
            },
            {
                path: 'reviewing',
                name: 'writing-reviewing',
                component: () => import('@/views/writing/pages/ReviewingPage.vue')
            }
        ]
    }, {
        path: '/manage',
        name: 'manage',
        component: () => import('@/views/manage/ManageView.vue'),
        children: [
            {
              path: '',
              name: 'manage-index',
              redirect: '/manage/article/review',
            },
            {
                path: 'article/review',
                name: 'manage-article-review',
                component: () => import('@/views/manage/pages/ReviewPage.vue')
            },
            {
                path: 'article/review/:id',
                name: 'manage-article-review-info',
                component: () => import('@/views/manage/pages/ReviewInfoPage.vue')
            }
        ]
    }, {
        path: '/:pathMatch(.*)*',
        name: 'notfound',
        component: () => import('@/views/NotFoundView.vue')
    }, {
        path: '/test',
        name: 'test',
        component: () => import('@/views/Test.vue')
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
router.beforeEach((to) => {
    const isAuthenticated = isAuthorized()
    const role = getAuthRole()

    if(!isAuthenticated && !to.name.startsWith('welcome')) return { name: 'welcome-login' } // 未登录，却访问非登录页面，返回登录页面

    if(isAuthenticated && to.name.startsWith('welcome')) {
        // 已登录，却访问登录页面，根据角色重定向
        if (role === 'ADMIN') {
            return { name: 'manage-index' } // 管理员跳转到管理页面
        } else {
            return { name: 'index-home' } // 普通用户跳转到主页面
        }
    }

    // 管理员，登录后返回管理页面
    if(role === 'ADMIN' && to.name.startsWith('index')) {
        return { name: 'manage-index' }
    }

    // 普通用户，却访问管理员页面，返回主页面
    if(role === 'USER' && to.name.startsWith('manage')) {
        ElMessage.warning('非法操作')
        return { name: 'index-home' }
    }

    return true;
})

export default router