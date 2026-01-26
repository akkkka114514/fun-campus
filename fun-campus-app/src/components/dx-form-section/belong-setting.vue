<template>
  <view class="belong-setting-section">
    <!-- 组织或学院选择 -->
    <view class="form-item">
      <view class="label">活动归属</view>
      <radio-group @change="onBelongToTypeChange">
        <label class="radio-item">
          <radio value="organization" :checked="localBelongToType === 'organization'" color="#ff8c42" />
          <text>组织</text>
        </label>
        <label class="radio-item">
          <radio value="college" :checked="localBelongToType === 'college'" color="#ff8c42" />
          <text>学院</text>
        </label>
      </radio-group>
    </view>

    <!-- 组织/学院下拉框 -->
    <view class="form-item" v-if="localBelongToType">
      <view class="label">{{ localBelongToType === 'organization' ? '选择组织' : '选择学院' }}</view>
      <picker
          @change="onOrganizationOrCollegeChange"
          :value="organizationOrCollegeIndex"
          :range="localBelongToType === 'organization' ? organizations : colleges"
          range-key="name"
      >
        <view class="picker">{{
            organizationOrCollegeIndex >= 0 && getCurrentBelongTo() && getCurrentBelongTo()[organizationOrCollegeIndex] ?
                getCurrentBelongTo()[organizationOrCollegeIndex].name :
                `请选择${localBelongToType === 'organization' ? '组织' : '学院'}`
          }}</view>
      </picker>
    </view>

    <!-- 审核人 -->
    <view class="form-item">
      <view class="label">审核人</view>
      <picker @change="onReviewerChange" :value="reviewerIndex" :range="getCurrentReviewers() || []" range-key="name">
        <view class="picker">{{
            reviewerIndex >= 0 && getCurrentReviewers() && getCurrentReviewers()?.length > 0 && getCurrentReviewers()[reviewerIndex] ?
                (getCurrentReviewers()[reviewerIndex].name || '暂无') :
                '请选择审核人'
          }}</view>
      </picker>
    </view>

    <!-- 活动分类 -->
    <view class="form-item">
      <view class="label">活动分类</view>
      <picker @change="onCategoryChange" :value="localCategoryIndex" :range="categories" range-key="name">
        <view class="picker">{{
            localCategoryIndex >= 0 && categories[localCategoryIndex] ?
                categories[localCategoryIndex].name :
                '请选择活动分类'
          }}</view>
      </picker>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, defineEmits, defineProps, computed, watch } from 'vue';

interface Reviewer {
  id: number;
  name: string;
}

interface Category {
  id: number;
  name: string;
}

interface Organization {
  id: number;
  name: string;
}

interface College {
  id: number;
  name: string;
}

interface FormData {
  activityBelongToOrganizationId: number;
  activityBelongToCollegeId: number;
  initialReviewer: number;
  categoryId: number;
}

interface Props {
  modelValue: FormData;
  belongToType: string;
  organizations: Organization[];
  colleges: College[];
  categories: Category[];
  collegeReviewers: Map<number, Reviewer[]>;
  organizationReviewers: Map<number, Reviewer[]>;
}

