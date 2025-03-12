<script setup>
const props = defineProps({
  style: {
    type: String,
    default: 'dark'
  }
})
</script>

<template>
  <div
      :class="['cursor-pointer py-2 px-4 rounded-md inline-block', style === 'grey' ? 'border border-gray-300 hover:bg-gray-100' : 'bg-zinc-950 hover:bg-zinc-800 text-white']"
  >
    <slot></slot>
  </div>
</template>

<style scoped>

</style>