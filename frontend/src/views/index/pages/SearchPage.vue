<script setup>
import {useRoute} from "vue-router";
import {computed, ref, onMounted, watch} from "vue";
import {searchArticleList} from "@/net/article.js";
import {UserFilled} from "@element-plus/icons-vue";
import {formatTimestamp} from "@/net/utils.js";

const route = useRoute()

const keyword = computed(() => route.query.keyword)

const articleList = ref([])

const currentPage = ref(1)

const pageSize = ref(6)

const total = ref(0)

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
};

// 获取文章列表
const fetchData = () => {
  if(keyword.value !== undefined && keyword.value !== '') {
    searchArticleList(keyword.value, (data) => {
      articleList.value = data
      // 按时间排序
      articleList.value.sort((a, b) => {
        const now = new Date();
        // 将日期字符串转换为日期对象
        const dateA = new Date(a.createdAt);
        const dateB = new Date(b.createdAt);

        // 计算与当前时间的差值
        const diffA = Math.abs(now - dateA);
        const diffB = Math.abs(now - dateB);

        // 按照差值进行排序，差值小的排在前面
        return diffA - diffB;
      })
    })
  }
}

onMounted(() => {
  fetchData()
})

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
<div>
  <div class="font-bold text-2xl"><span>文章</span></div>
  <div class="py-4 flex flex-wrap">
    <div v-for="(article, index) in currentPageArticles" :key="article.id" class="w-1/3">
      <div class="m-2 px-8 h-[300px] bg-white rounded shadow">
        <div class="h-full">
          <div class="h-1/4 flex items-center">
            <span class="cursor-pointer text-xl font-bold truncate">{{ article.title }}</span>
          </div>
          <div class="h-2/4">
            <p class="line-clamp-5 break-words">{{ article.summary }}</p>
          </div>
          <div class="h-1/4 flex items-center gap-4">
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
  <div class="flex justify-center items-center">
    <el-pagination
        layout="prev, pager, next"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :pager-count="11"
        :page-size="pageSize"
        :total="total"
    />
  </div>
</div>
</template>

<style scoped>

</style>