<template>
  <view class="basic-info-section">
    <!-- 活动标题 -->
    <view class="form-item">
      <view class="label">活动标题</view>
      <input 
        class="input" 
        v-model="localFormData.title" 
        placeholder="请输入活动标题"
        maxlength="50"
        @input="onTitleInput"
      />
    </view>

    <!-- 活动地点 -->
    <view class="form-item">
      <view class="label">活动地点</view>
      <input 
        class="input" 
        v-model="localFormData.position"
        placeholder="请输入活动地点"
        maxlength="100"
        @input="onPositionInput"
      />
    </view>

    <!-- 活动人数 -->
    <view class="form-item">
      <view class="label">活动人数</view>
      <input 
        class="input" 
        v-model.number="localFormData.enrollNumLimit" 
        type="number"
        placeholder="请输入活动人数限制(0-20000)"
        @input="onEnrollNumLimitInput"
      />
    </view>

    <!-- PU分 -->
    <view class="form-item">
      <view class="label">获得PU分</view>
      <input 
        class="input" 
        v-model.number="localFormData.scoreCanGet"
        type="number"
        placeholder="请输入活动可获得的PU分数"
        @input="onScoreInput"
      />
    </view>

    <!-- 活动详细描述 -->
    <view class="form-item">
      <view class="label">活动描述</view>
      <textarea 
        class="textarea" 
        v-model="localFormData.description" 
        placeholder="请输入活动详细描述"
        maxlength="500"
        auto-height
        @input="onDescriptionInput"
      />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, defineEmits, defineProps, watch } from 'vue';

interface FormData {
  title: string;
  position: string;
  scoreCanGet: number;
  enrollNumLimit: number;
  description: string;
}

interface Props {
  modelValue: FormData;
}

interface Emits {
  (e: 'update:modelValue', value: FormData): void;
  (e: 'change', value: FormData): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const localFormData = ref<FormData>({...props.modelValue});

// 监听外部值的变化
watch(() => props.modelValue, (newValue) => {
  localFormData.value = {...newValue};
}, { deep: true });

// 监听内部值的变化并同步到父组件
watch(localFormData, (newValue) => {
  emit('update:modelValue', {...newValue});
  emit('change', {...newValue});
}, { deep: true });

const onTitleInput = (e: any) => {
  localFormData.value.title = e.detail.value;
};

const onPositionInput = (e: any) => {
  localFormData.value.position = e.detail.value;
};

const onEnrollNumLimitInput = (e: any) => {
  let value = parseInt(e.detail.value) || 0;
  if (value < 0) value = 0;
  if (value > 20000) value = 20000;
  localFormData.value.enrollNumLimit = value;
};

const onScoreInput = (e: any) => {
  const value = parseFloat(e.detail.value);
  localFormData.value.scoreCanGet = isNaN(value) ? 0 : value;
};

const onDescriptionInput = (e: any) => {
  localFormData.value.description = e.detail.value;
};
</script>

<style lang="scss" scoped>
.form-item {
  margin-bottom: 40rpx;

  .label {
    font-size: 32rpx;
    font-weight: bold;
    color: #ffffff;
    margin-bottom: 20rpx;
  }

  .input {
    width: 100%;
    height: 80rpx;
    border: 2rpx solid #e0e0e0;
    border-radius: 12rpx;
    padding: 0 20rpx;
    font-size: 30rpx;
    color: #333;

    &:focus {
      border-color: #ff8c42;
      box-shadow: 0 0 10rpx rgba(255, 140, 66, 0.3);
    }
  }

  .textarea {
    width: 100%;
    min-height: 200rpx;
    border: 2rpx solid #555;
    border-radius: 12rpx;
    padding: 20rpx;
    font-size: 30rpx;
    color: #e0e0e0;
    background: #2a2a2a;
    line-height: 1.5;

    &::placeholder {
      color: #888;
    }

    &:focus {
      border-color: #ff8c42;
      box-shadow: 0 0 10rpx rgba(255, 140, 66, 0.3);
    }
  }
}
</style>