<template>
  <tm-app color="white">
    <tm-sheet :margin="[0,0]">
      <tm-cell showAvatar :avatar="item.user.avatar" :border="1" :bottomBorder="true" :margin="[0, 0]" :titleFontSize="30" :title="item.user.nickname" v-for="(item,index) in list" :key="index">
        <template v-slot:right>
          <tm-tag
            text
            :border="1"
            borderStyle="dashed"
            :shadow="0"
            :color="item.status == 1?'green':'red'"
            size="xs"
            :label="item.status == 1?'审核成功':item.status == 2?'审核拒绝':'等待审核'"
          >
          </tm-tag>
        </template>
      </tm-cell>
      <view class="mt-50 pt-50" v-if="!list.length && !loading">
        <tm-result :showBtn="false" title="暂无数据～" subTitle=" "></tm-result>
      </view>
    </tm-sheet>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import {applyList} from '@/common/index'
import { onLoad } from '@dcloudio/uni-app';
const list = ref<any>([]);
const loading = ref<boolean>(false)
onLoad((e:any) => {
 if(e.id){
  loading.value = true
  applyList({id:e.id}).then(res => {
    if(res.code === 1000){
      list.value = res.data;
    }
  }).finally(()=>{
    loading.value = false
  })
 }
})
</script>
<style lang="scss" scoped></style>
