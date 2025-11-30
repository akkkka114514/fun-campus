<template>
  <tm-app>
    <!-- 主内容区域 -->
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]" :transprent="true" class="main-content">
      <dx-home v-if="acc === 0" />
    </tm-sheet>
    <!-- 底部导航栏 -->
    <tm-tabbar
      :autoSelect="true"
      v-model:active="acc"
      :showSafe="false"
      @change="tabbarChange"
      class="custom-tabbar"
    >
      <tm-tabbar-item
        :activeColor="acc === 1 ? '#ff8c42' : '#999'"
        text="第二课堂"
        icon="tmicon-flag-fill"
        url="/pages/index/index"
        class="tabbar-item"
      />
      <tm-tabbar-item
        :activeColor="acc === 3 ? '#ff8c42' : '#999'"
        text="校园生活"
        icon="tmicon-layergroup-fill"
        url="/teams/index/index"
        class="tabbar-item"
      />
      <tm-tabbar-item
        :activeColor="acc === 4 ? '#ff8c42' : '#999'"
        text="我的"
        icon="tmicon-md-person"
        url="/pages/user/profile"
        class="tabbar-item"
      />
    </tm-tabbar>
  </tm-app>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import DxHome from "@/components/dx-home/dx-home.vue";

const acc = ref(0);

onMounted(() => {
});

// 监听 tabbar 的 change 事件
function tabbarChange(e: number) {
  acc.value = e;
}
</script>

<style lang="scss" scoped>
.page-enter-active {
  animation: fadeInUp 0.5s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.top-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 220rpx;
  background: linear-gradient(135deg, #ff8c42, #ff6b35, #ff9a3d);
  background-size: 300% 300%;
  z-index: -1;
  animation: gradientBG 10s ease infinite alternate;
  box-shadow: 0 10rpx 30rpx rgba(255, 140, 66, 0.2);
}

@keyframes gradientBG {
  0% {
    background-position: 0% 50%;
  }
  100% {
    background-position: 100% 50%;
  }
}

.main-content {
  min-height: calc(100vh - 160rpx);
  background: #f8f9fa;
  border-top-left-radius: 40rpx;
  border-top-right-radius: 40rpx;
  margin-top: 200rpx;
  box-shadow: 0 -12rpx 40rpx rgba(255, 140, 66, 0.12);
  padding: 30rpx 24rpx 0;
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.safe-area {
  height: 160rpx;
}

.custom-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  border-top-left-radius: 36rpx;
  border-top-right-radius: 36rpx;
  box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.06);
  padding: 24rpx 0 36rpx;
  z-index: 999;
  backdrop-filter: blur(10rpx); /* 毛玻璃效果（部分平台支持） */
}

.tabbar-item {
  transition: transform 0.25s ease, color 0.25s ease;
  position: relative;
  font-weight: 500;

  &:active {
    transform: scale(0.92);
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -12rpx;
    left: 50%;
    width: 0;
    height: 4rpx;
    background: #ff8c42;
    border-radius: 2rpx;
    transform: translateX(-50%);
    transition: width 0.3s ease;
  }

  &[class*='active']::after {
    width: 40rpx;
  }
}

.publish-btn {
  position: relative;
  top: -50rpx;
  width: 130rpx;
  height: 130rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 12rpx 30rpx rgba(255, 140, 66, 0.4),
    0 4rpx 12rpx rgba(255, 140, 66, 0.3);
  animation: floatPulse 3.5s ease-in-out infinite;
  transform-origin: center;
  background: linear-gradient(135deg, #ff8c42, #ff6b35);
  border: 6rpx solid #ffffff;

  &:active {
    transform: scale(0.9) rotate(5deg);
    animation: none;
    box-shadow:
      0 8rpx 20rpx rgba(255, 140, 66, 0.5),
      0 2rpx 8rpx rgba(255, 140, 66, 0.4);
  }
}

@keyframes floatPulse {
  0%, 100% {
    transform: translateY(0) scale(1);
    box-shadow:
      0 12rpx 30rpx rgba(255, 140, 66, 0.4),
      0 4rpx 12rpx rgba(255, 140, 66, 0.3);
  }
  50% {
    transform: translateY(-12rpx) scale(1.05);
    box-shadow:
      0 18rpx 40rpx rgba(255, 140, 66, 0.55),
      0 6rpx 16rpx rgba(255, 140, 66, 0.4);
  }
}

/* 响应式优化 */
@media (max-width: 768px) {
  .top-bg {
    height: 180rpx;
  }

  .main-content {
    margin-top: 170rpx;
    border-radius: 32rpx;
  }

  .publish-btn {
    top: -40rpx;
    width: 110rpx;
    height: 110rpx;
  }

  @keyframes floatPulse {
    50% {
      transform: translateY(-8rpx) scale(1.03);
    }
  }
}
</style>