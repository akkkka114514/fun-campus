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
      <tm-form @submit="confirm" :margin="[0, 0]" ref="form" v-model="formData" :label-width="180">
        <tm-form-item :margin="[15, 0]" required label="活动标题" field="title" :rules="[{ required: true, message: '请输入活动标题' }]">
          <tm-input v-model="formData.title" showClear placeholder="请输入活动标题"></tm-input>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" required label="选择组织" field="team_id" :rules="[{ required: true, message: '请选择组织' }]">
          <dx-select-team v-model="formData.team_id"></dx-select-team>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" required label="活动分类" field="category_id" :rules="[{ required: true, message: '请选择活动分类' }]">
          <view @click="showCategory = true" class="input-select round-3" :class="{ 'no-select': !formData.category_id }"> {{ categoryText }}</view>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" required label="截止时间" field="closing_date" :rules="[{ required: true, message: '请选择报名截止时间' }]">
          <view @click="openSelectTime('closing_date')" class="input-select round-3" :class="{ 'no-select': !formData.closing_date }">
            {{ closing_date_text || '请选择报名截止时间' }}
          </view>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" required label="开始时间" field="start_date" :rules="[{ required: true, message: '请选择开始时间' }]">
          <view @click="openSelectTime('start_date')" class="input-select round-3" :class="{ 'no-select': !formData.start_date }"> {{ start_date_text || '请选择开始时间' }}</view>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" required label="结束时间" field="end_date" :rules="[{ required: true, message: '请选择结束时间' }]">
          <view @click="openSelectTime('end_date')" class="input-select round-3" :class="{ 'no-select': !formData.end_date }"> {{ end_date_text || '请选择结束时间' }}</view>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" required label="活动地址" field="address" :rules="[{ required: true, message: '请输入或点击地图定位' }]">
          <tm-input v-model="formData.address" showClear>
            <template #right>
              <tm-icon name="tmicon-location" color="#999999" :font-size="30" @click="chooseAddress"></tm-icon>
            </template>
          </tm-input>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" label="报名需填写" field="form_list">
          <view class="input-select round-3" :class="{ 'no-select': !activityStore.fieldList.length }" @click="openLink('others/publish/fields')">
            {{formListText}}
          </view>
        </tm-form-item>
        <!-- 第二版
          <tm-form-item :margin="[15, 0]" required label="报名费用" field="team_id" :rules="[{ required: true, message: '请选择组织' }]">
          <view class="input-select round-3" :class="{ 'no-select': !formData.end_date }"></view>
        </tm-form-item> -->
        <tm-form-item :margin="[15, 0]" required label="是否公开" field="is_public">
          <tm-switch v-model="formData.is_public" :defaultValue="formData.is_public"></tm-switch>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" required label="客服电话" field="mobile" :rules="[{ required: true, message: '请输入活动标题' }]">
          <tm-input v-model="formData.mobile" showClear></tm-input>
        </tm-form-item>
        <view class="mt-30 pl-30">活动介绍</view>
        <tm-form-item :margin="[15, 0]" required field="content" :rules="[{ required: true, message: '请输入活动介绍' }]">
          <dx-editor v-model="formData.content"></dx-editor>
        </tm-form-item>
        <tm-form-item :margin="[15, 0]" :border="false">
          <tm-button :margin="[15]" :shadow="0" :round="20" size="small" form-type="submit" block :label="formData.id ? '保存' : '立即发布'"></tm-button>
        </tm-form-item>
      </tm-form>
    </tm-sheet>
    <!--  -->
    <tm-picker v-model:show="showCategory" :columns="categoryList" mapKey="name" v-model="categoryIndex"></tm-picker>
    <!-- 时间 -->
    <tm-time-picker
      v-if="showDate"
      :showDetail="{
        year: true,
        month: true,
        day: true,
        hour: true,
        minute: true,
      }"
      v-model:show="showDate"
      v-model="dateSAva"
      :defaultValue="defaultDate"
      format="YYYY年MM月DD日 HH时mm分"
      v-model:model-str="dateStr"
    ></tm-time-picker>
  </tm-app>
