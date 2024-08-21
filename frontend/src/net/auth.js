import {deleteAccessToken, get, post, takeAccessToken} from "@/net/index.js";
import {ElMessage} from "element-plus";

// 权限相关

// localStorage: ('access_token', {token: '', expire: })
const authItemName = 'access_token'

// access_token保存
function storeAccessToken(token, expire) {
    const authObj = { token: token, expire: expire}
    const strAuthObj = JSON.stringify(authObj)
    localStorage.setItem(authItemName, strAuthObj)
}

// 是否有权限校验（没有token返回false）
function isAuthorized() {
    return takeAccessToken() !== null
}

function login(username, password, success) {
    post({
        url: 'api/auth/login',
        data: {
            username: username,
            password: password
        },
        header: {
            // axios默认是以json格式发送数据，但是后端SpringSecurity登录接收的是表单数据
            'Content-Type': 'application/x-www-form-urlencoded' // 数据以表单形式发送
        },
        success: (data) => {
            storeAccessToken(data.token, data.expire)
            ElMessage.success(`登录成功，${data.username}`)
            success(data) // 可以取出其他信息保存....
        },
        withToken: false
    })
}

function logout(success) {
    get({
        url: 'api/auth/logout',
        success:() => {
            deleteAccessToken()
            ElMessage.success('退出登录成功')
            success()
        }
    })
}