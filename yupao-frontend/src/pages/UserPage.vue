<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../service/user.js";
import {showImagePreview} from 'vant';
import 'vant/es/image-preview/style';
import myAxios from "../plugins/myAxios.ts";

const user = ref();

const router = useRouter();

onMounted(async () => {
  user.value = await getCurrentUser();
  console.log(user.value)
})

const toEdit = (editKey: string, editName: string, currentValue: string) => {
  router.push({
    path: '/user/edit',
    query: {
      editKey,
      editName,
      currentValue,
    }
  })
}

const logout = async () => {
  const res = await myAxios.post('/user/logout');
  if (res.code === 0) {
    alert('退出成功')
    // 跳转到登录页面
    router.push('/user/login')
  } else {
    console.log(res.data)
  }
};

</script>

<template>
  <template v-if="user">
    <van-image class="avatar" round :src="user?.avatarUrl"
               width="10rem" height="10rem" fit="cover" @click="showImagePreview([user?.avatarUrl])"/>
    <van-cell-group inset>
      <van-cell title="当前用户" :value="user?.username" />
      <van-cell title="修改信息" is-link to="/user/update" />
      <van-cell title="我创建的队伍" is-link to="/user/team/add" />
      <van-cell title="我加入的队伍" is-link to="/user/team/join" />
      <van-cell title="切换用户" is-link @click="logout" />
    </van-cell-group>
  </template>


</template>

<style scoped>
.avatar {
  margin: 20px auto;
  display: flex;
  justify-content: center;
}
</style>
