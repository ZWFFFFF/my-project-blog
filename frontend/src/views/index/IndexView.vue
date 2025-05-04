<script setup>
import {Search, User, UserFilled, Setting, Edit, Tickets, Collection} from "@element-plus/icons-vue";
import {ref, computed, onMounted} from 'vue'
import {useRouter} from "vue-router";
import {logout} from "@/net/auth.js";
import {useStore} from "vuex";
import DropdownMenu from "@/components/DropdownMenu.vue";
import {throttle} from "@/net/utils.js";

const store = useStore()
const router = useRouter()
const keyword = ref('');
const userAvatar = computed(() => store.state.user.avatar)
const userId = computed(() => store.state.user.id)
const dropdownMenuOptions = [
  {
    label: '个人中心',
    icon: User,
    link: '/user/' + userId.value + '/lists'
  }, {
    label: '我的收藏',
    icon: Collection,
    link: '/user/' + userId.value + '/collects'
  }, {
    label: '作品管理',
    icon: Tickets,
    link: '/writing/draft'
  },{
    label: '设置',
    icon: Setting,
    link: '/me/settings'
  }
]

function searchArticle() {
  router.push({ path: '/search', query: { keyword: keyword.value } })
}

function userLogout() {
  logout(() => {
    store.dispatch('logout')
    router.push('/welcome')
  })
}

const handleUserLogout = throttle(userLogout, 1000)

</script>

<template>
  <div class="bg-[#FFFFFF] w-full min-h-screen flex flex-col">
    <header>
      <div class="bg-[#FFFFFF] flex justify-between py-2 px-4 gap-4 items-center border-b">
        <div class="w-1/2 flex gap-8 pl-4">
          <router-link to="/"><span class="font-extrabold font-serif text-2xl">Logo</span></router-link>
          <div>
            <el-input v-model="keyword" type="text" placeholder="搜索" @keyup.enter="searchArticle">
              <template #prefix>
                <el-icon><Search/></el-icon>
              </template>
            </el-input>
          </div>
        </div>
        <div class="flex w-1/2 justify-end items-center space-x-4">
          <button
              class="flex items-center gap-1 text-zinc-400 hover:text-zinc-500 transition-colors"
              @click="router.push('/editor')"
          >
            <el-icon :size="25"><Edit /></el-icon>
            <span>投稿</span>
          </button>
          <DropdownMenu
              :options="dropdownMenuOptions"
          >
            <template #header>
              <div class="p-2">
                <span class="font-bold">我的账号</span>
              </div>
            </template>
            <template #default>
              <el-avatar
                  :icon="UserFilled"
                  :src="userAvatar || undefined"
                  :fit="'fill'"
              />
            </template>
            <template #footer>
              <button
                  class="flex items-center px-2 py-4 text-sm text-zinc-500 hover:text-zinc-800 transition-colors"
                  @click="handleUserLogout"
              >
                <span class="pr-2">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="size-4">
                    <path fill-rule="evenodd" d="M2 4.75A2.75 2.75 0 0 1 4.75 2h3a2.75 2.75 0 0 1 2.75 2.75v.5a.75.75 0 0 1-1.5 0v-.5c0-.69-.56-1.25-1.25-1.25h-3c-.69 0-1.25.56-1.25 1.25v6.5c0 .69.56 1.25 1.25 1.25h3c.69 0 1.25-.56 1.25-1.25v-.5a.75.75 0 0 1 1.5 0v.5A2.75 2.75 0 0 1 7.75 14h-3A2.75 2.75 0 0 1 2 11.25v-6.5Zm9.47.47a.75.75 0 0 1 1.06 0l2.25 2.25a.75.75 0 0 1 0 1.06l-2.25 2.25a.75.75 0 1 1-1.06-1.06l.97-.97H5.25a.75.75 0 0 1 0-1.5h7.19l-.97-.97a.75.75 0 0 1 0-1.06Z" clip-rule="evenodd" />
                  </svg>
                </span>
                <span>退出登录</span>
              </button>
            </template>
          </DropdownMenu>
        </div>
      </div>
    </header>
    <div class="flex-1 flex">
      <div class="w-full flex-1">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>