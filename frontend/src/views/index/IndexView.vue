<script setup>
import {Search, SwitchButton, User, UserFilled, Setting} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from 'vue'
import {useRouter} from "vue-router";
import {logout} from "@/net/auth.js";
import {useStore} from "vuex";
import DropdownMenu from "@/components/DropdownMenu.vue";
import {getUserInfo} from "@/net/user.js";

const store = useStore()

const router = useRouter()

const user = reactive({})

const keyword = ref('');

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

function searchArticle() {
  router.push({ path: '/search', query: { keyword: keyword.value } })
}

function userLogout() {
  logout(() => {
    store.dispatch('logout')
    router.push('/welcome')
  })
}

function fetchUserInfo(id) {
  getUserInfo(id, (data) => {
    Object.assign(user, data)
  })
}

onMounted(() => {
  fetchUserInfo(store.state.userId) // 信息更新后需要重新获取
})
</script>

<template>
  <div class="bg-gray-100 w-full min-h-screen flex flex-col">
    <header>
      <div class="bg-white flex justify-between p-4 gap-4 items-center">
        <div class="w-1/2 flex gap-8">
          <router-link to="/"><span class="font-extrabold text-2xl">Logo</span></router-link>
          <div>
            <el-input v-model="keyword" type="text" placeholder="搜索" @keyup.enter="searchArticle">
              <template #prefix>
                <el-icon><Search/></el-icon>
              </template>
            </el-input>
          </div>
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
    <div class="flex-1 flex">
      <div class="w-full flex-1 overflow-auto">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>