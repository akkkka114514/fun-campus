<template>
  <tm-app>
    <tm-sheet :margin="[0, 0]" :padding="[30, 30]" v-if="list.length">
      <tm-cell :rightText="`${item.count}张`" :margin="[0, 0]" :bottomBorder="true" @click="openLink('teams/images/detail?id='+item._id)" v-for="(item,index) in list" :key="index">
        <template v-slot:title>
          <view class="flex-col-top-center" @click.stop="edit(item)">
            <text class="mr-15">{{item.name}}</text>
            <tm-icon name="tmicon-edit" size="20" color="#999"></tm-icon>
          </view>
        </template>
      </tm-cell>
    </tm-sheet>
    <view class="mt-50 pt-50">
      <tm-result :showBtn="false" title="暂无数据～" subTitle=" " v-if="!list.length && !loading"></tm-result>
    </view>
    <view class="footer py-10 px-50">
      <tm-button :round="20" type="primary" block label="新建相册" @click="showWin = true"></tm-button>
    </view>
    <!--  -->
    <tm-modal ref="modal" :height="350" :border="0" text okColor="blue" cancelColor="blue" okText="确定" linear="bottom" title="新建相册" @ok="submit" v-model:show="showWin">
      <view class="py-30">
        <tm-input color="grey-4" v-model.lazy="photoName" placeholder="请输入相册名称"></tm-input>
      </view>
    </tm-modal>
  </tm-app>
</template>
<script lang="ts" setup>
import { openLink } from '@/common/tools';
import { ref } from 'vue';
import {photoList,photoSave} from '@/common/index'
import { onLoad } from '@dcloudio/uni-app';
const list = ref<any>([]);
const showWin = ref(false);
const photoName = ref('');
const currentRow = ref<any>({});
const loading = ref(false);
//
function getPhotoList(){
  loading.value = true;
  photoList({
      team_id:currentRow.value.team_id
    }).then(res=>{
      if(res.code === 1000){
        list.value = res.data;
      }
    }).finally(()=>{
      loading.value = false;
    })
}
//
onLoad((e:any)=>{
  if(e.team_id){
    currentRow.value.team_id = e.team_id;
    getPhotoList();

  }
})
function edit(row:any) {
  currentRow.value = row;
  photoName.value = row.name;
  showWin.value = true;
}
function submit(){
  if(!photoName.value){
    uni.$tm.u.toast('请输入相册名称');
    return;
  }
  const param:any = {
    name:photoName.value,
    team_id:currentRow.value.team_id
  }
  if(currentRow.value._id){
    param.id = currentRow.value._id;
  }
  photoSave(param).then(res=>{
    if(res.code === 1000){
      uni.$tm.u.toast(res.message);
      showWin.value = false;
      getPhotoList()
    }
  }).finally(()=>{
    photoName.value = '';
  })
}
</script>
<style lang="scss" scoped>
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  box-shadow: 0 0 10rpx rgba(0, 0, 0, 0.1);
}
</style>
