<script setup lang="ts">
import { ref } from 'vue';
import dxActivity from '@/components/dx-activity/dx-activity.vue';
import tmTabs from '@/tmui/components/tm-tabs/tm-tabs.vue';
import tmTabsPane from '@/tmui/components/tm-tabs-pane/tm-tabs-pane.vue';
</script>

<template>
  <view class="activity-page">
    <tm-tabs default-name="registered" :width="750">
      <tm-tabs-pane name="registered" title="报名未签到">
        <dx-activity />
      </tm-tabs-pane>
      <tm-tabs-pane name="notStarted" title="未开始">
        <dx-activity />
      </tm-tabs-pane>
      <tm-tabs-pane name="ongoing" title="进行中">
        <dx-activity />
      </tm-tabs-pane>
      <tm-tabs-pane name="ended" title="已结束">
        <dx-activity />
      </tm-tabs-pane>
      <tm-tabs-pane name="completed" title="已完结">
        <dx-activity />
      </tm-tabs-pane>
    </tm-tabs>
  </view>
</template>

<style scoped>
.activity-page {
  padding: 20rpx;
}
</style>