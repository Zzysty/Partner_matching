<script setup lang="js">
import {useRouter} from 'vue-router'
import 'vant/es/toast/style';
import {ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import 'vant/es/notify/style';
import {showFailToast, showSuccessToast} from "vant";

const router = useRouter()

// 展示日期选择器
const showPicker = ref(false);
const minDate = new Date();

const currentDate = ref([]);
const currentTime = ref([]);

const onConfirm = () => {
  // 格式化日期 2024-10-10 08:00:00，结合currentDate和currentTime
  const date = new Date(`${currentDate.value.join('-')} ${currentTime.value.join(':')}`);
  addTeamData.value.expireTime = date.toISOString();  // 2024-10-10T08:00:00.000Z
  showPicker.value = false;
};

const initFormData = {
  "name": "",
  "description": "",
  "expireTime": null,
  "maxNum": 3,
  "password": "",
  "status": 0,
}
// 需要用户填写的表单数据
const addTeamData = ref({...initFormData})

// 提交
const onSubmit = async () => {
  const postData = {
    ...addTeamData.value,
    status: Number(addTeamData.value.status)
  }
  // todo 前端参数校验
  if (!addTeamData.value.name) {

  }
  const res = await myAxios.post("/team/add", postData);
  if (res?.code === 0 && res.data){
    showSuccessToast('添加成功');
    router.push({
      path: '/team',
      replace: true,
    });
  } else {
    showFailToast('添加失败');
  }
}

</script>

<template>
  <div id="teamAddPage">
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
            :rules="[{ required: true, message: '请输入队伍描述' }]"
        />

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
<!--        <van-popup v-model:show="showPicker" position="bottom">-->
<!--          <van-date-picker @confirm="onConfirm" @cancel="showPicker = false"-->
<!--                           title="请选择过期时间" :min-date="minDate"/>-->
<!--        </van-popup>-->

        <van-field name="stepper" label="最大人数">
          <template #input>
            <van-stepper v-model="addTeamData.maxNum" max="10" min="3"/>
          </template>
        </van-field>
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
