<template>
  <tm-app>
    <tm-sheet :margin="[0, 0]" :padding="[30, 30]">
      <tm-cell showAvatar avatar="https://picsum.photos/200" rightText="2023-04-21 20:22" :margin="[0, 0]" :bottomBorder="true">
        <template v-slot:title>
          <view class="flex-col-top-center"><text class="mr-15">昵称</text></view>
        </template>
      </tm-cell>
    </tm-sheet>
  </tm-app>
</template>
