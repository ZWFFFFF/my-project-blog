<script setup>
import {useRouter} from "vue-router";
import {ref, reactive, computed} from "vue";
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";

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

const isEmailValid = computed(() => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(formStep1.email))

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
      url: `api/auth/ask-code?email=${form.email}&type=login`,
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

function resetPassword() {

}
function confirmResetCode() {

}
</script>

<template>
  <div class="w-full h-full px-12 text-center pt-32">
    <div class="text-4xl font-bold">重置密码</div>
    <div class="pt-10">
      <div class="text-gray-500 font-semibold">请输入需要重置密码的信息</div>
      <div class="pt-12 px-12">
        <el-form>
          <el-form-item>
            <el-input></el-input>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>