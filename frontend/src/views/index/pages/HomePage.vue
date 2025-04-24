<script setup>
import {ref, onMounted, computed} from "vue"
import {UserFilled} from "@element-plus/icons-vue";
import {getArticleList} from "@/net/article.js";
import images from '@/assets/img';
import {formatTimestamp} from "@/net/utils.js";
import store from "@/store/index.js";

const articleList = ref([])
const displayList = ref([]) // 当前显示的文章列表
const recommendArticles = computed(() => store.state.recommendArticles)
const loadSize = 10 // 每次加载的文章数量
const currentPage = ref(1) // 当前页码
const noMore = ref(false) // 是否还有更多文章
const loading = ref(false) // 是否正在加载
const disabled = computed(() => loading.value || noMore.value)
const skeletonLoading = ref(true)
// 加载文章数据
const load = () => {
  if(loading.value || noMore.value) return
  loading.value = true
  // 模拟异步加载
  setTimeout(() => {
    const startIndex = (currentPage.value - 1) * loadSize
    const endIndex = startIndex + loadSize
    const newArticles = articleList.value.slice(startIndex, endIndex)

    displayList.value = [...displayList.value, ...newArticles]
    currentPage.value++
    loading.value = false
    if(displayList.value.length >= articleList.value.length) {
      noMore.value = true
    }

    // 首次加载完成后，将骨架屏隐藏
    if(skeletonLoading) {
      skeletonLoading.value = false
    }
  }, 1000)
}

const fetchData = () => {
  getArticleList((data) => {
    articleList.value = data
    articleList.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    // 加载数据
    load()
    // 将点赞数最高的四篇文章存入store中
    const topLikedArticles = [...articleList.value]
        .sort((a, b) => b.like - a.like)
        .slice(0, 4)
    store.dispatch('initializedRecommendArticles', topLikedArticles)
  })
}

onMounted(() => {
  fetchData()
})

</script>

<template>
  <div class="flex justify-center">
    <div class="w-[968px] border-r">
      <div class="w-full">
        <div
            v-infinite-scroll="load"
            :infinite-scroll-disabled="disabled"
            :infinite-scroll-immediate="false"
            class="mt-[50px] w-[728px] mb-12 mx-auto grid grid-cols-1 gap-y-8"
        >
          <el-skeleton
              :loading="skeletonLoading"
              animated
              :count="4"
          >
            <template #template>
              <div class="flex mb-14">
                <div class="w-[464px]">
                  <div class="flex items-center mb-4">
                    <el-skeleton-item variant="circle" style="width: 25px; height: 25px"/>
                    <el-skeleton-item variant="text" style="width: 100px; margin-left: 10px"/>
                  </div>
                  <div>
                    <el-skeleton-item variant="p" style="width: 100%"></el-skeleton-item>
                    <el-skeleton-item variant="p" style="width: 80%"></el-skeleton-item>
                  </div>
                </div>
                <div class="flex-1">
                  <div class="ml-14">
                    <el-skeleton-item variant="rect" style="height: 120px;"/>
                  </div>
                </div>
              </div>
            </template>
            <template #default>
              <div v-for="article in displayList" class="col-span-full mx-6 border-b">
                <div class="pb-6">
                  <router-link
                      :to="{ path: `/user/${article.authorId}/lists` }"
                      class="flex items-center gap-2 mb-4"
                  >
                    <el-avatar
                        :icon="UserFilled"
                        :src="article.authorAvatar || undefined"
                        :fit="'fill'"
                        :size="25"
                    />
                    <span class="text-sm">{{ article.author }}</span>
                  </router-link>
                  <router-link :to="'/article/approved/' + article.id" class="flex">
                    <div class="w-[464px]">
                      <div class="mb-2">
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
              <div v-if="noMore" class="col-span-full mx-6 text-center">
                <p class="text-xl font-bold text-zinc-400">没有内容了</p>
              </div>
            </template>
          </el-skeleton>
        </div>
      </div>
    </div>
    <div class="w-[368px]">
      <div class="ml-10">
        <div class="mt-12">
          <div>
            <p class="font-bold">推荐阅读</p>
          </div>
          <div class="mt-6 grid grid-cols-1 gap-y-8">
            <div v-for="article in recommendArticles" class="col-span-full">
              <router-link
                  :to="{ path: `/user/${article.authorId}/lists` }"
                  class="flex items-center gap-2 mb-2"
              >
                <el-avatar
                    :icon="UserFilled"
                    :src="article.authorAvatar || undefined"
                    :fit="'fill'"
                    :size="25"
                />
                <span class="text-sm">{{ article.author }}</span>
              </router-link>
              <div>
                <router-link :to="'/article/approved/' + article.id">
                  <div class="mb-2">
                    <p class="font-bold text-xl break-words line-clamp-2">{{ article.title }}</p>
                  </div>
                  <div><span class="text-sm text-zinc-400">{{ formatTimestamp(article.createdAt) }}</span></div>
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>