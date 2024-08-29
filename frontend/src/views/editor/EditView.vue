<script setup>
import { useRouter, useRoute } from 'vue-router'
import {ArrowRight} from "@element-plus/icons-vue";
import {ref, computed} from "vue";
import {createArticle, updateArticle} from "@/net/article.js";
import {ElMessage} from "element-plus";
import {useStore} from "vuex";

const store = useStore()

const router = useRouter()

const route = useRoute()

const editorRef = ref(null)

const pathTitle = computed(() => {
  if(route.fullPath.startsWith('/editor/create')) return '新建文章'
  if(route.fullPath.startsWith('/editor/update')) return '编辑文章'
})

const submit = () => {
  if(store.state.userId !== null) {
    if(pathTitle.value === '新建文章') create()
    if(pathTitle.value === '编辑文章') update()
  } else {
    ElMessage.warning('请先登录')
  }
}

// 新建文章
function create() {
  const article = {...editorRef.value.article, authorId: store.state.userId}
  if(article.title === '' || article.summary === '' || article.content === '') {
    ElMessage.warning('请填写完整信息')
  } else {
    createArticle(article, () => router.push('/writing/published'))
  }
}

// 编辑文章
function update() {
  const article = editorRef.value.article
  if(article.title === '' || article.summary === '' || article.content === '') {
    ElMessage.warning('请填写完整信息')
  } else {
    updateArticle(article, () => router.push('/writing/published'))
  }
}

</script>

<template>
  <div class="mx-auto min-h-screen w-full">
    <header class="w-full px-4">
      <div class="flex justify-between items-center py-4">
        <div class="flex items-center w-1/2 gap-8">
          <button class="font-extrabold text-2xl" @click="router.push('/')">Logo</button>
          <el-breadcrumb :separator-icon="ArrowRight">
            <el-breadcrumb-item :to="{ path: '/writing/published' }">投稿管理</el-breadcrumb-item>
            <el-breadcrumb-item>{{ pathTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="w-1/2 flex justify-end">
          <el-button @click="submit"><span class="font-bold p-2">投稿审核</span></el-button>
        </div>
      </div>
    </header>
    <div class="w-full mt-8">
      <router-view v-slot="{ Component }">
        <component :is="Component" ref="editorRef"/>
      </router-view>
    </div>
  </div>
</template>

<style scoped>

</style>