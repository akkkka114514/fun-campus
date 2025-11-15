<template>
  <tm-app color="white">
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]">
      <view class="list pt-30">
        <view class="item" @click="uploadImage">
          <view class="img upload round-3">
            <tm-icon name="tmicon-plus" color="#999999" :font-size="35"></tm-icon>
            <view class="tips">上传图片</view>
          </view>
        </view>
        <view class="item" v-for="(item, index) in list" :key="index">
          <image class="img" mode="aspectFill" @click="previewImage(index)" :src="item"></image>
          <view class="close">
            <tm-icon name="tmicon-times-circle-fill" color="#fe1b00" @click="delIndex = index, showWin = true"></tm-icon>
          </view>
        </view>
      </view>
    </tm-sheet>
    <tm-modal title="温馨提示" content="您确认删除当前图片嘛？" :height="300" okText="确定" v-model:show="showWin"
      @ok="deleteConfirm"></tm-modal>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { multiUpload, photoInfo, photoDel, photoUpdate } from '@/common/index'
import { onLoad } from '@dcloudio/uni-app';
const list = ref<any>([]);
const id = ref<any>('');
const showWin = ref(false);
const delIndex = ref(0);
function getInfo() {
  photoInfo({
    id: id.value
  }).then((res: any) => {
    if (res.code === 1000) {
      list.value = res.data;
    }
  })
}
onLoad((e: any) => {
  if (e.id) {
    id.value = e.id;
    getInfo();
  }
})
function previewImage(index: number) {
  uni.previewImage({
    urls: list.value,
    current: list.value[index],
  });
}
function uploadImage() {
  uni.chooseImage({
    count: 9, //默认9
    sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
    sourceType: ['album', 'camera'], //从相册选择
    success: async function (result: any) {
      uni.showLoading({
        title: '上传中...',
        mask: true,
      });
      const list = await multiUpload(result.tempFiles);
      const res = await photoUpdate({
        id: id.value,
        list
      })
      uni.hideLoading();
      if (res.code === 1000) {
        uni.$tm.u.toast('上传成功');
        getInfo();
      } else {
        uni.$tm.u.toast('上传失败');
      }
    }
  });
}
function deleteConfirm() {
  photoDel({
    id: id.value,
    index: delIndex.value
  }).then(res => {
    if (res.code === 1000) {
      uni.$tm.u.toast('删除成功');
      getInfo();
    } else {
      uni.$tm.u.toast('删除失败');
    }
  })
}
</script>
<style lang="scss" scoped>
.list {
  display: flex;
  flex-wrap: wrap;

  .item {
    width: calc((100% - 90rpx) / 2);
    margin-left: 30rpx;
    margin-bottom: 30rpx;
    position: relative;

    .img {
      width: 100%;
      height: 300rpx;
    }

    .close {
      position: absolute;
      top: 10rpx;
      right: 10rpx;
      z-index: 100;
      background: #ffffff;
      border-radius: 50%;
      padding: 0;
    }

    .upload {
      border: 1px solid #ccc;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;

      .tips {
        font-size: 26rpx;
        color: #999999;
        margin-top: 10rpx;
      }
    }
  }
}
</style>
