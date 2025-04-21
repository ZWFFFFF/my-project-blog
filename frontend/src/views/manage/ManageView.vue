<script setup>
import {DocumentChecked, DocumentDelete, Remove, User, UserFilled, DataBoard} from "@element-plus/icons-vue";
import {logout} from "@/net/auth.js";
import {useStore} from "vuex";
import {useRoute, useRouter} from "vue-router";
import {computed} from "vue";
import DropdownMenu from "@/components/DropdownMenu.vue";
import {throttle} from "@/net/utils.js";

const store = useStore()
const router = useRouter()
const route = useRoute()
const userAvatar = computed(() => store.state.user.avatar)

const title = computed(() => {
  if (route.name.startsWith('manage-article-review')) return '文章审核'
  if (route.name.startsWith('manage-article-takeDown')) return '文章下架'
  if (route.name.startsWith('manage-user')) return '用户管理'
  if (route.name.startsWith('manage-hub')) return '用户'
  if (route.name.startsWith('manage-log')) return '日志'
})

const dropdownMenuOptions = [
  {
    label: '个人中心',
    icon: User,
    link: '/manage/hub'
  },
  {
    label: '文章审核',
    icon: DocumentChecked,
    link: '/manage/article/review'
  },
  {
    label: '文章下架',
    icon: DocumentDelete,
    link: '/manage/article/takeDown'
  },
  {
    label: '封禁用户',
    icon: Remove,
    link: '/manage/ban'
  },
  {
    label: '操作记录',
    icon: DataBoard,
    link: '/manage/log'
  }
]

function userLogout() {
  logout(() => {
    store.dispatch('logout')
    router.push('/welcome')
  })
}

const handleUserLogout = throttle(userLogout, 1000)
</script>

<template>
  <div class="h-screen bg-gray-100 flex">
    <!-- aside -->
    <aside class="w-56 text-gray-700 h-screen z-10 bg-[#fff] flex flex-col">
      <div class="p-4 mx-auto">
        <h2 class="text-xl font-bold">管理面板</h2>
      </div>
      <div class="flex flex-col gap-4 px-2">
        <div class="flex flex-col gap-2">
          <div class="px-5 text-gray-500 h-9 flex items-center">文章管理</div>
          <div
              :class="['h-10 rounded border hover:bg-gray-100 shadow-sm transition ease-in-out', route.name.startsWith('manage-article-review') ? 'ring-1 ring-gray-300 bg-gray-100' : 'ring-transparent']"
          >
            <router-link class="px-5 h-full flex items-center" to="/manage/article/review">
              <div class="mr-2 mt-1">
                <el-icon><DocumentChecked /></el-icon>
              </div>
              <div class="text-sm"><span>文章审核</span></div>
            </router-link>
          </div>
          <div
              :class="['h-10 rounded border hover:bg-gray-100 shadow-sm', route.name.startsWith('manage-article-takeDown') ? 'ring-1 ring-gray-300 bg-gray-100' : 'ring-transparent']"
          >
            <router-link class="px-5 h-full flex items-center" to="/manage/article/takeDown">
              <div class="mr-2 mt-1">
                <el-icon ><DocumentDelete /></el-icon>
              </div>
              <div class="text-sm"><span>文章下架</span></div>
            </router-link>
          </div>
        </div>
        <div>
          <div class="px-5 text-gray-500 h-9 flex items-center">用户管理</div>
          <div
              :class="['h-10 rounded border hover:bg-gray-100 shadow-sm', route.name.startsWith('manage-user') ? 'ring-1 ring-gray-300 bg-gray-100' : 'ring-transparent']"
          >
            <router-link class="px-5 h-full flex items-center" to="/manage/ban">
              <div class="mr-2 mt-1">
                <el-icon><Remove /></el-icon>
              </div>
              <div class="text-sm"><span>封禁用户</span></div>
            </router-link>
          </div>
        </div>
        <div>
          <div class="px-5 text-gray-500 h-9 flex items-center">用户</div>
          <div
              :class="['h-10 rounded border hover:bg-gray-100 shadow-sm', route.name.startsWith('manage-hub') ? 'ring-1 ring-gray-300 bg-gray-100' : 'ring-transparent']"
          >
            <router-link class="px-5 h-full flex items-center" to="/manage/hub">
              <div class="mr-2 mt-1">
                <el-icon><User /></el-icon>
              </div>
              <div class="text-sm"><span>个人中心</span></div>
            </router-link>
          </div>
        </div>
        <div>
          <div class="px-5 text-gray-500 h-9 flex items-center">日志</div>
          <div
              :class="['h-10 rounded border hover:bg-gray-100 shadow-sm', route.name.startsWith('manage-log') ? 'ring-1 ring-gray-300 bg-gray-100' : 'ring-transparent']"
          >
            <router-link class="px-5 h-full flex items-center" to="/manage/log">
              <div class="mr-2 mt-1">
                <el-icon><DataBoard /></el-icon>
              </div>
              <div class="text-sm"><span>操作记录</span></div>
            </router-link>
          </div>
        </div>
      </div>
    </aside>
    <div class="flex flex-col flex-1 overflow-auto">
      <div id="header" class="w-full h-16 border flex justify-between items-center px-5 bg-[#fff]">
        <div>
          <span>{{ title }}</span>
        </div>
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
      <div id="content" class="flex-1 flex flex-col overflow-hidden">
        <main class="p-4 flex-1 overflow-auto">
          <router-view v-slot="{ Component }">
            <transition name="el-fade-in-linear" mode="out-in">
              <component :is="Component"/>
            </transition>
          </router-view>
        </main>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>