import './style.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from "./store";
import axios from 'axios'

axios.defaults.baseURL = 'http://localhost:8080' // axios异步请求后端地址

const app = createApp(App)
app.use(router)
app.use(store)
app.mount('#app')

store.dispatch('initializeUserId')