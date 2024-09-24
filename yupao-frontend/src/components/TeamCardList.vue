<script setup lang="ts">
import TeamType from "../models/team";
import 'vant/es/toast/style';
import {showFailToast, showSuccessToast} from "vant";
import {teamStatusEnum} from "../constants/team.ts";
import {onMounted, ref, watchEffect} from "vue";
import {useRouter} from "vue-router";
import {getCurrentUser} from "../service/user.ts";
import myAxios from "../plugins/myAxios.ts";

interface TeamCardListProps {
  teamList: TeamType[];
}

// props 接收父组件传递的 teamList
// withDefaults 函数用于设置默认值
const props = withDefaults(defineProps<TeamCardListProps>(), {
  teamList: [],
});

const showPasswordDialog = ref(false);  // 密码弹窗
const password = ref('');
const joinTeamId = ref(0);  // 加入队伍的 id
const currentUser = ref();

const router = useRouter();

onMounted(async () => {
  currentUser.value = await getCurrentUser();
})

const preJoinTeam = (team: TeamType) => {
  joinTeamId.value = team.id; // 保存当前选择的队伍 id
  if (team.status === 0) {
    doJoinTeam()
  } else {
    showPasswordDialog.value = true;
  }
}

const doJoinCancel = () => {
  joinTeamId.value = 0;
  password.value = '';
}

/**
 * 加入队伍
 */
const doJoinTeam = async () => {
  if (!joinTeamId.value) {  // 未选择队伍
    return;
  }
  const res = await myAxios.post('/team/join', {
    teamId: joinTeamId.value,
    password: password.value
  });
  if (res?.code === 0) {
    showSuccessToast('加入成功');
    doJoinCancel();
  } else {
    showFailToast('加入失败' + (res.description ? `，${res.description}` : ''));
  }
  location.reload();   // 刷新页面
}

/**
 * 跳转至更新队伍页
 * @param id
 */
const doUpdateTeam = (id: number) => {
  router.push({
    path: '/team/update',
    query: {
      id,
    }
  })
}

/**
 * 退出队伍
 * @param id
 */
const doQuitTeam = async (id: number) => {
  const res = await myAxios.post('/team/quit', {
    teamId: id
  });
  if (res?.code === 0) {
    showSuccessToast('操作成功');
    // 刷新页面
    location.reload();
  } else {
    showFailToast('操作失败' + (res.description ? `，${res.description}` : ''));
  }
}

/**
 * 解散队伍
 * @param id
 */
const doDeleteTeam = async (id: number) => {
  const res = await myAxios.post('/team/delete', {
    id,
  });
  if (res?.code === 0) {
    showSuccessToast('操作成功');
  } else {
    showFailToast('操作失败' + (res.description ? `，${res.description}` : ''));
  }
}

/**
 * 格式化日期时间
 * 2025-05-20T18:00:00.000+00:00 -> 2025-05-20 18:00
 * @param dateTimeString
 */
const formatDateTime = (dateTimeString) => {
  const date = new Date(dateTimeString);
  const formattedDate = date.toLocaleDateString('zh-CN', {year: 'numeric', month: '2-digit', day: '2-digit'}).replace(/\//g, '-');
  const formattedTime = date.toLocaleTimeString('zh-CN', {hour: '2-digit', minute: '2-digit', hour12: false});
  return `${formattedDate} ${formattedTime}`;
};

</script>

<template>
  <div class="team-card-list">
    <van-card
        v-for="team in props.teamList"
        :thumb="'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
        :desc="team.description"
        :title="`${team.name}`"
    >
      <template #tags>
        <van-tag plain type="danger" style="margin-right: 8px; margin-top: 8px">
          {{
            teamStatusEnum[team.status]
          }}
        </van-tag>
      </template>
      <template #bottom>
        <div>
          {{ `队伍人数: ${team.hasJoinNum}/${team.maxNum}` }}
        </div>
        <div v-if="team.expireTime">
          {{ '过期时间: ' + formatDateTime(team.expireTime) }}
        </div>
        <div>
          {{ '创建时间: ' + formatDateTime(team.createTime) }}
        </div>
      </template>
      <template #footer>
        <van-button size="small" type="primary" v-if="team.userId !== currentUser?.id && !team.hasJoin" plain
                    @click="preJoinTeam(team)">
          加入队伍
        </van-button>
        <van-button v-if="team.userId === currentUser?.id" size="small" plain
                    @click="doUpdateTeam(team.id)">更新队伍
        </van-button>
        <!-- 仅加入队伍可见 -->
        <van-button v-if="team.userId !== currentUser?.id && team.hasJoin" size="small" plain
                    @click="doQuitTeam(team.id)">退出队伍
        </van-button>
        <van-button v-if="team.userId === currentUser?.id" size="small" type="danger" plain
                    @click="doDeleteTeam(team.id)">解散队伍
        </van-button>
      </template>
    </van-card>
    <van-dialog v-model:show="showPasswordDialog" title="请输入密码" show-cancel-button @confirm="doJoinTeam"
                @cancel="doJoinCancel">
      <van-field v-model="password" placeholder="请输入密码"/>
    </van-dialog>
  </div>
</template>

<style scoped>

</style>
