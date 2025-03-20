<script setup>
import {useRouter} from "vue-router";
import {useStore} from "vuex";
import {onMounted, ref, watch, computed} from "vue";
import {deleteDraft, getUserDrafts} from "@/net/article.js";
import {formatTimestamp} from "@/net/utils.js";
import {Search} from "@element-plus/icons-vue";

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
  getUserDrafts((data) => {
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

function deleteWriting(id) {
  if(confirm("确定删除该内容吗？")) {
    deleteDraft(id, () => {
      articleList.value = articleList.value.filter(article => article.id !== id)
    })
  }
}
</script>

<template>
  <div>
    <div class="container mx-auto w-2/3">
      <div class="mb-4 flex items-center gap-2 justify-end">
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
      <div class="flex flex-col gap-8">
        <div v-for="article in filteredData" :key="article.id" class="bg-white w-full min-h-72 p-8 flex flex-col justify-between gap-8 rounded-md shadow-md ring-1 ring-black ring-opacity-5">
          <div>
            <div class="flex justify-between gap-2">
              <h3 class="mb-4 font-bold text-xl truncate">{{ article.title }}</h3>
              <span class="text-gray-400">草稿</span>
            </div>
            <p class="break-words">{{ article.summary }}</p>
          </div>
          <div class="flex flex-col lg:flex-row lg:items-center justify-between items-start gap-2">
            <div class="text-sm"><span>{{ formatTimestamp(article.createdAt) }}创建</span><span class="ml-4">于{{ formatTimestamp(article.updatedAt) }}有过修改</span></div>
            <div class="flex gap-4">
              <Button class="text-sm" @click="router.push('/article/draft/' + article.id)">预览</Button>
              <Button class="text-sm" @click="router.push('/editor/update/draft/' + article.id)">编辑</Button>
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