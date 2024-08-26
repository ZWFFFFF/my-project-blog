<script setup>
import {ref, onMounted, computed} from "vue"
import {UserFilled} from "@element-plus/icons-vue";
import {useRouter} from "vue-router";
import {getArticleList} from "@/net/article.js";
import {formatTimestamp} from "@/net/utils.js";

const router = useRouter()

const articleList = ref([])

const currentPage = ref(1)

const pageSize = ref(6)

const total = ref(30)

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
};

onMounted(() => {
  getArticleList((data) => {
    articleList.value = data
    total.value = data.length
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
})

// 计算当前页显示的数据
const currentPageArticles = computed(() => {
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
        <el-card class="m-2">
          <template #header>
            <div>
              <span class="cursor-pointer">{{ article.title }}</span>
            </div>
          </template>
          <div>{{ article.summary }}</div>
          <template #footer>
            <div class="flex items-center gap-4">
              <el-avatar :size="30" :icon="UserFilled" class="cursor-pointer"></el-avatar>
              <div class="flex flex-col text-xs">
                <span>{{ article.author }}</span>
                <span>{{ formatTimestamp(article.createdAt) }}</span>
              </div>
            </div>
          </template>
        </el-card>
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