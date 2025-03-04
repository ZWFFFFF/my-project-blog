<script setup>
import { onMounted } from 'vue'

const test = () => {
  let a = 1;
  if(a === 1) {
    console.log('a is 1')
    return;
  }
  console.log('a is not 1')
}

onMounted(() => {
  test()
})
</script>

<template>
</template>

<style scoped>

</style>