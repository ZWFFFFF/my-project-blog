<script setup>

import {Search, UserFilled} from "@element-plus/icons-vue";
</script>

<template>
  <el-container class="bg-gray-100 w-full h-screen">
    <el-header>
      <div class="flex justify-between p-4 gap-4 items-center">
        <div class="w-1/2 flex gap-8">
          <button class="font-extrabold text-2xl">Logo</button>
          <div>
            <el-input type="text">
              <template #prefix>
                <el-icon><Search/></el-icon>
              </template>
            </el-input>
          </div>
        </div>
        <div class="flex w-1/2 justify-end gap-8 items-center">
          <el-button>投稿</el-button>
          <el-dropdown>
            <el-avatar :icon="UserFilled"></el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人主页</el-dropdown-item>
                <el-dropdown-item>设置</el-dropdown-item>
                <el-dropdown-item>退出</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <el-main>
      <div class="w-full p-8">
        <router-view></router-view>
      </div>
    </el-main>
  </el-container>
</template>

<style scoped>
</style>