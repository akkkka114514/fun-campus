<!--
  * 活动审核日志
  *
  * @Author:    akkkka114514
  * @Date:      2026-01-10 18:34:56
  * @Copyright  akkkka114514
-->
<template>
  <a-modal
      :title="form.id ? '编辑' : '添加'"
      :width="300"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="主键"  name="id">
          <a-input-number style="width: 100%" v-model:value="form.id" placeholder="主键" />
        </a-form-item>
        <a-form-item label="活动id"  name="activityId">
          <a-input-number style="width: 100%" v-model:value="form.activityId" placeholder="活动id" />
        </a-form-item>
        <a-form-item label="审核人id"  name="reviewerId">
          <a-input-number style="width: 100%" v-model:value="form.reviewerId" placeholder="审核人id" />
        </a-form-item>
        <a-form-item label="审核人姓名"  name="reviewerName">
          <a-input style="width: 100%" v-model:value="form.reviewerName" placeholder="审核人姓名" />
        </a-form-item>
        <a-form-item label="审核阶段，1-》初审，2-》审阅，3-》终审，4-》完结审核"  name="reviewStage">
          <a-input-number style="width: 100%" v-model:value="form.reviewStage" placeholder="审核阶段，1-》初审，2-》审阅，3-》终审，4-》完结审核" />
        </a-form-item>
        <a-form-item label="审核行为，1-》通过，2-》驳回，3-》建议"  name="action">
          <a-input-number style="width: 100%" v-model:value="form.action" placeholder="审核行为，1-》通过，2-》驳回，3-》建议" />
        </a-form-item>
        <a-form-item label="创建时间"  name="createTime">
          <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.createTime" style="width: 100%" placeholder="创建时间" />
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
  import { activityReviewLogApi } from '/@/api/business/activity-review-log/activity-review-log-api';
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
      id: undefined, //主键
      activityId: undefined, //活动id
      reviewerId: undefined, //审核人id
      reviewerName: undefined, //审核人姓名
      reviewStage: undefined, //审核阶段，1-》初审，2-》审阅，3-》终审，4-》完结审核
      action: undefined, //审核行为，1-》通过，2-》驳回，3-》建议
      createTime: undefined, //创建时间
  };

  let form = reactive({ ...formDefault });

  const rules = {
      id: [{ required: true, message: '主键 必填' }],
      activityId: [{ required: true, message: '活动id 必填' }],
      reviewerId: [{ required: true, message: '审核人id 必填' }],
      reviewerName: [{ required: true, message: '审核人姓名 必填' }],
      reviewStage: [{ required: true, message: '审核阶段，1-》初审，2-》审阅，3-》终审，4-》完结审核 必填' }],
      action: [{ required: true, message: '审核行为，1-》通过，2-》驳回，3-》建议 必填' }],
      createTime: [{ required: true, message: '创建时间 必填' }],
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
        await activityReviewLogApi.update(form);
      } else {
        await activityReviewLogApi.add(form);
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
