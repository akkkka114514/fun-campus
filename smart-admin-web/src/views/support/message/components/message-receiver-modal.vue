<template>
  <a-modal v-model:open="visible" title="选择接收人" width="800px" ok-text="确定" cancel-text="取消" @ok="onSubmit" @cancel="onClose" :zIndex="9999">
    <a-form class="smart-query-form">
      <a-row class="smart-query-form-row">
        <a-form-item label="关键词搜索" class="smart-query-form-item">
          <a-input v-model:value="queryParam.keyword" :style="{ width: '250px' }" placeholder="请输入用户名" @pressEnter="searchQuery" />
        </a-form-item>
        <a-form-item class="smart-query-form-item">
          <a-button type="primary" @click="searchQuery">
            <template #icon>
              <SearchOutlined />
            </template>
            查询
          </a-button>
        </a-form-item>
        <a-form-item class="smart-query-form-item">
          <a-button @click="searchReset">
            <template #icon>
              <ReloadOutlined />
            </template>
            重置
          </a-button>
        </a-form-item>
      </a-row>
    </a-form>
    <a-table
      rowKey="id"
      :loading="tableLoading"
      :columns="columns"
      :data-source="tableData"
      bordered
      :pagination="false"
      :row-selection="{
        selectedRowKeys: selectedRowKeyList,
        onChange: onSelectChange,
      }"
    >
    </a-table>
    <div class="smart-query-table-page">
      <a-pagination
        showSizeChanger
        showQuickJumper
        show-less-items
        :pageSizeOptions="PAGE_SIZE_OPTIONS"
        :defaultPageSize="queryParam.pageSize"
        v-model:current="queryParam.pageNum"
        v-model:pageSize="queryParam.pageSize"
        :total="total"
        @change="queryList"
        :show-total="(total) => `共${total}条`"
      />
    </div>
  </a-modal>
</template>

<script setup>
  import { reactive, ref } from 'vue';
  import { PAGE_SIZE, PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { messageApi } from '/@/api/support/message-api';
  // ---------------查询条件----------------
  const queryParamState = {
    keyword: null,
    receiverUserType: null,
    pageNum: 1,
    pageSize: PAGE_SIZE,
  };

  const queryParam = reactive({ ...queryParamState });
  const tableData = ref([]);
  let tableLoading = ref(false);
  const total = ref(0);

  // 当前查询的接收人类型（由发送表单传入：1 后台用户 / 2 前台用户）
  const receiverUserType = ref();

  // 搜索
  function searchQuery() {
    queryParam.pageNum = 1;
    queryList();
  }

  // 弹窗打开
  const visible = ref(false);
  // 重置
  function searchReset() {
    Object.assign(queryParam, queryParamState);
    queryList();
  }
  function showModal(receiverIdList, userType) {
    selectedRowKeyList.value = receiverIdList || [];
    selectedRowsList.value = [];
    receiverUserType.value = userType;
    queryParam.pageNum = 1;
    queryParam.keyword = null;
    queryList();
    visible.value = true;
  }
  const columns = [
    {
      title: '用户名',
      dataIndex: 'username',
      align: 'center',
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      align: 'center',
    },
  ];
  // ----------查询------------
  async function queryList() {
    try {
      tableLoading.value = true;
      queryParam.receiverUserType = receiverUserType.value;
      let res = await messageApi.queryReceiverUser(queryParam);
      tableData.value = res.data.list;
      total.value = res.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  // 选择接收人
  const selectedRowKeyList = ref([]);
  const selectedRowsList = ref([]);
  function onSelectChange(keyArray, selectedRows) {
    selectedRowKeyList.value = keyArray;
    selectedRowsList.value = selectedRows;
  }

  // 弹窗管理
  function onClose() {
    visible.value = false;
    let nameList = selectedRowsList.value.map((item) => item.username);
    emit('reloadList', selectedRowKeyList.value, nameList);
  }
  const emit = defineEmits(['reloadList']);

  // 提交
  function onSubmit() {
    try {
      onClose();
    } catch (error) {
      smartSentry.captureError(error);
    }
  }

  defineExpose({
    showModal,
  });
</script>

<style scoped lang="less"></style>
