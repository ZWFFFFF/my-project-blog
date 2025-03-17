<script setup>
import {ref, onMounted, watch} from "vue";
import { useStore } from "vuex";
import { useRouter  } from "vue-router";
import {deleteArticle, getUserArticles} from "@/net/article.js";
import {formatTimestamp} from "@/net/utils.js";

const router = useRouter()
const store = useStore()
const originalArticleList = ref([]) // 存储原始数据
const articleList = ref([])
const sortSelectValue = ref('createdAtDesc')
const sortSelectOptions = [
  {
    value: 'createdAtAsc',
    label: '按最创建早时间',
  },
  {
    value: 'createdAtDesc',
    label: '按最近创建时间',
  },
  {
    value: 'updatedAtAsc',
    label: '按最早修改时间',
  },
  {
    value: 'updatedAtDesc',
    label: '按最近修改时间',
  },
]
const  typeSelectValue = ref('allTypes')
const typeSelectOptions = [
  {
    value: 'allTypes',
    label: '全部',
  },
  {
    value: 'approved',
    label: '已发布投稿',
  },
  {
    value: 'take_down',
    label: '已下架投稿',
  }
]

const fetchData = () => {
  if(store.state.userId !== null) {
    getUserArticles(store.state.userId, (data) => {
      originalArticleList.value = data; // 保存原始数据
      filterAndSortArticles(); // 初始过滤和排序
    })
  } else {
    router.push('/welcome')
  }
}

// 过滤和排序文章
const filterAndSortArticles = () => {
  let filteredArticles = [...originalArticleList.value]; // 基于原始数据过滤

  // 根据文章类型过滤
  if (typeSelectValue.value !== 'allTypes') {
    filteredArticles = filteredArticles.filter(article => article.status === typeSelectValue.value);
  }

  // 根据排序方式排序
  switch (sortSelectValue.value) {
    case 'createdAtAsc':
      filteredArticles.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
      break;
    case 'createdAtDesc':
      filteredArticles.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      break;
    case 'updatedAtAsc':
      filteredArticles.sort((a, b) => new Date(a.updatedAt) - new Date(b.updatedAt));
      break;
    case 'updatedAtDesc':
      filteredArticles.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
      break;
  }

  // 更新显示的文章列表
  articleList.value = filteredArticles;
};

watch(sortSelectValue, () => {
  filterAndSortArticles()
});

watch(typeSelectValue, () => {
  filterAndSortArticles()
})

onMounted(() => {
  fetchData()
})

function deleteWriting(id) {
  if(confirm("确定删除该内容吗？")) {
    deleteArticle(id, () => {
      articleList.value = articleList.value.filter(article => article.id !== id)
    })
  }
}
</script>

<template>
  <div>
    <div class="container mx-auto w-2/3">
      <div class="mb-4 flex items-center gap-2 justify-end">
        <el-select
            v-model="typeSelectValue"
            placeholder="投稿类型"
            style="width: 240px"
        >
          <template #label="{ label }">
            <span class="text-gray-400">文章类型：</span>
            <span>{{ label }}</span>
          </template>
          <el-option
              v-for="item in typeSelectOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
        <el-select
            v-model="sortSelectValue"
            clearable
            placeholder="排序方式"
            style="width: 240px"
        >
          <template #label="{ label }">
            <span class="text-gray-400">排序方式：</span>
            <span>{{ label }}</span>
          </template>
          <el-option
              v-for="item in sortSelectOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </div>
      <div class="flex flex-col gap-8">
        <div v-for="article in articleList" :key="article.id" class="bg-white w-full min-h-72 p-8 flex flex-col justify-between gap-8 rounded-md shadow-md ring-1 ring-black ring-opacity-5">
          <div>
            <div class="flex justify-between gap-2">
              <h3 class="mb-4 font-bold text-xl truncate">{{ article.title }}</h3>
              <span class="text-gray-400">{{ article.status === 'take_down' ? '已下架投稿' : '已发布投稿' }}</span>
            </div>
            <p class="break-words">{{ article.summary }}</p>
          </div>
          <div class="flex flex-col lg:flex-row lg:items-center justify-between items-start gap-2">
            <div class="text-sm"><span>{{ formatTimestamp(article.createdAt) }}创建</span><span class="ml-4">于{{ formatTimestamp(article.updatedAt) }}有过修改</span></div>
            <div class="flex gap-4">
              <Button v-if="article.status === 'approved'" class="text-sm" @click="router.push('/article/approved/' + article.id)">预览</Button>
              <Button v-if="article.status === 'approved'" class="text-sm" @click="router.push('/editor/update/article/' + article.id)">编辑</Button>
              <Button class="text-sm" @click="deleteWriting(article.id)">删除</Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>