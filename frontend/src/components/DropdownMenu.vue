<template>
  <div
      ref="dropdownRef"
      class="inline-block relative"
      @click="toggleDropdown"
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
          class="min-w-[220px] absolute right-0 mt-2 w-36 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 z-50"
      >
        <div class="p-1">
          <header class="border-b">
            <slot name="header"></slot>
          </header>
          <main class="py-2 border-b">
            <router-link
                v-for="option in options"
                :key="option.label"
                class="text-gray-700 p-2 text-sm hover:bg-gray-100 rounded flex items-center"
                :to="option.link"
            >
              <span>
                <component :is="option.icon" class="w-4 h-4 mr-2" />
              </span>
              <span>{{ option.label }}</span>
            </router-link>
          </main>
          <footer>
            <slot name="footer"></slot>
          </footer>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const dropdownRef = ref(null);

// Props
const props = defineProps({
  options: {
    type: Array,
    required: true,
  },
});

// State
const isOpen = ref(false);

const toggleDropdown = () => {
  isOpen.value = !isOpen.value;
};

const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    isOpen.value = false
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
</style>
