<script setup>
import {UserFilled} from "@element-plus/icons-vue";
import {QuillEditor} from "@vueup/vue-quill";
import {reactive, ref} from "vue";
import {approveReview, getReviewedArticle, rejectReview} from "@/net/article.js";
import {useRoute, useRouter} from "vue-router";
import {onMounted} from "vue";
import {formatTimestamp} from "@/net/utils.js";
import '@vueup/vue-quill/dist/vue-quill.bubble.css';

const route = useRoute();

const router = useRouter();

const editorRef = ref()

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

const fetchArticle = () => {
  getReviewedArticle(articleId.value, (data) => {
    Object.assign(article, data)
  })
}

function articleApprove(id) {
  if(confirm("确定通过审核吗？")) {
    approveReview(id, () => router.push('/manage'))
  }
}

function articleReject(id) {
  if(confirm("确定不通过审核吗？")) {
    rejectReview(id, () => router.push('/manage'))
  }
}

onMounted(() => {
  fetchArticle()
})
</script>

<template>
  <div class="h-full">
    <div class="bg-white py-8">
      <div class="px-8 py-4 border-b">
        <span class="text-xl">文章审核</span>
      </div>
      <div class="container border-b">
        <div class="mx-auto w-1/2 my-5">
          <div class="mb-2">
            <div class="mb-4 break-words"><h1 class="font-extrabold text-3xl">{{ article.title }}</h1></div>
            <div class="mb-4 break-words"><p>summary</p></div>
            <div class="flex items-center">
              <a class="flex items-center mr-8"><el-avatar :icon="UserFilled" class="mr-2"></el-avatar><span>{{ article.author }}</span></a>
              <span class="text-gray-400">{{ formatTimestamp(article.createdAt) }}</span>
            </div>
          </div>
          <div>
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
      <div class="px-8">
        <div class="py-4">
          <span class="text-xl">审核结果</span>
        </div>
        <div class="flex justify-center items-center gap-8">
          <div class="py-2 px-4 rounded border hover:bg-gray-100 shadow-sm cursor-pointer" @click="articleApprove(articleId)"><span>审核通过</span></div>
          <div class="py-2 px-4 rounded border hover:bg-gray-100 shadow-sm cursor-pointer" @click="articleReject(articleId)"><span>审核不通过</span></div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>