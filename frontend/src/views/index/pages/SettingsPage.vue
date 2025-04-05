<script setup>

import {Plus, UserFilled} from "@element-plus/icons-vue";
import {ref} from 'vue'
import {ElMessage} from "element-plus";
import {uploadAvatar} from "@/net/user.js";
import Button from "@/components/Button.vue";
import {throttle} from "@/net/utils.js";
import store from "@/store/index.js";

const avatarDialogVisible = ref(false)
const avatarPreviewImage = ref('')
const selectedAvatarFile = ref(null)

// 头像文件选择时的处理
const handleAvatarFileChange = (file) => {
  // 验证文件类型和大小
  if (file.raw.type !== 'image/jpeg') {
    ElMessage.warning('头像图片必须是 JPG 格式!')
    return false
  } else if (file.raw.size / 1024 / 1024 > 5) {
    ElMessage.warning('头像图片大小不能超过 5MB!')
    return false
  }

  // 创建预览图
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreviewImage.value = e.target.result
  }
  reader.readAsDataURL(file.raw)

  // 保存选中的文件
  selectedAvatarFile.value = file.raw
}

// 上传头像
const uploadAvatarFun = () => {
  if (!selectedAvatarFile.value) {
    ElMessage.warning("请选择一张图片")
    return
  }

  const formData = new FormData()
  formData.append('file', selectedAvatarFile.value)

  uploadAvatar(formData, (data) => {
    store.dispatch('updateAvatar', data.url)
    avatarDialogVisible.value = false
  })
}

const handleUploadAvatar = throttle(uploadAvatarFun, 1000)

const cancelUploadAvatar = () => {
  avatarPreviewImage.value = ''
  selectedAvatarFile.value = null
  avatarDialogVisible.value = false
}

const handleCancelUploadAvatar = throttle(cancelUploadAvatar, 1000)
</script>

<template>
  <div class="mx-auto w-[668px]">
    <div class="mt-20 mb-12">
      <div class="mb-12">
        <h1 class="text-3xl font-bold">设置</h1>
      </div>
      <p class="text-zinc-600">请在这里进行个人资料编辑</p>
    </div>
    <form>
      <div class="space-y-12">
        <div class="border-b border-gray-900/10 pb-12">
          <div class="mt-10 grid grid-cols-1 gap-y-8">
            <div class="col-span-full flex items-center justify-between">
              <label class="block font-medium text-gray-900">头像</label>
              <div class="mt-2 flex items-center gap-x-3">
                <el-avatar
                    :icon="UserFilled"
                    :src="store.state.user.avatar || undefined"
                    :fit="'fill'"
                />
                <div><Button class="text-sm" :style="'grey'" @click="avatarDialogVisible = true">修改头像</Button></div>
                <el-dialog
                  v-model="avatarDialogVisible"
                  width="500"
                  @close="cancelUploadAvatar"
                >
                  <div>
                    <div class="text-center mb-12">
                      <h1 class="font-bold text-xl">头像信息</h1>
                    </div>
                    <div class="pb-12 mb-12 border-b">
                      <div class="flex gap-4">
                        <el-upload
                            class="w-20 h-20 border rounded-full"
                            :show-file-list="false"
                            :auto-upload="false"
                            :on-change="handleAvatarFileChange"
                            accept="image/jpeg"
                        >
                          <img v-if="avatarPreviewImage" :src="avatarPreviewImage" class="w-full h-full block rounded-full object-cover" alt=""/>
                          <div v-else class="flex-1 flex items-center justify-center text-[#8c939d] text-xl">
                            <el-icon class=""><Plus /></el-icon>
                          </div>
                        </el-upload>
                        <div class="flex-1 flex flex-col justify-center">
                          <p>推荐：</p>
                          <p>头像图片必须是 JPG 格式，头像图片大小不能超过 5MB</p>
                        </div>
                      </div>
                    </div>
                    <div class="flex justify-end space-x-4">
                      <Button :style="'grey'" class="text-sm" @click="handleCancelUploadAvatar">
                        取消
                      </Button>
                      <Button class="text-sm" @click="handleUploadAvatar">
                        确认上传
                      </Button>
                    </div>
                  </div>
                </el-dialog>
              </div>
            </div>

            <div class="col-span-full flex items-center justify-between">
              <label class="block font-medium text-gray-900">名称</label>
              <div>username</div>
            </div>

            <div class="col-span-full flex items-center justify-between">
              <label class="block font-medium text-gray-900">邮箱</label>
              <div>test@example.com</div>
            </div>

            <div class="col-span-full flex items-center justify-between">
              <label class="block font-medium text-gray-900">密码</label>
              <div><Button class="text-sm" :style="'grey'">修改密码</Button></div>
            </div>
          </div>
        </div>
        <div>
          <button @click="">
            <span class="text-red-700">删除账号</span>
          </button>
        </div>
      </div>
    </form>

  </div>
</template>

<style>
.el-upload.el-upload--text {
  @apply w-full h-full flex items-center justify-center;
}
</style>