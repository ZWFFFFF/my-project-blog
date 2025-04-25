<script setup>
import {useRouter} from "vue-router";
import {ref, onMounted, computed, watch} from "vue";
import {getArticleList, getTakeDownList, recoverArticle, takeDownArticle} from "@/net/article.js";
import {convertToLocalTime, throttle} from "@/net/utils.js";
import Button from "@/components/Button.vue";

const router = useRouter()
const tableData = ref([])
const searchKeyword = ref(''); // 搜索关键字
const searchColumn = ref('id'); // 默认搜索列
const activeTab = ref('takeDown');
const selectedRows = ref([]); // 存储选中的行数据
const sortSelectValue = ref('createdAtDesc')
const sortSelectOptions = [
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
  }
]
const currentPage = ref(1) // 当前页码
const pageSize = ref(10)   // 每页显示数量
const initialLoading = ref(true) // 初始的加载动画

const fetchApprovedArticles = () => {
  getArticleList((data) => {
    setTimeout(() => {
      tableData.value = data.map(item => ({
        ...item,
        formattedCreatedAt: convertToLocalTime(item.createdAt),
        formattedUpdatedAt: convertToLocalTime(item.updatedAt)
      }))
      sortData()
      initialLoading.value = false
    }, 1000)
  })
}

const fetchTakeDownArticles = () => {
  getTakeDownList((data) => {
    setTimeout(() => {
      tableData.value = data.map(item => ({
        ...item,
        formattedCreatedAt: convertToLocalTime(item.createdAt),
        formattedUpdatedAt: convertToLocalTime(item.updatedAt)
      }))
      sortData()
      initialLoading.value = false
    }, 1000)
  })
}

const toggleSwitch = () => {
  activeTab.value = activeTab.value === 'takeDown' ? 'recover' : 'takeDown';
  currentPage.value = 1 // 切换标签页时重置页码
  selectedRows.value = [] // 切换标签页时清空选中的行数据
  initialLoading.value = true // 重置加载动画
  handleAction();
};

// 节流后的切换函数，500ms 内只能触发一次
const throttledToggleSwitch = throttle(toggleSwitch, 500);

const handleAction = () => {
  if (activeTab.value === 'takeDown') {
    handleTakingDown()
  } else {
    handleRecovering()
  }
};

const handleTakingDown = () => {
  fetchApprovedArticles()
};

const handleRecovering = () => {
  fetchTakeDownArticles()
};

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

// 计算分页后的数据
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredTableData.value.slice(start, end)
})

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 处理多选
const handleSelectionChange = (selection) => {
  selectedRows.value = selection;
};

// 批量下架
const handleBatchTakeDown = throttle(() => {
  const ids = selectedRows.value.map(row => row.id);
  takeDown(ids)
}, 500)

// 批量恢复
const handleBatchRecover = throttle(() => {
  const ids = selectedRows.value.map(row => row.id);
  recover(ids)
}, 500)

function takeDown(ids) {
  if(confirm("确定要下架吗？")){
    takeDownArticle(ids, () => {
      tableData.value = tableData.value.filter((item) => !ids.includes(item.id));
    })
  }
}

function recover(ids) {
  if(confirm("确定要恢复吗？")) {
    recoverArticle(ids, () => {
      tableData.value = tableData.value.filter((item) => !ids.includes(item.id));
    })
  }
}

const sortData = () => {
  switch (sortSelectValue.value) {
    case 'createdAtAsc':
      tableData.value.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
      break;
    case 'createdAtDesc':
      tableData.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      break;
    case 'updatedAtAsc':
      tableData.value.sort((a, b) => new Date(a.updatedAt) - new Date(b.updatedAt));
      break;
    case 'updatedAtDesc':
      tableData.value.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
      break;
  }
}

watch(sortSelectValue, () => {
  currentPage.value = 1 // 排序时重置页码
  selectedRows.value = [] // 排序时清空选中的行数据
  sortData()
});

watch(searchKeyword, () => {
  selectedRows.value = [] // 搜索时清空选中的行数据
  currentPage.value = 1 // 搜索时重置页码
})

onMounted(() => {
  fetchApprovedArticles()
})
</script>

<template>
  <div class="h-full">
    <div class="bg-white py-4 rounded-md">
      <div class="px-8">
        <span class="text-xl font-bold">文章下架</span>
      </div>
      <div class="m-4">
        <div
            class="h-10 w-1/4 px-1 bg-gray-100 rounded-md grid grid-cols-2 text-center cursor-pointer items-center"
            @click="throttledToggleSwitch"
        >
          <div
              :class="['rounded-md transition-colors duration-200 py-1', activeTab === 'takeDown' ? 'bg-white text-black' : 'bg-transparent text-gray-500',]"
          >
            <span>已发布的文章</span>
          </div>
          <div
              :class="['rounded-md transition-colors duration-200 py-1', activeTab === 'recover' ? 'bg-white text-black' : 'bg-transparent text-gray-500',]"
          >
            <span>已下架的文章</span>
          </div>
        </div>
      </div>
      <div class="px-4" v-loading="initialLoading">
        <!-- 搜索框和列选择器 -->
        <div class="mb-5 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div>
              <el-select v-model="searchColumn" placeholder="请选择搜索列" style="width: 150px; margin-right: 10px;">
                <el-option label="文章 ID" value="id" />
                <el-option label="用户 ID" value="authorId" />
                <el-option label="创建时间" value="formattedCreatedAt" />
                <el-option label="修改时间" value="formattedUpdatedAt" />
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
          <div>
            <Button class="text-sm font-bold" v-show="activeTab === 'takeDown'" @click="handleBatchTakeDown">批量下架</Button>
            <Button class="text-sm font-bold" v-show="activeTab === 'recover'" @click="handleBatchRecover">批量恢复</Button>
          </div>
        </div>
        <!-- 表格 -->
        <el-table
            :data="paginatedData"
            style="width: 100%"
            @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="文章id" width="200" />
          <el-table-column prop="authorId" label="用户id" width="200" />
          <el-table-column prop="formattedCreatedAt" label="创建于" width="280" />
          <el-table-column prop="formattedUpdatedAt" label="修改于" width="280" />
          <el-table-column label="操作">
            <template #default="scope">
              <span v-show="activeTab === 'takeDown'" class="text-base font-black cursor-pointer" @click="takeDown([scope.row.id])">下架</span>
              <span v-show="activeTab === 'recover'" class="text-base font-black cursor-pointer" @click="recover([scope.row.id])">恢复</span>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <div class="mt-4 flex justify-center">
          <el-pagination
              :current-page="currentPage"
              :page-size="pageSize"
              :pager-count="11"
              layout="prev, pager, next"
              :hide-on-single-page="true"
              :total="filteredTableData.length"
              @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>