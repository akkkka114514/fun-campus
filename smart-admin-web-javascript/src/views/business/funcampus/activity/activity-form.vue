<!--
  * 活动管理
  *
  * @Author:    akkkka114514
  * @Date:      2025-09-04 13:41:42
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
        <a-form-item label="活动标题"  name="title">
          <a-input style="width: 100%" v-model:value="form.title" placeholder="活动标题" />
        </a-form-item>
        <a-form-item label="活动地点"  name="position">
          <a-input style="width: 100%" v-model:value="form.position" placeholder="活动地点" />
        </a-form-item>
        <a-form-item label="能得到的学分"  name="scoreCanGet">
          <a-input-number style="width: 100%" v-model:value="form.scoreCanGet" placeholder="能得到的学分" />
        </a-form-item>
        <a-form-item label="报名人数限制"  name="enrollNumLimit">
          <a-input-number style="width: 100%" v-model:value="form.enrollNumLimit" placeholder="报名人数限制" />
        </a-form-item>
        <a-form-item label="活动所属组织"  name="activityOrganizerId">
          <a-input-number style="width: 100%" v-model:value="form.activityOrganizerId" placeholder="活动所属组织" />
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
  import { activityApi } from '/@/api/business/activity/activity-api';
  import { smartSentry } from '/@/lib/smart-sentry';

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
      title: undefined, //活动标题
      position: undefined, //活动地点
      scoreCanGet: undefined, //能得到的学分
      enrollNumLimit: undefined, //报名人数限制
      activityOrganizerId: undefined, //活动所属组织
  };

  let form = reactive({ ...formDefault });

  const rules = {
      title: [{ required: true, message: '活动标题 必填' }],
      position: [{ required: true, message: '活动地点 必填' }],
      scoreCanGet: [{ required: true, message: '能得到的学分 必填' }],
      enrollNumLimit: [{ required: true, message: '报名人数限制 必填' }],
      activityOrganizerId: [{ required: true, message: '活动所属组织 必填' }],
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
        await activityApi.update(form);
      } else {
        await activityApi.add(form);
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
