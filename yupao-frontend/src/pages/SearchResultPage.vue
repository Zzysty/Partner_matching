<script setup>
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios"
import qs from 'qs'
import UserCardList from "../components/UserCardList.vue";

const route = useRoute()

const {tags} = route.query

const userList = ref([])

onMounted(async () => {
  const userListData = await myAxios.get('/user/search/tags', {
    params: {
      tagNameList: tags
    },
    paramsSerializer: params => {
      return qs.stringify(params, {indices: false})
    }
  })
  .then(function (response) {
    console.log('/user/search/tags success', response);
    return response?.data;
  })
  .catch(function (error) {
    console.error('/user/search/tags error', error);
  })
  if (userListData) {
    userListData.forEach(user => {
      if (user.tags && typeof user.tags === 'string') {
        try {
          user.tags = JSON.parse(user.tags);
        } catch (e) {
          console.error('Error parsing user.tags:', e);
        }
      }
      // console.log(Array.isArray(user.tags));
      // console.log(user.planetCode);
    })
    userList.value = userListData;
  }
})

// const mockUser = {
//   id: 22,
//   username: 'zzy',
//   userAccount: 'zzy',
//   profile: '自我介绍',
//   avatarUrl: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
//   gender: '男',
//   phone: '123435',
//   email: '123435',
//   tags: ['java','python','emo','hhhhhhhhhhhhhhhh'],
//   planetCode: 1234,
//   createTime: new Date()
// }

</script>

<template>
  <user-card-list :user-list="userList"/>
  <!-- 搜索提示 -->
  <van-empty image="search" description="搜索为空" v-if="!userList || userList.length < 1"/>
</template>

<style scoped>

</style>
