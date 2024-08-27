<script setup>
import {useRouter} from "vue-router";
import {Lock, Message, User} from "@element-plus/icons-vue";
import {ref, reactive, computed} from 'vue'
import {passwordLogin, verifyCodeLogin} from "@/net/auth.js";
import {ElMessage} from "element-plus";
import {get} from "@/net"
import {useStore} from "vuex";

const router = useRouter()

const store = useStore()

const coldTime = ref(0) // 验证码请求冷却时间

const active = ref(0) // 0: 邮箱验证码登录 1: 密码登录

const passwordLoginFormRef = ref()

const verifyCodeLoginFormRef = ref()

const passwordLoginForm = reactive({
  username: '',
  password: ''
})

const verifyCodeLoginForm = reactive({
  email: '',
  code: ''
})

const rules = reactive({
  username: [
    { required: true, message: '请输入邮箱',  trigger: 'blur'},
    { type: 'email', message: '请输入有效邮箱地址', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱',  trigger: 'blur'},
    { type: 'email', message: '请输入有效邮箱地址', trigger: ['blur', 'change'] }
  ],
  code: [
    { required: true, message: '请输入验证码',  trigger: 'blur'}
  ],
})

const isEmailValid = computed(() => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(verifyCodeLoginForm.email))

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
      url: `api/auth/ask-code?email=${verifyCodeLoginForm.email}&type=login`,
      success: () => {
        startInterval()
        ElMessage.success(`验证码已发送至邮箱${verifyCodeLoginForm.email}`)
      },
      withToken: false
    })
  } else {
    ElMessage.warning('请输入正确的电子邮箱格式')
  }
}

function userLogin() {
  if(active.value === 0) {
    verifyCodeLoginFormRef.value.validate((valid) => {
      if(valid) {
        verifyCodeLogin(verifyCodeLoginForm.email, verifyCodeLoginForm.code, (data) => {
          store.dispatch('login', data.id)
          router.push('/')
        })
      } else {
        ElMessage.warning('请输入正确信息')
      }
    })
  } else {
    passwordLoginFormRef.value.validate((valid) => {
      if(valid) {
        passwordLogin(passwordLoginForm.username, passwordLoginForm.password, (data) => {
          store.dispatch('login', data.id)
          router.push('/')
        })
      } else {
        ElMessage.warning('请输入正确信息')
      }
    })
  }
}
</script>

<template>
  <div class="text-center w-full h-full px-12">
    <div class="pt-36">
      <h1 class="text-4xl font-bold py-4">登录</h1>
      <p class="text-gray-500 font-semibold">请选择邮箱验证码登录或密码登录</p>
    </div>
    <div class="pt-12 px-12">
      <div v-show="active === 0">
        <el-form ref="verifyCodeLoginFormRef" :model="verifyCodeLoginForm" :rules="rules">
          <el-form-item prop="email">
            <el-input v-model="verifyCodeLoginForm.email" type="text" maxlength="20" placeholder="邮箱" clearable>
              <template #prefix>
                <el-icon><Message/></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-input v-model="verifyCodeLoginForm.code" type="text" maxlength="6" placeholder="邮箱验证码">
              <template #append>
                <el-button :disabled="!isEmailValid || coldTime !== 0" @click="askCode">
                  {{ coldTime > 0 ? `已发送(${coldTime}s)` : '获取验证码' }}
                </el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
        <el-row justify="start">
          <el-link @click="active++">密码登录</el-link>
        </el-row>
      </div>
      <div v-show="active === 1">
        <el-form ref="passwordLoginFormRef" :model="passwordLoginForm" :rules="rules">
          <el-form-item prop="username">
            <el-input v-model="passwordLoginForm.username" type="text" maxlength="20" placeholder="邮箱" clearable>
              <template #prefix>
                <el-icon><User/></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="passwordLoginForm.password" type="password" maxlength="20" placeholder="密码" show-password>
              <template #prefix>
                <el-icon><Lock/></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-row justify="space-between">
            <el-link @click="active--">验证码登录</el-link>
            <el-link @click="router.push('/welcome/forget')">忘记密码</el-link>
          </el-row>
        </el-form>
      </div>
      <div class="pt-5">
        <el-button @click="userLogin" class="w-36">登录</el-button>
      </div>
      <el-divider>
        <span class="text-sm text-gray-500 font-semibold">没有账号</span>
      </el-divider>
      <div>
        <el-button @click="router.push('/welcome/register')" class="w-36">立即注册</el-button>
      </div>
    </div>
  </div>
</template>
