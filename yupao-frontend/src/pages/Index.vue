<script setup>
import {useRoute, useRouter} from "vue-router";
import myAxios from "../plugins/myAxios.ts";
import 'vant/es/toast/style';
import {showFailToast, showToast} from "vant";
import {onMounted, ref} from "vue";
import UserCardList from "../components/UserCardList.vue";

const route = useRoute()
const router = useRouter()

const {tags} = route.query

const userList = ref([])
const loading = ref(false) // 添加一个新的响应式引用来跟踪数据是否正在加载
const username = ref('')
const isMatchMode = ref(false)
const active = ref('all')

// 解析用户标签 JSON 字符串
const parseUserTags = (userListData) => {
  if (userListData) {
    userListData.forEach(user => {
      if (user.tags && typeof user.tags === 'string') {
        try {
          user.tags = JSON.parse(user.tags);
        } catch (e) {
          console.error('Error parsing user.tags:', e);
        }
      }
    })
  }
}

const loadDataAll = async () => {
  let userListData;
  // showToast('正在获取数据...');
  loading.value = true;
  // 心动模式，根据标签匹配用户
  if (isMatchMode.value) {
    const num = 10;
    userListData = await myAxios.get('/user/match', {
      params: {
        num,
      },
    })
        .then(function (response) {
          console.log('/user/match succeed', response);
          showToast('匹配成功');
          return response?.data;
        })
        .catch(function (error) {
          console.error('/user/match error', error);
          Toast.fail('请求失败');
        })
  } else {
    // 普通模式，直接分页查询用户
    userListData = await myAxios.get('/user/recommend', {
      params: {
        pageSize: 8,
        pageNum: 1,
      },
    })
        .then(function (response) {
          console.log('/user/recommend succeed', response);
          return response?.data?.records;
        })
        .catch(function (error) {
          console.error('/user/recommend error', error);
          Toast.fail('请求失败');
        })
  }
  parseUserTags(userListData);
  userList.value = userListData;
  loading.value = false;
}

onMounted(async () => {
  loadDataAll();
})

/**
 * 搜索用户
 */
const listUser = async (val = '') => {
  loading.value = true // 开始请求数据时，将isLoading设置为true
  // 显示轻提示
  showToast('正在搜索数据...');
  const res = await myAxios.get("/user/search", {
    params: {
      username: val,
    },
  });
  if (res.code === 0) {
    parseUserTags(res.data);
    userList.value = res.data;
  } else {
    showFailToast('搜索失败');
  }
  loading.value = false
}

const onTabChange = (name) => {
  // 所有用户 分页
  if (name === 'all') {
    isMatchMode.value = false;
    loadDataAll();
  } else {
    // 推荐用户 分页
    isMatchMode.value = true;
    loadDataAll();
  }
}

const onSearch = (val) => {
  listUser(val);
};
</script>

<template>
  <van-search v-model="username" placeholder="搜索用户" @search="onSearch" />
  <van-tabs v-model:active="active" @change="onTabChange">
    <van-tab title="所有" name="all" />
    <van-tab title="推荐" name="recommend" />
  </van-tabs>
  <van-loading v-if="loading" vertical size="50px"/>
  <user-card-list :user-list="userList" v-else-if="userList && userList.length > 0"/>
  <van-empty image="search" description="数据为空" v-else/>
</template>

<style scoped>

</style>
