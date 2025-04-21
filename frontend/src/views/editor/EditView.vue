<script setup>
import { useRouter, useRoute } from 'vue-router'
import {ArrowRight, Plus} from "@element-plus/icons-vue";
import {ref, computed, watch} from "vue";
import {createArticle, updateArticle, updateDraft, submitToReview, uploadPreviewImage} from "@/net/article.js";
import {ElMessage} from "element-plus";
import {useStore} from "vuex";
import {throttle} from "@/net/utils.js";
import Button from "@/components/Button.vue";

const store = useStore()
const router = useRouter()
const route = useRoute()
const submitDialogVisible = ref(false)
const editorRef = ref(null)
const pathTitle = computed(() => {
  if(route.fullPath.startsWith('/editor/create')) return '新建文章'
  if(route.fullPath.startsWith('/editor/update/article')) return '编辑文章'
  if(route.fullPath.startsWith('/editor/update/draft')) return '编辑草稿'
})
const articlePreviewImage = ref('')
const selectedImageFile = ref(null)

const submit = async () => {
  if(pathTitle.value === '新建文章') await create('article')
  if(pathTitle.value === '编辑文章') await update('article')
  if(pathTitle.value === '编辑草稿') await update('article')
}

// 新建文章
async function create(type) {
  const article = {...editorRef.value.article, authorId: store.state.user.id}
  if(article.title === '' || article.summary === '' || article.content === '') {
    ElMessage.warning('请填写完整信息')
    return;
  }

  try {
    // 如果有选中的图片文件，先上传图片
    if(selectedImageFile.value) {
      const formData = new FormData()
      formData.append('file', selectedImageFile.value)
      const data = await uploadPreviewImage(formData)
      article.previewImage = data.url
    }

    // 如果是投稿审核但没有上传图片
    if(type === 'article' && !article.previewImage) {
      ElMessage.warning("请上传一张文章预览图")
      return
    }

    switch (type) {
      case 'draft':
        createArticle(article, () => router.push('/writing'));
        break;
      case 'article':
        submitToReview(article, () => router.push('/writing'));
        break;
      default:
        ElMessage.warning('发生了一些错误，请联系管理员')
    }
  } catch (error) {
    ElMessage.warning("文章预览图片上传失败")
  }
}

// 编辑文章
async function update(type) {
  const article = editorRef.value.article
  if(article.title === '' || article.summary === '' || article.content === '') {
    ElMessage.warning('请填写完整信息')
    return;
  }

  try {
    // 如果有选中的图片文件，先上传图片
    if(selectedImageFile.value) {
      const formData = new FormData()
      formData.append('file', selectedImageFile.value)
      const data = await uploadPreviewImage(formData)
      article.previewImage = data.url
    }

    // 编辑文章后投稿审核没有上传图片
    if(type === 'article' && !article.previewImage) {
      ElMessage.warning("请上传一张文章预览图")
      return
    }

    switch (type) {
      case 'article':
        updateArticle(article, () => router.push('/writing'));
        break;
      case 'draft':
        updateDraft(article, () => router.push('/writing'));
        break;
      default:
        ElMessage.warning('发生了一些错误，请联系管理员')
    }
  } catch (error) {
    ElMessage.warning("文章预览图片上传失败")
  }

}

const handleUpdate = throttle(update, 1000)
const handleCreate = throttle(create, 1000)
const handleSubmit = throttle(submit, 1000)

// 文章封面文件选择时的处理
const handleImageFileChange = (file) => {
  // 验证文件类型和大小
  if (file.raw.type !== 'image/jpeg') {
    ElMessage.warning('图片必须是 JPG 格式!')
    return false
  } else if (file.raw.size / 1024 / 1024 > 5) {
    ElMessage.warning('图片大小不能超过 5MB!')
    return false
  }

  // 创建预览图
  const reader = new FileReader()
  reader.onload = (e) => {
    articlePreviewImage.value = e.target.result
  }
  reader.readAsDataURL(file.raw)

  // 保存选中的文件
  selectedImageFile.value = file.raw
}

const openSubmitDialog = () => {
  if (editorRef.value?.article?.previewImage) {
    articlePreviewImage.value = editorRef.value.article.previewImage
  }
  submitDialogVisible.value = true
}

const handleOpenSubmitDialog = throttle(openSubmitDialog, 1000)

const closeSubmitDialog = () => {
  if(editorRef.value?.article?.previewImage) {
    articlePreviewImage.value = editorRef.value.previewImage // 恢复原来的图片
  }
  selectedImageFile.value = null
}
</script>

<template>
  <div class="mx-auto min-h-screen w-full flex flex-col">
    <header class="w-full px-4 border-b">
      <div class="flex justify-between items-center py-4">
        <div class="flex items-center w-1/2 gap-8 pl-4">
          <router-link to="/"><span class="font-extrabold font-serif text-2xl">Logo</span></router-link>
          <el-breadcrumb :separator-icon="ArrowRight">
            <el-breadcrumb-item :to="{ path: '/writing' }">投稿管理</el-breadcrumb-item>
            <el-breadcrumb-item>{{ pathTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div>
          <div><Button class="text-sm font-bold" @click="handleOpenSubmitDialog">下一步</Button></div>
        </div>
        <el-dialog
            v-model="submitDialogVisible"
            width="500"
            @close="closeSubmitDialog"
        >
          <div>
            <div class="text-center mb-12">
              <h1 class="font-bold text-xl">投稿</h1>
            </div>
            <div class="pb-12 mb-12 border-b grid grid-cols-1 gap-y-8">
              <div class="col-span-full">
                <div class="ml-4 mb-4">
                  <span class="font-bold text-black text-lg">封面上传图</span>
                </div>
                <div class="flex flex-col items-center">
                  <el-upload
                      class="w-[440px] h-[200px] border"
                      :show-file-list="false"
                      :auto-upload="false"
                      :on-change="handleImageFileChange"
                      accept="image/jpeg"
                  >
                    <img v-if="articlePreviewImage" :src="articlePreviewImage" class="w-full h-full block object-cover" alt=""/>
                    <div v-else class="flex items-center justify-center text-[#8c939d] text-xl">
                      <el-icon class=""><Plus /></el-icon>
                    </div>
                  </el-upload>
                  <div class="mt-4">
                    <p>推荐：图片必须是 JPG 格式，图片大小不能超过 5MB</p>
                  </div>
                </div>
              </div>
            </div>
            <div class="flex justify-end gap-4">
              <Button :style="'grey'" class="text-sm" v-if="pathTitle === '编辑草稿'" @click="handleUpdate('draft')"><span class="font-bold p-2">保存草稿</span></Button>
              <Button :style="'grey'" class="text-sm" v-else @click="handleCreate('draft')"><span class="font-bold p-2">存为草稿</span></Button>
              <Button class="text-sm" @click="handleSubmit"><span class="font-bold p-2">投稿审核</span></Button>
            </div>
          </div>
        </el-dialog>
      </div>
    </header>
    <div class="w-full mt-8 mb-20 flex-1 flex overflow-auto">
      <router-view v-slot="{ Component }">
        <component :is="Component" ref="editorRef"/>
      </router-view>
    </div>
  </div>
  <el-backtop :right="100" :bottom="100" />
</template>

<style>
.el-upload.el-upload--text {
  @apply w-full h-full flex items-center justify-center;
}
</style>