import {get} from '@/net'

function getArticleList(success) {
    get({
        url: 'api/article/article-list',
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
export {getArticleList, searchArticleList}