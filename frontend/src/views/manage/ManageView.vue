<script setup>
import {DocumentChecked, DocumentDelete, Remove, User, UserFilled, SwitchButton} from "@element-plus/icons-vue";
import {logout} from "@/net/auth.js";
import {useStore} from "vuex";
import {useRouter} from "vue-router";
import {getUserInfo} from "@/net/user.js";
import {onMounted, reactive} from "vue";
import DropdownMenu from "@/components/DropdownMenu.vue";

const store = useStore()

const router = useRouter()

const user = reactive({})

const dropdownMenuOptions = [
  {
    label: '个人中心',
    icon: User,
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
  <div class="min-h-screen bg-gray-100 flex">
    <!-- aside -->
    <aside class="w-56  text-gray-700 h-screen z-10 bg-[#fff] flex flex-col">
      <div class="p-4 mx-auto">
        <h2 class="text-xl font-bold">管理面板</h2>
      </div>
      <div class="flex flex-col gap-2 px-2">
        <div>
          <div class="px-5 text-gray-500 h-9 flex items-center">用户管理</div>
          <div class="mt-1 h-10 rounded border hover:bg-gray-100">
            <router-link class="px-5 h-full flex items-center" to="">
              <div class="mr-2 mt-1">
                <el-icon><Remove /></el-icon>
              </div>
              <div class="text-sm"><span>封禁/解禁 用户</span></div>
            </router-link>
          </div>
        </div>
        <div>
          <div class="px-5 text-gray-500 h-9 flex items-center">文章管理</div>
          <div class="mt-1 h-10 rounded border hover:bg-gray-100">
            <router-link class="px-5 h-full flex items-center" to="">
              <div class="mr-2 mt-1">
                <el-icon><DocumentChecked /></el-icon>
              </div>
              <div class="text-sm"><span>文章审核</span></div>
            </router-link>
          </div>
          <div class="mt-1 h-10 rounded border hover:bg-gray-100">
            <router-link class="px-5 h-full flex items-center" to="">
              <div class="mr-2 mt-1">
                <el-icon ><DocumentDelete /></el-icon>
              </div>
              <div class="text-sm"><span>文章下架</span></div>
            </router-link>
          </div>
        </div>
        <div>
          <div class="px-5 text-gray-500 h-9 flex items-center">用户</div>
          <div class="mt-1 h-10 rounded border hover:bg-gray-100">
            <router-link class="px-5 h-full flex items-center" to="">
              <div class="mr-2 mt-1">
                <el-icon><User /></el-icon>
              </div>
              <div class="text-sm"><span>个人中心</span></div>
            </router-link>
          </div>
        </div>
      </div>
    </aside>
    <div class="flex flex-col flex-1">
      <div id="header" class="w-full h-16 border flex justify-end items-center px-5 bg-[#fff]">
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
      <div id="content" class="flex-1">
        <main class="h-full w-full p-4">
          <div class="bg-[#fff] h-full w-full rounded-lg">
            <router-view></router-view>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>