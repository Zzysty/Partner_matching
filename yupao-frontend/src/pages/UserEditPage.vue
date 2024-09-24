 <script setup>
 import {useRoute, useRouter} from "vue-router";
 import {ref} from "vue";
 import myAxios from "../plugins/myAxios";
 // 引入css
 import 'vant/es/notify/style';
 import {showNotify} from "vant";
 import {getCurrentUser} from "../service/user";
 import {setCurrentUserState} from "../states/user";

const router = useRouter();
const route = useRoute();

const editUser = ref({
  editKey: route.query.editKey,
  editName: route.query.editName,
  currentValue: route.query.currentValue
})

const onSubmit = async () => {
  const currentUser = await getCurrentUser();

  if (!currentUser) {
    showNotify({ type: 'primary', message: '请先登录' });
    return;
  }
  // 校验值判空
  if (!editUser.value.currentValue || editUser.value.currentValue.trim() === '') {
    showNotify({ type: 'primary', message: '请输入要修改的值' });
    return;
  }
  if (editUser.value.currentValue === route.query.currentValue) {
    showNotify({ type: 'primary', message: '您未进行任何修改' });
    return;
  }
  const res = await myAxios.post('/user/update', {
  'id': currentUser.id,
  [editUser.value.editKey]: editUser.value.currentValue
  })
  console.log(res, '修改成功');
  if (res.code === 0 && res.data > 0) {
    // 更新状态 states 中的用户信息
    currentUser[editUser.value.editKey] = editUser.value.currentValue;
    setCurrentUserState(currentUser);
    showNotify({ type: 'success', message: '修改成功' });
    // 睡眠1秒后返回上一页
    setTimeout(() => {
      router.go(-1);
    }, 1000);
  } else {
    showNotify({ type: 'danger', message: '修改失败' });
  }
};
 </script>

<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="editUser.currentValue"
          :name="editUser.editKey"
          :label="editUser.editName"
          :placeholder="`请输入${editUser.editName}`"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit" >
        提交
      </van-button>
    </div>
  </van-form>

</template>

<style scoped>

</style>
