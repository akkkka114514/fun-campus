<!--
  * 活动时间表
  *
  * @Author:    akkkka114514
  * @Date:      2025-09-06 15:54:07
  * @Copyright  akkkka114514
-->
<template>
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
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
                <a-button @click="showForm" type="primary" size="small">
                    <template #icon>
                        <PlusOutlined />
                    </template>
                    新建
                </a-button>
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
            rowKey="activityId"
            bordered
            :loading="tableLoading"
            :pagination="false"
            :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
        >
            <template #bodyCell="{ text, record, column }">


                <template v-if="column.dataIndex === 'action'">
                    <div class="smart-table-operate">
                        <a-button @click="showForm(record)" type="link">编辑</a-button>
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

        <ActivityScheduleForm  ref="formRef" @reloadList="queryData"/>

    </a-card>
</template>
<script setup>
    import { reactive, ref, onMounted } from 'vue';
    import { message, Modal } from 'ant-design-vue';
    import { SmartLoading } from '/@/components/framework/smart-loading';
    import { activityScheduleApi } from '/@/api/business/activity-schedule/activity-schedule-api';
    import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
    import { smartSentry } from '/@/lib/smart-sentry';
    import TableOperator from '/@/components/support/table-operator/index.vue';
    import ActivityScheduleForm from './activity-schedule-form.vue';

    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        {
            title: '主键',
            dataIndex: 'activityId',
            ellipsis: true,
        },
        {
            title: '报名开始时间',
            dataIndex: 'enrollStartTime',
            ellipsis: true,
        },
        {
            title: '报名结束时间',
            dataIndex: 'enrollEndTime',
            ellipsis: true,
        },
        {
            title: '活动开始时间',
            dataIndex: 'activityStartTime',
            ellipsis: true,
        },
        {
            title: '活动结束时间',
            dataIndex: 'activityEndTime',
            ellipsis: true,
        },
        {
            title: '签到开始时间',
            dataIndex: 'signinStartTime',
            ellipsis: true,
        },
        {
            title: '签到结束时间',
            dataIndex: 'signinEndTime',
            ellipsis: true,
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            ellipsis: true,
        },
        {
            title: '修改时间',
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

    const queryFormState = {
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
            let queryResult = await activityScheduleApi.queryPage(queryForm);
            tableData.value = queryResult.data.list;
            total.value = queryResult.data.total;
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            tableLoading.value = false;
        }
    }


    onMounted(queryData);

    // ---------------------------- 添加/修改 ----------------------------
    const formRef = ref();

    function showForm(data) {
        formRef.value.show(data);
    }

    // ---------------------------- 单个删除 ----------------------------
    //确认删除
    function onDelete(data){
        Modal.confirm({
            title: '提示',
            content: '确定要删除选吗?',
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
            let deleteForm = {
                goodsIdList: selectedRowKeyList.value,
            };
            await activityScheduleApi.delete(data.activityId);
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

    function onSelectChange(selectedRowKeys) {
        selectedRowKeyList.value = selectedRowKeys;
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
            await activityScheduleApi.batchDelete(selectedRowKeyList.value);
            message.success('删除成功');
            queryData();
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            SmartLoading.hide();
        }
    }
</script>
