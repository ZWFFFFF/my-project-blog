<script setup>
import {ref, onMounted, computed, onUnmounted, watch} from "vue"
import {UserFilled} from "@element-plus/icons-vue";
import {getArticleList} from "@/net/article.js";
import {formatTimestamp} from "@/net/utils.js";
import router from "@/router/index.js";

const articleList = ref([])
const currentPage = ref(1)
const screenWidth = ref(window.innerWidth)
const total = ref(0) // 文章总数
// 根据屏幕宽度计算一页物品的数量
const pageSize = computed(() => {
  if(screenWidth.value < 768) return 2
  if(screenWidth.value < 1024) return 4
  if(screenWidth.value < 1280) return 6
  if(screenWidth.value < 1536) return 9
  return 11
})
const sortSelectValue = ref('hot')
const sortSelectOptions = [
  {
    value: 'hot',
    label: '热门',
  },
  {
    value: 'createdAtDesc',
    label: '最新',
  },
]

const sortArticles = () => {
  switch (sortSelectValue.value) {
    case 'createdAtDesc':
      articleList.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      break;
    case 'hot':
      articleList.value.sort((a, b) => b.like - a.like);
  }
};

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
};

const fetchData = () => {
  getArticleList((data) => {
    articleList.value = data
    total.value = data.length
    sortArticles()
  })
}

watch(sortSelectValue, () => {
  sortArticles();
});

onMounted(() => {
  fetchData() // 获取数据
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

// 计算当前页显示的数据
const currentPageArticles = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return articleList.value.slice(start, end);
})

</script>

<template>
  <div class="flex justify-center">
    <div class="w-[968px] border-r">
      <div>
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
                    <router-link :to="'/article/approved/' + article.id">
                      <div class="truncate mb-4">
                        <span class="cursor-pointer text-xl font-bold">{{ article.title }}</span>
                      </div>
                    </router-link>
                    <div class="border-t-2">
                      <p class="line-clamp-3 break-words">{{ article.summary }}</p>
                    </div>
                  </div>
                  <div class="h-1/4 flex items-center justify-between gap-4 border-t-2">
                    <div class="flex items-center gap-2">
                      <el-avatar :size="30" :icon="UserFilled" class="cursor-pointer"></el-avatar>
                      <div class="flex flex-col text-xs">
                        <span>{{ article.author }}</span>
                        <span>{{ formatTimestamp(article.createdAt) }}</span>
                      </div>
                    </div>
                    <div class="flex items-center gap-1">
                    <span class="text-zinc-400">
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="size-4">
                        <path d="M2.09 15a1 1 0 0 0 1-1V8a1 1 0 1 0-2 0v6a1 1 0 0 0 1 1ZM5.765 13H4.09V8c.663 0 1.218-.466 1.556-1.037a4.02 4.02 0 0 1 1.358-1.377c.478-.292.907-.706.989-1.26V4.32a9.03 9.03 0 0 0 0-2.642c-.028-.194.048-.394.224-.479A2 2 0 0 1 11.09 3c0 .812-.08 1.605-.235 2.371a.521.521 0 0 0 .502.629h1.733c1.104 0 2.01.898 1.901 1.997a19.831 19.831 0 0 1-1.081 4.788c-.27.747-.998 1.215-1.793 1.215H9.414c-.215 0-.428-.035-.632-.103l-2.384-.794A2.002 2.002 0 0 0 5.765 13Z" />
                      </svg>
                    </span>
                      <span class="text-sm text-zinc-400">{{ article.like }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="w-[368px]">
      asdfdsa
    </div>
  </div>
  <div class="flex justify-center items-center">
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
</template>

<style scoped>

</style>