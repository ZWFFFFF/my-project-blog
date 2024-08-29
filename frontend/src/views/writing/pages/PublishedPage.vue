<script setup>
import { ref, onMounted } from "vue";
import { useStore } from "vuex";
import { useRouter  } from "vue-router";

const router = useRouter()

const store = useStore()

const articleList = ref([])

const fetchData = () => {
  if(store.state.userId !== null) {

  } else {
    router.push('/welcome')
  }
}

onMounted(() => {

})
</script>

<template>
  <div class="mt-20">
    <div class="container mx-auto w-2/3">
      <div class="mb-4"><span class="font-bold text-xl">投稿管理</span></div>
      <div class="flex flex-col gap-8">
        <div class="bg-white w-full min-h-72 p-8 flex flex-col justify-between gap-8">
          <div>
            <h3 class="mb-4 cursor-pointer font-bold text-xl">title</h3>
            <p class="break-words">summary</p>
          </div>
          <div class="flex flex-col lg:flex-row lg:items-center justify-between items-start gap-2">
            <div class="text-sm"><span>2024-08-27创建</span><span class="ml-4">于2024-08-28有过修改</span></div>
            <div>
              <el-button>预览</el-button>
              <el-button>编辑</el-button>
              <el-button>删除</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>