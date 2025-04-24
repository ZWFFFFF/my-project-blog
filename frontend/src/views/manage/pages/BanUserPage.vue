<script setup>
import {ref, onMounted, computed, watch} from "vue";
import {useRouter} from "vue-router";
import {banUser, getUserList, unbanUser} from "@/net/user.js";
import {convertToLocalTime, throttle} from "@/net/utils.js";
import {ElMessage} from "element-plus";

const router = useRouter()
const tableData = ref([])
const searchKeyword = ref(''); // 搜索关键字
const searchColumn = ref('id'); // 默认搜索列
const sortSelectValue = ref('registerTimeDesc')
const sortSelectOptions = [
  {
    value: 'registerTimeAsc',
    label: '按最早操作时间',
  },
  {
    value: 'registerTimeDesc',
    label: '按最近操作时间',
  },
]
const currentPage = ref(1) // 当前页码
const pageSize = ref(10)   // 每页显示数量

const fetchData = () => {
  getUserList((data) => {
    tableData.value = data.map(item => ({
      ...item,
      formattedRegisterTime: convertToLocalTime(item.registerTime),
      ban: !item.active
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

function ban(userId) {
  banUser(userId, () => {
    ElMessage.success('操作成功')
    const user = tableData.value.find((user) => user.id === userId);
    if (user) {
      user.ban = true;
    }
  })
}

function unban(userId) {
  unbanUser(userId, () => {
    ElMessage.success('操作成功')
    const user = tableData.value.find((user) => user.id === userId);
    if (user) {
      user.ban = false;
    }
  })
}

const handleBanChange = throttle((userId, banStatus) => {
  if (banStatus) {
    ban(userId); // 如果 banStatus 为 true，执行封禁操作
  } else {
    unban(userId); // 如果 banStatus 为 false，执行解封操作
  }
}, 500)

const sortData = () => {
  switch (sortSelectValue.value) {
    case 'registerTimeAsc':
      tableData.value.sort((a, b) => new Date(a.registerTime) - new Date(b.registerTime));
      break;
    case 'registerTimeDesc':
      tableData.value.sort((a, b) => new Date(b.registerTime) - new Date(a.registerTime));
      break;
  }
}

watch(sortSelectValue, () => {
  currentPage.value = 1 // 排序时重置页码
  sortData()
});

watch(searchKeyword, () => {
  currentPage.value = 1 // 搜索时重置页码
})

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="h-full">
    <div class="bg-white py-4 rounded-md">
      <div class="px-8">
        <span class="text-xl font-bold">封禁用户</span>
      </div>
      <div class="mt-4 px-4">
        <div class="mb-5 flex items-center gap-3">
          <div>
            <el-select v-model="searchColumn" placeholder="请选择搜索列" style="width: 150px; margin-right: 10px;">
              <el-option label="用户 ID" value="id" />
              <el-option label="用户名" value="username" />
              <el-option label="电子邮箱" value="email" />
              <el-option label="注册时间" value="formattedRegisterTime" />
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
        <!-- 表格 -->
        <el-table
            :data="paginatedData"
            style="width: 100%"
        >
          <el-table-column prop="id" label="用户id" width="150" />
          <el-table-column prop="username" label="用户名" width="200" />
          <el-table-column prop="email" label="电子邮箱" width="200" />
          <el-table-column prop="formattedRegisterTime" label="注册时间" width="280" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-switch
                  v-model="scope.row.ban"
                  class="pr-2"
                  @change="handleBanChange(scope.row.id, scope.row.ban)"
                  style="--el-switch-on-color: #09090b;"
              />
              <span :class="[scope.row.ban ? 'text-zinc-950' : 'text-gray-500']">封禁</span>
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