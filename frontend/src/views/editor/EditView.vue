<script setup>
import { useRouter } from 'vue-router'
import {ArrowRight} from "@element-plus/icons-vue";
import {ref} from "vue";
import {createArticle} from "@/net/article.js";
import {ElMessage} from "element-plus";
import {useStore} from "vuex";

const store = useStore()

const router = useRouter()

const editorRef = ref(null)

const submit = () => {
  if(store.state.userId !== null) {
    const article = {...editorRef.value.article, authorId: store.state.userId}
    if(article.title === '' || article.summary === '' || article.content === '') {
      ElMessage.warning('请填写完整信息')
    } else {
      createArticle(article, () => router.push('/writing'))
    }
  } else {
    ElMessage.warning('请先登录')
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
            <el-breadcrumb-item :to="{ path: '/writing' }">投稿管理</el-breadcrumb-item>
            <el-breadcrumb-item>新建文章</el-breadcrumb-item>
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