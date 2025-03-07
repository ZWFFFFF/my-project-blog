import {get} from "@/net/index.js";
function getUserInfo(id, success) {
    get({
        url: 'api/user/user-info?id=' + id,
        success: (data) => {
            success(data)
        }
    })
}

export {getUserInfo}