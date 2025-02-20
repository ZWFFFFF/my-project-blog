<script setup>
import { ref, provide, onMounted } from 'vue'

const tabs = ref([])

const activeTab = ref('') // 当前激活的标签页名称

// 注册所有tab到tabs数组
provide('registerTab', (tab) => {
  tabs.value.push(tab)
})

provide('activeTab', activeTab)

onMounted(() => {
  if (tabs.value.length >= 1) {
    activeTab.value = tabs.value[0].name; // 默认激活第一个标签页
  }
})

const activateTab = (name) => {
  activeTab.value = name
}

</script>

<template>
  <div>
    <div>
      <button
          v-for="tab in tabs"
          :key="tab.name"
          @click="activateTab(tab.name)"
          :class="{ active: activeTab === tab.name }"
      >
        {{ tab.title }}
      </button>
    </div>
    <div>
      <slot></slot>
    </div>
  </div>
</template>

<style scoped>
.active {
  color: blue;
  font-weight: bold;
}
</style>