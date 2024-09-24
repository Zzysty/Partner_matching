<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {ref} from "vue";
import myAxios from "../plugins/myAxios";
import 'vant/es/notify/style';
import {showNotify} from "vant";

const router = useRouter();
const route = useRoute();

const userAccount = ref('');
const userPassword = ref('');

const onSubmit = async () => {
  const res = await myAxios.post('/user/login', {
    userAccount: userAccount.value,
    userPassword: userPassword.value
  });
  if (res.code === 0 && res.data) {
    showNotify({type: 'success', message: '登录成功'});
    // 跳转到首页
    await router.push('/');
  }
};
</script>

<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="userAccount"
          name="账号"
          label="账号"
          placeholder="账号"
          :rules="[{ required: true, message: '请填写账号' }]"
      />
      <van-field
          v-model="userPassword"
          type="password"
          name="密码"
          label="密码"
          placeholder="密码"
          :rules="[{ required: true, message: '请填写密码' }]"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>


</template>

<style scoped>

</style>
