<!--
  * 企业 后台用户
  *
  * @Author:    1024创新实验室-主任：卓大
  * @Date:      2022-08-15 20:15:49
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
-->
<template>
  <div>
    <div class="header">
      <div>
        关键字：
        <a-input style="width: 250px" v-model:value="queryForm.keyword" placeholder="姓名/手机号/登录账号" />
        <a-button class="button-style" type="primary" @click="onSearch">搜索</a-button>
        <a-button class="button-style" type="default" @click="resetQueryBackendUser">重置</a-button>
      </div>
      <div>
        <a-button class="button-style" type="primary" @click="addBackendUser" v-privilege="'oa:enterprise:addBackendUser'"> 添加后台用户 </a-button>
        <a-button class="button-style" type="primary" danger @click="batchDelete" v-privilege="'oa:enterprise:deleteBackendUser'"> 批量移除 </a-button>
      </div>
    </div>
    <a-card size="small" :bordered="false" :hoverable="false">
      <a-row justify="end">
        <TableOperator
          class="smart-margin-bottom5"
          v-model="columns"
          :tableId="TABLE_ID_CONST.BUSINESS.OA.ENTERPRISE_EMPLOYEE"
          :refresh="queryBackendUser"
        />
      </a-row>
      <a-table
        :loading="tableLoading"
        :dataSource="tableData"
        :columns="columns"
        :pagination="false"
        rowKey="employeeId"
        :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
        size="small"
        bordered
      >
        <template #bodyCell="{ text, record, column }">
          <template v-if="column.dataIndex === 'disabledFlag'">
            <a-tag :color="text ? 'error' : 'processing'">{{ text ? '禁用' : '启用' }}</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'gender'">
            <span>{{ $smartEnumPlugin.getDescByValue('GENDER_ENUM', text) }}</span>
          </template>
          <template v-if="column.dataIndex === 'operate'">
            <a-button type="link" @click="deleteBackendUser(record.employeeId)" v-privilege="'oa:enterprise:deleteBackendUser'">移除</a-button>
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
          @change="queryBackendUser"
          :show-total="showTableTotal"
        />
      </div>
      <BackendUserTableSelectModal ref="selectBackendUserModal" @selectData="selectData" />
    </a-card>
  </div>
</template>
<script setup>
  import BackendUserTableSelectModal from '/@/components/system/employee-table-select-modal/index.vue';

  import { message, Modal } from 'ant-design-vue';
  import _ from 'lodash';
  import { computed, reactive, ref, watch } from 'vue';
  import { enterpriseApi } from '/@/api/business/oa/enterprise-api';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { PAGE_SIZE, PAGE_SIZE_OPTIONS, showTableTotal } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';

  const props = defineProps({
    enterpriseId: {
      type: Number,
      default: null,
    },
  });

  const columns = reactive([
    {
      title: '姓名',
      dataIndex: 'actualName',
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      width: 120,
    },
    {
      title: '登录账号',
      dataIndex: 'loginName',
    },
    {
      title: '企业',
      dataIndex: 'enterpriseName',
      ellipsis: true,
    },
    {
      title: '部门',
      dataIndex: 'departmentName',
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'disabledFlag',
      width: 80,
    },
    {
      title: '操作',
      dataIndex: 'operate',
      width: 90,
      align: 'center',
    },
  ]);

  // --------------------------- 查询 ---------------------------

  const defaultQueryForm = {
    pageNum: 1,
    pageSize: PAGE_SIZE,
    enterpriseId: undefined,
    keyword: undefined,
  };
  // 查询表单
  const queryForm = reactive({ ...defaultQueryForm });
  const total = ref(0);
  const tableData = ref([]);
  const tableLoading = ref(false);

  function resetQueryBackendUser() {
    queryForm.keyword = '';
    queryBackendUser();
  }

  function onSearch() {
    queryForm.pageNum = 1;
    queryBackendUser();
  }

  async function queryBackendUser() {
    try {
      tableLoading.value = true;
      queryForm.enterpriseId = props.enterpriseId;
      let res = await enterpriseApi.queryPageBackendUserList(queryForm);
      tableData.value = res.data.list;
      total.value = res.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  async function selectData(list) {
    if (_.isEmpty(list)) {
      message.warning('请选择后台用户');
      return;
    }
    SmartLoading.show();
    try {
      let params = {
        employeeIdList: list,
        enterpriseId: props.enterpriseId,
      };
      await enterpriseApi.addBackendUser(params);
      message.success('添加成功');
      await queryBackendUser();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // --------------------------- 添加后台用户 ---------------------------

  // 添加后台用户
  const selectBackendUserModal = ref();
  async function addBackendUser() {
    let res = await enterpriseApi.employeeList([props.enterpriseId]);
    let selectedIdList = res.data.map((e) => e.employeeId) || [];
    selectBackendUserModal.value.showModal(selectedIdList);
  }

  // --------------------------- 删除 ---------------------------

  // 删除后台用户方法
  async function deleteBackendUser(employeeId) {
    Modal.confirm({
      title: '提示',
      content: '确定要删除该企业下的后台用户么？',
      okText: '确定',
      okType: 'danger',
      async onOk() {
        SmartLoading.show();
        try {
          let param = {
            employeeIdList: [employeeId],
            enterpriseId: props.enterpriseId,
          };
          await enterpriseApi.deleteBackendUser(param);
          message.success('移除成功');
          await queryBackendUser();
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

  // 批量删除
  const selectedRowKeyList = ref([]);
  const hasSelected = computed(() => selectedRowKeyList.value.length > 0);
  function onSelectChange(selectedRowKeys) {
    selectedRowKeyList.value = selectedRowKeys;
  }

  // 批量移除
  function batchDelete() {
    if (!hasSelected.value) {
      message.warning('请选择要删除的后台用户');
      return;
    }
    Modal.confirm({
      title: '提示',
      content: '确定要删除该企业下的后台用户么？',
      okText: '确定',
      okType: 'danger',
      async onOk() {
        SmartLoading.show();
        try {
          let params = {
            employeeIdList: selectedRowKeyList.value,
            enterpriseId: props.enterpriseId,
          };
          await enterpriseApi.deleteBackendUser(params);
          message.success('移除成功');
          selectedRowKeyList.value = [];
          await queryBackendUser();
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

  watch(
    () => props.enterpriseId,
    (e) => {
      if (e) {
        queryBackendUser();
      }
    },
    { immediate: true }
  );
</script>

<style scoped lang="less">
  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 10px;
    margin-bottom: 10px;
  }

  .button-style {
    margin: 0 10px;
  }
</style>
