<script setup>
import { onMounted, ref, reactive} from 'vue'
import { useRoute, useRouter } from "vue-router";
import {disLikeArticle, getArticle, getDraft, likeArticle} from "@/net/article.js";
import {UserFilled} from "@element-plus/icons-vue";
import '@vueup/vue-quill/dist/vue-quill.bubble.css';
import { QuillEditor } from '@vueup/vue-quill'
import {ElMessage} from "element-plus";
import {formatTimestamp, throttle} from "@/net/utils.js";
import {useStore} from "vuex";

const route = useRoute();
const router = useRouter();
const store = useStore()
const editorRef = ref()
const articleType = ref(route.params.type)
const articleId = ref(route.params.id)
const article = reactive({
  id: null,
  title: '',
  summary: '',
  content: '',
  authorId: null,
  author: '',
  createdAt: '',
  updatedAt: '',
  view: null,
  like: null
})
const isLiked = ref(false)
const isCollected = ref(false)

const fetchData = () => {
  if(articleType.value === 'approved') {
    getArticle(articleId.value, (data) => {
      Object.assign(article, data)
      // 不需要检测userId，因为任何人都可以看已发布的文章
    })
  } else if(articleType.value === 'draft') {
    getDraft(articleId.value, (data) => {
      Object.assign(article, data)
      // 访问草稿需要检测userId，因为必须时登录的用户才可以看自己的草稿
      if(article.authorId !== store.state.userId) {
        ElMessage.error('非法操作')
        router.push('/')
      }
    })
  }
}

const Like = () => {
  if(articleType.value === 'approved') {
    if(isLiked.value === false) {
      isLiked.value = true
      likeArticle(articleId.value, () => {
        article.like++
      })
    } else {
      isLiked.value = false
      disLikeArticle(articleId.value, () => {
        article.like--
      })
    }
  }
}

const handleLike = throttle(Like, 500)

const Collect = () => {
  if(articleType.value === 'approved') {
    if(isCollected.value === false) {
      isCollected.value = true
      console.log('collect')
    } else {
      isCollected.value = false
      console.log('no-collect')
    }
  }
}

const handleCollect = throttle(Collect, 500)

const share = () => {
  navigator.clipboard.writeText(window.location.href)
  ElMessage.success('链接已复制到剪贴板')
}

const handleShare = throttle(share, 1000)

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="bg-white pt-20 border-t">
    <div class="container mx-auto w-1/2">
      <div>
        <div class="mb-10">
          <div class="flex flex-col justify-center items-center mb-10">
            <div class="mb-4 break-words w-full text-center"><p class="font-extrabold text-4xl">{{ article.title }}</p></div>
            <div class="mb-4 break-words w-full text-center"><p>{{ article.summary }}</p></div>
          </div>
          <div class="flex flex-col justify-center items-center gap-2 mb-5">
            <a>
              <el-avatar :icon="UserFilled"></el-avatar>
            </a>
            <span>{{ article.author }}</span>
          </div>
          <div class="text-center">
            发布于：<span class="text-gray-400">{{ formatTimestamp(article.createdAt) }}</span>
          </div>
        </div>
        <div class="px-2 mb-10">
          <QuillEditor
              ref="editorRef"
              theme="bubble"
              contentType="html"
              :content="article.content"
              :readOnly="true"
          >

          </QuillEditor>
        </div>
        <div class="flex items-center justify-center gap-10 py-6 border-t border-b">
          <button
              class="flex flex-col items-center justify-center gap-2"
              @click="handleLike"
          >
            <span v-show="isLiked === false" class="hover:text-zinc-500 transition">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="none" stroke-width="1.5" stroke="currentColor" class="size-5">
                <path d="M1 8.25a1.25 1.25 0 1 1 2.5 0v7.5a1.25 1.25 0 1 1-2.5 0v-7.5ZM11 3V1.7c0-.268.14-.526.395-.607A2 2 0 0 1 14 3c0 .995-.182 1.948-.514 2.826-.204.54.166 1.174.744 1.174h2.52c1.243 0 2.261 1.01 2.146 2.247a23.864 23.864 0 0 1-1.341 5.974C17.153 16.323 16.072 17 14.9 17h-3.192a3 3 0 0 1-1.341-.317l-2.734-1.366A3 3 0 0 0 6.292 15H5V8h.963c.685 0 1.258-.483 1.612-1.068a4.011 4.011 0 0 1 2.166-1.73c.432-.143.853-.386 1.011-.814.16-.432.248-.9.248-1.388Z" />
              </svg>
            </span>
            <span v-show="isLiked">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="size-5">
                <path d="M1 8.25a1.25 1.25 0 1 1 2.5 0v7.5a1.25 1.25 0 1 1-2.5 0v-7.5ZM11 3V1.7c0-.268.14-.526.395-.607A2 2 0 0 1 14 3c0 .995-.182 1.948-.514 2.826-.204.54.166 1.174.744 1.174h2.52c1.243 0 2.261 1.01 2.146 2.247a23.864 23.864 0 0 1-1.341 5.974C17.153 16.323 16.072 17 14.9 17h-3.192a3 3 0 0 1-1.341-.317l-2.734-1.366A3 3 0 0 0 6.292 15H5V8h.963c.685 0 1.258-.483 1.612-1.068a4.011 4.011 0 0 1 2.166-1.73c.432-.143.853-.386 1.011-.814.16-.432.248-.9.248-1.388Z" />
              </svg>
            </span>
            <span class="text-sm text-zinc-500">{{ article.like === null ? 0 : article.like }}</span>
          </button>
          <button
              class="flex flex-col items-center justify-center gap-2"
              @click="handleCollect"
          >
            <span v-show="isCollected === false" class="hover:text-zinc-500 transition">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="none" stroke-width="1.5" stroke="currentColor" class="size-5">
                <path fill-rule="evenodd" d="M10 2c-1.716 0-3.408.106-5.07.31C3.806 2.45 3 3.414 3 4.517V17.25a.75.75 0 0 0 1.075.676L10 15.082l5.925 2.844A.75.75 0 0 0 17 17.25V4.517c0-1.103-.806-2.068-1.93-2.207A41.403 41.403 0 0 0 10 2Z" clip-rule="evenodd" />
              </svg>
            </span>
            <span v-show="isCollected">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="size-5">
                <path fill-rule="evenodd" d="M10 2c-1.716 0-3.408.106-5.07.31C3.806 2.45 3 3.414 3 4.517V17.25a.75.75 0 0 0 1.075.676L10 15.082l5.925 2.844A.75.75 0 0 0 17 17.25V4.517c0-1.103-.806-2.068-1.93-2.207A41.403 41.403 0 0 0 10 2Z" clip-rule="evenodd" />
              </svg>
            </span>
            <span class="text-sm text-zinc-500">收藏</span>
          </button>
          <button
              class="flex flex-col items-center justify-center gap-2"
              @click="handleShare"
          >
            <span class="hover:text-zinc-500 transition">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="size-5">
                <path d="M13 4.5a2.5 2.5 0 1 1 .702 1.737L6.97 9.604a2.518 2.518 0 0 1 0 .792l6.733 3.367a2.5 2.5 0 1 1-.671 1.341l-6.733-3.367a2.5 2.5 0 1 1 0-3.475l6.733-3.366A2.52 2.52 0 0 1 13 4.5Z" />
              </svg>
            </span>
            <span class="text-sm text-zinc-500">分享</span>
          </button>
        </div>
      </div>
      <div>
        评论
      </div>
    </div>
  </div>
  <el-backtop :right="100" :bottom="100" />
</template>

<style scoped>

</style>