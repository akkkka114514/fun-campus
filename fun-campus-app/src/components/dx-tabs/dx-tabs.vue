<template>
  <view class="dx-tabs">
    <!-- 水平选项卡头部 -->
    <scroll-view 
      class="tabs-header" 
      scroll-x 
      :scroll-left="scrollLeft" 
      scroll-with-animation
      show-scrollbar="false"
    >
      <view class="tabs-container">
        <view
          v-for="(tab, index) in tabs"
          :key="index"
          class="tab-item"
          :class="{ active: modelValue === index }"
          :style="{
            color: modelValue === index ? activeColor : defaultColor,
            backgroundColor: modelValue === index ? activeBgColor : defaultBgColor
          }"
          @click="selectTab(index)"
        >
          <text class="tab-text">{{ tab }}</text>
          <view 
            v-if="modelValue === index" 
            class="active-indicator"
            :style="{ backgroundColor: indicatorColor }"
          ></view>
        </view>
      </view>
    </scroll-view>

    <!-- 选项卡内容区域 -->
    <view class="tabs-content">
      <slot></slot>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

// 定义组件属性
const props = defineProps({
  // 选项卡标题数组
  tabs: {
    type: Array as () => string[],
    required: true
  },
  // 当前激活的选项卡索引
  modelValue: {
    type: Number,
    default: 0
  },
  // 激活选项卡的文字颜色
  activeColor: {
    type: String,
    default: '#ff8c42'
  },
  // 默认选项卡的文字颜色
  defaultColor: {
    type: String,
    default: '#666666'
  },
  // 激活选项卡的背景颜色
  activeBgColor: {
    type: String,
    default: 'rgba(255, 140, 66, 0.1)'
  },
  // 默认选项卡的背景颜色
  defaultBgColor: {
    type: String,
    default: 'transparent'
  },
  // 指示器颜色
  indicatorColor: {
    type: String,
    default: '#ff8c42'
  }
})

// 定义事件
const emit = defineEmits(['update:modelValue'])

// 滚动位置
const scrollLeft = ref(0)

// 选择选项卡
function selectTab(index: number) {
  emit('update:modelValue', index)
}

// 监听当前激活的选项卡变化，自动滚动到可视区域
watch(() => props.modelValue, (newVal) => {
  // 计算滚动位置，使激活的选项卡居中显示
  const tabWidth = 120 // 假设每个选项卡宽度为120rpx
  const containerWidth = 750 // 屏幕宽度
  const targetScroll = newVal * tabWidth - (containerWidth - tabWidth) / 2
  scrollLeft.value = Math.max(0, targetScroll)
}, { immediate: true })
</script>

<style scoped lang="scss">
.dx-tabs {
  width: 100%;
}

.tabs-header {
  width: 100%;
  white-space: nowrap;
  background-color: #ffffff;
  border-bottom: 1rpx solid #eeeeee;
  position: relative;
}

.tabs-container {
  display: flex;
  flex-direction: row;
  padding: 0 20rpx;
}

.tab-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80rpx;
  padding: 0 30rpx;
  margin: 0 10rpx;
  border-radius: 30rpx;
  transition: all 0.3s ease;
  
  &:first-child {
    margin-left: 0;
  }
  
  &:last-child {
    margin-right: 0;
  }
}

.tab-text {
  font-size: 28rpx;
  font-weight: 500;
}

.active-indicator {
  position: absolute;
  bottom: 0;
  width: 50%;
  height: 6rpx;
  border-radius: 3rpx;
}

.tabs-content {
  width: 100%;
  min-height: 300rpx;
  padding: 20rpx;
}
</style>