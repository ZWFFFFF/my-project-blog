import {get, post} from '@/net'
import {ElMessage} from "element-plus";

function creatComment(comment, success) {
   post({
       url: 'api/comment/add-comment',
       data: {...comment},
       success: (data) => {
           ElMessage.success('发送成功')
           success(data)
       }
   })
}

function getComments(id, success) {
    get({
        url: 'api/comment/get-comments?articleId=' + id,
        success: (data) => {
            success(data)
        }
    })
}

function deleteComment(id, success) {
    get({
        url: 'api/comment/delete-comment?commentId=' + id,
        success: () => {
            success()
        }
    })
}

function likeComment(id, success) {
    get({
        url: 'api/comment/like-comment?commentId=' + id,
        success: () => {
            success()
        }
    })
}

function dislikeComment(id, success) {
    get({
        url: 'api/comment/dislike-comment?commentId=' + id,
        success: () => {
            success()
        }
    })
}

export {
    creatComment, getComments, deleteComment,
    likeComment, dislikeComment
}