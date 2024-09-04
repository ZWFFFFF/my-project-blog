// axios异步请求封装
import axios from'axios'
import {ElMessage} from "element-plus";
import keys from "@/net/const.js";

// localStorage: (key, authItem -> { token: token, expire: expire, id: id} )
const authItemName = keys.authItemName

const defaultError = (err) => {
    console.log(err)
    ElMessage.error('发生了一些错误，请联系管理员')
}

const defaultFailure = (message, code, url) => {
    console.warn(`request url: ${url}, code: ${code}, message: ${message}`)
    ElMessage.warning(message)
}

function internalPost(url, data, header, success, failure, error = defaultError) {
    axios.post(url, data, { headers: header }).then(({data}) => {
        if(data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err))
}

function internalGet(url, header, success, failure, error = defaultError) {
    axios.get(url, { headers: header }).then(({data}) => {
        if(data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err))
}

// 获取access_token
function takeAccessToken() {
    const str = localStorage.getItem(authItemName)
    if(str === null) return null;

    const authObj = JSON.parse(str)
    const expireTime = new Date(authObj.expire).getTime()
    const currentTime = new Date().getTime()

    if(expireTime <= currentTime) {
        deleteAccessToken()
        ElMessage.warning('登录状态已过期，请重新登录')
        return null
    }
    return authObj.token
}

// access_token删除
function deleteAccessToken() {
    localStorage.removeItem(authItemName)
}

// 生成携带上access_token的请求头
function accessHeader() {
    const token = takeAccessToken()

    return token ? {
        'Authorization': `Bearer ${token}`
    } : {}
}

// 对外开放的get方法（默认携带上token）
function get({ url, header = {}, success, failure = defaultFailure, withToken = true }) {
    if(withToken) {
        let tokenHeader = accessHeader()
        internalGet(url, { ...tokenHeader, ...header }, success, failure)
    } else {
        internalGet(url, header, success, failure)
    }
}

// 对外开放的post方法（默认携带上token）
function post({ url, data, header = {}, success, failure = defaultFailure, withToken = true }) {
    if(withToken) {
        let tokenHeader = accessHeader()
        internalPost(url, data, { ...tokenHeader, ...header }, success, failure)
    } else {
        internalPost(url, data, header, success, failure)
    }
}

export { get, post, takeAccessToken, deleteAccessToken }