<script setup>

import {Lock, User} from "@element-plus/icons-vue";
</script>

<template>
  <div id="general" >
    <div id="left-general" >
      <!--<el-image style="width: 100%;height: 100%" fit="cover" src="a picture to insert"></el-image>-->
      <div style="position: absolute;bottom: 7vh;left: 3vw">
        <div style="font-size: 6rem;color: azure">Welcome</div>
        <div style="font-size: 2rem;color: azure">This is only a test frontend</div>
      </div>
    </div>
    <div id="right-general">
      <router-view/>
    </div>
  </div>
</template>

<style scoped>
#general{
  overflow: hidden;
  width: 100vw;
  height: 100vh;
  display: flex;
}

#left-general{
  flex: 1 1 auto;
  background-color: #1d1d1f;
}

#right-general{
  width: 38.2vw;
  background-color: #f5f5f7;
}
</style>