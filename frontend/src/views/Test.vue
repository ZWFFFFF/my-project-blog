<script setup>
import Tabs from "@/components/myTabs/Tabs.vue"
import Tab from "@/components/myTabs/Tab.vue"
</script>

<template>
  <Tabs>
    <!-- 使用 Tab 组件，设置 name 和 title 属性 -->
    <Tab name="tab1" title="Tab 1">
      <!-- 标签页 1 的内容 -->
      <p>这是标签页 1 的内容。</p>
    </Tab>
    <Tab name="tab2" title="Tab 2">
      <!-- 标签页 2 的内容 -->
      <p>这是标签页 2 的内容。</p>
    </Tab>
    <Tab name="tab3" title="Tab 3">
      <!-- 标签页 3 的内容 -->
      <p>这是标签页 3 的内容。</p>
    </Tab>
  </Tabs>
</template>

<style scoped>

</style>