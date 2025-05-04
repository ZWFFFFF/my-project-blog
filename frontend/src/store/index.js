import {createStore} from 'vuex'
import keys from "@/net/const.js";
import {getUserInfo} from "@/net/user.js";

const authItemName = keys.authItemName

const store = createStore({
    // 存储应用的状态数据。比如用户信息、购物车商品等
    state() {
        return {
            user: {
                id: '',
                username: '',
                email: '',
                avatar: ''
            },
            recommendArticles: [],
        }
    },
    // 修改状态的事件(需要提交才执行)
    mutations: {
        setUser(state, user) {
            state.user = { ...user }
        },
        setUserId(state, userId) {
            state.user.id = userId
        },
        cleanUser(state) {
            state.user = {
                id: '',
                username: '',
                email: '',
                avatar: ''
            }
        },
        updateUserAvatar(state, avatar) {
            state.user.avatar = avatar
        },
        updateUsername(state, username) {
            state.user.username = username
        },
        updateEmail(state, email) {
            state.user.email = email
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
                commit('setUserId', authObj.id)

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
        updateUsername({ commit }, username) {
            commit('updateUsername', username)
        },
        updateEmail({ commit }, email) {
            commit('updateEmail', email)
        },
        deleteAccount({ commit }) {
          commit('cleanUser')
        },
        initializedRecommendArticles({ commit }, articles) {
            commit('setRecommendArticles', articles)
        }
    }
})

export default store