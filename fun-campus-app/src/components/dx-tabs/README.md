# DxTabs 水平选项卡组件

## 介绍

DxTabs 是一个水平选项卡组件，支持滑动切换和自定义样式。该组件适用于需要在有限空间内展示多个内容面板的场景。

## 使用示例

```vue
<template>
  <view>
    <dx-tabs 
      :tabs="['选项卡1', '选项卡2', '选项卡3']" 
      v-model="activeTab"
      active-color="#ff8c42"
      default-color="#666666"
    >
      <view v-if="activeTab === 0">内容1</view>
      <view v-if="activeTab === 1">内容2</view>
      <view v-if="activeTab === 2">内容3</view>
    </dx-tabs>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import DxTabs from '@/components/dx-tabs/dx-tabs.vue'

const activeTab = ref(0)
</script>
```

## Props

| 参数名 | 说明 | 类型 | 默认值 |
|--------|------|------|--------|
| tabs | 选项卡标题数组 | Array<string> | [] |
| modelValue | 当前激活的选项卡索引 | Number | 0 |
| activeColor | 激活选项卡的文字颜色 | String | '#ff8c42' |
| defaultColor | 默认选项卡的文字颜色 | String | '#666666' |
| activeBgColor | 激活选项卡的背景颜色 | String | 'rgba(255, 140, 66, 0.1)' |
| defaultBgColor | 默认选项卡的背景颜色 | String | 'transparent' |
| indicatorColor | 指示器颜色 | String | '#ff8c42' |

## Events

| 事件名 | 说明 | 参数 |
|--------|------|------|
| update:modelValue | 切换选项卡时触发 | 当前激活的选项卡索引 |

## 插槽

默认插槽用于放置选项卡内容。