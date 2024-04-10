
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import axios from "axios";
import ElementPlus from 'element-plus'
const app = createApp(App)

axios.defaults.baseURL='http://localhost:8080'
app.use(createPinia())
app.use(router)
app.use(ElementPlus, { size: 'small', zIndex: 3000 })

app.mount('#app')
