<script setup lang="ts">
import {computed, ref} from 'vue';
import {useRouter} from "vue-router";

const searchText = ref('');

const initTagList = [
  {
    text: '编程语言',
    children: [
      {text: 'java', id: 7},
      {text: 'C', id: 8},
      {text: 'python', id: 9}
    ],
  },
  {
    text: '性别',
    children: [
      {text: '男', id: 1},
      {text: '女', id: 2},
    ],
  },
  {
    text: '年级',
    children: [
      {text: '大一', id: 3},
      {text: '大二', id: 4},
      {text: '大三', id: 5},
      {text: '大四', id: 6}
    ],
  }
];

// 根据id查找标签文本的函数
const findTagTextById = (id: number) => {
  for (const group of initTagList) {
    for (const tag of group.children) {
      if (tag.id === id) {
        return tag.text;
      }
    }
  }
  return '';
};

let tagList = ref(initTagList);

const onSearch = () => {
  tagList.value = initTagList.map((parentTag) => {
    const tempChildren = [...parentTag.children]   // 防止修改原数组
    const tempParent = {...parentTag}
    tempParent.children = tempChildren.filter(item => item.text.includes(searchText.value))
    return tempParent;
  });
};
const onCancel = () => {
  searchText.value = '';
  tagList.value = initTagList;
  activeIds.value = [];
};

// 取消选择标签
const doClose = (tag: any) => {
  activeIds.value = activeIds.value.filter((item) => item !== tag);
};

const activeIds = ref([]);
const activeIndex = ref(0);

// 计算属性，将activeIds中的每个id映射到其对应的文本
const activeTags = computed(() => activeIds.value.map(findTagTextById));

/**
 * 执行搜索
 */
const router = useRouter()
const doSearchResult = () => {
  router.push({
    path: '/user/list',
    query: {
      tags: activeTags.value
    }
  })
}
</script>

<template>
  <form action="/">
    <van-search
        v-model="searchText"
        show-action
        placeholder="请输入要搜索的标签"
        @search="onSearch"
        @cancel="onCancel"
    />
  </form>
  <van-divider>已选标签</van-divider>
  <div v-if="activeIds.length === 0">请在下方选择标签</div>
  <van-row gutter="16" style="padding: 10px;">
    <van-col v-for="tag in activeTags" style="padding: 10px;">
      <van-tag closeable size="medium" type="primary" @close="doClose(tag)">
        {{ tag }}
      </van-tag>
    </van-col>
  </van-row>
  <van-divider>选择标签</van-divider>
  <van-tree-select
      v-model:active-id="activeIds"
      v-model:main-active-index="activeIndex"
      :items="tagList"
  />
<!--  <van-divider>提交</van-divider>-->
  <div class="floating-button">
    <van-button round icon="search" type="primary" @click="doSearchResult()">搜索</van-button>
  </div>
</template>

<style scoped>
.floating-button {
  position: fixed;
  right: 20px;
  bottom: 70px;
  z-index: 1000;
}
</style>
