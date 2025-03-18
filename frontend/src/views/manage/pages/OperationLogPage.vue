<script setup>

import {getLogs} from "@/net/admin.js";
import {useRouter} from "vue-router";
import {ref, onMounted, computed, watch} from "vue";
import {convertToLocalTime} from "@/net/utils.js";

const router = useRouter()

const tableData = ref([])
const searchKeyword = ref(''); // 搜索关键字
const searchColumn = ref('operatorId'); // 默认搜索列
const sortSelectValue = ref('operationTimeDesc')
const sortSelectOptions = [
  {
    value: 'operationTimeAsc',
    label: '按最早操作时间',
  },
  {
    value: 'operationTimeDesc',
    label: '按最近操作时间',
  },
]

const fetchData = () => {
  getLogs((data) => {
    tableData.value = data.map(item => ({
      ...item,
      operationType: convertOperationType(item.operationType),
      operationDetail: item.operationDetail.replace(/[\[\]]/g, ''),
      formattedOperationTime: convertToLocalTime(item.operationTime),
      result: item.result === 'SUCCESS' ? '成功' : '失败',
    }))
    sortData()
  })
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

function convertOperationType(operationType) {
  switch(operationType) {
    case 'BAN_USER': return '封禁用户';
    case 'UNBAN_USER': return '解封用户';
    case 'TAKE_DOWN_ARTICLE': return '下架文章';
    case 'RECOVER_ARTICLE': return '恢复文章';
    case 'REVIEW_APPROVE_ARTICLE': return '文章审核通过';
    case 'REVIEW_REJECT_ARTICLE': return '文章审核不通过';
  }
}

const sortData = () => {
  switch (sortSelectValue.value) {
    case 'operationTimeAsc':
      tableData.value.sort((a, b) => new Date(a.operationTime) - new Date(b.operationTime));
      break;
    case 'operationTimeDesc':
      tableData.value.sort((a, b) => new Date(b.operationTime) - new Date(a.operationTime));
      break;
  }
}

watch(sortSelectValue, () => {
  sortData()
});

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="h-full">
    <div class="bg-white py-8 rounded-md">
      <div class="py-4 px-8">
        <span class="text-xl font-bold">操作记录</span>
      </div>
      <div class="px-4">
        <div class="mb-5 flex items-center gap-3">
          <div>
            <el-select v-model="searchColumn" placeholder="请选择搜索列" style="width: 150px; margin-right: 10px;">
              <el-option label="用户 ID" value="operatorId" />
              <el-option label="用户" value="operator" />
              <el-option label="操作" value="operationType" />
              <el-option label="操作时间" value="formattedOperationTime" />
            </el-select>
            <el-input
                v-model="searchKeyword"
                placeholder="请输入搜索关键字"
                clearable
                style="width: 300px;"
            />
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
        <el-table :data="filteredTableData" style="width: 100%" empty-text="No Data">
          <el-table-column prop="operatorId" label="用户id" width="100" />
          <el-table-column prop="operator" label="用户" width="200" />
          <el-table-column prop="operationType" label="操作" width="200" />
          <el-table-column prop="operationDetail" label="操作对象id" width="200" />
          <el-table-column prop="formattedOperationTime" label="操作时间" width="200" />
          <el-table-column prop="result" label="操作结果"/>
        </el-table>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>