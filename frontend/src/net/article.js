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

function getUserArticles(id, success) {
    get({
        url: 'api/article/user-articles?userId=' + id,
        success: (data) => {
            success(data)
        },
        withToken: false
    })
}

function getArticle(id, success, failure) {
    get({
        url: 'api/article/article-info?articleId=' + id,
        success: (data) => {
            success(data)
        },
        failure: (message, code, url) => {
            console.warn(`request url: ${url}, code: ${code}, message: ${message}`)
            ElMessage.warning(message)
            failure()
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
        success: () => {
            ElMessage.success('创建成功')
            success()
        }
    })
}

function updateArticle(article, success) {
    post({
        url: 'api/article/update-article',
        data: {...article},
        success: () => {
            ElMessage.success('更新成功')
            success()
        }
    })
}

function deleteArticle(id, success) {
    get({
        url: 'api/article/delete-article?articleId=' + id,
        success: () => {
            ElMessage.success('删除成功')
            success()
        }
    })
}
export {getArticleList, getArticle, getUserArticles, searchArticleList, createArticle, updateArticle, deleteArticle}