<template>
  <div class="flex flex-1 items-center justify-center">
    <div class="p-4">
      <DropdownMenu
          :options="options"
          @option-selected="handleOptionSelected"
      >
        <span>select</span>
      </DropdownMenu>
    </div>
  </div>
</template>

<script setup>
import DropdownMenu from '@/components/DropdownMenu.vue';
import {User} from "@element-plus/icons-vue";
import router from "@/router/index.js";

const options = [
  {
    label: '个人中心',
    icon: User,
    onClick: () => { router.push('/') }
  }, {
    label: '退出',
    icon: User,
    onClick: () => {}
  }
]

// Handle selected option
const handleOptionSelected = (option) => {
  option.onClick();
};
</script>