import { createApp } from 'vue'
import * as VueRouter from 'vue-router'
import Vant from 'vant';
import App from './App.vue'
import routes from "./config/route.ts";

const app= createApp(App)
app.use(Vant);



const router = VueRouter.createRouter({
  // 4. 内部提供了 history 模式的实现。为了简单起见，我们在这里使用 hash 模式。
  history: VueRouter.createWebHistory(),
  routes, // `routes: routes` 的缩写
})

app.use(router)
app.mount('#app')


