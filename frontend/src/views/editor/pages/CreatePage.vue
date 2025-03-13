<script setup>
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import '@vueup/vue-quill/dist/vue-quill.bubble.css';
import { QuillEditor } from '@vueup/vue-quill'
import { ref, reactive, defineExpose } from 'vue'

const article = reactive({
  title: '',
  summary: '',
  content: ''
})

const titleRef = ref()

const summaryRef = ref()

const editorRef = ref()

const updateTextareaHeight = (event) => {
  const target = event.target
  target.style.height = 'auto'
  target.style.height = target.scrollHeight + 'px'
}

const handleInput = (event, field) => {
  // 去除多余的回车符
  article[field] = event.target.value.replace(/(\r\n|\n|\r){2,}/g, '\n')
  updateTextareaHeight(event)
}

defineExpose({
  article
})
</script>

<template>
  <div class="w-1/2 mx-auto">
    <div class="w-full">
      <div class="mt-20">
        <div>
          <textarea ref="titleRef" v-model="article.title" @input="(event) => handleInput(event, 'title')" maxlength="80" placeholder="输入标题" class="px-4 w-full h-12 outline-none resize-none font-bold text-3xl"/>
        </div>
        <div>
          <textarea ref="summaryRef" v-model="article.summary" @input="(event) => handleInput(event, 'summary')" maxlength="500" placeholder="输入摘要" class="px-4 w-full h-6 outline-none resize-none"></textarea>
        </div>
      </div>
      <div class="mt-10">
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