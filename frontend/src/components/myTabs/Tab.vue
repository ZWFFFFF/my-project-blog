<script setup>
import { onMounted, defineProps, inject } from 'vue'

const props = defineProps({
  name: {
    type: String,
    required: true
  },
  title: {
    type: String,
    required: true
  }
})

const registerTab = inject('registerTab')

const activeTab = inject('activeTab')

onMounted(() => {
  // 注册当前tab到tabs数组中
  registerTab({ name: props.name, title: props.title  })
})
</script>

<template>
  <div v-show="activeTab === props.name">
    <slot></slot>
  </div>
</template>

<style scoped>

</style>