<!--
  * 活动时间表
  *
  * @Author:    akkkka114514
  * @Date:      2025-09-06 15:54:07
  * @Copyright  akkkka114514
-->
<template>
  <a-modal
      :title="form.activityId ? '编辑' : '添加'"
      :width="30"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="报名开始时间"  name="enrollStartTime">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.enrollStartTime" style="width: 100%" placeholder="报名开始时间"/>
        </a-form-item>
        <a-form-item label="报名结束时间"  name="enrollEndTime">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.enrollEndTime" style="width: 100%" placeholder="报名结束时间"/>
        </a-form-item>
        <a-form-item label="活动开始时间"  name="activityStartTime">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.activityStartTime" style="width: 100%" placeholder="活动开始时间"/>
        </a-form-item>
        <a-form-item label="活动结束时间"  name="activityEndTime">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.activityEndTime" style="width: 100%" placeholder="活动结束时间"/>
        </a-form-item>
        <a-form-item label="签到开始时间"  name="signinStartTime">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.signinStartTime" style="width: 100%" placeholder="签到开始时间"/>
        </a-form-item>
        <a-form-item label="签到结束时间"  name="signinEndTime">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.signinEndTime" style="width: 100%" placeholder="签到结束时间"/>
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
  import { activityScheduleApi } from '/@/api/business/activity-schedule/activity-schedule-api';
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
      enrollStartTime: undefined, //报名开始时间
      enrollEndTime: undefined, //报名结束时间
      activityStartTime: undefined, //活动开始时间
      activityEndTime: undefined, //活动结束时间
      signinStartTime: undefined, //签到开始时间
      signinEndTime: undefined, //签到结束时间
  };

  let form = reactive({ ...formDefault });

  const rules = {
      enrollStartTime: [{ required: true, message: '报名开始时间 必填' }],
      enrollEndTime: [{ required: true, message: '报名结束时间 必填' }],
      activityStartTime: [{ required: true, message: '活动开始时间 必填' }],
      activityEndTime: [{ required: true, message: '活动结束时间 必填' }],
      signinStartTime: [{ required: true, message: '签到开始时间 必填' }],
      signinEndTime: [{ required: true, message: '签到结束时间 必填' }],
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
      if (form.activityId) {
        await activityScheduleApi.update(form);
      } else {
        await activityScheduleApi.add(form);
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
