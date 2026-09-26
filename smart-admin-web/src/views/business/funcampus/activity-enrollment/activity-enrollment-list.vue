<!--
  * 活动报名关系
  *
  * @Author:    akkkka114514
  * @Date:      2025-10-02 13:54:42
  * @Copyright  akkkka114514
-->
<template>
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
            <a-form-item label="活动id" class="smart-query-form-item">
                <a-input-number style="width: 200px" v-model:value="queryForm.activityId" placeholder="活动id" :min="1" />
            </a-form-item>
            <a-form-item label="用户id" class="smart-query-form-item">
                <a-input-number style="width: 200px" v-model:value="queryForm.userId" placeholder="用户id" :min="1" />
            </a-form-item>
            <a-form-item label="是否已删除" class="smart-query-form-item">
                <a-select style="width: 200px" v-model:value="queryForm.deletedFlag" placeholder="请选择" allowClear :options="booleanOptions" />
            </a-form-item>
            <a-form-item label="是否已签到" class="smart-query-form-item">
                <a-select style="width: 200px" v-model:value="queryForm.signInStatus" placeholder="请选择" allowClear :options="booleanOptions" />
            </a-form-item>
            <a-form-item class="smart-query-form-item">
                <a-button type="primary" @click="onSearch">
                    <template #icon>
                        <SearchOutlined />
                    </template>
                    查询
                </a-button>
                <a-button @click="resetQuery" class="smart-margin-left10">
                    <template #icon>
                        <ReloadOutlined />
                    </template>
                    重置
                </a-button>
            </a-form-item>
        </a-row>
    </a-form>
    <!---------- 查询表单form end ----------->

    <a-card size="small" :bordered="false" :hoverable="true">
        <!---------- 表格操作行 begin ----------->
        <a-row class="smart-table-btn-block">
            <div class="smart-table-operate-block">
                <a-button @click="confirmBatchDelete" type="primary" danger size="small" :disabled="selectedRowKeyList.length == 0">
                    <template #icon>
                        <DeleteOutlined />
                    </template>
                    批量删除
                </a-button>
            </div>
            <div class="smart-table-setting-block">
                <TableOperator v-model="columns" :tableId="null" :refresh="queryData" />
            </div>
        </a-row>
        <!---------- 表格操作行 end ----------->

        <!---------- 表格 begin ----------->
        <a-table
            size="small"
            :scroll="{ y: 800 }"
            :dataSource="tableData"
            :columns="columns"
            :rowKey="(record) => record.activityId + '_' + record.userId"
            bordered
            :loading="tableLoading"
            :pagination="false"
            :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
        >
            <template #bodyCell="{ text, record, column }">


                <template v-if="column.dataIndex === 'action'">
                    <div class="smart-table-operate">
                        <a-button @click="onDelete(record)" danger type="link">删除</a-button>
                    </div>
                </template>
            </template>
        </a-table>
        <!---------- 表格 end ----------->

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
                @change="queryData"
                @showSizeChange="queryData"
                :show-total="(total) => `共${total}条`"
            />
        </div>


    </a-card>
</template>
<script setup>
    import { reactive, ref, onMounted } from 'vue';
    import { message, Modal } from 'ant-design-vue';
    import { SmartLoading } from '/src/components/framework/smart-loading';
    import { activityEnrollmentApi } from '/@/api/business/funcampus/activity-enrollment-api';
    import { PAGE_SIZE_OPTIONS } from '/src/constants/common-const';
    import { smartSentry } from '/src/lib/smart-sentry';
    import TableOperator from '/src/components/support/table-operator/index.vue';

    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        {
            title: '活动id',
            dataIndex: 'activityId',
            ellipsis: true,
        },
        {
            title: '用户id',
            dataIndex: 'userId',
            ellipsis: true,
        },
        {
            title: '是否已签到',
            dataIndex: 'signInStatus',
            ellipsis: true,
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            ellipsis: true,
        },
        {
            title: '更新时间',
            dataIndex: 'updateTime',
            ellipsis: true,
        },
        {
            title: '是否已删除',
            dataIndex: 'deletedFlag',
            ellipsis: true,
        },
        {
            title: '操作',
            dataIndex: 'action',
            fixed: 'right',
            width: 90,
        },
    ]);

    // ---------------------------- 查询数据表单和方法 ----------------------------

    const booleanOptions = [
        { label: "否", value: false },
        { label: "是", value: true },
    ];

    const queryFormState = {
        activityId: undefined, //根据活动id查询
        userId: undefined, //根据用户id查询
        deletedFlag: undefined, //根据是否已删除查询
        signInStatus: undefined, //是否已签到，1-》是，0-》否
        pageNum: 1,
        pageSize: 10,
    };
    // 查询表单form
    const queryForm = reactive({ ...queryFormState });
    // 表格加载loading
    const tableLoading = ref(false);
    // 表格数据
    const tableData = ref([]);
    // 总数
    const total = ref(0);

    // 重置查询条件
    function resetQuery() {
        let pageSize = queryForm.pageSize;
        Object.assign(queryForm, queryFormState);
        queryForm.pageSize = pageSize;
        queryData();
    }

    // 搜索
    function onSearch(){
      queryForm.pageNum = 1;
      queryData();
    }

    // 查询数据
    async function queryData() {
        tableLoading.value = true;
        try {
            let queryResult = await activityEnrollmentApi.queryPage(queryForm);
            tableData.value = queryResult.data.list;
            total.value = queryResult.data.total;
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            tableLoading.value = false;
        }
    }


    onMounted(queryData);


    // ---------------------------- 单个删除 ----------------------------
    //确认删除
    function onDelete(data){
        Modal.confirm({
            title: '提示',
            content: '确定要删除吗?',
            okText: '删除',
            okType: 'danger',
            onOk() {
                requestDelete(data);
            },
            cancelText: '取消',
            onCancel() {},
        });
    }

    //请求删除
    async function requestDelete(data){
        SmartLoading.show();
        try {
            await activityEnrollmentApi.delete(data.activityId, data.userId);
            message.success('删除成功');
            queryData();
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            SmartLoading.hide();
        }
    }

    // ---------------------------- 批量删除 ----------------------------

    // 选择表格行
    const selectedRowKeyList = ref([]);
    const selectedRows = ref([]);

    function onSelectChange(selectedRowKeys, rows) {
        selectedRowKeyList.value = selectedRowKeys;
        selectedRows.value = rows;
    }

    // 批量删除
    function confirmBatchDelete() {
        Modal.confirm({
            title: '提示',
            content: '确定要批量删除这些数据吗?',
            okText: '删除',
            okType: 'danger',
            onOk() {
                requestBatchDelete();
            },
            cancelText: '取消',
            onCancel() {},
        });
    }

    //请求批量删除
    async function requestBatchDelete() {
        try {
            SmartLoading.show();
            const keyList = selectedRows.value.map((row) => ({ activityId: row.activityId, userId: row.userId }));
            await activityEnrollmentApi.batchDelete(keyList);
            message.success('删除成功');
            queryData();
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            SmartLoading.hide();
        }
    }
</script>
