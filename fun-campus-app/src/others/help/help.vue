<template>
  <tm-app>
    <tm-collapse>
      <tm-collapse-item v-for="(item,index) in list" :key="index" :title="`${index+1}、${item.title}`" :name="index">
        <view class="pa-24">
          <tm-text :font-size="28" _class="font-weight-b" :label="item.content"></tm-text>
        </view>
      </tm-collapse-item>
    </tm-collapse>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { indexHelp } from '@/common/index';
const list = ref<any>([]);
indexHelp().then(res => {
  if(res.code === 1000){
    list.value = res.data;
  }
})
</script>