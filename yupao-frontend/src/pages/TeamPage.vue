<script setup lang="js">
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";
import myAxios from "../plugins/myAxios.ts";
import 'vant/es/toast/style';
import {showFailToast} from "vant";
import TeamCardList from "../components/TeamCardList.vue";

const active = ref('')
const router = useRouter();
const searchText = ref('');

/**
 * 切换查询状态
 * @param name
 */
const onTabChange = (name) => {
  // 查公开
  if (name === 'public') {
    listTeam(searchText.value, 0);
  } else {
    // 查加密
    listTeam(searchText.value, 2);
  }
}

// 跳转到创建队伍页
const toAddTeam = () => {
  router.push({
    path: "/team/add"
  })
}


const teamList = ref([]);

/**
 * 搜索队伍
 * @param val
 * @param status
 * @returns {Promise<void>}
 */
const listTeam = async (val = '', status = 0) => {
  const res = await myAxios.get("/team/list", {
    params: {
      searchText: val,
      pageNum: 1,
      status,
    },
  });
  if (res.code === 0) {
    teamList.value = res.data;
  } else {
    showFailToast('加载队伍失败，请刷新重试');
  }
}

// 页面加载时只触发一次
onMounted( () => {
  listTeam();
})

const onSearch = (val) => {
  listTeam(val);
};

</script>

<template>
<div class="team-page">
  <van-search v-model="searchText" placeholder="搜索队伍" @search="onSearch" />
  <van-tabs v-model:active="active" @change="onTabChange">
    <van-tab title="公开" name="public" />
    <van-tab title="加密" name="private" />
  </van-tabs>
  <van-button round class="add-button floating-button" type="primary" icon="plus" @click="toAddTeam" />
  <team-card-list :teamList="teamList" />
  <van-empty v-if="teamList?.length < 1" description="数据为空"/>
</div>
</template>

<style scoped>
.floating-button {
  position: fixed;
  right: 20px;
  bottom: 70px;
  z-index: 999;
}
</style>
