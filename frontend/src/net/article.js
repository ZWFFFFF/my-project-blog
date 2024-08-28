import {get, post} from '@/net'
import {ElMessage} from "element-plus";

function getArticleList(success) {
    get({
        url: 'api/article/article-list',
        success: (data) => {
            success(data)
        },
        withToken: false
    })
}

function getArticle(id, success) {
    get({
        url: 'api/article/article-info?articleId=' + id,
        success: (data) => {
            success(data)
        },
        withToken: false
    })
}

function searchArticleList(keyword, success) {
    get({
        url: 'api/article/search?keyword=' + keyword,
        success: (data) => {
            success(data)
        },
        withToken: false
    })
}

function createArticle(article, success) {
    post({
        url: 'api/article/create-article',
        data: {...article},
        success: (data) => {
            ElMessage.success('创建成功')
            success()
        }
    })
}
export {getArticleList, getArticle, searchArticleList, createArticle}