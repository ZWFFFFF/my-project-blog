<script setup>
import Tabs from '@/components/myTabs/Tabs.vue'
import Tab from '@/components/myTabs/Tab.vue'
</script>

<template>
  <div class="min-h-screen">
    <div class="container mx-auto w-2/3 mt-12">
      <div class="px-4 grid grid-cols-3 gap-4">
        <div class="bg-white col-span-2 rounded">
          <Tabs>
            <Tab name="tab1" title="标签1">第一个标签页的内容</Tab>
            <Tab name="tab2" title="标签2">第二个标签页的内容</Tab>
          </Tabs>
        </div>
        <div class="bg-white rounded">
          头像，作品管理，设置
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
