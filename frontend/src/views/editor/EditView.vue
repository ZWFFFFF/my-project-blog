<script setup>
import { useRouter, useRoute } from 'vue-router'
import {ArrowRight} from "@element-plus/icons-vue";
import {ref, computed} from "vue";
import {createArticle, updateArticle, updateDraft, submitToReview} from "@/net/article.js";
import {ElMessage} from "element-plus";
import {useStore} from "vuex";
import {throttle} from "@/net/utils.js";

const store = useStore()

const router = useRouter()

const route = useRoute()

const editorRef = ref(null)

const pathTitle = computed(() => {
  if(route.fullPath.startsWith('/editor/create')) return '新建文章'
  if(route.fullPath.startsWith('/editor/update/article')) return '编辑文章'
  if(route.fullPath.startsWith('/editor/update/draft')) return '编辑草稿'
})

const submit = () => {
  if(pathTitle.value === '新建文章') create('article')
  if(pathTitle.value === '编辑文章') update('article')
  if(pathTitle.value === '编辑草稿') update('article')
}

// 新建文章
function create(type) {
  const article = {...editorRef.value.article, authorId: store.state.userId}
  if(article.title === '' || article.summary === '' || article.content === '') {
    ElMessage.warning('请填写完整信息')
    return;
  }

  switch (type) {
    case 'draft':
      createArticle(article, () => router.push('/writing'));
      break;
    case 'article':
      submitToReview(article, () => router.push('/writing'));
      break;
    default:
      ElMessage.warning('发生了一些错误，请联系管理员')
  }
}

// 编辑文章
function update(type) {
  const article = editorRef.value.article
  if(article.title === '' || article.summary === '' || article.content === '') {
    ElMessage.warning('请填写完整信息')
    return;
  }

  switch (type) {
    case 'article':
      updateArticle(article, () => router.push('/writing'));
      break;
    case 'draft':
      updateDraft(article, () => router.push('/writing'));
      break;
    default:
      ElMessage.warning('发生了一些错误，请联系管理员')
  }
}

const handleUpdate = throttle(update, 1000)
const handleCreate = throttle(create, 1000)
const handleSubmit = throttle(submit, 1000)
</script>

<template>
  <div class="mx-auto min-h-screen w-full flex flex-col">
    <header class="w-full px-4 border-b">
      <div class="flex justify-between items-center py-4">
        <div class="flex items-center w-1/2 gap-8">
          <button class="font-extrabold text-2xl" @click="router.push('/')">Logo</button>
          <el-breadcrumb :separator-icon="ArrowRight">
            <el-breadcrumb-item :to="{ path: '/writing' }">投稿管理</el-breadcrumb-item>
            <el-breadcrumb-item>{{ pathTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="w-1/2 flex justify-end gap-4">
          <Button :style="'grey'" class="text-sm" v-if="pathTitle === '编辑草稿'" @click="handleUpdate('draft')"><span class="font-bold p-2">保存草稿</span></Button>
          <Button :style="'grey'" class="text-sm" v-else @click="handleCreate('draft')"><span class="font-bold p-2">存为草稿</span></Button>
          <Button class="text-sm" @click="handleSubmit"><span class="font-bold p-2">投稿审核</span></Button>
        </div>
      </div>
    </header>
    <div class="w-full mt-8 mb-20 flex-1 flex overflow-auto">
      <router-view v-slot="{ Component }">
        <component :is="Component" ref="editorRef"/>
      </router-view>
    </div>
  </div>
</template>

<style scoped>

</style>