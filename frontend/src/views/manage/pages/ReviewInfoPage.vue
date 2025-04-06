<script setup>
import {UserFilled} from "@element-plus/icons-vue";
import {QuillEditor} from "@vueup/vue-quill";
import {reactive, ref} from "vue";
import {approveReview, getReviewedArticle, rejectReview} from "@/net/article.js";
import {useRoute, useRouter} from "vue-router";
import {onMounted} from "vue";
import {formatTimestamp} from "@/net/utils.js";
import '@vueup/vue-quill/dist/vue-quill.bubble.css';
import Button from "@/components/Button.vue";

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
        <span class="text-xl font-bold">文章审核</span>
      </div>
      <div class="border-b">
        <div class="container mx-auto w-[680px]">
          <div class="px-2 border-b">
            <div class="my-12">
              <div class="mb-10 break-words w-full">
                <p class="font-extrabold text-4xl">{{ article.title }}</p>
              </div>
              <div class="flex items-center gap-4 mb-10">
                <router-link :to="{ path: `/user/${article.authorId}/lists` }">
                  <el-avatar
                      :icon="UserFilled"
                      :src="article.authorAvatar || undefined"
                      :fit="'fill'"
                      :size="40"
                  />
                </router-link>
                <div class="text-sm">
                  <span>{{ article.author }}</span>
                  <div>
                    <span>发布于：</span>
                    <span class="text-gray-400">{{ formatTimestamp(article.createdAt) }}</span>
                  </div>
                </div>
              </div>
              <div class="break-words w-full mb-10">
                <p>{{ article.summary }}</p>
              </div>
              <div class="my-12">
                <div class="w-full">
                  <img class="max-h-[326px] w-full object-cover" :src="article.previewImage">
                </div>
              </div>
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
        </div>
      </div>
      <div class="px-8">
        <div class="py-4">
          <span class="font-bold text-xl text-zinc-400">审核结果</span>
        </div>
        <div class="flex justify-center items-center gap-8">
          <Button class="text-sm" :style="'grey'" @click="articleReject(articleId)"><span>审核不通过</span></Button>
          <Button class="text-sm" @click="articleApprove(articleId)"><span>审核通过</span></Button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>