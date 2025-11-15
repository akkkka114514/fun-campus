<template>
  <tm-app>
    <tm-form :margin="[0, 0]" layout="vertical" @submit="confirm" ref="form" v-model="formData" :label-width="190">
      <tm-form-item :margin="[15, 0]" label="报名成功提示文字" field="text">
        <tm-input type="textarea" :inputPadding="[24, 20]" :height="150" v-model.lazy="formData.text"> </tm-input>
      </tm-form-item>

      <tm-form-item :margin="[15, 0]" label="转发好友标题" field="title">
        <tm-input :inputPadding="[24, 5]" v-model.lazy="formData.title"> </tm-input>
      </tm-form-item>

      <tm-form-item label="转发好友图片" field="image">
        <dx-upload v-model="formData.image"></dx-upload>
      </tm-form-item>

      <tm-form-item :margin="[15, 0]" label="“立即参与”按钮文案" field="btn_text">
        <tm-input :inputPadding="[24, 5]" v-model.lazy="formData.btn_text"> </tm-input>
      </tm-form-item>
      <tm-form-item :margin="[15, 0]" :border="false">
        <view class="flex flex-row">
          <view class="flex-1 mr-30">
            <tm-button :shadow="0" text form-type="reset" label="重置" block></tm-button>
          </view>
          <view class="flex-1 ">
            <tm-button form-type="submit" label="提交" block></tm-button>
          </view>
        </view>
      </tm-form-item>
    </tm-form>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
const formData = ref<any>({ title: '' ,text:'',image:'',btn_text:''});
const confirm = () => {
  console.log(formData.value);
};
</script>
<style lang="scss" scoped></style>
