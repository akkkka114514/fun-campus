<!--
  * 角色 后台用户 列表
  *
  * @Author:    1024创新实验室-主任：卓大
  * @Date:      2022-09-12 22:34:00
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
  *
-->
<template>
  <div>
    <div class="header">
      <div>
        关键字：
        <a-input style="width: 250px" v-model:value="queryForm.keywords" placeholder="登录账号/邮箱" />
        <a-button class="button-style" v-if="selectRoleId" type="primary" @click="onSearch">搜索</a-button>
        <a-button class="button-style" v-if="selectRoleId" type="default" @click="resetQueryRoleBackendUser">重置</a-button>
      </div>

      <div>
        <a-button class="button-style" v-if="selectRoleId" type="primary" @click="addRoleBackendUser" v-privilege="'system:role:backendUser:add'"
          >添加后台用户</a-button
        >
        <a-button
          class="button-style"
          v-if="selectRoleId"
          type="primary"
          danger
          @click="batchDelete"
          v-privilege="'system:role:backendUser:batch:delete'"
          >批量移除</a-button
        >
      </div>
    </div>

    <a-table
      :loading="tableLoading"
      :dataSource="tableData"
      :columns="columns"
      :pagination="false"
      :scroll="{ y: 400 }"
      rowKey="id"
      :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
      size="small"
      bordered
    >
      <template #bodyCell="{ text, record, column }">
        <template v-if="column.dataIndex === 'disabledFlag'">
          <a-tag :color="text ? 'error' : 'processing'">{{ text ? '禁用' : '启用' }}</a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'operate'">
          <a @click="deleteBackendUserRole(record.id)" v-privilege="'system:role:backendUser:delete'">移除</a>
        </template>
      </template>
    </a-table>
    <div class="smart-query-table-page">
      <a-pagination
        showSizeChanger
        showQuickJumper
        show-less-items
        :pageSizeOptions="PAGE_SIZE_OPTIONS"
        :defaultPageSize="queryForm.pageSize"
        v-model:current="queryForm.pageNum"
        v-model:pageSize="queryForm.pageSize"
        :total="total"
        @change="queryRoleBackendUser"
        :show-total="showTableTotal"
      />
    </div>
    <BackendUserTableSelectModal ref="selectBackendUserModal" @selectData="selectData" />
  </div>
</template>
<script setup>
  import { message, Modal } from 'ant-design-vue';
  import _ from 'lodash';
  import { computed, inject, onMounted, reactive, ref, watch } from 'vue';
  import { roleApi } from '/@/api/system/role-api';
  import { PAGE_SIZE, showTableTotal, PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import BackendUserTableSelectModal from '/src/components/system/backend-user-table-select-modal/index.vue';
  import { smartSentry } from '/@/lib/smart-sentry';

  // ----------------------- 以下是字段定义 emits props ---------------------
  let selectRoleId = inject('selectRoleId');

  // ----------------------- 后台用户列表：显示和搜索 ------------------------
  watch(
    () => selectRoleId.value,
    () => queryRoleBackendUser()
  );

  onMounted(queryRoleBackendUser);

  const defaultQueryForm = {
    pageNum: 1,
    pageSize: PAGE_SIZE,
    roleId: undefined,
    keywords: undefined,
  };
  // 查询表单
  const queryForm = reactive({ ...defaultQueryForm });
  // 总数
  const total = ref(0);
  // 表格数据
  const tableData = ref([]);
  // 表格loading效果
  const tableLoading = ref(false);

  function resetQueryRoleBackendUser() {
    queryForm.keywords = '';
    queryRoleBackendUser();
  }

  function onSearch() {
    queryForm.pageNum = 1;
    queryRoleBackendUser();
  }

  async function queryRoleBackendUser() {
    try {
      tableLoading.value = true;
      queryForm.roleId = selectRoleId.value;
      let res = await roleApi.queryRoleBackendUser(queryForm);
      tableData.value = res.data.list;
      total.value = res.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  const columns = reactive([
    {
      title: '登录账号',
      dataIndex: 'username',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'disabledFlag',
    },
    {
      title: '操作',
      dataIndex: 'operate',
      width: 60,
    },
  ]);

  // ----------------------- 添加成员 ---------------------------------
  const selectBackendUserModal = ref();

  async function addRoleBackendUser() {
    let res = await roleApi.getRoleAllBackendUser(selectRoleId.value);
    let selectedIdList = res.data.map((e) => e.id) || [];
    selectBackendUserModal.value.showModal(selectedIdList);
  }

  async function selectData(list) {
    if (_.isEmpty(list)) {
      message.warning('请选择角色人员');
      return;
    }
    SmartLoading.show();
    try {
      let params = {
        backendUserIdList: list,
        roleId: selectRoleId.value,
      };
      await roleApi.batchAddRoleBackendUser(params);
      message.success('添加成功');
      await queryRoleBackendUser();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // ----------------------- 移除成员 ---------------------------------
  // 删除角色成员方法
  async function deleteBackendUserRole(backendUserId) {
    Modal.confirm({
      title: '提示',
      content: '确定要删除该角色成员么？',
      okText: '确定',
      okType: 'danger',
      async onOk() {
        SmartLoading.show();
        try {
          await roleApi.deleteBackendUserRole(backendUserId, selectRoleId.value);
          message.success('移除成功');
          await queryRoleBackendUser();
        } catch (e) {
          smartSentry.captureError(e);
        } finally {
          SmartLoading.hide();
        }
      },
      cancelText: '取消',
      onCancel() {},
    });
  }

  // ----------------------- 批量删除 ---------------------------------

  const selectedRowKeyList = ref([]);
  const hasSelected = computed(() => selectedRowKeyList.value.length > 0);

  function onSelectChange(selectedRowKeys) {
    selectedRowKeyList.value = selectedRowKeys;
  }

  // 批量移除
  function batchDelete() {
    if (!hasSelected.value) {
      message.warning('请选择要删除的角色成员');
      return;
    }
    Modal.confirm({
      title: '提示',
      content: '确定移除这些角色成员吗？',
      okText: '确定',
      okType: 'danger',
      async onOk() {
        SmartLoading.show();
        try {
          let params = {
            backendUserIdList: selectedRowKeyList.value,
            roleId: selectRoleId.value,
          };
          await roleApi.batchRemoveRoleBackendUser(params);
          message.success('移除成功');
          selectedRowKeyList.value = [];
          await queryRoleBackendUser();
        } catch (e) {
          smartSentry.captureError(e);
        } finally {
          SmartLoading.hide();
        }
      },
      cancelText: '取消',
      onCancel() {},
    });
  }
</script>

<style scoped lang="less">
  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 20px 0;
  }
  .button-style {
    margin: 0 10px;
  }
</style>
