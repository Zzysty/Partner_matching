<script setup lang="ts">
import {ref} from "vue";
import {useRouter} from "vue-router";
import CustomNavbar from "../components/CustomNavbar.vue";
import routes from "../config/route.ts";

const DEFAULT_TITLE: string | undefined = "主页";

const router = useRouter();
const title = ref(DEFAULT_TITLE);

router.beforeEach((to, from) => {
  const toPath = to.path;
  const route = routes.find((route) => {
    return toPath == route.path;
  })
  if (!route?.title) {
    title.value = DEFAULT_TITLE;
  }
  title.value = route.title;
})

const onClickHome = () => {
  router.push('/');
  title.value = "主页"; // 改变标题的值
}

const onClickTeam = () => {
  router.push('/team');
  title.value = "队伍"; // 改变标题的值
}
const onClickUser = () => {
  router.push('/user');
  title.value = "个人"; // 改变标题的值
}



</script>

<template>
  <div class="header">
    <custom-navbar :title="title"/>
  </div>

  <div id="content">
    <router-view/>
  </div>

  <div class="footer">
    <van-tabbar route>
      <van-tabbar-item replace to="/" icon="home-o" @click="onClickHome">主页</van-tabbar-item>
      <van-tabbar-item replace to="/team" icon="friends-o" @click="onClickTeam">队伍</van-tabbar-item>
      <van-tabbar-item replace to="/user" icon="contact-o" @click="onClickUser">个人</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<style scoped>
#content {
  padding-bottom: 50px;
}
</style>
