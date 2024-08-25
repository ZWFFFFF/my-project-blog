<script setup>
import {useRouter} from "vue-router";

const router = useRouter()
</script>

<template>
  <div class="text-center w-full h-full px-12">
    <div class="pt-36">
      <div class="text-4xl font-bold py-4">注册新用户</div>
      <div class="text-gray-500 font-semibold">请在下方填写相关信息</div>
    </div>
    <div class="pt-12 px-12">
      <el-form>

      </el-form>
      <div class="mt-5">
        <el-button class="w-36" @click="">立即注册</el-button>
      </div>
      <div class="mt-5">
        <span class="text-sm">已有帐号？</span>
        <el-link @click="router.push('/welcome/')">立即登录</el-link>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>