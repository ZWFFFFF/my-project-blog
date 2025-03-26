<script setup>
import images from '@/assets/img';
</script>

<template>
  <div class="h-screen flex w-full">
    <div
        class="w-2/3 relative overflow-hidden bg-cover bg-center"
        :style="{ backgroundImage: `url(${images.welcome_page})` }"
    >
      <div class="text-white absolute bottom-20 left-20">
        <div class="mb-10">
          <p class="font-extrabold font-serif text-7xl">Human</p>
          <p class="font-extrabold font-serif text-7xl">stories & ideas</p>
        </div>
        <div>
          <p class="font-bold text-xl">A place to read, write, and deepen your understanding</p>
        </div>
      </div>
    </div>
    <div class="flex-1">
      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>