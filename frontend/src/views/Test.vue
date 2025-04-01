<template>
  <el-upload
      class="avatar-uploader"
      :show-file-list="false"
      :before-upload="beforeAvatarUpload"
      :http-request="handleUpload"
  >
    <img v-if="imageUrl" :src="imageUrl" class="avatar"  alt=""/>
    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
  </el-upload>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { uploadAvatar } from '@/net/user.js'

const imageUrl = ref('')

const handleUpload = (options) => {
  const { file } = options
  const formData = new FormData()
  formData.append('file', file)

  uploadAvatar(formData, (data) => {
    console.log(data)
    imageUrl.value = data.url
  })
}

const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg') {
    ElMessage.error('Avatar picture must be JPG format!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!')
    return false
  }
  return true
}
onMounted(() => {
})
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>