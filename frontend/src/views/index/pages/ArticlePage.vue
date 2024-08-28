<script setup>
import { onMounted, ref, reactive} from 'vue'
import { useRoute, useRouter } from "vue-router";
import {getArticle} from "@/net/article.js";
import {UserFilled} from "@element-plus/icons-vue";
import '@vueup/vue-quill/dist/vue-quill.bubble.css';
import { QuillEditor } from '@vueup/vue-quill'
import {formatTimestamp} from "../../../net/utils.js";

const route = useRoute();

const router = useRouter();

const editorRef = ref()

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
  getArticle(route.params.id, (data) => {
    Object.assign(article, data);
  })
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="bg-white pt-20 min-h-screen">
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