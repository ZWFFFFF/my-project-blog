<script setup>
import {ref, onMounted, computed} from 'vue';
import {convertToLocalTime, throttle} from "@/net/utils.js";
import {getPendingReviewList, getReviewingList, resetReviewing, startReview} from "@/net/article.js";
import {useRouter} from "vue-router";

const router = useRouter()

const tableData = ref([])
const searchKeyword = ref(''); // 搜索关键字
const searchColumn = ref('id'); // 默认搜索列
const activeTab = ref('pending');

const fetchPendingReviewArticles = () => {
  getPendingReviewList((data) => {
    tableData.value = data.map(item => ({
      ...item,
      createdAt: convertToLocalTime(item.createdAt),
      updatedAt: convertToLocalTime(item.updatedAt)
    }))
  })
}

const fetchReviewingArticles = () => {
  getReviewingList((data) => {
    tableData.value = data.map(item => ({
      ...item,
      createdAt: convertToLocalTime(item.createdAt),
      updatedAt: convertToLocalTime(item.updatedAt)
    }))
  })
}

const toggleSwitch = () => {
  activeTab.value = activeTab.value === 'pending' ? 'reviewing' : 'pending';
  handleAction();
};

// 节流后的切换函数，500ms 内只能触发一次
const throttledToggleSwitch = throttle(toggleSwitch, 500);

const handleAction = () => {
  if (activeTab.value === 'pending') {
    handlePending();
  } else {
    handleReviewing();
  }
};

const handlePending = () => {
  fetchPendingReviewArticles()
};

const handleReviewing = () => {
  fetchReviewingArticles()
};

function reviewArticle(id) {
  if(confirm('是否要审核该文章')) {
    startReview(id, () => router.push('review/' + id))
  }
}

function cancelReviewing(id) {
  if(confirm('是否要取消该文章的审核'))  {
    resetReviewing(id, () => {
      tableData.value = tableData.value.filter(item => item.id !== id)
    })
  }
}

// 根据搜索关键字和列过滤表格数据
const filteredTableData = computed(() => {
  if (!searchKeyword.value) {
    return tableData.value; // 如果没有搜索关键字，返回全部数据
  }
  const keyword = searchKeyword.value.toLowerCase();
  return tableData.value.filter((row) => {
    return String(row[searchColumn.value]).toLowerCase().includes(keyword);
  });
});

onMounted(() => {
  fetchPendingReviewArticles()
})
</script>

<template>
  <div class="h-full">
    <div class="bg-white py-8 rounded-md">
      <div class="py-4 px-8">
        <span class="text-xl font-bold">文章审核</span>
      </div>
      <div class="m-4">
        <div
            class="h-10 w-1/4 px-1 bg-gray-100 rounded-md grid grid-cols-2 text-center cursor-pointer items-center"
            @click="throttledToggleSwitch"
        >
          <div
              :class="['rounded-md transition-colors duration-200 py-1', activeTab === 'pending' ? 'bg-white text-black' : 'bg-transparent text-gray-500',]"
          >
            <span>待审核文章</span>
          </div>
          <div
              :class="['rounded-md transition-colors duration-200 py-1', activeTab === 'reviewing' ? 'bg-white text-black' : 'bg-transparent text-gray-500',]"
          >
            <span>审核中文章</span>
          </div>
        </div>
      </div>
      <div class="px-4">
        <!-- 搜索框和列选择器 -->
        <div class="mb-5">
          <el-select v-model="searchColumn" placeholder="请选择搜索列" style="width: 150px; margin-right: 10px;">
            <el-option label="文章 ID" value="id" />
            <el-option label="用户 ID" value="authorId" />
            <el-option label="创建时间" value="createdAt" />
            <el-option label="修改时间" value="updatedAt" />
          </el-select>
          <el-input
              v-model="searchKeyword"
              placeholder="请输入搜索关键字"
              clearable
              style="width: 300px;"
          />
        </div>
        <!-- 表格 -->
        <el-table :data="filteredTableData" style="width: 100%" empty-text="No Data">
          <el-table-column prop="id" label="文章id" width="200" />
          <el-table-column prop="authorId" label="用户id" width="200" />
          <el-table-column prop="createdAt" label="创建于" width="280" />
          <el-table-column prop="updatedAt" label="修改于" width="280" />
          <el-table-column label="操作">
            <template #default="scope">
              <span v-show="activeTab === 'pending'" class="text-base font-black cursor-pointer" @click="reviewArticle(scope.row.id)">审核</span>
              <span v-show="activeTab === 'reviewing'" class="text-base font-black cursor-pointer" @click="cancelReviewing(scope.row.id)">取消</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>