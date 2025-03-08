<template>
  <div
      class="inline-block relative"
      @mouseenter="openDropdown"
      @mouseleave="closeDropdown"
  >
    <!-- Dropdown Button -->
    <button
        type="button"
        class="inline-flex justify-center w-full px-4 py-2 bg-inherit text-sm font-medium text-gray-700"
        aria-expanded="true"
        aria-haspopup="true"
    >
      <slot></slot>
    </button>

    <!-- Dropdown Menu -->
    <transition
        enter-active-class="transition ease-out duration-100"
        enter-from-class="transform opacity-0 scale-95"
        enter-to-class="transform opacity-100 scale-100"
        leave-active-class="transition ease-in duration-75"
        leave-from-class="transform opacity-100 scale-100"
        leave-to-class="transform opacity-0 scale-95"
    >
      <div
          v-if="isOpen"
          class="absolute left-1/2 -translate-x-1/2 mt-2 w-36 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 z-50"
      >
        <div class="p-1">
          <div
              v-for="option in options"
              :key="option.label"
              @click="selectOption(option)"
              class="text-gray-700 px-4 py-2 text-sm hover:bg-gray-100 rounded flex items-center cursor-pointer"
          >
            <span>
              <component :is="option.icon" class="w-4 h-4 mr-4" />
            </span>
            <span>{{ option.label }}</span>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// Props
const props = defineProps({
  options: {
    type: Array,
    required: true,
  },
});

// Emits
const emit = defineEmits(['option-selected']);

// State
const isOpen = ref(false);

// Open dropdown on hover
const openDropdown = () => {
  isOpen.value = true;
};

// Close dropdown when mouse leaves
const closeDropdown = () => {
  isOpen.value = false;
};

// Select an option
const selectOption = (option) => {
  emit('option-selected', option);
  closeDropdown(); // Close dropdown after selection
};
</script>

<style scoped>
</style>
