<script setup>
import {ref, onMounted, watch, computed} from "vue";
import { useStore } from "vuex";
import { useRouter  } from "vue-router";
import {deleteArticle, getUserArticles} from "@/net/article.js";
import {formatTimestamp} from "@/net/utils.js";
import {Search} from "@element-plus/icons-vue";
import images from "@/assets/img/index.js";

const router = useRouter()
const store = useStore()
const originalArticleList = ref([]) // 存储原始数据
const articleList = ref([])
const searchTitleKeyword = ref(''); // 标题搜索关键字
const sortSelectValue = ref('createdAtDesc')
const sortSelectOptions = [
  {
    value: 'like',
    label: '按点赞数',
  },
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
const currentPage = ref(1) // 当前页码
const pageSize = ref(5)   // 每页显示数量
const initialLoading = ref(true) // 初始的加载动画

const fetchData = () => {
  if(store.state.user.id !== null) {
    getUserArticles(store.state.user.id, (data) => {
      setTimeout(() => {
        originalArticleList.value = data; // 保存原始数据
        filterAndSortArticles(); // 初始过滤和排序
        initialLoading.value = false;
      }, 1000)
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
    case 'like':
      filteredArticles.sort((a, b) => b.like - a.like);
  }

  // 更新显示的文章列表
  articleList.value = filteredArticles;
  currentPage.value = 1 // 重置到第一页
};

// 根据搜索标题关键字过滤数据
const searchFilteredData = computed(() => {
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
  return searchFilteredData.value.slice(start, end)
})

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
}

watch(sortSelectValue, () => {
  filterAndSortArticles()
});

watch(typeSelectValue, () => {
  filterAndSortArticles()
})

watch(searchTitleKeyword, () => {
  currentPage.value = 1; // 搜索时强制回到第一页
});

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
        </div>
        <div>
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
      </div>
      <div class="flex flex-col gap-10">
        <div v-for="article in paginatedData"
             :key="article.id"
             class="w-full pb-8 flex flex-col justify-between border-b-2"
        >
          <div class="mb-4 text-right">
            <span class="font-bold text-gray-400">{{ article.status === 'take_down' ? '已下架投稿' : '已发布投稿' }}</span>
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
            <div class="flex flex-row items-end justify-between gap-2">
              <div>
                <div class="flex mb-2">
                  <span class="text-zinc-500 pr-1">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="size-5">
                      <path d="M1 8.25a1.25 1.25 0 1 1 2.5 0v7.5a1.25 1.25 0 1 1-2.5 0v-7.5ZM11 3V1.7c0-.268.14-.526.395-.607A2 2 0 0 1 14 3c0 .995-.182 1.948-.514 2.826-.204.54.166 1.174.744 1.174h2.52c1.243 0 2.261 1.01 2.146 2.247a23.864 23.864 0 0 1-1.341 5.974C17.153 16.323 16.072 17 14.9 17h-3.192a3 3 0 0 1-1.341-.317l-2.734-1.366A3 3 0 0 0 6.292 15H5V8h.963c.685 0 1.258-.483 1.612-1.068a4.011 4.011 0 0 1 2.166-1.73c.432-.143.853-.386 1.011-.814.16-.432.248-.9.248-1.388Z" />
                    </svg>
                  </span>
                  <span class="text-sm text-zinc-500">{{ article.like === null ? 0 : article.like }}</span>
                </div>
                <div class="text-sm text-zinc-400"><span>{{ formatTimestamp(article.createdAt) }}创建</span><span class="ml-4">于{{ formatTimestamp(article.updatedAt) }}有过修改</span></div>
              </div>
              <div class="flex gap-4">
                <Button v-if="article.status === 'approved'" class="text-sm font-bold" @click="router.push('/article/approved/' + article.id)">查看</Button>
                <Button v-if="article.status === 'approved'" class="text-sm font-bold" @click="router.push('/editor/update/article/' + article.id)">编辑</Button>
                <Button class="text-sm font-bold" @click="deleteWriting(article.id)">删除</Button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 分页组件 -->
      <div class="mt-4 flex justify-center">
        <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :pager-count="11"
            layout="prev, pager, next"
            :hide-on-single-page="true"
            :total="searchFilteredData.length"
            @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>