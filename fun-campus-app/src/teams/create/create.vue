<template>
  <tm-app>
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]" :round="3" :shadow="2">
      <view class="cover" @click="uploadCover">
        <image :src="formData.cover || '../../static/image.jpg'" mode="aspectFill"></image>
        <view class="upload round-3 flex-row-center-center">
          <tm-icon name="tmicon-plus" color="white" :font-size="25"></tm-icon>
          <text class="ml-5">上传封面(800*500)</text>
        </view>
      </view>
      <tm-form @submit="confirm" :margin="[0, 0]" ref="form" v-model="formData" :label-width="150">
        <tm-form-item :margin="[15, 0]" required label="组织名称" field="title" :rules="[{ required: true, message: '请输入组织名称' }]">
          <tm-input v-model="formData.title" showClear></tm-input>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" required label="组织分类" field="category_id" :rules="[{ required: true, message: '请选择活动分类' }]">
          <view @click="showCategory = true" class="input-select round-3" :class="{ 'no-select': !formData.category_id }"> {{ categoryText }}</view>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" required label="LOGO" field="logo" :rules="[{ required: true, message: '请上传LOGO' }]">
          <dx-upload v-model="formData.logo"></dx-upload>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" required label="组织地址" field="address" :rules="[{ required: true, message: '请输入或点击地图定位' }]">
          <tm-input v-model="formData.address" showClear>
            <template #right>
              <tm-icon name="tmicon-location" color="#999999" :font-size="30" @click="chooseAddress"></tm-icon>
            </template>
          </tm-input>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" label="是否公开" field="is_public">
          <tm-switch v-model="formData.is_public"></tm-switch>
        </tm-form-item>
        <tm-form-item
          :margin="[15, 0]"
          required
          label="联系电话"
          field="mobile"
          :rules="[{ required: true, message: '请输入联系人电话' }, {
          validator: (val: string) => {
            if (!/^1[3456789]\d{9}$/.test(val)) {
              return false
            }
            return true
          },
          message: '请输入正确的手机号码'
        }]"
        >
          <tm-input v-model="formData.mobile" showClear></tm-input>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" required label="联系姓名" field="name" :rules="[{ required: true, message: '请输入联系人姓名' }]">
          <tm-input v-model="formData.name" showClear></tm-input>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" label="组织介绍" field="content">
          <tm-input type="textarea" :inputPadding="[24, 15]" :height="100" v-model="formData.content" showClear></tm-input>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" :border="false">
          <tm-button :margin="[15]" :shadow="0" :round="20" size="small" form-type="submit" block :label="formData.id ? '保存' : '立即创建'"></tm-button>
        </tm-form-item>
      </tm-form>
    </tm-sheet>
    <!--  -->
    <tm-picker v-model:show="showCategory" :columns="categoryList" mapKey="name" v-model="categoryIndex"></tm-picker>
  </tm-app>
</template>
<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { getCategory, upload, saveTeam, myTeamDetail } from '@/common/index'
import { arrayToTree } from '@/common/tools'
import { openLink } from '@/common/tools';
import { onLoad } from '@dcloudio/uni-app';
const categoryList = ref<any>([]);
const showCategory = ref(false);
const categoryIndex = ref<number[]>([]);
const categoryStr = ref('');
const categoryText = computed(() => {
  return categoryStr.value ? categoryStr.value : '请选择组织分类';
})
const formData = ref({
  cover: '',
  title: '',
  category_id: '',
  logo: '',
  is_public: false,
  address: '',
  mobile: '',
  name: '',
  content: '',
  lat: 0,
  lon: 0,
  id: ''
});
watch(categoryIndex, (val) => {
  formData.value.category_id = categoryList.value[val[0]]?.children[val[1]]?._id;
  categoryStr.value = categoryList.value[val[0]].name + '-' + categoryList.value[val[0]]?.children[val[1]]?.name;
});
onLoad(async (e: any) => {
  const rest = await getCategory();
  if (rest.code === 1000) {
    categoryList.value = arrayToTree(rest.data);
  }
  if (e.id) {
    myTeamDetail({ id: e.id }).then(res => {
      if (res.code === 1000) {
        formData.value.id = e.id;
        formData.value.is_public = res.data.is_public;
        formData.value.title = res.data.title;
        formData.value.logo = res.data.logo;
        formData.value.address = res.data.address;
        formData.value.mobile = res.data.mobile;
        formData.value.name = res.data.name;
        formData.value.content = res.data.content;
        formData.value.cover = res.data.cover;
        categoryList.value.map((item: any, index: number) => {
          item.children.map((row: any, rowIndex: number) => {
            if (row._id === res.data.category_id) {
              categoryIndex.value = [index, rowIndex];
            }
          })
        })
      }
    })
  }
})
function uploadCover() {
  uni.chooseImage({
    count: 1,
    success(res: any) {
      upload(res.tempFiles[0]).then((url: any) => {
        formData.value.cover = url;
      });
    },
  });
}
function chooseAddress() {
  uni.chooseLocation({
    success: function (res) {
      formData.value.address = res.address;
      formData.value.lat = res.latitude;
      formData.value.lon = res.longitude;
    }
  });
}
function confirm(e: any) {
  if (e.validate) {
    if (!e.data.cover) {
      uni.$tm.u.toast('请上传封面');
      return;
    }
    saveTeam(e.data).then(res => {
      if (res.code === 1000) {
        uni.$tm.u.toast(res.message);
        if (e.data.id) {
          uni.navigateBack();
        } else {
          setTimeout(() => {
            openLink('/teams/manage/manage?id=' + res.data)
          }, 1500)
        }
      } else {
        uni.$tm.u.toast(res.message);
      }
    })

  }
}
</script>
<style lang="scss" scoped>
.cover {
  position: relative;
  width: 100%;

  image {
    height: 450rpx;
    width: 100%;
  }

  .upload {
    position: absolute;
    bottom: 30rpx;
    right: 30rpx;
    background: rgba(0, 0, 0, 0.3);
    color: #ffffff;
    padding: 10rpx 20rpx;
  }
}
</style>
