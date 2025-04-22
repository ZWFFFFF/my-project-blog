<script setup>

import {Plus, UserFilled} from "@element-plus/icons-vue";
import {ref, reactive, computed} from 'vue'
import {ElMessage} from "element-plus";
import {changeEmail, changePassword, changeUsername, deleteAccount, uploadAvatar} from "@/net/user.js";
import Button from "@/components/Button.vue";
import {throttle} from "@/net/utils.js";
import {useStore} from "vuex";
import {useRouter} from "vue-router";

const router = useRouter()
const store = useStore()
const id = store.state.user.id
const userAvatar = computed(() => store.state.user.avatar)
const username = computed(() => store.state.user.username)
const email = computed(() => store.state.user.email)
const avatarDialogVisible = ref(false)
const avatarPreviewImage = ref('')
const selectedAvatarFile = ref(null)
const usernameDialogVisible = ref(false)
const emailDialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const deleteAccountDialogVisible = ref(false)
const usernameFormRef = ref()
const usernameForm = reactive({
  username: username.value
})
const emailFormRef = ref()
const emailForm = reactive({
  email: email.value
})
const passwordFormRef = ref()
const passwordForm = reactive({
  old_password: '',
  new_password: '',
  confirm_new_password: ''
})
const deleteAccountInput = ref('')

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

// 更新用户名
const updateUsername = () => {
  usernameFormRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.warning('请输入正确的信息');
      return;
    }

    // 检查用户名是否符合规则（仅字母、数字、中文）
    const usernameRegex = /^[a-zA-Z0-9\u4e00-\u9fa5]+$/;
    if (!usernameRegex.test(usernameForm.username)) {
      ElMessage.warning('用户名只能包含字母、数字或中文');
      return;
    }

    // 检查是否真的修改了用户名
    if (usernameForm.username === username.value) {
      ElMessage.warning('新用户名不能与原用户名相同');
      return;
    }

    // 验证通过，执行修改逻辑
    changeUsername(id, usernameForm.username, () => {
      store.dispatch('updateUsername', usernameForm.username)
      ElMessage.success('修改成功');
      usernameDialogVisible.value = false;
    })
  })
}

const handleUpdateUsername = throttle(updateUsername, 1000)

const cancelUpdateUsername = () => {
  usernameDialogVisible.value = false
  usernameForm.username = username.value
}

const handleCancelUpdateUsername = throttle(cancelUpdateUsername, 1000)

// 更新邮箱
const updateEmail = () => {
  emailFormRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.warning('请输入正确的信息');
      return;
    }
    // 检查是否真的修改了邮箱
    if (emailForm.email === email.value) {
      ElMessage.warning('新邮箱不能与原邮箱相同');
      return;
    }
    // 验证通过，执行修改逻辑
    changeEmail(id, emailForm.email, () => {
      store.dispatch('updateEmail', emailForm.email)
      ElMessage.success('修改成功');
      emailDialogVisible.value = false;
    })
  })
}

const handleUpdateEmail = throttle(updateEmail, 1000)

const cancelUpdateEmail = () => {
  emailDialogVisible.value = false
  emailForm.email = email.value
}

const handleCancelUpdateEmail = throttle(cancelUpdateEmail, 1000)

// 修改密码
const changePwd = () => {
  passwordFormRef.value.validate((valid) => {
    if(valid) {
      changePassword(id, passwordForm.old_password, passwordForm.new_password, () => {
        ElMessage.success('修改密码成功')
        passwordDialogVisible.value = false
      })
    } else {
      ElMessage.warning('请输入正确的信息')
    }
  })
}

const handlePasswordChange = throttle(changePwd, 1000)

const cancelUpdatePassword = () => {
  passwordForm.old_password = ''
  passwordForm.new_password = ''
  passwordForm.confirm_new_password = ''
  passwordDialogVisible.value = false
}

const handleCancelUpdatePassword = throttle(cancelUpdatePassword, 1000)

// 注销账号
const delAccount = () => {
    if(deleteAccountInput.value === 'delete') {
      if(confirm("确定要注销账号吗？")) {
        deleteAccount(id, () => {
          ElMessage.success('删除成功')
          store.dispatch('deleteAccount')
          router.push('/welcome')
        })
      }
    } else {
      ElMessage.warning('请输入正确的信息')
    }
}

