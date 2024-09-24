<script setup lang="ts">
import UserType from "../models/user";

interface UserCardListProps {
  userList: UserType[];
}
// props 接收父组件传递的 userList
// withDefaults 函数用于设置默认值
const props = withDefaults(defineProps<UserCardListProps>(), {
  userList: [],
});
</script>

<template>
  <van-card
      v-for="user in props.userList"
      :desc="user.profile"
      :title="`${user.username} (${user.planetCode})`"
      :thumb="user.avatarUrl"
  >
    <template #tags>
      <van-tag plain type="danger" v-for="tag in user.tags" style="margin: 5px 5px 0 0">
        {{ tag }}
      </van-tag>
    </template>
    <template #footer>
      <van-button size="mini">联系我</van-button>
    </template>
  </van-card>
</template>

<style scoped>

</style>
