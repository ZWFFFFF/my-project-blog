import './style.css'
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css'; // 引入element-plus的样式文件
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from "./store";
import axios from 'axios'

axios.defaults.baseURL = 'http://localhost:8080' // axios异步请求后端地址

const app = createApp(App)
app.use(ElementPlus);
app.use(router)
app.use(store)
app.mount('#app')

store.dispatch('initializeUser').then()