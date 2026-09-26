<template>
  <div class="center-container">
    <!--  页面标题-->
    <div class="header-title">个人中心</div>

    <!--  内容区域-->
    <div class="center-form-area">
      <a-row>
        <a-col flex="350px">
          <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
            <a-form-item label="登录账号" name="username">
              <a-input class="form-item" v-model:value.trim="form.username" placeholder="请输入登录账号" disabled />
            </a-form-item>
            <a-form-item label="邮箱" name="email">
              <a-input class="form-item" v-model:value.trim="form.email" placeholder="请输入邮箱" />
            </a-form-item>
          </a-form>
          <a-button type="primary" @click="onSubmit">更新个人信息</a-button>
        </a-col>
      </a-row>
    </div>
  </div>
</template>
<script setup>
  import { onMounted, reactive, ref } from 'vue';
  import { loginApi } from '/@/api/system/login-api.js';
  import { useUserStore } from '/@/store/modules/system/user.js';
  import { message } from 'ant-design-vue';
  import { smartSentry } from '/@/lib/smart-sentry.js';
  import { backendUserApi } from '/src/api/system/backend-user-api';
  import { SmartLoading } from '/@/components/framework/smart-loading/index.js';

  // 组件ref
  const formRef = ref();

  const formDefault = {
    // 后台用户ID
    id: undefined,
    // 登录账号
    username: '',
    // 邮箱
    email: undefined,
  };
  let form = reactive({ ...formDefault });
  const rules = {
    email: [{ required: true, message: '请输入邮箱' }],
  };

  // 查询登录信息
  async function getLoginInfo() {
    try {
      //获取登录用户信息
      const res = await loginApi.getLoginInfo();
      let data = res.data;
      //更新用户信息到pinia
      useUserStore().setUserLoginInfo(data);
      // 当前form展示
      form.id = data.id;
      form.username = data.username;
      // getLoginInfo接口不返回邮箱，从用户列表中查询当前用户的邮箱
      const allUserRes = await backendUserApi.queryAll();
      const currentUser = (allUserRes.data || []).find((e) => e.id === data.id);
      form.email = currentUser ? currentUser.email : undefined;
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  // 更新后台用户信息
  async function updateBackendUser() {
    SmartLoading.show();
    try {
      await backendUserApi.updateCenter({ id: form.id, email: form.email });
      message.success('更新成功');
      // 重新获取详情，刷新整体缓存
      await getLoginInfo();
    } catch (error) {
      smartSentry.captureError(error);
    } finally {
      SmartLoading.hide();
    }
  }

  // 表单提交
  function onSubmit() {
    formRef.value
      .validate()
      .then(() => {
        updateBackendUser();
      })
      .catch((error) => {
        console.log('error', error);
        message.error('参数验证错误，请仔细填写表单数据!');
      });
  }

  onMounted(() => {
    getLoginInfo();
  });
</script>
<style lang="less" scoped>
  .center-container {
    height: 100%;
    display: flex;
    flex-direction: column;

    .header-title {
      font-size: 20px;
      flex-shrink: 0;
    }

    .center-form-area {
      margin-top: 20px;
      flex: 1;
      overflow-y: auto;
      min-height: 0;

      .form-item {
        width: 100%;
      }
    }
  }
</style>
