import {get} from "@/net/index.js";
function getLogs(success) {
    get({
        url: 'api/admin/get-operation-log',
        success: (data) => {
            success(data)
        }
    })
}

export {getLogs}
