<script setup lang="ts">
import {ref} from "vue";
import {useRouter} from "vue-router";

const router = useRouter();
const title = ref('主页');
const props = defineProps({
  // 标题
  title: {
    type: String,
    required: true
  },
});

const onClickRight = () => {
  router.push('/search');
  title.value = "搜索";
}

const onClickLeft = () => {
  // 返回上一页
  router.back();
  title.value = "主页"; // 改变标题的值
}
</script>

<template>
  <div class="customNavbar">
    <van-nav-bar
        left-arrow
        @click-left="onClickLeft"
        @click-right="onClickRight"
    >
      <template #title>
        <slot name="titleContent">{{ props.title }}</slot>
      </template>
      <template #right>
        <slot name="rightContent">
          <van-icon name="search" size="18" @click="onClickRight"/>
        </slot>
      </template>
    </van-nav-bar>
  </div>
</template>

<style scoped>

</style>
