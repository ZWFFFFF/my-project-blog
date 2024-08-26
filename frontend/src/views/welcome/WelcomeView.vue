<script setup>
import images from '@/assets/img';
</script>

<template>
  <div class="h-screen flex w-full">
    <div class="w-2/3 overflow-hidden">
      <el-image class="w-full h-full" :src="images.welcome_page" fit="cover"/>
      <div class="absolute left-16 bottom-16 text-gray-200 flex flex-col gap-2 text-wrap">
        <p class="font-bold text-4xl">Welcome!</p>
        <p class="font-semibold text-2xl">Lorem ipsum dolor sit amet.</p>
        <p class="font-semibold text-2xl">Sint optio dignissimos, adipisci minus.</p>
      </div>
    </div>
    <div class="w-1/3">
      <router-view v-slot="{ Component }"> <!-- Component是当前路由的组件， transition是vue提供的动画效果 -->
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>