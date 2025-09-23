<!--
  * 学校信息表
  *
  * @Author:    akkkka114514
  * @Date:      2025-09-23 08:34:37
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
        <a-form-item label="学校ID"  name="id">
          <a-input-number style="width: 100%" v-model:value="form.id" placeholder="学校ID" />
        </a-form-item>
        <a-form-item label="学校名称"  name="name">
          <a-input style="width: 100%" v-model:value="form.name" placeholder="学校名称" />
        </a-form-item>
        <a-form-item label="学校编码"  name="code">
          <a-input style="width: 100%" v-model:value="form.code" placeholder="学校编码" />
        </a-form-item>
        <a-form-item label="学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)"  name="type">
          <a-input-number style="width: 100%" v-model:value="form.type" placeholder="学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)" />
        </a-form-item>
        <a-form-item label="详细地址"  name="address">
          <a-input style="width: 100%" v-model:value="form.address" placeholder="详细地址" />
        </a-form-item>
        <a-form-item label="联系人"  name="contactPerson">
          <a-input style="width: 100%" v-model:value="form.contactPerson" placeholder="联系人" />
        </a-form-item>
        <a-form-item label="联系电话"  name="contactPhone">
          <a-input style="width: 100%" v-model:value="form.contactPhone" placeholder="联系电话" />
        </a-form-item>
        <a-form-item label="邮箱"  name="email">
          <a-input style="width: 100%" v-model:value="form.email" placeholder="邮箱" />
        </a-form-item>
        <a-form-item label="官网"  name="website">
          <a-input style="width: 100%" v-model:value="form.website" placeholder="官网" />
        </a-form-item>
        <a-form-item label="学校简介"  name="description">
          <a-textarea style="width: 100%" v-model:value="form.description" placeholder="学校简介" />
        </a-form-item>
        <a-form-item label="学校logo图片URL"  name="logoUrl">
          <a-input style="width: 100%" v-model:value="form.logoUrl" placeholder="学校logo图片URL" />
        </a-form-item>
        <a-form-item label="状态(0:禁用,1:启用)"  name="status">
          <a-input-number style="width: 100%" v-model:value="form.status" placeholder="状态(0:禁用,1:启用)" />
        </a-form-item>
        <a-form-item label="排序"  name="sort">
          <a-input-number style="width: 100%" v-model:value="form.sort" placeholder="排序" />
        </a-form-item>
        <a-form-item label="创建时间"  name="createTime">
          <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.createTime" style="width: 100%" placeholder="创建时间" />
        </a-form-item>
        <a-form-item label="更新时间"  name="updateTime">
          <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.updateTime" style="width: 100%" placeholder="更新时间" />
        </a-form-item>
        <a-form-item label="删除标识(0:未删除,1:已删除)"  name="deletedFlag">
          <a-input-number style="width: 100%" v-model:value="form.deletedFlag" placeholder="删除标识(0:未删除,1:已删除)" />
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
  import { schoolInfoApi } from '/@/api/business/school-info/school-info-api';
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
      id: undefined, //学校ID
      name: undefined, //学校名称
      code: undefined, //学校编码
      type: undefined, //学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)
      address: undefined, //详细地址
      contactPerson: undefined, //联系人
      contactPhone: undefined, //联系电话
      email: undefined, //邮箱
      website: undefined, //官网
      description: undefined, //学校简介
      logoUrl: undefined, //学校logo图片URL
      status: undefined, //状态(0:禁用,1:启用)
      sort: undefined, //排序
      createTime: undefined, //创建时间
      updateTime: undefined, //更新时间
      deletedFlag: undefined, //删除标识(0:未删除,1:已删除)
  };

  let form = reactive({ ...formDefault });

  const rules = {
      id: [{ required: true, message: '学校ID 必填' }],
      name: [{ required: true, message: '学校名称 必填' }],
      code: [{ required: true, message: '学校编码 必填' }],
      type: [{ required: true, message: '学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科) 必填' }],
      address: [{ required: true, message: '详细地址 必填' }],
      contactPerson: [{ required: true, message: '联系人 必填' }],
      contactPhone: [{ required: true, message: '联系电话 必填' }],
      email: [{ required: true, message: '邮箱 必填' }],
      website: [{ required: true, message: '官网 必填' }],
      description: [{ required: true, message: '学校简介 必填' }],
      logoUrl: [{ required: true, message: '学校logo图片URL 必填' }],
      status: [{ required: true, message: '状态(0:禁用,1:启用) 必填' }],
      sort: [{ required: true, message: '排序 必填' }],
      createTime: [{ required: true, message: '创建时间 必填' }],
      updateTime: [{ required: true, message: '更新时间 必填' }],
      deletedFlag: [{ required: true, message: '删除标识(0:未删除,1:已删除) 必填' }],
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
        await schoolInfoApi.update(form);
      } else {
        await schoolInfoApi.add(form);
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
