<script setup>
import router from "@/router/index.js";
import {reactive, ref} from "vue";
import {Message,EditPen,Lock} from "@element-plus/icons-vue"
import {post} from "@/net/index.js";
import {ElMessage} from "element-plus";

const active = ref(0)
const form=reactive({
  email:'',
  code:'',
  password: '',
  password_confirmation: '',
})


const validatePasswordCon = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('enter the password again'))
  } else if (value !== form.password) {
    callback(new Error("dif in two password again"))
  } else {
    callback()
  }
}
const rules={
  email:[
    {required:true, message:'must have email',trigger: ['blur','change']},
    {type: 'email',message:'please enter your email address',trigger: ['blur','change']},
  ],
  code:[
    {required:true,message:'must have code',trigger: ['blur','change']},
  ],  password:[
    {required: true,message:'must have password',trigger: ['blur','change']},
    {min:6,max:16,message:'Length should be 6 to 16', trigger: ['blur','change']},
  ],
  password_confirmation:[
    {validator: validatePasswordCon,trigger: ['blur','change']},
  ],
}

const formRef=ref()
const coldTime=ref(0)
const isValidEmail=ref(false);
const te=ref("Reset Password")
const onValidEmail=(prop,isValid,message)=>{
  if (prop==='email'){
    isValidEmail.value=isValid;
  }
}

const validateEmail=()=>{
  post("/api/auth/valid-reset-email",{
    email: form.email,
  },(message)=>{
    ElMessage({
      message: message,
      grouping: true,
      type: 'success',
    });
    coldTime.value=60;
    // clearInterval(a)
    let a=setInterval(()=>{
          coldTime.value--;
        },1000
    )
  })
}

const startReset=()=>{
  formRef.value.validate((isValid) => {
    if(isValid) {
      post('/api/auth/start-reset', {
        email: form.email,
        code: form.code
      }, () => {
        active.value++
      })
    } else {
      ElMessage.warning('fill the email and verify code')
    }
  })
}

const doReset = () => {
  formRef.value.validate((isValid) => {
    if(isValid) {
      post('/api/auth/do-reset', {
        password: form.password
      }, (message) => {
        ElMessage.success(message)
        router.push('/')
      })
    } else {
      ElMessage.warning('enter the new password')
    }
  })
}
</script>

<template>
  <div style="text-align: center;margin: 0 7vw">
    <div style="margin-top: 10vh">
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="verify" />
        <el-step title="reset" />
      </el-steps>
    </div>
    <transition name="el-fade-in-linear" mode="out-in">
        <div style="text-align: center;margin: 0 0vw;height: 100%" v-if="active===0">
          <div style ="margin-top: 15vh">
            <div style="font-size: 1.5rem;font-weight: bold">reset the password</div>
            <div style="font-size: 1rem;color: gray">need verify email</div>
          </div>
          <div style="margin-top: 10vh">
            <el-form :model="form" :rules="rules" @validate="onValidEmail" ref="formRef">
              <el-form-item prop="email">
                <el-input v-model="form.email" type="email" placeholder="email">
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item prop="code">
                <el-row :gutter="10" style="width: 100%">
                  <el-col :span="17">
                    <el-input v-model="form.code" :maxlength="6" type="text" placeholder="password">
                      <template #prefix>
                        <el-icon><EditPen /></el-icon>
                      </template>
                    </el-input>
                  </el-col>
                  <el-col :span="5">
                    <el-button type="success" @click="validateEmail"
                               :disabled="!isValidEmail || coldTime > 0">
                      {{coldTime > 0 ? 'wait ' + coldTime + ' s' : 'get verify code'}}
                    </el-button>
                  </el-col>
                </el-row>
              </el-form-item>
            </el-form>
          </div>
          <div style="margin-top: 70px">
            <el-button @click="startReset()" style="width: 270px;" type="danger" plain>next step</el-button>
          </div>
        </div>
    </transition>
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align: center;margin: 0 20px;height: 100%" v-if="active === 1">
        <div style="margin-top: 80px">
          <div style="font-size: 25px;font-weight: bold">{{te}}</div>
          <div style="font-size: 14px;color: grey">do not forget again</div>
        </div>
        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rules" @validate="onValidEmail" ref="formRef">
            <el-form-item prop="password">
              <el-input v-model="form.password" :maxlength="16" type="password" placeholder="new password">
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password_confirmation">
              <el-input v-model="form.password_confirmation" :maxlength="16" type="password" placeholder="repeat password">
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 70px">
          <el-button @click="doReset()" style="width: 270px;" type="danger" plain>reset</el-button>
        </div>
      </div>
    </transition>
  </div>

</template>

<style scoped>

</style>