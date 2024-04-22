<script setup>

import {Lock, User,Message,EditPen} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {reactive} from "vue";
import {ref}  from "vue";
import {post} from "@/net/index.js";

const form=reactive({
  username: '',
  password:'',
  password_confirmation:'',
  email:'',
  code:'',
})

const validateUsername=(rule,value,callback)=>{
  if (value===''){
    callback(new Error('please enter your username'));
  }else if(!/^[a-zA-z0-9]+$/.test(value)){
    callback(new Error('invalid text'))
  }else{
    callback()
  }
}

const validatePasswordCon=(rule,value,callback)=>{
  if (value===''){
    callback(new Error('please enter your username'));
  }else if(value!==form.password){
    callback(new Error('wrong text'))
  }else{
    callback()
  }
}

const rules={
  username:[
    { validator: validateUsername,trigger: ['blur','change']},
    { min: 3, max: 25, message: 'Length should be 3 to 25', trigger: ['blur','change'] },
  ],
  password:[
    {required: true,message:'must have password',trigger: ['blur','change']},
    {min:6,max:16,message:'Length should be 6 to 16', trigger: ['blur','change']},
  ],
  password_confirmation:[
    {validator: validatePasswordCon,trigger: ['blur','change']},
  ],
  email:[
    {required:true, message:'must have email',trigger: ['blur','change']},
    {type: 'email',message:'please enter your email address',trigger: ['blur','change']},
  ],
  code:[
    {required:true,message:'must have code',trigger: ['blur','change']},
  ]

}

const formRef=ref()
const tem=ref('register new account')
const isValidEmail=ref(false);
const codeTime=ref(0);
const onValidEmail=(prop,isValid,message)=>{
  if (prop==='email'){
    isValidEmail.value=isValid;
  }
}

const register=()=>{

  formRef.value.validate((valid)=>{
    if(valid){
        post('/api/auth/register',{
          username: form.username,
          password: form.password,
          email: form.email,
          code: form.code,
        },()=>{
          ElMessage({
            message: 'register success',
            grouping: true,
            type: 'success',
          })
          router.push('/');
        })
    }else {
      ElMessage({
        message: 'please enter valid information',
        grouping: true,
        type: 'warning',
      })
    }
  })
}

const validateEmail=()=>{
  post("/api/auth/valid-email",{
    email: form.email,
  },(message)=>{
    ElMessage({
      message: message,
      grouping: true,
      type: 'success',
    });
    codeTime.value=60;
    clearInterval(a)
    let a=setInterval(()=>{
          codeTime.value--;
    },1000
    )
  })
}
</script>

<template>
<div style="text-align: center;margin: 0vh 7vw">
  <div style ="margin-top: 15vh">
    <div style="font-size: 3rem;font-weight: bold">{{ tem }}</div>
    <div style="font-size: 1rem;color: gray">fill the information below</div>
  </div>
  <div style="margin-top: 2vh">
    <el-form :model="form" :rules="rules" @validate="onValidEmail" ref="formRef">
      <el-form-item prop="username">
        <el-input v-model="form.username" :maxlength="8" type="text" placeholder="username/email" style="height: 3.5vh">
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="form.password" :maxlength="16" type="password" placeholder="password" style="height: 3.5vh">
          <template #prefix>
            <el-icon><Lock/></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password_confirmation">
        <el-input v-model="form.password_confirmation" :maxlength="6" type="password" placeholder="repeat password" style="height: 3.5vh">
          <template #prefix>
            <el-icon><Lock/></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input v-model="form.email" type="email" placeholder="email address" style="height: 3.5vh">
          <template #prefix>
            <el-icon><Message /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="code">
          <el-row>
            <el-col :span="18">
              <el-input v-model="form.code" type="email" placeholder="email address verify code" style="height: 3.5vh">
                <template #prefix>
                  <el-icon><EditPen/></el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="6">
              <el-button @click="validateEmail" :disabled="!isValidEmail||codeTime>0" type="success" style="height: 3.5vh">{{codeTime>0?'wait '+codeTime+'seconds':'get verify code'}}</el-button>
            </el-col>
          </el-row>
      </el-form-item>
    </el-form>
  </div>
  <div style="margin-top: 2vh">
    <el-button @click="register" type="warning" style="width: 100%;height:4vh" plain>register</el-button>
  </div>
  <div style="margin-top: 2vh">
    <span style="font-size: 1rem;color: gray">have account?</span>

    <el-link @click="router.push('/')" type="primary" style="translate: 0 -0.25vh">log in</el-link>
  </div>
</div>
</template>

<style scoped>

</style>