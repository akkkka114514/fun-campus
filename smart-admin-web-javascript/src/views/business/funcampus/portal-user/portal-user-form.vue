<!--
  * 前端用户
  *
  * @Author:    akkkka114514
  * @Date:      2025-10-09 16:29:35
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
        <a-form-item label="性别"  name="gender">
          <BooleanSelect v-model:value="form.gender" style="width: 100%" />
        </a-form-item>
        <a-form-item label="学校名"  name="schoolName">
          <a-input style="width: 100%" v-model:value="form.schoolName" placeholder="学校名" />
        </a-form-item>
        <a-form-item label="学院名"  name="collegeName">
          <a-input style="width: 100%" v-model:value="form.collegeName" placeholder="学院名" />
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
  import { portalUserApi } from '/@/api/business/portal-user/portal-user-api';
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
      gender: undefined, //性别
      schoolName: undefined, //学校名
      collegeName: undefined, //学院名
  };

  let form = reactive({ ...formDefault });

  const rules = {
      username: [{ required: true, message: '用户名 必填' }],
      password: [{ required: true, message: '密码 必填' }],
      gender: [{ required: true, message: '性别 必填' }],
      schoolName: [{ required: true, message: '学校名 必填' }],
      collegeName: [{ required: true, message: '学院名 必填' }],
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
        await portalUserApi.update(form);
      } else {
        await portalUserApi.add(form);
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
