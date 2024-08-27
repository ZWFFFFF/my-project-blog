import {createStore} from 'vuex'
import keys from "@/net/const.js";

const authItemName = keys.authItemName

const store = createStore({
    // 存储应用的状态数据。比如用户信息、购物车商品等
    state() {
        return {
            userId: null
        }
    },
    // 修改状态的事件(需要提交才执行)
    mutations: {
        setUserId(state, id) {
            state.userId = id
        },
        clearUserId(state) {
            state.userId = null
        }
    },
    // 对事件进行提交的动作
    actions: {
        initializeUserId({ commit }) {
            const str = localStorage.getItem(authItemName)
            if(str !== null) { // 有token才设置
                const authObj = JSON.parse(str)
                commit('setUserId', authObj.id)
            }
        },
        login({ commit }, userId) {
            commit('setUserId', userId)
        },
        logout({ commit }) {
            commit('clearUserId')
        }
    }
})

export default store