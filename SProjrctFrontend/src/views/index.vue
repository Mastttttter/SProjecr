<script setup>
import router from "@/router/index.js";
import {get} from "@/net"
const logout=()=>{
  get('/api/auth/logout',(message)=>{
    ElMessage({
      message: message,
      grouping: true,
      type: 'success',
    })
    router.push('/');
  })
}
</script>

<template>
<el-button @click="logout()" type="info" plain>log out</el-button>
</template>

<style scoped>

</style>