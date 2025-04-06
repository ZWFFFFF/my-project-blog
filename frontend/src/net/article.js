import {get, post} from '@/net'
import {ElMessage} from "element-plus";

function getArticleList(success) {
    get({
        url: 'api/article/article-list',
        success: (data) => {
            success(data)
        }
    })
}

function getUserArticles(id, success) {
    get({
        url: 'api/article/user-articles?userId=' + id,
        success: (data) => {
            success(data)
        }
    })
}

function getUserDrafts(success) {
    get({
        url: 'api/article/draft-list',
        success: (data) => {
            success(data)
        }
    })
}

function getUserReviewArticles(success) {
    get({
        url: 'api/article/user-review-list',
        success: (data) => {
            success(data)
        }
    })
}

function getArticle(id, success, failure = () => {}) {
    get({
        url: 'api/article/article-info?articleId=' + id,
        success: (data) => {
            success(data)
        },
        failure: (message, code, url) => {
            console.warn(`request url: ${url}, code: ${code}, message: ${message}`)
            ElMessage.warning(message)
            failure()
        }
    })
}

function getDraft(id, success, failure = () => {}) {
    get({
        url: 'api/article/draft-info?articleId=' + id,
        success: (data) => {
            success(data)
        },
        failure: (message, code, url) => {
            console.warn(`request url: ${url}, code: ${code}, message: ${message}`)
            ElMessage.warning(message)
            failure()
        }
    })
}

function searchArticleList(keyword, success) {
    get({
        url: 'api/article/search?keyword=' + keyword,
        success: (data) => {
            success(data)
        }
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
            ElMessage.success('等待审核')
            success()
        }
    })
}

function updateDraft(draft, success) {
    post({
        url: 'api/article/update-draft',
        data: {...draft},
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

function deleteDraft(id, success) {
    get({
        url: 'api/article/delete-draft?articleId=' + id,
        success: () => {
            ElMessage.success('删除成功')
            success()
        }
    })
}

function submitToReview(article, success) {
    post({
        url: 'api/article/submission-review',
        data: {...article},
        success: () => {
            ElMessage.success('等待审核')
            success()
        }
    })
}

function getPendingReviewList(success) {
    get({
        url: 'api/article/pending-review-list',
        success: (data) => {
            success(data)
        }
    })
}

function getReviewingList(success) {
    get({
        url: 'api/article/reviewing-list',
        success: (data) => {
            success(data)
        }
    })
}

function getReviewedArticle(id, success) {
    get({
        url: 'api/article/reviewing-article-info?articleId=' + id,
        success: (data) => {
            success(data)
        }
    })
}

function startReview(articleId, success) {
    get({
        url: 'api/article/start-review?articleId=' + articleId,
        success: () => {
            success()
        }
    })
}

function approveReview(articleId, success) {
    get({
        url: 'api/article/approve-review?articleId=' + articleId,
        success: () => {
            ElMessage.success('审核成功')
            success()
        }
    })
}

function rejectReview(articleId, success) {
    get({
        url: 'api/article/reject-review?articleId=' + articleId,
        success: () => {
            ElMessage.success('审核成功')
            success()
        }
    })
}

function resetReviewing(articleId, success) {
    get({
        url: 'api/article/reset-reviewing?articleId=' + articleId,
        success: () => {
            ElMessage.success('操作成功')
            success()
        }
    })
}

function getTakeDownList(success) {
    get({
        url: 'api/article/take-down-list',
        success: (data) => {
            success(data)
        }
    })
}

function takeDownArticle(articleIds, success) {
    post({
        url: 'api/article/take-down',
        data: articleIds,
        success: () => {
            ElMessage.success('操作成功')
            success()
        }
    })
}

function recoverArticle(articleIds, success) {
    post({
        url: 'api/article/recover',
        data: articleIds,
        success: () => {
            ElMessage.success('操作成功')
            success()
        }
    })
}

function likeArticleToggle(articleId, success) {
    get({
        url: 'api/article/like-toggle?articleId=' + articleId,
        success: () => {
            success();
        }
    })
}

function collectArticleToggle(articleId, success) {
    get({
        url: 'api/article/collect-toggle?articleId=' + articleId,
        success: () => {
            success()
        }
    })
}

function getUserArticleCollects(userId, success) {
    get({
        url: 'api/article/collect-list?userId=' + userId,
        success: (data) => {
            success(data)
        }
    })
}

function uploadPreviewImage(file) {
    return new Promise((resolve, reject) => {
        post({
            url: 'api/article/upload-preview-image',
            data: file,
            success: (data) => resolve(data),
            failure: (message, code, url) => {
                console.warn(`request url: ${url}, code: ${code}, message: ${message}`)
                reject(new Error(message))
            }
        })
    })
}

export {
    getArticleList, getArticle, getDraft, getUserArticles,
    getUserDrafts, getUserReviewArticles, searchArticleList, createArticle,
    updateArticle, updateDraft, deleteArticle, deleteDraft,
    submitToReview, getPendingReviewList, getReviewingList, getReviewedArticle,
    startReview, approveReview, rejectReview, resetReviewing,
    getTakeDownList, takeDownArticle, recoverArticle, likeArticleToggle,
    collectArticleToggle, getUserArticleCollects, uploadPreviewImage
}