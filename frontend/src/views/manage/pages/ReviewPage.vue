<script setup>
import {ref, onMounted} from 'vue';
import {convertToLocalTime, throttle} from "@/net/utils.js";
import {getPendingReviewList, getReviewingList} from "@/net/article.js";
import {DocumentChecked} from "@element-plus/icons-vue";

const tableData = ref([])

const fetchPendingReviewArticles = () => {
  getPendingReviewList((data) => {
    tableData.value = data.map(item => ({
      ...item,
      createdAt: convertToLocalTime(item.createdAt),
      updatedAt: convertToLocalTime(item.updatedAt)
    }))
    console.log(tableData.value)
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

const activeTab = ref('pending');

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

onMounted(() => {
  fetchPendingReviewArticles()
})
</script>

<template>
  <div class="h-full">
    <div class="bg-white">
      <div class="p-8">
        <span class="text-2xl">文章审核</span>
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
        <el-table :data="tableData" style="width: 100%">
          <el-table-column prop="id" label="文章id" width="200" />
          <el-table-column prop="authorId" label="用户id" width="200" />
          <el-table-column prop="createdAt" label="创建于" width="280" />
          <el-table-column prop="updatedAt" label="修改于" width="280" />
          <el-table-column label="操作">
            <span v-show="activeTab === 'pending'" class="text-base font-black cursor-pointer">审核</span>
            <span v-show="activeTab === 'reviewing'" class="text-base font-black cursor-pointer">取消</span>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>