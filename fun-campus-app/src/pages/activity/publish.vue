<template>
  <view class="publish-activity-container">
    <view class="header">
      <text class="title">发布活动</text>
    </view>
    
    <scroll-view class="form-container" scroll-y="true">
      <form @submit="onSubmit">
        <!-- 归属设置 -->
        <dx-belong-setting
          v-model="formData"
          :organizations="organizations"
          :colleges="colleges"
          :categories="categories"
          :collegeReviewers="collegeReviewers"
          :organizationReviewers="organizationReviewers"
          v-model:belongToType="belongToType"
          @belongToTypeChange="onBelongToTypeChange"
        />
        
        <!-- 活动封面 -->
        <view class="form-item">
          <view class="label">活动封面</view>
          <dx-cover-upload v-model="formData.coverImg" />
        </view>
        
        <!-- 基本信息 -->
        <dx-basic-info v-model="formData" />
        
        <!-- 参与设置 -->
        <dx-participation-setting
          v-model="formData"
          v-model:participationType="participationType"
          :grades="grades"
          :colleges="colleges"
          @participationTypeChange="onParticipationTypeChangeRadio"
        />
        
        <!-- 附件上传 -->
        <view class="form-item">
          <view class="label">附件上传</view>
          <dx-attachment-upload v-model="formData.attachment" />
        </view>

        <!-- 提交按钮 -->
        <view class="button-container">
          <button class="submit-button" form-type="submit">发布活动</button>
        </view>
      </form>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import {ref, onMounted} from 'vue';
import {
  initActivityPublishPage
} from "@/common/Api";
import dxBelongSetting from '@/components/dx-form-section/belong-setting.vue';
import dxBasicInfo from '@/components/dx-form-section/basic-info.vue';
import dxParticipationSetting from '@/components/dx-form-section/participation-setting.vue';
import dxCoverUpload from '@/components/dx-cover-upload/cover-upload.vue';
import dxAttachmentUpload from '@/components/dx-attachment-upload/attachment-upload.vue';

// 表单数据
const formData = ref({
  title: '',                    // 活动标题
  position: '',                 // 活动地点
  scoreCanGet: 0.0,                  // PU分
  enrollNumLimit: 0,            // 活动人数限制
  activityBelongToSchoolId: -1,      // 学校ID
  activityBelongToOrganizationId: -1,              // 组织ID
  activityBelongToCollegeId: -1,    // 院系ID
  description: '',              // 活动描述
  enrollNeedReview: false,         // 是否需要审核
  needSignOut: false,      // 是否需要签退
  attachment: '',               // 附件
  categoryId: -1,               // 活动分类ID
  coverImg: '',                    // 活动封面 - 用于提交fileKey
  activityManagerId: -1,          // 活动管理员ID
  enrollStartTime: '',           // 活动开始时间
  enrollEndTime: '',           // 活动结束时间
  activityStartTime: '',          // 活动开始时间
  activityEndTime: '',          // 活动结束时间
  signinStartTime: '',          // 签到开始时间
  signinEndTime: '',            // 签到结束时间
  initialReviewer:-1,            //初审人id
  canEnrollGradeIdList: [],       // 活动可报名的班级ID列表
  canEnrollCollegeIdList: [],      // 活动可报名的院系ID列表
});

const belongToType = ref('');
const participationType = ref('');

// 模拟院系数据
const colleges = ref([
  { id: 1, name: '计算机学院' },
  { id: 2, name: '电子信息学院' },
  { id: 3, name: '机械工程学院' },
  { id: 4, name: '经济管理学院' },
  { id: 5, name: '外国语学院' }
]);

// 模拟年级数据
const grades = ref([
  { id: 1, name: '2020级' },
  { id: 2, name: '2021级' },
  { id: 3, name: '2022级' },
  { id: 4, name: '2023级' },
  { id: 5, name: '2024级' }
]);

// 模拟组织/学院数据
const organizations = ref([
  { id: 1, name: '学生会' },
  { id: 2, name: '社团联合会' },
  { id: 3, name: '志愿者协会' },
  { id: 4, name: '科技创新社' }
]);

// 活动分类
const categories = ref([
  { id: 1, name: '学术讲座' },
  { id: 2, name: '文艺活动' },
  { id: 3, name: '体育竞赛' },
  { id: 4, name: '志愿服务' },
  { id: 5, name: '技能培训' }
]);

// 审核人列表 - 使用 Map 结构
const collegeReviewers = ref<Map<number, Array<{id: number, name: string}>>>(
    new Map()
);

const organizationReviewers = ref<Map<number, Array<{id: number, name: string}>>>(
    new Map()
);

// 活动归属类型选择改变事件
const onBelongToTypeChange = (e: any) => {
  if (!e.detail){
    return;
  }
  belongToType.value = e.detail.value;
  // 清除之前的选择
  formData.value.activityBelongToOrganizationId = -1;
  formData.value.activityBelongToCollegeId = -1;
  // 同时清除审核人选择
  formData.value.initialReviewer = -1;
};

// 参与类型改变事件
const onParticipationTypeChangeRadio = (e: any) => {
  if(!e.detail){
    return;
  }
  participationType.value = e.detail.value;
};

