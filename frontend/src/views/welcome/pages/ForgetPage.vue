<script setup>
import {useRouter} from "vue-router";
import {ref, reactive, computed} from "vue";
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";
import {Lock, Message} from "@element-plus/icons-vue";
import {resetPassword} from "@/net/auth.js";

const router = useRouter()

const formRef = ref()

const coldTime = ref(0) // 验证码请求冷却时间

const form = reactive({
  email: '',
  password: '',
  password_repeat: '',
  code: ''
})

const validatePassword = (rule, value, callback) => {
  if(value === '') {
    callback(new Error('请再次输入密码'))
  } else if(form.password !== value) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = reactive({
  email: [
    { required: true, message: '请输入邮箱',  trigger: 'blur'},
    { type: 'email', message: '请输入有效邮箱地址', trigger: ['blur', 'change'] }
  ],
  code: [
    { required: true, message: '请输入验证码',  trigger: 'blur'}
  ],
  password: [
    { required: true, message: '请输入密码',  trigger: 'blur'},
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: ['blur', 'change'] }
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ['blur', 'change'] }
  ]
})

const isEmailValid = computed(() => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email))

// 验证码申请成功倒计时
function startInterval() {
  const intervalId = setInterval(() => {
    if (coldTime.value > 0) {
      coldTime.value--;
    } else {
      clearInterval(intervalId);
    }
  }, 1000);
}

function askCode() {
  if(isEmailValid) {
    coldTime.value = 60
    get({
      url: `api/auth/ask-code?email=${form.email}&type=reset`,
      success: () => {
        startInterval()
        ElMessage.success(`验证码已发送至邮箱${form.email}`)
      },
      withToken: false
    })
  } else {
    ElMessage.warning('请输入正确的电子邮箱格式')
  }
}

function reset() {
  formRef.value.validate((valid) => {
    if(valid) {
      resetPassword(form.email, form.code, form.password, () => {
        router.push('/welcome')
      })
    } else {
      ElMessage.warning('请输入正确的信息')
    }
  })
}
</script>

<template>
  <div class="w-full h-full px-12 text-center pt-32">
    <div class="text-4xl font-bold">重置密码</div>
    <div class="pt-10">
      <div class="text-gray-500 font-semibold">请输入需要重置密码的信息</div>
      <div class="pt-12 px-12">
        <el-form ref="formRef" :model="form" :rules="rules">
          <el-form-item prop="email">
            <el-input v-model="form.email" type="text" placeholder="邮箱" maxlength="20" clearable>
              <template #prefix>
                <el-icon><Message/></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="text" placeholder="密码" maxlength="20" show-password>
              <template #prefix>
                <el-icon><Lock/></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password_repeat">
            <el-input v-model="form.password_repeat" type="text" placeholder="重复密码" maxlength="20" show-password>
              <template #prefix>
                <el-icon><Lock/></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-input v-model="form.code" type="text" placeholder="邮箱验证码" maxlength="6">
              <template #append>
                <el-button :disabled="!isEmailValid || coldTime !== 0" @click="askCode">
                  {{ coldTime > 0 ? `已发送(${coldTime}s)` : '获取验证码' }}
                </el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
        <div class="mt-10">
          <el-button class="w-36" @click="reset">重置密码</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>