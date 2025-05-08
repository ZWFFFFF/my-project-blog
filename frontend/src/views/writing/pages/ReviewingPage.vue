<script setup>
import {useRouter} from "vue-router";
import {useStore} from "vuex";
import {onMounted, ref, watch, computed} from "vue";
import {getUserReviewArticles} from "@/net/article.js";
import {formatTimestamp} from "@/net/utils.js";
import {Search} from "@element-plus/icons-vue";
import images from "@/assets/img/index.js";

const router = useRouter()
const store = useStore()
const originalArticleList = ref([]) // 存储原始数据
const articleList = ref([])
const searchTitleKeyword = ref(''); // 标题搜索关键字
const selectValue = ref('createdAtDesc')
const selectOptions = [
  {
    value: 'createdAtAsc',
    label: '按最早创建时间',
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
const currentPage = ref(1) // 当前页码
const pageSize = ref(5)   // 每页显示数量
const initialLoading = ref(true) // 初始的加载动画

const fetchData = () => {
  if(store.state.user.id === null) {
    router.push('/welcome')
    return;
  }

  getUserReviewArticles((data) => {
    setTimeout(() => {
      originalArticleList.value = data
      sortArticles()
      initialLoading.value = false
    }, 1000)
  })
}

const sortArticles = () => {
  let sortedArticles = [...originalArticleList.value];

  switch (selectValue.value) {
    case 'createdAtAsc':
      sortedArticles.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
      break;
    case 'createdAtDesc':
      sortedArticles.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      break;
    case 'updatedAtAsc':
      sortedArticles.sort((a, b) => new Date(a.updatedAt) - new Date(b.updatedAt));
      break;
    case 'updatedAtDesc':
      sortedArticles.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
      break;
  }
  articleList.value = sortedArticles
  currentPage.value = 1 // 重置到第一页
};

// 根据搜索标题关键字过滤数据
const filteredData = computed(() => {
  if (!searchTitleKeyword.value) {
    return articleList.value; // 如果没有搜索关键字，返回全部数据
  }
  const keyword = searchTitleKeyword.value.toLowerCase();
  return articleList.value.filter((row) => {
    return String(row["title"]).toLowerCase().includes(keyword);
  });
});
// 计算分页后的数据
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredData.value.slice(start, end)
})
// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 监听 selectValue 的变化
watch(selectValue, () => {
  sortArticles();
});

watch(searchTitleKeyword, () => {
  currentPage.value = 1; // 搜索时强制回到第一页
});

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div>
    <div v-loading="initialLoading">
      <div class="mb-10 flex items-center gap-2 justify-start">
        <div>
          <el-input v-model="searchTitleKeyword" type="text" placeholder="请输入标题关键字">
            <template #prefix>
              <el-icon><Search/></el-icon>
            </template>
          </el-input>
        </div>
        <div>
          <el-select
              v-model="selectValue"
              clearable
              placeholder="排序方式"
              style="width: 240px"
          >
            <template #label="{ label }">
              <span class="text-gray-400">排序方式：</span>
              <span>{{ label }}</span>
            </template>
            <el-option
                v-for="item in selectOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </div>
      </div>
      <div class="flex flex-col gap-10">
        <div v-for="article in paginatedData"
             :key="article.id"
             class="w-full pb-8 flex flex-col justify-between border-b-2"
        >
          <div class="mb-4 text-right">
            <span class="font-bold text-gray-400">正在审核中</span>
          </div>
          <div class="flex mb-12">
            <div class="w-[464px] overflow-auto">
              <div>
                <p class="mb-2 font-bold text-xl break-words">{{ article.title }}</p>
                <p class="line-clamp-2 break-words">{{ article.summary }}</p>
              </div>
            </div>
            <div class="flex-1">
              <div class="ml-14">
                <div>
                  <img class="max-h-[120px] w-full object-cover" :src="article.previewImage ? article.previewImage : images.welcome_page" alt="">
                </div>
              </div>
            </div>
          </div>
          <div>
            <div class="flex flex-row items-center justify-between gap-2">
              <div class="text-sm text-zinc-400"><span>{{ formatTimestamp(article.createdAt) }}创建</span><span class="ml-4">于{{ formatTimestamp(article.updatedAt) }}有过修改</span></div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="!originalArticleList.length" class="text-center">
        <p class="text-xl font-bold text-zinc-400">还没有内容</p>
      </div>
      <!-- 分页组件 -->
      <div class="mt-4 flex justify-center">
        <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :pager-count="11"
            layout="prev, pager, next"
            :hide-on-single-page="true"
            :total="filteredData.length"
            @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>