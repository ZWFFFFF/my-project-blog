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
        },
        withToken: false
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

function getReviewedArticle(id, success, failure = () => {}) {
    get({
        url: 'api/article/reviewing-article-info?articleId=' + id,
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

function startReview(articleId, success, failure = () => {}) {
    get({
        url: 'api/article/start-review?articleId=' + articleId,
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

function approveReview(articleId, success, failure = () => {}) {
    get({
        url: 'api/article/approve-review?articleId=' + articleId,
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

function rejectReview(articleId, success, failure = () => {}) {
    get({
        url: 'api/article/reject-review?articleId=' + articleId,
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

function resetReviewing(articleId, success, failure = () => {}) {
    get({
        url: 'api/article/reset-reviewing?articleId=' + articleId,
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

export {
    getArticleList, getArticle, getDraft, getUserArticles,
    getUserDrafts, getUserReviewArticles, searchArticleList, createArticle,
    updateArticle, updateDraft, deleteArticle, deleteDraft,
    submitToReview, getPendingReviewList, getReviewingList, getReviewedArticle,
    startReview, approveReview, rejectReview, resetReviewing
}