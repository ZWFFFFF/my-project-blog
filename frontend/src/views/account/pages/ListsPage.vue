<script setup>
import {useRoute} from "vue-router";
import {defineProps, watch, ref, computed} from "vue";
import {getUserArticles} from "@/net/article.js";
import images from "@/assets/img/index.js";
import {formatTimestamp} from "@/net/utils.js";
import store from "@/store/index.js";

const route = useRoute()
const articleList = ref([])
const displayList = ref([]) // 当前显示的文章列表
const loadSize = 10 // 每次加载的文章数量
const currentPage = ref(1) // 当前页码
const noMore = ref(false) // 是否还有更多文章
const loading = ref(false) // 是否正在加载
const disabled = computed(() => loading.value || noMore.value)
const initialLoading = ref(true) // 初始的加载动画
const props = defineProps({
  account: {
    type: Object,
    required: true
  }
})

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
    if(initialLoading.value) {
      initialLoading.value = false // 将初始加载动画效果关闭
    }
    loading.value = false
    if(displayList.value.length >= articleList.value.length) {
      noMore.value = true
    }
  }, 1000)
}

const fetchData = () => {
  if(props.account.id) {
    getUserArticles(props.account.id, (data) => {
      articleList.value = data.filter(article => article.status !== 'take_down')
      articleList.value.sort((a, b) => b.like - a.like);
      // 加载显示数据
      load()
    })
  }
}

watch(
    () => props.account.id,
    (newId) => {
      if(newId) fetchData()
    },
    { immediate: true } // 立即执行一次
)
</script>

<template>
  <div class="flex justify-center">
    <div class="w-[968px] border-r">
      <div class="w-[680px] mx-auto py-20">
        <div class="mb-10 border-b">
          <div class="mb-10">
            <h1 class="text-3xl font-bold">{{ account.username }}</h1>
          </div>
          <div class="flex justify-start gap-x-10">
            <router-link
                class="pb-3 font-bold text-lg text-zinc-400 hover:text-zinc-800 transition-colors"
                :class="{ 'border-black border-b-2 text-zinc-800' : route.path === '/user/' + account.id + '/lists'  }"
                :to="{ path: `/user/${account.id}/lists` }"
            >
              <span>投稿</span>
            </router-link>
            <router-link
                class="pb-3 font-bold text-lg text-zinc-400 hover:text-zinc-800 transition-colors"
                :class="{ 'border-black border-b-2 text-zinc-800' : route.path === '/user/' + account.id + '/collects' }"
                :to="{ path: `/user/${account.id}/collects` }"
            >
              <span>收藏</span>
            </router-link>
          </div>
        </div>
        <div
            v-infinite-scroll="load"
            :infinite-scroll-disabled="disabled"
            :infinite-scroll-immediate="false"
            v-loading="initialLoading"
            class="mt-[50px] w-full grid grid-cols-1 gap-y-8"
        >
          <div v-for="article in displayList" class="col-span-full border-b">
            <div class="pb-6">
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
          <div v-if="articleList.length && noMore" class="col-span-full mx-6 text-center">
            <p class="text-xl font-bold text-zinc-400">没有内容了</p>
          </div>
          <div v-if="!articleList.length" class="text-center">
            <p class="text-xl font-bold text-zinc-400">还没有内容</p>
          </div>
        </div>
      </div>
    </div>
    <div class="w-[368px]">
      <div class="ml-10">
        <div class="mt-12">
          <el-avatar
              :src="account.avatar || undefined"
              :fit="'fill'"
              :size="88"
          >user</el-avatar>
        </div>
        <div class="mt-2">
          <p class="font-bold">{{ account.username }}</p>
        </div>
        <div v-if="account.id === store.state.user.id" class="mt-4 text-sm text-green-600">
          <router-link to="/me/settings">编辑个人信息</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>