</template>
<script setup lang="ts">
import { ref,watch,computed } from 'vue';
import {getCategory,upload,activitySave,myActivityInfo} from '@/common/index'
import {arrayToTree, openLink} from '@/common/tools'
import {useActivityStore} from '@/stores/activity'
import { onLoad } from '@dcloudio/uni-app';
import * as dayjs from "@/tmui/tool/dayjs/esm/index"

const categoryList = ref<any>([]);
const activityStore = useActivityStore();
const showCategory = ref(false);
const categoryIndex = ref<number[]>([]);
const categoryStr = ref('');
const categoryText = computed(() => {
  return categoryStr.value ? categoryStr.value : '请选择组织分类';
})
const formListText = computed(() => {
  if(activityStore.fieldList.length){
      let strArr = activityStore.fieldList.map((item:any) => item.title);
    return strArr.join('、');
  }else{
    return '请选择报名需填写'
  }
})
const showDate = ref(false);
const dateStr = ref('');
const dateSAva = ref<number>();
const defaultDate = ref();
const currentField = ref('');
const closing_date_text = ref('');
const start_date_text = ref('');
const end_date_text = ref('');
const formData = ref<any>({
  id: '',
  title: '',
  team_id: '',
  category_id: '',
  cover: '',
  address: '',
  lat: 0,
  lon: 0,
  closing_date: 0,
  start_date: 0,
  end_date: 0,
  is_public: true,
  form_list: [],
  mobile: '',
  content: '',
});
function openSelectTime(field:string){
  currentField.value = field;
  showDate.value = true;
  if(formData.value[field]){
    defaultDate.value = formData.value[field];
  }else{
    defaultDate.value = new Date().getTime();
  }
}
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
watch(categoryIndex, (val) => {
  formData.value.category_id = categoryList.value[val[0]]?.children[val[1]]?._id;
  categoryStr.value = categoryList.value[val[0]].name + '-' + categoryList.value[val[0]]?.children[val[1]]?.name;
});
watch(
  () => dateSAva.value,
  (val) => {
    console.log('dateSAva', val);
    switch (currentField.value) {
      case 'closing_date':
        closing_date_text.value = dateStr.value;
        formData.value.closing_date = new Date(val as any).getTime();
        break;
      case 'start_date':
        start_date_text.value = dateStr.value;
        formData.value.start_date = new Date(val as any).getTime();
        break;
      case 'end_date':
        end_date_text.value = dateStr.value;
        formData.value.end_date = new Date(val as any).getTime();
        break;
      default:
        break;
    }
  }
);
onLoad(async (e: any) => {
  const rest = await getCategory();
  if (rest.code === 1000) {
    categoryList.value = arrayToTree(rest.data);
  }
  if (e.id) {
    myActivityInfo({ id: e.id }).then(res => {
      if (res.code === 1000) {
        formData.value.id = e.id;
        formData.value.is_public = res.data.is_public;
        formData.value.title = res.data.title;
        formData.value.address = res.data.address;
        formData.value.mobile = res.data.mobile;
        formData.value.content = res.data.content;
        formData.value.cover = res.data.cover;
        formData.value.team_id = res.data.team_id;      
        formData.value.closing_date = res.data.closing_date;
        closing_date_text.value = dayjs.default(res.data.closing_date).format('YYYY年MM月DD日 HH时mm分');
        formData.value.start_date = res.data.start_date;
        start_date_text.value = dayjs.default(res.data.start_date).format('YYYY年MM月DD日 HH时mm分');
        formData.value.end_date = res.data.end_date;
        end_date_text.value = dayjs.default(res.data.end_date).format('YYYY年MM月DD日 HH时mm分');
        activityStore.setFieldList(res.data.form_list);
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
function confirm(e:any) {
  console.log('confirm',formData.value);
  if(e.validate){
    activitySave({
      ...e.data,
      form_list: activityStore.fieldList,
    }).then((res:any) => {
      uni.$tm.u.toast(res.message);
      if (res.code === 1000) {
        if (e.data.id) {
          uni.navigateBack();
        } else {
          setTimeout(() => {
            openLink('others/activity/manage?id=' + res.data)
          }, 1500)
        }
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
