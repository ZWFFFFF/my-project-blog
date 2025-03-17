import {get} from "@/net/index.js";
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

export {getUserInfo, getUserList, banUser, unbanUser}