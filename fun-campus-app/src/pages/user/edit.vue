<template>
  <tm-app color="white">
    <tm-form @submit="confirm" :margin="[0, 0]" ref="form" v-model="editForm" :label-width="90">
      <tm-form-item label="头像" field="cale">
        <dx-upload v-model="editForm.avatar"></dx-upload>
      </tm-form-item>
      <tm-form-item label="昵称" field="nickname">
        <tm-input :inputPadding="[20, 0]" :round="20" v-model.lazy="editForm.nickname" placeholder="修改昵称,留空不修改" :showBottomBotder="false"> </tm-input>
      </tm-form-item>
      <tm-form-item label="性别" field="gender">
        <tm-radio-group v-model="editForm.gender">
          <tm-radio :value="1" label="男"></tm-radio>
          <tm-radio :value="2" label="女"></tm-radio>
        </tm-radio-group>
      </tm-form-item>
      <!-- <tm-form-item label="手机" field="mobile">
        <tm-input :inputPadding="[20, 0]" :round="20" v-model.lazy="editForm.mobile" placeholder="修改手机,留空不修改" :showBottomBotder="false"> </tm-input>
      </tm-form-item> -->
      <tm-form-item label="邮箱" field="email">
        <tm-input :inputPadding="[20, 0]" :round="20" v-model.lazy="editForm.email" placeholder="修改邮箱,留空不修改" :showBottomBotder="false"> </tm-input>
      </tm-form-item>
      <tm-form-item label="密码" field="password">
        <tm-input :inputPadding="[20, 0]" :round="20" v-model.lazy="editForm.password" placeholder="修改密码,留空不修改" :showBottomBotder="false"> </tm-input>
      </tm-form-item>
      <tm-form-item :border="false">
        <tm-button :margin="[10]" :shadow="0" :round="20" size="small" form-type="submit" block label="保存"></tm-button>
      </tm-form-item>
    </tm-form>
    
    <!-- 底部编辑浮动按钮 -->
    <tm-float-button
      :icon="tmicon.edit"
      :size="48"
      :color="'#007AFF'"
      @click="$router.push('/user/edit')"
      style="position: fixed; bottom: 80px; right: 20px; z-index: 999;"
    />
  </tm-app>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { tmicon } from '@/tmui/tool/tmicon/tmicon';
import {useUserStore} from '@/stores/user';
import {editUserInfo} from '@/common/index';
const userStore = useUserStore();
const editForm = ref({
  nickname: userStore.userInfo.nickname,
  avatar: userStore.userInfo.avatar,
  // mobile: userStore.userInfo.mobile,
  email:  userStore.userInfo.email,
  gender:  userStore.userInfo.gender,
  password: '',
});

function confirm(e:any) {
  editUserInfo(e.data).then((res) => {
    if(res.code === 1000){
      userStore.setUserInfo(res.data);
      uni.$tm.u.toast('修改成功');
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }else{
      uni.$tm.u.toast(res.message);
    }
  });
}
</script>
