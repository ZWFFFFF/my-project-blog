<script setup>
import {useRouter} from "vue-router";
import {reactive, ref} from "vue";
import {Lock} from "@element-plus/icons-vue";
import Button from "@/components/Button.vue";
import {ElMessage} from "element-plus";
import {changePassword} from "@/net/user.js";
import {useStore} from "vuex";
import {throttle} from "@/net/utils.js";

const router = useRouter()
const store = useStore()
const formRef = ref()
const form = reactive({
  old_password: '',
  new_password: '',
  confirm_new_password: ''
})

const validatePassword = (rule, value, callback) => {
  if(value === '') {
    callback(new Error('请再次输入密码'))
  } else if(form.new_password !== value) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 表单校验规则
const rules = reactive({
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

const changePwd = () => {
  formRef.value.validate((valid) => {
    if(valid) {
      changePassword(store.state.user.id, form.old_password, form.new_password, () => {
        ElMessage.success('修改密码成功')
      })
    } else {
      ElMessage.warning('请输入正确的信息')
    }
  })
}

const handlePasswordChange = throttle(changePwd, 1000)

</script>

<template>
  <div class="h-full">
    <div class="bg-white py-4 px-8 rounded-md">
      <div>
        <span class="text-xl font-bold">修改密码</span>
      </div>
      <div class="mt-4 flex flex-col justify-center">
        <el-form
            class="max-w-[500px]"
            ref="formRef"
            :model="form"
            :rules="rules"
        >
          <el-form-item prop="old_password">
            <el-input v-model="form.old_password" type="text" placeholder="旧密码" maxlength="20" show-password>
              <template #prefix>
                <el-icon><Lock/></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="new_password">
            <el-input v-model="form.new_password" type="text" placeholder="新密码" maxlength="20" show-password>
              <template #prefix>
                <el-icon><Lock/></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="confirm_new_password">
            <el-input v-model="form.confirm_new_password" type="text" placeholder="确认新密码" maxlength="20" show-password>
              <template #prefix>
                <el-icon><Lock/></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
        <div>
          <Button class="text-sm" @click="handlePasswordChange">确认修改</Button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>