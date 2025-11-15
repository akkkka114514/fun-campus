<template>
  <view class="input-select round-3" :class="{ 'no-select': !teamTitle }" @click="showWin = true">{{teamTitle||'请选择组织'}}</view>
  <!--  -->
  <tm-drawer ref="calendarView" placement="bottom" v-model:show="showWin" @ok="confirm">
    <tm-radio-group v-model="radio">
      <view class="item flex" v-for="(item,index) in list" :key="index">
        <view class="pl-15">
          <tm-radio :value="item._id"></tm-radio>
        </view>
        <view class="flex flex-between flex-1" @click="radio=item._id">
          <view class="flex">
            <tm-avatar :size="50" :img="item.logo"></tm-avatar>
            <view class="title ml-15 text-overflow-1">{{item.title}}</view>
          </view>
          <view class="pr-30">
            <tm-icon name="tmicon-angle-right" size="20" color="#999"></tm-icon>
          </view>
        </view>
      </view>
    </tm-radio-group>
  </tm-drawer>
</template>
<script setup lang="ts">
import { ref,watch } from 'vue';
import {myTeamList} from '@/common/index'
const props = defineProps<{
  modelValue: any;
}>();
const emit = defineEmits(['update:modelValue']);
const showWin = ref(false);
const radio = ref('');
const teamTitle = ref('');
const list = ref<any>([]);
myTeamList({limit:500}).then(res=>{
  if(res.code === 1000){
    list.value = res.data;
    teamTitle.value = res.data.find((item:any)=>item._id === props.modelValue)?.title;
  }
})
watch(() => props.modelValue, (val) => {
  radio.value = val;
  teamTitle.value = list.value.find((item:any)=>item._id === val)?.title;
})
function confirm(){
  const item = list.value.find((item:any)=>item._id === radio.value);
  teamTitle.value = item?.title;
  emit('update:modelValue',radio.value);
  showWin.value = false;
}
</script>
<style lang="scss" scoped>
.item{
    width: 100%;
    padding: 15rpx 0;
    border-top: 1px solid #e8e8e8;
}
.flex{
  align-items: center;
}
</style>