interface Emits {
  (e: 'update:modelValue', value: FormData): void;
  (e: 'update:belongToType', value: string): void;
  (e: 'change', value: FormData): void;
  (e: 'belongToTypeChange', value: string): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const localFormData = ref<FormData>({...props.modelValue});
const localBelongToType = ref<string>(props.belongToType);

// 监听外部值的变化
watch(() => props.modelValue, (newValue) => {
  localFormData.value = {...newValue};
}, { deep: true });

watch(() => props.belongToType, (newValue) => {
  localBelongToType.value = newValue;
});

// 监听内部值的变化并同步到父组件
watch(localFormData, (newValue) => {
  emit('update:modelValue', {...newValue});
  emit('change', {...newValue});
}, { deep: true });

watch(localBelongToType, (newValue) => {
  emit('update:belongToType', newValue);
  emit('belongToTypeChange', newValue);
}, { immediate: true });

// 计算属性：当前选中的组织或学院索引
const organizationOrCollegeIndex = computed(() => {
  if (localBelongToType.value === 'organization' && localFormData.value.activityBelongToOrganizationId !== -1) {
    return props.organizations.findIndex(item => item.id === localFormData.value.activityBelongToOrganizationId);
  } else if (localBelongToType.value === 'college' && localFormData.value.activityBelongToCollegeId !== -1) {
    return props.colleges.findIndex(item => item.id === localFormData.value.activityBelongToCollegeId);
  }
  return -1;
});

// 计算属性：当前选中的分类索引
const localCategoryIndex = computed(() => {
  if (!localFormData.value.categoryId) return -1;
  return props.categories.findIndex(item => item.id === localFormData.value.categoryId);
});

// 计算属性：当前选中的审核人索引
const reviewerIndex = computed(() => {
  if (localFormData.value.initialReviewer === -1) return -1;
  const currentReviewerList = getCurrentReviewers();
  if (!currentReviewerList) return -1;
  return currentReviewerList.findIndex(item => item.id === localFormData.value.initialReviewer);
});

// 获取当前组织/学院列表
const getCurrentBelongTo = () => {
  return localBelongToType.value === 'organization' ? props.organizations : props.colleges;
};

// 获取当前审核人列表
const getCurrentReviewers = (): Reviewer[] => {
  if (localBelongToType.value === 'organization') {
    if (props.organizationReviewers instanceof Map) {
      const reviewers = props.organizationReviewers.get(localFormData.value.activityBelongToOrganizationId);
      return reviewers || [];
    }
  }
  if (localBelongToType.value === 'college') {
    if (props.collegeReviewers instanceof Map) {
      const reviewers = props.collegeReviewers.get(localFormData.value.activityBelongToCollegeId);
      return reviewers || [];
    }
  }
  return [];
};

// 活动归属类型选择改变事件
const onBelongToTypeChange = (e: any) => {
  const newValue = e.detail.value;
  localBelongToType.value = newValue;
  
  // 清除之前的选择
  if (newValue === 'organization') {
    localFormData.value.activityBelongToOrganizationId = -1;
    localFormData.value.activityBelongToCollegeId = -1;
  } else if (newValue === 'college') {
    localFormData.value.activityBelongToOrganizationId = -1;
    localFormData.value.activityBelongToCollegeId = -1;
  }
  
  // 同时清除审核人选择
  localFormData.value.initialReviewer = -1;
};

// 组织或学院选择改变事件
const onOrganizationOrCollegeChange = (e: any) => {
  const index = parseInt(e.detail.value);
  const currentList = getCurrentBelongTo();
  if (index >= 0 && currentList[index]) {
    if (localBelongToType.value === 'organization') {
      localFormData.value.activityBelongToOrganizationId = currentList[index].id;
      // 重置审核人选择
      localFormData.value.initialReviewer = -1;
    } else if (localBelongToType.value === 'college') {
      localFormData.value.activityBelongToCollegeId = currentList[index].id;
      // 重置审核人选择
      localFormData.value.initialReviewer = -1;
    }
  }
};

// 分类选择改变事件
const onCategoryChange = (e: any) => {
  const index = parseInt(e.detail.value);
  if (index >= 0 && props.categories[index]) {
    localFormData.value.categoryId = props.categories[index].id;
  }
};

// 审核人选择改变事件
const onReviewerChange = (e: any) => {
  const index = parseInt(e.detail.value);
  const currentReviewerList = getCurrentReviewers();
  if (currentReviewerList && index >= 0 && currentReviewerList[index]) {
    localFormData.value.initialReviewer = currentReviewerList[index].id;
  }
};
</script>

<style lang="scss" scoped>
.form-item {
  margin-bottom: 40rpx;

  .label {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }

  .picker {
    width: 100%;
    height: 80rpx;
    border: 2rpx solid #e0e0e0;
    border-radius: 12rpx;
    padding: 0 20rpx;
    font-size: 30rpx;
    color: #333;
    display: flex;
    align-items: center;
    background: #fafafa;
  }

  .radio-item {
    display: inline-block;
    margin-right: 40rpx;
    font-size: 30rpx;
    color: #333;
    align-items: center;

    radio {
      transform: scale(0.8);
    }
  }
}
</style>