// 提交表单
const onSubmit = async () => {
  console.log('提交活动信息:', formData.value);
  
  // 实际提交验证和API调用逻辑
  if (!formData.value.title.trim()) {
    uni.showToast({
      title: '请输入活动标题',
      icon: 'none'
    });
    return;
  }
  
  if (!formData.value.position.trim()) {
    uni.showToast({
      title: '请输入活动地点',
      icon: 'none'
    });
    return;
  }
  
  if (formData.value.scoreCanGet < 0) {
    uni.showToast({
      title: 'PU分不能为负数',
      icon: 'none'
    });
    return;
  }
  
  if (formData.value.enrollNumLimit < 0 || formData.value.enrollNumLimit > 20000) {
    uni.showToast({
      title: '活动人数必须在0-20000之间',
      icon: 'none'
    });
    return;
  }
  
  if (!belongToType.value) {
    uni.showToast({
      title: '请选择活动归属',
      icon: 'none'
    });
    return;
  }
  
  if (belongToType.value === 'organization' && formData.value.activityBelongToOrganizationId === -1) {
    uni.showToast({
      title: '请选择组织',
      icon: 'none'
    });
    return;
  }
  if (belongToType.value === 'college' && formData.value.activityBelongToCollegeId === -1) {
    uni.showToast({
      title: '请选择学院',
      icon: 'none'
    });
    return;
  }
  
  if (!formData.value.description.trim()) {
    uni.showToast({
      title: '请输入活动描述',
      icon: 'none'
    });
    return;
  }
  
  if (!formData.value.categoryId) {
    uni.showToast({
      title: '请选择活动分类',
      icon: 'none'
    });
    return;
  }
  
  if (formData.value.initialReviewer === -1) {
    uni.showToast({
      title: '请选择审核人',
      icon: 'none'
    });
    return;
  }
  
  // 如果验证通过，提交数据
  uni.showLoading({
    title: '发布中...'
  });
  
  // 模拟API调用
  setTimeout(() => {
    uni.hideLoading();
    uni.showToast({
      title: '活动发布成功！',
      icon: 'success'
    });
  }, 1500);
};

onMounted(()=> {
  initActivityPublishPage().then((res)=>{
    console.log(res.data)
    colleges.value = res.data.data.collegeInfoVOList
    categories.value = res.data.data.activityCategoryVOList
    grades.value = res.data.data.gradeInfoVOList
    organizations.value = res.data.data.organizationInfoVOList
    
    // 将对象转换为Map，以便通过ID进行查找
    if (res.data.data.collegeReviewerList && typeof res.data.data.collegeReviewerList === 'object') {
      const collegeReviewerMap = new Map();
      Object.keys(res.data.data.collegeReviewerList).forEach(key => {
        // 转换username为name以适配picker组件
        const reviewersWithNames = res.data.data.collegeReviewerList[key].map(reviewer => ({
          id: reviewer.id,
          name: reviewer.username
        }));
        collegeReviewerMap.set(Number(key), reviewersWithNames);
      });
      collegeReviewers.value = collegeReviewerMap;
    }
    
    if (res.data.data.organizationReviewerList && typeof res.data.data.organizationReviewerList === 'object') {
      const organizationReviewerMap = new Map();
      Object.keys(res.data.data.organizationReviewerList).forEach(key => {
        // 转换username为name以适配picker组件
        const reviewersWithNames = res.data.data.organizationReviewerList[key].map(reviewer => ({
          id: reviewer.id,
          name: reviewer.username
        }));
        organizationReviewerMap.set(Number(key), reviewersWithNames);
      });
      organizationReviewers.value = organizationReviewerMap;
    }
  }).catch( error=> {
    console.log(error)
  })
  console.log('colleges',colleges.value)
})
</script>

<style lang="scss">
.publish-activity-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a1a, #2d2d2d);
  padding: 20rpx;

  .header {
    text-align: center;
    margin-bottom: 40rpx;

    .title {
      font-size: 48rpx;
      font-weight: bold;
      color: #ffffff;
      text-shadow: 2rpx 2rpx 4rpx rgba(0, 0, 0, 0.5);
    }
  }

  .form-container {
    background: #2d2d2d;
    border-radius: 20rpx;
    padding: 40rpx;
    box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.3);

    .form-item {
      margin-bottom: 40rpx;

      .label {
        font-size: 32rpx;
        font-weight: bold;
        color: #e0e0e0;
        margin-bottom: 20rpx;
      }
    }
  }

  .button-container {
    margin-top: 60rpx;
    padding: 0 40rpx 40rpx;

    .submit-button {
      width: 100%;
      height: 100rpx;
      background: linear-gradient(to right, #ffffff, #e0e0e0);
      color: #1a1a1a;
      border: none;
      border-radius: 50rpx;
      font-size: 36rpx;
      font-weight: bold;
      box-shadow: 0 10rpx 20rpx rgba(0, 0, 0, 0.4);

      &:active {
        transform: translateY(2rpx);
        box-shadow: 0 5rpx 10rpx rgba(0, 0, 0, 0.4);
      }
    }
  }
}
</style>