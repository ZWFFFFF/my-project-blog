<script setup>
import {useRouter} from "vue-router";
import {ref, onMounted, computed} from "vue";
import {getArticleList, getTakeDownList, recoverArticle, takeDownArticle} from "@/net/article.js";
import {convertToLocalTime, throttle} from "@/net/utils.js";
import Button from "@/components/Button.vue";

const router = useRouter()

const tableData = ref([])
const searchKeyword = ref(''); // 搜索关键字
const searchColumn = ref('id'); // 默认搜索列
const activeTab = ref('takeDown');
const selectedRows = ref([]); // 存储选中的行数据

const fetchApprovedArticles = () => {
  getArticleList((data) => {
    tableData.value = data.map(item => ({
      ...item,
      createdAt: convertToLocalTime(item.createdAt),
      updatedAt: convertToLocalTime(item.updatedAt)
    }))
  })
}

const fetchTakeDownArticles = () => {
  getTakeDownList((data) => {
    tableData.value = data.map(item => ({
      ...item,
      createdAt: convertToLocalTime(item.createdAt),
      updatedAt: convertToLocalTime(item.updatedAt)
    }))
  })
}

const toggleSwitch = () => {
  activeTab.value = activeTab.value === 'takeDown' ? 'recover' : 'takeDown';
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

// 处理多选
const handleSelectionChange = (selection) => {
  selectedRows.value = selection;
};

// 批量下架
const handleBatchTakeDown = () => {
  const ids = selectedRows.value.map(row => row.id);
  takeDown(ids)
};

// 批量恢复
const handleBatchRecover = () => {
  const ids = selectedRows.value.map(row => row.id);
  recover(ids)
};

function takeDown(ids) {
  if(confirm("确定要下架吗？")){
    takeDownArticle(ids, () => {
      fetchApprovedArticles()
    })
  }
}

function recover(ids) {
  if(confirm("确定要恢复吗？")) {
    recoverArticle(ids, () => {
      fetchTakeDownArticles()
    })
  }
}

onMounted(() => {
  fetchApprovedArticles()
})
</script>

<template>
  <div class="h-full">
    <div class="bg-white py-8 rounded-md">
      <div class="py-4 px-8">
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
      <div class="px-4">
        <!-- 搜索框和列选择器 -->
        <div class="mb-5 flex items-center justify-between">
          <div>
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
          <div>
            <Button class="text-sm" v-show="activeTab === 'takeDown'" @click="handleBatchTakeDown">批量下架</Button>
            <Button class="text-sm" v-show="activeTab === 'recover'" @click="handleBatchRecover">批量恢复</Button>
          </div>
        </div>
        <!-- 表格 -->
        <el-table
            :data="filteredTableData"
            style="width: 100%"
            @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="文章id" width="200" />
          <el-table-column prop="authorId" label="用户id" width="200" />
          <el-table-column prop="createdAt" label="创建于" width="280" />
          <el-table-column prop="updatedAt" label="修改于" width="280" />
          <el-table-column label="操作">
            <template #default="scope">
              <span v-show="activeTab === 'takeDown'" class="text-base font-black cursor-pointer" @click="takeDown([scope.row.id])">下架</span>
              <span v-show="activeTab === 'recover'" class="text-base font-black cursor-pointer" @click="recover([scope.row.id])">恢复</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>