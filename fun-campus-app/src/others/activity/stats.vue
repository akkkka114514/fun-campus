<template>
  <tm-app>
    <tm-cell :margin="[0, 0]" title="基本统计" :bottomBorder="true" rightIcon=""> </tm-cell>
    <tm-grid :width="750" :col="4">
      <tm-grid-item color="pink" :height="150">
        <view>1</view>
        <tm-text _class="pt-10" :font-size="22" label="待审核人数"></tm-text>
      </tm-grid-item>
      <tm-grid-item :height="150">
        <view>1</view>
        <tm-text _class="pt-10" :font-size="22" label="报名人数"></tm-text>
      </tm-grid-item>
      <tm-grid-item color="orange" :height="150">
        <view>1</view>
        <tm-text _class="pt-10" :font-size="22" label="浏览人数"></tm-text>
      </tm-grid-item>
      <tm-grid-item color="green" :height="150">
        <view>1</view>
        <tm-text :font-size="22" label="关注人数"></tm-text>
      </tm-grid-item>
    </tm-grid>
  </tm-app>
</template>
