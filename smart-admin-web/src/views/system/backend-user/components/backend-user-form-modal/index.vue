<!--
  *  后台用户 表单 弹窗
  *
  * @Author:    1024创新实验室-主任：卓大
  * @Date:      2022-08-08 20:46:18
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
-->
<template>
  <a-drawer
    :title="form.id ? '编辑' : '添加'"
    :width="600"
    :open="visible"
    :body-style="{ paddingBottom: '80px' }"
    @close="onClose"
    destroyOnClose
  >
    <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
      <a-form-item label="登录账号" name="username">
        <a-input v-model:value.trim="form.username" placeholder="4-30位字母、数字或下划线" />
        <p class="hint">初始密码默认为：随机</p>
      </a-form-item>
      <a-form-item label="邮箱" name="email">
        <a-input v-model:value.trim="form.email" placeholder="请输入邮箱" />
      </a-form-item>
      <a-form-item label="状态" name="disabledFlag">
        <a-select v-model:value="form.disabledFlag" placeholder="请选择状态">
          <a-select-option :value="false">启用</a-select-option>
          <a-select-option :value="true">禁用</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="角色" name="roleIdList">
        <a-select mode="multiple" v-model:value="form.roleIdList" optionFilterProp="title" placeholder="请选择角色">
          <a-select-option v-for="item in roleList" :key="item.roleId" :title="item.roleName">{{ item.roleName }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="学校id" name="schoolId">
        <a-input-number v-model:value="form.schoolId" placeholder="请输入学校id" :min="1" style="width: 100%" />
      </a-form-item>
      <a-form-item label="学院id" name="collegeId">
        <a-input-number v-model:value="form.collegeId" placeholder="请输入学院id" :min="1" style="width: 100%" />
      </a-form-item>
      <a-form-item label="组织id" name="organizationId">
        <a-input-number v-model:value="form.organizationId" placeholder="请输入组织id" :min="1" style="width: 100%" />
      </a-form-item>
      <a-form-item label="审核权限" name="canReview">
        <a-select v-model:value="form.canReview" placeholder="请选择是否具有审核权限">
          <a-select-option :value="true">是</a-select-option>
          <a-select-option :value="false">否</a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
    <div class="footer">
      <a-button style="margin-right: 8px" @click="onClose">取消</a-button>
      <a-button type="primary" style="margin-right: 8px" @click="onSubmit(false)">保存</a-button>
      <a-button v-if="!form.id" type="primary" @click="onSubmit(true)">保存并继续添加</a-button>
    </div>
  </a-drawer>
</template>
<script setup>
  import { message } from 'ant-design-vue';
  import _ from 'lodash';
  import { nextTick, reactive, ref } from 'vue';
  import { backendUserApi } from '/src/api/system/backend-user-api';
  import { roleApi } from '/@/api/system/role-api';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';
  // ----------------------- 以下是字段定义 emits props ---------------------
  // emit
  const emit = defineEmits(['refresh', 'show-account']);

  // ----------------------- 显示/隐藏 ---------------------

  const visible = ref(false); // 是否展示抽屉
  // 隐藏
  function onClose() {
    reset();
    visible.value = false;
  }
  // 显示
  async function showDrawer(rowData) {
    Object.assign(form, formDefault);
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    visible.value = true;
    nextTick(() => {
      queryAllRole();
    });
  }

  // ----------------------- 表单显示 ---------------------

  const roleList = ref([]); //角色列表
  async function queryAllRole() {
    let res = await roleApi.queryAll();
    roleList.value = res.data;
  }

  const formRef = ref(); // 组件ref
  const formDefault = {
    id: undefined,
    disabledFlag: false,
    username: undefined,
    roleIdList: undefined,
    email: undefined,
    schoolId: undefined,
    collegeId: undefined,
    organizationId: undefined,
    canReview: false,
  };

  let form = reactive(_.cloneDeep(formDefault));
  function reset() {
    Object.assign(form, formDefault);
    formRef.value.resetFields();
  }

  // ----------------------- 表单提交 ---------------------
  // 表单规则
  const rules = {
    username: [
      { required: true, message: '登录账号不能为空' },
      { min: 4, max: 30, message: '登录账号长度为4-30个字符', trigger: 'blur' },
      { pattern: /^[a-zA-Z0-9_]+$/, message: '登录账号只能包含字母、数字和下划线', trigger: 'blur' },
    ],
    gender: [{ required: true, message: '性别不能为空' }],
    disabledFlag: [{ required: true, message: '状态不能为空' }],
    email: [{ required: true, message: '请输入邮箱' }],
    roleIdList: [{ required: true, message: '角色不能为空' }],
    schoolId: [{ required: true, message: '学校id不能为空' }],
  };
  // 校验表单
  function validateForm(formRef) {
    return new Promise((resolve) => {
      formRef
        .validate()
        .then(() => {
          resolve(true);
        })
        .catch(() => {
          resolve(false);
        });
    });
  }

  // 提交数据
  async function onSubmit(keepAdding) {
    let validateFormRes = await validateForm(formRef.value);
    if (!validateFormRes) {
      message.error('参数验证错误，请仔细填写表单数据!');
      return;
    }
    SmartLoading.show();
    if (form.id) {
      await updateBackendUser(keepAdding);
    } else {
      await addBackendUser(keepAdding);
    }
  }

  async function addBackendUser(keepAdding) {
    try {
      let { data } = await backendUserApi.addBackendUser(form);
      message.success('添加成功');
      emit('show-account', form.username, data);
      if (keepAdding) {
        reset();
      } else {
        onClose();
      }
      emit('refresh');
    } catch (error) {
      smartSentry.captureError(error);
    } finally {
      SmartLoading.hide();
    }
  }
  async function updateBackendUser(keepAdding) {
    try {
      let result = await backendUserApi.updateBackendUser(form);
      message.success('更新成功');
      if (keepAdding) {
        reset();
      } else {
        onClose();
      }
      emit('refresh');
    } catch (error) {
      smartSentry.captureError(error);
    } finally {
      SmartLoading.hide();
    }
  }

  // ----------------------- 以下是暴露的方法内容 ----------------------------
  defineExpose({
    showDrawer,
  });
</script>
<style scoped lang="less">
  .footer {
    position: absolute;
    right: 0;
    bottom: 0;
    width: 100%;
    border-top: 1px solid #e9e9e9;
    padding: 10px 16px;
    background: #fff;
    text-align: right;
    z-index: 1;
  }
  .hint {
    margin-top: 5px;
    color: #bfbfbf;
  }
</style>
