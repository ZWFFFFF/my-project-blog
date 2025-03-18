import {get, post} from "@/net/index.js";
import {ElMessage} from "element-plus";
function getUserInfo(id, success) {
    get({
        url: 'api/user/user-info?id=' + id,
        success: (data) => {
            success(data)
        }
    })
}

function getUserList(success) {
    get({
        url: 'api/user/user-list',
        success: (data) => {
            success(data)
        }
    })
}

function banUser(id, success) {
    get({
        url: 'api/admin/ban-user?userId=' + id,
        success: () => {
            success()
        }
    })
}

function unbanUser(id, success) {
    get({
        url: 'api/admin/unban-user?userId=' + id,
        success: () => {
            success()
        }
    })
}

function changePassword(id, oldPassword, newPassword, success) {
    post({
        url: 'api/user/change-password',
        data: {
            id: id,
            oldPassword: oldPassword,
            newPassword: newPassword
        },
        success: () => {
            success()
        }
    })
}

export {getUserInfo, getUserList, banUser, unbanUser, changePassword}