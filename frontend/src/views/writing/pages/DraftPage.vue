<script setup>
import {useRouter} from "vue-router";
import {useStore} from "vuex";
import {onMounted, ref} from "vue";
import {deleteDraft, getUserDrafts} from "@/net/article.js";
import {formatTimestamp} from "@/net/utils.js";

const router = useRouter()

const store = useStore()

const articleList = ref([])

const fetchData = () => {
  getUserDrafts((data) => {
    articleList.value = data
    articleList.value.sort((a, b) => {
      const now = new Date();
      // 将日期字符串转换为日期对象
      const dateA = new Date(a.createdAt);
      const dateB = new Date(b.createdAt);

      // 计算与当前时间的差值
      const diffA = Math.abs(now - dateA);
      const diffB = Math.abs(now - dateB);

      // 按照差值进行排序，差值小的排在前面
      return diffA - diffB;
    })
  })
}

onMounted(() => {
  fetchData()
})

function deleteWriting(id) {
  if(confirm("确定删除该内容吗？")) {
    deleteDraft(id, () => fetchData()) // 删除成功后重新获取数据
  }
}
</script>

<template>
  <div>
    <div class="container mx-auto w-2/3">
      <div class="flex flex-col gap-8">
        <div v-for="(article, index) in articleList" :key="article.id" class="bg-white w-full min-h-72 p-8 flex flex-col justify-between gap-8">
          <div>
            <h3 class="mb-4 font-bold text-xl truncate">{{ article.title }}</h3>
            <p class="break-words">{{ article.summary }}</p>
          </div>
          <div class="flex flex-col lg:flex-row lg:items-center justify-between items-start gap-2">
            <div class="text-sm"><span>{{ formatTimestamp(article.createdAt) }}创建</span><span class="ml-4">于{{ formatTimestamp(article.updatedAt) }}有过修改</span></div>
            <div>
              <el-button @click="router.push('/article/draft/' + article.id)">预览</el-button>
              <el-button @click="router.push('/editor/update/draft/' + article.id)">编辑</el-button>
              <el-button @click="deleteWriting(article.id)">删除</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>