const handleDeleteAccount = throttle(delAccount, 1000)

const cancelDeleteAccount = () => {
  deleteAccountDialogVisible.value = false
  deleteAccountInput.value = ''
}

const handleCancelDeleteAccount = throttle(cancelDeleteAccount, 1000)

// 密码检测规则
const validatePassword = (rule, value, callback) => {
  if(passwordForm.new_password !== value) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 名称表单校验规则
const usernameRules = reactive({
  username: [
    { required: true, min: 1, max: 20, message: '用户名最短为1个字符', trigger: ['blur', 'change'] }
  ]
})

// 邮箱表单校验规则
const emailRules = reactive({
  email: [
    { required: true, type: 'email', message: '请输入有效邮箱地址', trigger: ['blur', 'change'] }
  ]
})

// 密码表单校验规则
const validatePasswordRules = reactive({
  old_password: [
    { required: true, message: '请输入旧密码',  trigger: 'blur'},
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: ['blur', 'change'] }
  ],
  new_password: [
    { required: true, message: '请输入新密码',  trigger: 'blur'},
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: ['blur', 'change'] }
  ],
  confirm_new_password: [
    { validator: validatePassword, trigger: ['blur', 'change'] }
  ]
})

</script>

<template>
  <div class="mx-auto w-[668px]">
    <div class="mt-20 mb-12">
      <div class="mb-12">
        <h1 class="text-3xl font-bold">设置</h1>
      </div>
      <p class="text-zinc-600">请在这里进行个人资料编辑</p>
    </div>
    <div>
      <div class="space-y-12">
        <div class="border-b border-gray-900/10 pb-12">
          <div class="mt-10 grid grid-cols-1 gap-y-8">
            <!-- 头像 -->
            <div class="col-span-full flex items-center justify-between">
              <label class="block font-medium text-gray-900">头像</label>
              <div class="mt-2 flex items-center gap-x-3">
                <el-avatar
                    :icon="UserFilled"
                    :src="userAvatar || undefined"
                    :fit="'fill'"
                />
                <div><Button class="text-sm" :style="'grey'" @click="avatarDialogVisible = true">修改头像</Button></div>
                <el-dialog
                  v-model="avatarDialogVisible"
                  width="500"
                  @close="cancelUploadAvatar"
                >
                  <div class="p-4">
                    <div class="text-center mb-12">
                      <h1 class="font-bold text-xl">头像信息</h1>
                    </div>
                    <div class="pb-6 mb-12 border-b">
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
                      <Button :style="'grey'" class="font-bold text-sm" @click="handleCancelUploadAvatar">
                        取消
                      </Button>
                      <Button class="font-bold text-sm" @click="handleUploadAvatar">
                        确认上传
                      </Button>
                    </div>
                  </div>
                </el-dialog>
              </div>
            </div>
            <!-- 用户名 -->
            <div class="col-span-full flex items-center justify-between">
              <label class="block font-medium text-gray-900">名称</label>
              <div>
                <button
                    class="text-zinc-500 hover:text-zinc-800"
                    @click="usernameDialogVisible = true"
                >
                  {{ username }}
                </button>
              </div>
              <el-dialog
                  v-model="usernameDialogVisible"
                  width="500"
                  @close="cancelUpdateUsername"
              >
                <div class="p-4">
                  <div class="text-center mb-12">
                    <h1 class="font-bold text-xl">名称信息</h1>
                  </div>
                  <div class="pb-6 mb-12 border-b">
                    <div>
                      <el-form
                          ref="usernameFormRef"
                          :model="usernameForm"
                          :rules="usernameRules"
                      >
                        <div class="mb-2"><p>你可以在这里更改名称信息</p></div>
                        <el-form-item prop="username">
                          <el-input v-model="usernameForm.username" type="text" maxlength="20"/>
                        </el-form-item>
                      </el-form>
                    </div>
                  </div>
                  <div class="flex justify-end space-x-4">
                    <Button :style="'grey'" class="font-bold text-sm" @click="handleCancelUpdateUsername">
                      取消
                    </Button>
                    <Button class="font-bold text-sm" @click="handleUpdateUsername">
                      确认
                    </Button>
                  </div>
                </div>
              </el-dialog>
            </div>
            <!-- 邮箱 -->
            <div class="col-span-full flex items-center justify-between">
              <label class="block font-medium text-gray-900">邮箱</label>
              <div>
                <button
                    class="text-zinc-500 hover:text-zinc-800"
                    @click="emailDialogVisible = true"
                >
                  {{ email }}
                </button>
              </div>
              <el-dialog
                  v-model="emailDialogVisible"
                  width="500"
                  @close="cancelUpdateEmail"
              >
                <div class="p-4">
                  <div class="text-center mb-12">
                    <h1 class="font-bold text-xl">邮箱信息</h1>
                  </div>
                  <div class="pb-6 mb-12 border-b">
                    <div>
                      <el-form
                          ref="emailFormRef"
                          :model="emailForm"
                          :rules="emailRules"
                      >
                        <div class="mb-2"><p>你可以在这里更改邮箱信息</p></div>
                        <el-form-item prop="email">
                          <el-input v-model="emailForm.email" type="text" maxlength="20"/>
                        </el-form-item>
                      </el-form>
                    </div>
                  </div>
                  <div class="flex justify-end space-x-4">
                    <Button :style="'grey'" class="font-bold text-sm" @click="handleCancelUpdateEmail">
                      取消
                    </Button>
                    <Button class="font-bold text-sm" @click="handleUpdateEmail">
                      确认
                    </Button>
                  </div>
                </div>
              </el-dialog>
            </div>
            <!-- 密码 -->
            <div class="col-span-full flex items-center justify-between">
              <label class="block font-medium text-gray-900">密码</label>
              <div><Button class="text-sm" :style="'grey'" @click="passwordDialogVisible = true">修改密码</Button></div>
              <el-dialog
                  v-model="passwordDialogVisible"
                  width="500"
                  @close="cancelUpdatePassword"
              >
                <div class="p-4">
                  <div class="text-center mb-12">
                    <h1 class="font-bold text-xl">密码信息</h1>
                  </div>
                  <div class="pb-6 mb-12 border-b">
                    <div>
                      <el-form
                          ref="passwordFormRef"
                          :model="passwordForm"
                          :rules="validatePasswordRules"
                      >
                        <div class="mb-2"><p>你可以在这里更改密码</p></div>
                        <el-form-item prop="old_password">
                          <el-input v-model="passwordForm.old_password" type="text" placeholder="旧密码" maxlength="20" show-password/>
                        </el-form-item>
                        <el-form-item prop="new_password">
                          <el-input v-model="passwordForm.new_password" type="text" placeholder="新密码" maxlength="20" show-password/>
                        </el-form-item>
                        <el-form-item prop="confirm_new_password">
                          <el-input v-model="passwordForm.confirm_new_password" type="text" placeholder="确认新密码" maxlength="20" show-password/>
                        </el-form-item>
                      </el-form>
                    </div>
                  </div>
                  <div class="flex justify-end space-x-4">
                    <Button :style="'grey'" class="font-bold text-sm" @click="handleCancelUpdatePassword">
                      取消
                    </Button>
                    <Button class="font-bold text-sm" @click="handlePasswordChange">
                      确认
                    </Button>
                  </div>
                </div>
              </el-dialog>
            </div>
          </div>
        </div>
        <div>
          <button @click="deleteAccountDialogVisible = true">
            <span class="text-red-700">注销账号</span>
          </button>
          <el-dialog
              v-model="deleteAccountDialogVisible"
              width="500"
              @close="cancelDeleteAccount"
          >
            <div class="p-4">
              <div class="text-center mb-12">
                <h1 class="font-bold text-xl">注销账号</h1>
              </div>
              <div class="pb-6 mb-12 border-b">
                <div>
                  <div class="mb-2"><p class="text-red-700">如需注销账号，请在以下输入框输入"delete"</p></div>
                  <el-input v-model="deleteAccountInput" type="text" maxlength="20"/>
                </div>
              </div>
              <div class="flex justify-end space-x-4">
                <Button :style="'grey'" class="font-bold text-sm" @click="handleCancelDeleteAccount">
                  取消
                </Button>
                <Button class="font-bold text-sm" @click="handleDeleteAccount">
                  确认
                </Button>
              </div>
            </div>
          </el-dialog>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
.el-upload.el-upload--text {
  @apply w-full h-full flex items-center justify-center;
}
</style>