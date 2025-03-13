<script setup>
import {useRoute} from "vue-router";
import {computed, ref, onMounted, watch, onUnmounted} from "vue";
import {searchArticleList} from "@/net/article.js";
import {UserFilled} from "@element-plus/icons-vue";
import {formatTimestamp} from "@/net/utils.js";
import router from "@/router/index.js";

const route = useRoute()
const keyword = computed(() => route.query.keyword)
const articleList = ref([])
const currentPage = ref(1)
const screenWidth = ref(window.innerWidth)
const total = ref(0)
// 根据屏幕宽度计算一页物品的数量
const pageSize = computed(() => {
  if(screenWidth.value < 768) return 4
  if(screenWidth.value < 1024) return 6
  if(screenWidth.value < 1280) return 9
  if(screenWidth.value < 1536) return 12
  return 15
})
const sortSelectValue = ref('createdAtDesc')
const sortSelectOptions = [
  {
    value: 'createdAtAsc',
    label: '按最早发布时间',
  },
  {
    value: 'createdAtDesc',
    label: '按最近发布时间',
  },
]

const sortArticles = () => {
  switch (sortSelectValue.value) {
    case 'createdAtAsc':
      articleList.value.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
      break;
    case 'createdAtDesc':
      articleList.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      break;
  }
};

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
};

// 获取文章列表
const fetchData = () => {
  if(keyword.value === "") {
    router.push('/home')
    return
  }

  if(keyword.value !== undefined) {
    searchArticleList(keyword.value, (data) => {
      articleList.value = data
      sortArticles()
    })
  }
}

watch(sortSelectValue, () => {
  sortArticles();
});

onMounted(() => {
  fetchData()
  // 屏幕宽度改变时，更新宽度值
  window.addEventListener("resize", updateScreenWidth)
})

onUnmounted(() => {
  window.removeEventListener("resize", updateScreenWidth)
})

// 更新屏幕宽度
const updateScreenWidth = () => {
  screenWidth.value = window.innerWidth
}

// 监听路由参数变化
watch(() => route.query.keyword, () => {
  fetchData()
})

// 计算当前页显示的数据
const currentPageArticles = computed(() => {
  if(keyword.value === '') return []
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return articleList.value.slice(start, end);
})
</script>

<template>
<div class="p-8">
  <div class="font-bold text-2xl"><span>文章</span></div>
  <div class="w-full">
    <div class="mr-2 flex items-center gap-2 justify-end">
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
    <div class="mb-4 flex flex-wrap">
      <div v-for="article in currentPageArticles" :key="article.id" class="w-full md:w-1/2 lg:w-1/3 xl:w-1/4 2xl:w-1/5">
        <div class="p-8 m-2 h-[300px] rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5">
          <div class="h-full">
            <div class="h-3/4">
              <div class="truncate mb-4" @click="router.push('/article/approved/' +article.id)">
                <span class="cursor-pointer text-xl font-bold">{{ article.title }}</span>
              </div>
              <div class="border-t-2">
                <p class="line-clamp-3 break-words">{{ article.summary }}</p>
              </div>
            </div>
            <div class="h-1/4 flex items-center gap-4 border-t-2">
              <el-avatar :size="30" :icon="UserFilled" class="cursor-pointer"></el-avatar>
              <div class="flex flex-col text-xs">
                <span>{{ article.author }}</span>
                <span>{{ formatTimestamp(article.createdAt) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="mb-10 flex justify-center items-center">
      <el-pagination
          layout="prev, pager, next"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :pager-count="11"
          :page-size="pageSize"
          :total="total"
          :hide-on-single-page="true"
      />
    </div>
  </div>
</div>
</template>

<style scoped>

</style>