<script setup>
import {reactive} from "vue";
import {post} from "@/net/index.js";
import {Lock, User} from "@element-plus/icons-vue";
import router from "@/router/index.js";

const form=reactive({
  username : '',
  password : '',
  remember : false
})

const login=()=>{
  if (!form.username||!form.password){
    ElMessage({
      message: 'please enter your username.',
      grouping: true,
      type: 'warning',
    })
  }else {
    post('/api/auth/login',{
      username: form.username,
      password: form.password,
      remember: form.remember
    },(message)=>{
      ElMessage({
        message: message,
        grouping: true,
        type: 'success',
      })
      router.push('/index');
    })
  }
}
</script>

<template>
  <div style="text-align: center;margin: 0 7vw">
    <div style="margin-top: 15vh">
      <div style="font-size: 5rem">login</div>
      <div style="font-size: 1rem; color: gray">please log in for entrance</div>
    </div>
    <div style="margin-top: 2vh">
      <el-input  style="height: 3.5vh" v-model="form.username"  type="text" placeholder="username/email">
        <template #prefix>
          <el-icon><User /></el-icon>
        </template>
      </el-input>
      <el-input v-model="form.password"  type="password" style="height: 3.5vh;margin-top: 2vh" placeholder="password">
        <template #prefix>
          <el-icon><Lock/></el-icon>
        </template>
      </el-input>
    </div>
    <el-row style="margin-top: 1.5vh">
      <el-col :span="12" style="text-align: left">
        <el-checkbox v-model="form.remember"  label="remember me"/>
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-link @click="router.push('/forgot')">forgot password</el-link>
      </el-col>
    </el-row>
    <div style="margin-top: 2vh">
      <el-button @click="login()" style="width: 20vw;height: 4vh" type="success" plain>login</el-button>
    </div>
    <el-row style="margin: 2vh 0vw">
      <el-col :span="10">
        <el-divider content-position="center"/>
      </el-col>
      <el-col :span="4" style="color: gray;font-size: 1rem">
        without account
      </el-col>
      <el-col :span="10">
        <el-divider content-position="center"/>
      </el-col>
    </el-row>
    <div>
      <el-button @click="router.push('/register')" style="width: 20vw;height: 4vh" type="warning" plain>create account</el-button>
    </div>
  </div>

</template>

<style scoped>

</style>