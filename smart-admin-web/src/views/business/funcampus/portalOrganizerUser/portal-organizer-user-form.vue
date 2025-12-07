<!--
  * 组织账号运营者
  *
  * @Author:    akkkka114514
  * @Date:      2025-09-19 10:12:39
  * @Copyright  akkkka114514
-->
<template>
  <a-modal
      :title="form.id ? '编辑' : '添加'"
      :width="30"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="主键"  name="id">
          <a-input-number style="width: 100%" v-model:value="form.id" placeholder="主键" />
        </a-form-item>
        <a-form-item label="用户名"  name="username">
          <a-input style="width: 100%" v-model:value="form.username" placeholder="用户名" />
        </a-form-item>
        <a-form-item label="密码"  name="password">
          <a-input style="width: 100%" v-model:value="form.password" placeholder="密码" />
        </a-form-item>
        <a-form-item label="是否已删除"  name="deletedFlag">
          <BooleanSelect v-model:value="form.deletedFlag" style="width: 100%" />
        </a-form-item>
        <a-form-item label="头像"  name="avatar">
          <a-input style="width: 100%" v-model:value="form.avatar" placeholder="头像" />
        </a-form-item>
        <a-form-item label="手机号"  name="phone">
          <a-input style="width: 100%" v-model:value="form.phone" placeholder="手机号" />
        </a-form-item>
        <a-form-item label="学校名"  name="schoolName">
          <a-input style="width: 100%" v-model:value="form.schoolName" placeholder="学校名" />
        </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit">保存</a-button>
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
  import { reactive, ref, nextTick } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { portalOrganizerUserApi } from '/@/api/business/portal-organizer-user/portal-organizer-user-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import BooleanSelect from '/@/components/framework/boolean-select/index.vue';

  // ------------------------ 事件 ------------------------

  const emits = defineEmits(['reloadList']);

  // ------------------------ 显示与隐藏 ------------------------
  // 是否显示
  const visibleFlag = ref(false);

  function show(rowData) {
    Object.assign(form, formDefault);
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    // 使用字典时把下面这注释修改成自己的字典字段 有多个字典字段就复制多份同理修改 不然打开表单时不显示字典初始值
    // if (form.status && form.status.length > 0) {
    //   form.status = form.status.map((e) => e.valueCode);
    // }
    visibleFlag.value = true;
    nextTick(() => {
      formRef.value.clearValidate();
    });
  }

  function onClose() {
    Object.assign(form, formDefault);
    visibleFlag.value = false;
  }

  // ------------------------ 表单 ------------------------

  // 组件ref
  const formRef = ref();

  const formDefault = {
      id: undefined, //主键
      username: undefined, //用户名
      password: undefined, //密码
      deletedFlag: undefined, //是否已删除
      avatar: undefined, //头像
      phone: undefined, //手机号
      schoolName: undefined, //学校名
  };

  let form = reactive({ ...formDefault });

  const rules = {
      id: [{ required: true, message: '主键 必填' }],
      username: [{ required: true, message: '用户名 必填' }],
      password: [{ required: true, message: '密码 必填' }],
      deletedFlag: [{ required: true, message: '是否已删除 必填' }],
      schoolName: [{ required: true, message: '学校名 必填' }],
  };

  // 点击确定，验证表单
  async function onSubmit() {
    try {
      await formRef.value.validateFields();
      save();
    } catch (err) {
      message.error('参数验证错误，请仔细填写表单数据!');
    }
  }

  // 新建、编辑API
  async function save() {
    SmartLoading.show();
    try {
      if (form.id) {
        await portalOrganizerUserApi.update(form);
      } else {
        await portalOrganizerUserApi.add(form);
      }
      message.success('操作成功');
      emits('reloadList');
      onClose();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  defineExpose({
    show,
  });
</script>
