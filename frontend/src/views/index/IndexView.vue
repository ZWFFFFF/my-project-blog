<script setup>
import {Search, UserFilled} from "@element-plus/icons-vue";
import {ref} from 'vue'
import {useRouter} from "vue-router";
import {logout} from "@/net/auth.js";
import {useStore} from "vuex";

const store = useStore()

const router = useRouter()

const keyword = ref('');

function searchArticle() {
  router.push({ path: '/search', query: { keyword: keyword.value } })
}

function userLogout() {
  logout(() => {
    store.dispatch('logout')
    router.push('/welcome')
  })
}
</script>

<template>
  <div class="bg-gray-100 w-full min-h-screen">
    <header>
      <div class="bg-white flex justify-between p-4 gap-4 items-center">
        <div class="w-1/2 flex gap-8">
          <button class="font-extrabold text-2xl" @click="router.push('/')">Logo</button>
          <div>
            <el-input v-model="keyword" type="text" placeholder="搜索" @keyup.enter="searchArticle">
              <template #prefix>
                <el-icon><Search/></el-icon>
              </template>
            </el-input>
          </div>
        </div>
        <div class="flex w-1/2 justify-end gap-8 items-center">
          <el-button @click="router.push('/editor')">投稿</el-button>
          <el-dropdown>
            <el-avatar :icon="UserFilled"></el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人主页</el-dropdown-item>
                <el-dropdown-item>设置</el-dropdown-item>
                <el-dropdown-item @click="userLogout">退出</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>
    <div>
      <div class="w-full">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>