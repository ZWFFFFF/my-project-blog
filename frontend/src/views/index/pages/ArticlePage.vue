<script setup>
import { onMounted, ref, reactive} from 'vue'
import { useRoute, useRouter } from "vue-router";
import {getArticle, getDraft} from "@/net/article.js";
import {UserFilled} from "@element-plus/icons-vue";
import '@vueup/vue-quill/dist/vue-quill.bubble.css';
import { QuillEditor } from '@vueup/vue-quill'
import {ElMessage} from "element-plus";
import {formatTimestamp} from "@/net/utils.js";
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

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="bg-white pt-20 h-full border-t">
    <div class="container mx-auto w-1/2">
      <div class="mb-20">
        <div class="mb-4 break-words"><h1 class="font-extrabold text-4xl">{{ article.title }}</h1></div>
        <div class="mb-4 break-words"><p>{{ article.summary }}</p></div>
        <div class="flex items-center">
          <a class="flex items-center mr-8"><el-avatar :icon="UserFilled" class="mr-2"></el-avatar><span>{{ article.author }}</span></a>
          <span class="text-gray-400">{{ formatTimestamp(article.createdAt) }}</span>
        </div>
      </div>
      <div class="px-16">
        <QuillEditor
            ref="editorRef"
            theme="bubble"
            contentType="html"
            :content="article.content"
            :readOnly="true"
        >

        </QuillEditor>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>