<script setup>
import {watch, ref, reactive, onMounted, computed, defineExpose} from "vue";
import {useRoute, useRouter} from "vue-router";
import {getArticle} from "@/net/article.js";
import {useStore} from "vuex";
import {ElMessage} from "element-plus";
import {QuillEditor} from "@vueup/vue-quill";
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import '@vueup/vue-quill/dist/vue-quill.bubble.css';

const router = useRouter()

const route = useRoute()

const store = useStore()

const articleId = computed(() => route.params.id)

const article = reactive({
  id: null,
  title: '',
  summary: '',
  content: '',
  authorId: null
})

const editorRef = ref()

const titleRef = ref()

const summaryRef = ref()

const fetchData = () => {
  getArticle(articleId.value, (data) => {
    Object.assign(article, data)

    if(article.authorId !== store.state.userId) {
      ElMessage.error('非法操作')
      router.push('/')
    }
  }, () => {
    router.push('/')
  })
}

const updateTextareaHeight = () => {
  summaryRef.value.style.height = summaryRef.value.scrollHeight + 'px'
  titleRef.value.style.height = titleRef.value.scrollHeight + 'px'
}

onMounted(() => {
  fetchData()
})

watch(articleId, () => {
  fetchData()
})

defineExpose({
  article
})
</script>

<template>
  <div class="w-1/2 mx-auto">
    <div class="w-full">
      <div class="mt-20">
        <div>
          <textarea ref="titleRef" v-model="article.title" @input="updateTextareaHeight" maxlength="80" placeholder="输入标题" class="px-4 w-full h-12 outline-none resize-none font-bold text-3xl"/>
        </div>
        <div>
          <textarea ref="summaryRef" v-model="article.summary" @input="updateTextareaHeight" maxlength="500" placeholder="输入摘要" class="px-4 w-full h-6 outline-none resize-none"></textarea>
        </div>
      </div>
      <div class="my-8">
        <QuillEditor
            ref="editorRef"
            v-model:content="article.content"
            theme="bubble"
            toolbar="full"
            contentType="html"
            placeholder="请在此输入，建议内容控制在两万字以内"
        >
        </QuillEditor>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>