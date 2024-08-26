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

export {getArticleList}