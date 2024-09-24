<script setup lang="js">
import {useRoute, useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import 'vant/es/toast/style';
import {showFailToast, showSuccessToast} from "vant";

const router = useRouter();
const route = useRoute();

const currentDate = ref([]);
const currentTime = ref([]);

const onConfirm = () => {
  // 格式化日期 2024-10-10 08:00:00，结合currentDate和currentTime
  const date = new Date(`${currentDate.value.join('-')} ${currentTime.value.join(':')}`);
  addTeamData.value.expireTime = date.toISOString();  // 2024-10-10T08:00:00.000Z
  showPicker.value = false;
};

// 展示日期选择器
const showPicker = ref(false);

const minDate = new Date();

const id = route.query.id;

// 需要用户填写的表单数据
const addTeamData = ref({})

// 获取之前的队伍信息
onMounted(async () => {
  if (id <= 0) {
    showFailToast('加载队伍失败');
    return;
  }
  const res = await myAxios.get("/team/get", {
    params: {
      id,
    }
  });
  if (res?.code === 0) {
    addTeamData.value = res.data;
  } else {
    showFailToast('加载队伍失败，请刷新重试');
  }}
)

// 提交
const onSubmit = async () => {
  const postData = {
    ...addTeamData.value,
    status: Number(addTeamData.value.status)
  }
  // todo 前端参数校验
  const res = await myAxios.post("/team/update", postData);
  if (res?.code === 0 && res.data){
    showSuccessToast('更新成功');
    router.push({
      path: '/team',
      replace: true,
    });
  } else {
    showFailToast('更新失败');
  }
}

</script>

<template>
  <div id="teamUpdatePage">
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            v-model="addTeamData.name"
            name="name"
            label="队伍名"
            placeholder="请输入队伍名"
            :rules="[{ required: true, message: '请输入队伍名' }]"
        />
        <van-field
            v-model="addTeamData.description"
            rows="4"
            autosize
            label="队伍描述"
            type="textarea"
            placeholder="请输入队伍描述"
        />
<!--        <van-field-->
<!--            is-link-->
<!--            readonly-->
<!--            name="datetimePicker"-->
<!--            label="过期时间"-->
<!--            :placeholder="addTeamData.expireTime ?? '点击选择过期时间'"-->
<!--            @click="showPicker = true"-->
<!--        />-->
<!--        <van-popup v-model:show="showPicker" position="bottom">-->
<!--          <van-datetime-picker-->
<!--              v-model="addTeamData.expireTime"-->
<!--              @confirm="showPicker = false"-->
<!--              type="datetime"-->
<!--              title="请选择过期时间"-->
<!--              :min-date="minDate"-->
<!--          />-->
<!--        </van-popup>-->
        <van-field
            v-model="addTeamData.expireTime"
            is-link
            readonly
            name="datePicker"
            label="时间选择"
            placeholder="点击选择过期时间"
            @click="showPicker = true"
        />
        <van-popup v-model:show="showPicker" position="bottom">
          <van-picker-group
              title="过期时间"
              :tabs="['选择日期', '选择时间']"
              @confirm="onConfirm"
              @cancel="showPicker = false"
          >
            <van-date-picker v-model="currentDate" :min-date="minDate"/>
            <van-time-picker v-model="currentTime" />
          </van-picker-group>
        </van-popup>
        <van-field name="radio" label="队伍状态">
          <template #input>
            <van-radio-group v-model="addTeamData.status" direction="horizontal">
              <van-radio name="0">公开</van-radio>
              <van-radio name="1">私有</van-radio>
              <van-radio name="2">加密</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field
            v-if="Number(addTeamData.status) === 2"
            v-model="addTeamData.password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入队伍密码"
            :rules="[{ required: true, message: '请填写密码' }]"
        />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
  </div>

</template>

<style scoped>

</style>
