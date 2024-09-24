<script setup>
import {useRouter} from "vue-router";
import qs from "qs";
import myAxios from "../plugins/myAxios";
import {onMounted, ref} from "vue";

import 'vant/es/notify/style';
import {showNotify} from "vant";
import {getCurrentUser} from "../service/user";

const user = ref();

onMounted(async () => {
  const res = await getCurrentUser();
  if (res) {
    user.value = res;
    showNotify({ type: 'success', message: '获取用户信息成功' });
  } else {
    showNotify({ type: 'danger', message: '获取用户信息失败' });
  }
});

const router = useRouter();

const doEdit = (editKey, editName, currentValue) => {
  router.push({
    path: '/user/edit',
    query: {
      editKey,
      editName,
      currentValue
    }
  })
}
</script>

<template>
  <div v-if="user">
    <van-cell title="头像" is-link href="#" to="/user/edit">
      <van-image
          round
          width="50"
          height="50"
          fit="cover"
          :src="user.avatarUrl"
      />
    </van-cell>

    <van-cell title="账号" :value="user.userAccount"/>
    <van-cell title="昵称" is-link :value="user.username" @click="doEdit('username', '昵称', user.username)"/>
    <van-cell title="性别" is-link :value="user.gender" @click="doEdit('gender', '性别', user.gender)"/>
    <van-cell title="电话" is-link :value="user.phone" @click="doEdit('phone', '电话', user.phone)"/>
    <van-cell title="邮箱" is-link :value="user.email" @click="doEdit('email', '邮箱', user.email)"/>
    <van-cell title="注册时间" :value="user.createTime"/>
  </div>
  <div v-else>
    <van-empty description="请先登录"/>
  </div>

</template>

<style scoped>

</style>
