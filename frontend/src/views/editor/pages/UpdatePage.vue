<script setup>
import {ref, reactive, onMounted, defineExpose, nextTick} from "vue";
import {useRoute, useRouter} from "vue-router";
import {getArticle, getDraft} from "@/net/article.js";
import {useStore} from "vuex";
import {ElMessage} from "element-plus";
import {QuillEditor} from "@vueup/vue-quill";
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import '@vueup/vue-quill/dist/vue-quill.bubble.css';
import BlotFormatter from 'quill-blot-formatter'

const router = useRouter()
const route = useRoute()
const store = useStore()
const articleId = ref(route.params.id)
const articleType = ref(route.params.type)
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
const modules = {
  name: 'blotFormatter',
  module: BlotFormatter,
};

const fetchData = () => {
  if(articleType.value === 'article') {
    getArticle(articleId.value, (data) => {
      Object.assign(article, data)
      if(article.authorId !== store.state.userId) {
        ElMessage.error('非法操作')
        router.push('/')
      }
      // 数据加载完成后再调整高度
      nextTick(() => {
        if (titleRef.value) {
          titleRef.value.dispatchEvent(new Event('input'));
        }
        if (summaryRef.value) {
          summaryRef.value.dispatchEvent(new Event('input'));
        }
      });
    }, () => {
      router.push('/')
    })
  } else if(articleType.value === 'draft') {
    getDraft(articleId.value, (data) => {
      Object.assign(article, data)
      if(article.authorId !== store.state.userId) {
        ElMessage.error('非法操作')
        router.push('/')
      }
      // 数据加载完成后再调整高度
      nextTick(() => {
        if (titleRef.value) {
          titleRef.value.dispatchEvent(new Event('input'));
        }
        if (summaryRef.value) {
          summaryRef.value.dispatchEvent(new Event('input'));
        }
      });
    }, () => {
      router.push('/')
    })
  }
}

const updateTextareaHeight = (event) => {
  const target = event.target
  target.style.height = 'auto'
  target.style.height = target.scrollHeight + 'px'
}

const handleInput = (event) => {
  updateTextareaHeight(event)
}

onMounted(() => {
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
          <textarea ref="titleRef" v-model="article.title" @input="(event) => handleInput(event)" @keydown.enter.prevent maxlength="80" placeholder="输入标题" class="px-4 w-full outline-none resize-none font-bold text-3xl"/>
        </div>
        <div>
          <textarea ref="summaryRef" v-model="article.summary" @input="(event) => handleInput(event)" @keydown.enter.prevent maxlength="500" placeholder="输入摘要" class="px-4 w-full outline-none resize-none"/>
        </div>
      </div>
      <div class="mt-10">
        <QuillEditor
            ref="editorRef"
            v-model:content="article.content"
            :modules="modules"
            theme="snow"
            toolbar="#toolbar"
            contentType="html"
            placeholder="请在此输入，建议内容控制在两万字以内"
        >
          <template #toolbar>
            <div id="toolbar">
              <button class="ql-bold"></button>
              <button class="ql-italic"></button>

              <button class="ql-header" value="1"></button>
              <button class="ql-header" value="2"></button>

              <select class="ql-size">
                <option selected>Normal</option>
                <option value="large">large</option>
              </select>

              <button class="ql-list" value="ordered"></button>
              <button class="ql-list" value="bullet"></button>

              <button class="ql-align" value=""></button>
              <button class="ql-align" value="center"></button>
              <button class="ql-align" value="right"></button>
              <button class="ql-align" value="justify"></button>

              <button class="ql-blockquote"></button>
              <button class="ql-code-block"></button>

              <button class="ql-link"></button>
              <button class="ql-image"></button>
              <button class="ql-video"></button>
            </div>
          </template>
        </QuillEditor>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>