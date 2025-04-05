<template>
  <el-upload
      class="w-20 h-20 border rounded-full"
      :show-file-list="false"
      :auto-upload="false"
      :on-change="handleFileChange"
      accept="image/jpeg"
  >
    <img v-if="previewImage" :src="previewImage" class="w-20 h-20 block rounded-md object-cover" alt=""/>
    <div v-else class="flex-1 flex items-center justify-center text-[#8c939d] text-xl">
      <el-icon class=""><Plus /></el-icon>
    </div>
  </el-upload>

  <Button
      @click="handleUpload"
  >
    确认上传
  </Button>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { uploadAvatar } from '@/net/user.js'

const previewImage = ref('')
const selectedFile = ref(null)

// 文件选择时的处理
const handleFileChange = (file) => {
  // 验证文件类型和大小
  if (file.raw.type !== 'image/jpeg') {
    ElMessage.error('头像图片必须是 JPG 格式!')
    return false
  } else if (file.raw.size / 1024 / 1024 > 5) {
    ElMessage.error('头像图片大小不能超过 5MB!')
    return false
  }

  // 创建预览图
  const reader = new FileReader()
  reader.onload = (e) => {
    previewImage.value = e.target.result
  }
  reader.readAsDataURL(file.raw)

  // 保存选中的文件
  selectedFile.value = file.raw
}

const handleUpload = () => {
  if (!selectedFile.value) {
    ElMessage.warning("请选择一张图片")
    return
  }

  const formData = new FormData()
  formData.append('file', selectedFile.value)

  uploadAvatar(formData, (data) => {
    console.log(data)
    previewImage.value = data.url
  })
}
onMounted(() => {
})
</script>

<style>
.el-upload.el-upload--text {
  @apply w-full h-full flex items-center justify-center;
}
</style>