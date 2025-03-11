<script setup>
import {useRoute, useRouter} from 'vue-router'
import {Setting, SwitchButton, User, UserFilled} from "@element-plus/icons-vue";
import {logout} from "@/net/auth.js";
import store from "@/store/index.js";
import DropdownMenu from "@/components/DropdownMenu.vue";
import {getUserInfo} from "@/net/user.js";
import {onMounted, reactive} from "vue";
import {throttle} from "@/net/utils.js";
const router = useRouter()
const route = useRoute()
const user = reactive({})

const dropdownMenuOptions = [
  {
    label: '个人中心',
    icon: User,
    onClick: () => {}
  }, {
    label: '设置',
    icon: Setting,
    onClick: () => {}
  }, {
    label: '退出',
    icon: SwitchButton,
    onClick: () => { userLogout() }
  }
]

const handleOptionSelected = (option) => {
  option.onClick();
};

function fetchUserInfo(id) {
  getUserInfo(id, (data) => {
    Object.assign(user, data)
  })
}

function userLogout() {
  logout(() => {
    store.dispatch('logout')
    router.push('/welcome')
  })
}

onMounted(() => {
  fetchUserInfo(store.state.userId) // 信息更新后需要重新获取
})

function linkTo(path) {
  router.push(path)
}

const throttledLinkTo = throttle(linkTo, 500)

</script>

<template>
  <div class="bg-gray-100 w-full min-h-screen">
    <header>
      <div class="bg-white flex justify-between p-4 gap-4 items-center">
        <div class="w-1/2">
          <button class="font-extrabold text-2xl" @click="router.push('/')">Logo</button>
        </div>
        <div class="flex w-1/2 justify-end items-center">
          <el-button @click="router.push('/editor')">投稿</el-button>
          <DropdownMenu
              :options="dropdownMenuOptions"
              @option-selected="handleOptionSelected"
          >
            <div class="flex items-center gap-4">
              <el-avatar :icon="UserFilled"></el-avatar>
              <span class="text-base font-bold">{{ user.username }}</span>
            </div>
          </DropdownMenu>
        </div>
      </div>
    </header>
    <div>
      <div class="w-full py-20">
        <div class="container mx-auto w-2/3 mb-4">
          <ul class="flex justify-start gap-x-10">
            <li :class="{ 'border-black border-b-2' : route.path === '/writing/draft'  }">
              <span class="font-bold text-xl cursor-pointer" @click="throttledLinkTo('/writing/draft')">草稿</span>
            </li>
            <li :class="{ 'border-black border-b-2' : route.path === '/writing/published' }">
              <span class="font-bold text-xl cursor-pointer" @click="throttledLinkTo('/writing/published')">投稿管理</span>
            </li>
            <li :class="{ 'border-black border-b-2' : route.path === '/writing/reviewing' }">
              <span class="font-bold text-xl cursor-pointer" @click="throttledLinkTo('/writing/reviewing')">审核</span>
            </li>
          </ul>
        </div>
        <router-view v-slot="{ Component }">
          <transition name="el-fade-in-linear" mode="out-in">
            <component :is="Component"/>
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>