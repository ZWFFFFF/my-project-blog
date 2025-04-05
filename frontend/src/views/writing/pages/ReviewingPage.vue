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

const fetchData = () => {
  if(store.state.user.id === null) {
    router.push('/welcome')
    return;
  }

  getUserReviewArticles((data) => {
    articleList.value = data
    sortArticles()
  })
}

const sortArticles = () => {
  switch (selectValue.value) {
    case 'createdAtAsc':
      articleList.value.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
      break;
    case 'createdAtDesc':
      articleList.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      break;
    case 'updatedAtAsc':
      articleList.value.sort((a, b) => new Date(a.updatedAt) - new Date(b.updatedAt));
      break;
    case 'updatedAtDesc':
      articleList.value.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
      break;
  }
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

// 监听 selectValue 的变化
watch(selectValue, () => {
  sortArticles();
});

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div>
    <div class="">
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
        <div v-for="article in filteredData"
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
    </div>
  </div>
</template>

<style scoped>

</style>