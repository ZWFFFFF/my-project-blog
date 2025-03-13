<script setup>
import {useRouter} from "vue-router";
import {useStore} from "vuex";
import {onMounted, ref, watch} from "vue";
import {getUserReviewArticles} from "@/net/article.js";
import {formatTimestamp} from "@/net/utils.js";

const router = useRouter()
const store = useStore()
const articleList = ref([])
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
  if(store.state.userId === null) {
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
    <div class="container mx-auto w-2/3">
      <div class="mb-4 flex items-center gap-2 justify-end">
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
      <div class="flex flex-col gap-8">
        <div v-for="article in articleList" :key="article.id" class="bg-white w-full min-h-72 p-8 flex flex-col justify-between gap-8 rounded-md shadow-md ring-1 ring-black ring-opacity-5">
          <div>
            <h3 class="mb-4 font-bold text-xl truncate">{{ article.title }}</h3>
            <p class="break-words">{{ article.summary }}</p>
          </div>
          <div class="flex flex-col lg:flex-row lg:items-center justify-between items-start gap-2">
            <div class="text-sm"><span>{{ formatTimestamp(article.createdAt) }}创建</span><span class="ml-4">于{{ formatTimestamp(article.updatedAt) }}有过修改</span></div>
            <div class="text-lg text-red-700"><span>正在审核</span></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>