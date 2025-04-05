<script setup>
import {useRoute} from "vue-router";
import {computed, ref, onMounted, watch, onUnmounted} from "vue";
import {searchArticleList} from "@/net/article.js";
import {UserFilled} from "@element-plus/icons-vue";
import {formatTimestamp} from "@/net/utils.js";
import router from "@/router/index.js";
import images from "@/assets/img/index.js";

const route = useRoute()
const keyword = computed(() => route.query.keyword)
const articleList = ref([])

// 获取文章列表
const fetchData = () => {
  if(keyword.value === "") {
    router.push('/home')
    return
  }

  if(keyword.value !== undefined) {
    searchArticleList(keyword.value, (data) => {
      articleList.value = data
    })
  }
}

onMounted(() => {
  fetchData()
})

// 监听路由参数变化
watch(() => route.query.keyword, () => {
  fetchData()
})
</script>

<template>
  <div class="flex justify-center">
    <div class="w-[968px] border-r">
      <div class="w-full">
        <div class="mt-[50px] w-[728px] mx-auto grid grid-cols-1 gap-y-8">
          <div v-for="article in articleList" class="col-span-full mx-6 border-b">
            <div class="pb-6">
              <div class="flex items-center gap-2 mb-4">
                <el-avatar
                    :icon="UserFilled"
                    :src="undefined"
                    :fit="'fill'"
                    :size="30"
                />
                <span>{{ article.author }}</span>
              </div>
              <router-link :to="'/article/approved/' + article.id" class="flex">
                <div class="w-[464px]">
                  <div class="mb-4">
                    <p class="font-bold text-xl break-words">{{ article.title }}</p>
                  </div>
                  <div class="mb-6">
                    <p class="line-clamp-1 text-zinc-600 break-words">{{ article.summary }}</p>
                  </div>
                  <div class="flex gap-4 items-center">
                    <div><span class="text-sm text-zinc-400">{{ formatTimestamp(article.createdAt) }}</span></div>
                    <div class="flex items-center gap-1">
                      <span class="text-zinc-500">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="size-4">
                          <path d="M2.09 15a1 1 0 0 0 1-1V8a1 1 0 1 0-2 0v6a1 1 0 0 0 1 1ZM5.765 13H4.09V8c.663 0 1.218-.466 1.556-1.037a4.02 4.02 0 0 1 1.358-1.377c.478-.292.907-.706.989-1.26V4.32a9.03 9.03 0 0 0 0-2.642c-.028-.194.048-.394.224-.479A2 2 0 0 1 11.09 3c0 .812-.08 1.605-.235 2.371a.521.521 0 0 0 .502.629h1.733c1.104 0 2.01.898 1.901 1.997a19.831 19.831 0 0 1-1.081 4.788c-.27.747-.998 1.215-1.793 1.215H9.414c-.215 0-.428-.035-.632-.103l-2.384-.794A2.002 2.002 0 0 0 5.765 13Z" />
                        </svg>
                      </span>
                      <span class="text-sm text-zinc-400">{{ article.like }}</span>
                    </div>
                  </div>
                </div>
                <div class="flex-1">
                  <div class="ml-14">
                    <div>
                      <img class="max-h-[120px] w-full object-cover" :src="article.previewImage ? article.previewImage : images.welcome_page" alt="">
                    </div>
                  </div>
                </div>
              </router-link>
            </div>
          </div>
          <div v-if="articleList.length === 0" class="flex justify-center items-center">
            <p class="font-extrabold text-xl text-zinc-400">没有相关内容哦</p>
          </div>
        </div>
      </div>
    </div>
    <div class="w-[368px]">
      asdfdsa
    </div>
  </div>
</template>

<style scoped>

</style>