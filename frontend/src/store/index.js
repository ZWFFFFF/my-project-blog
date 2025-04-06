import {createStore} from 'vuex'
import keys from "@/net/const.js";
import {getUserInfo} from "@/net/user.js";

const authItemName = keys.authItemName

const store = createStore({
    // 存储应用的状态数据。比如用户信息、购物车商品等
    state() {
        return {
            user: {
                id: null,
                username: null,
                email: null,
                avatar: null
            },
            recommendArticles: [],
        }
    },
    // 修改状态的事件(需要提交才执行)
    mutations: {
        setUser(state, user) {
            state.user = { ...user }
        },
        cleanUser(state) {
            state.user = {
                id: null,
                username: null,
                email: null,
                avatar: null
            }
        },
        updateUserAvatar(state, avatar) {
            state.user.avatar = avatar
        },
        setRecommendArticles(state, articles) {
            state.recommendArticles = articles
        }
    },
    // 对事件进行提交的动作
    actions: {
        initializeUser({ commit }) {
            const str = localStorage.getItem(authItemName)
            if(str) { // 有token才设置
                const authObj = JSON.parse(str)
                const user = {
                    id: null,
                    username: null,
                    email: null,
                    avatar: null
                }
                getUserInfo(authObj.id, (data) => {
                    user.id = data.id
                    user.username = data.username
                    user.email = data.email
                    user.avatar = data.avatar
                    commit('setUser', user)
                })
            }
        },
        logout({ commit }) {
            commit('cleanUser')
        },
        updateAvatar({ commit }, avatar) {
            commit('updateUserAvatar', avatar)
        },
        initializedRecommendArticles({ commit }, articles) {
            commit('setRecommendArticles', articles)
        }
    }
})

export default store