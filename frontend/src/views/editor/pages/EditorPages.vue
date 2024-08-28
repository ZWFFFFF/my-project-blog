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

const summaryRef = ref()

const editorRef = ref()

const updateSummaryHeight = () => {
  summaryRef.value.style.height = summaryRef.value.scrollHeight + 'px'
}

defineExpose({
  article
})
</script>

<template>
  <div class="w-1/2 mx-auto">
    <div class="w-full">
      <div>
        <div>
          <input v-model="article.title" maxlength="40" placeholder="输入标题" class="mb-4 px-4 w-full outline-none font-bold text-3xl">
        </div>
        <div>
          <textarea ref="summaryRef" v-model="article.summary" @input="updateSummaryHeight" maxlength="500" placeholder="输入摘要" class="px-4 w-full h-6 outline-none resize-none"></textarea>
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