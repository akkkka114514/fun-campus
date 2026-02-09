<!--
  * 各学校组织信息
  *
  * @Author:    akkkka114514
  * @Date:      2026-01-15 13:24:05
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
        <a-form-item label="组织id"  name="id">
          <a-input-number style="width: 100%" v-model:value="form.id" placeholder="组织id" />
        </a-form-item>
        <a-form-item label="属于学校的id"  name="schoolId">
          <a-input-number style="width: 100%" v-model:value="form.schoolId" placeholder="属于学校的id" />
        </a-form-item>
        <a-form-item label="组织名称"  name="name">
          <a-input style="width: 100%" v-model:value="form.name" placeholder="组织名称" />
        </a-form-item>
        <a-form-item label="是否已删除"  name="deletedFlag">
          <BooleanSelect v-model:value="form.deletedFlag" style="width: 100%" />
        </a-form-item>
        <a-form-item label="创建时间"  name="createTime">
          <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.createTime" style="width: 100%" placeholder="创建时间" />
        </a-form-item>
        <a-form-item label="修改时间"  name="updateTime">
          <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.updateTime" style="width: 100%" placeholder="修改时间" />
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
  import { organizationInfoApi } from '/@/api/business/organization-info/organization-info-api';
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
      id: undefined, //组织id
      schoolId: undefined, //属于学校的id
      name: undefined, //组织名称
      deletedFlag: undefined, //是否已删除
      createTime: undefined, //创建时间
      updateTime: undefined, //修改时间
  };

  let form = reactive({ ...formDefault });

  const rules = {
      id: [{ required: true, message: '组织id 必填' }],
      schoolId: [{ required: true, message: '属于学校的id 必填' }],
      name: [{ required: true, message: '组织名称 必填' }],
      deletedFlag: [{ required: true, message: '是否已删除 必填' }],
      createTime: [{ required: true, message: '创建时间 必填' }],
      updateTime: [{ required: true, message: '修改时间 必填' }],
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
        await organizationInfoApi.update(form);
      } else {
        await organizationInfoApi.add(form);
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
