<!--
  * 活动退款管理（管理端）
  *
  * @Author:    akkkka114514
  * @Date:      2026-09-26 10:00:00
  * @Copyright  akkkka114514
-->
<template>
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
            <a-form-item label="退款状态" class="smart-query-form-item">
                <a-select style="width: 140px" v-model:value="queryForm.status" placeholder="全部" allowClear :options="statusOptions" />
            </a-form-item>
            <a-form-item label="关键词" class="smart-query-form-item">
                <a-input style="width: 240px" v-model:value="queryForm.keyword" placeholder="退款单号 / 订单号 / 用户名" @pressEnter="onSearch" />
            </a-form-item>
            <a-form-item label="创建时间" class="smart-query-form-item">
                <a-range-picker v-model:value="queryForm.createTime" :presets="defaultTimeRanges" style="width: 240px" @change="onChangeCreateTime" />
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
                <span>退款到账以渠道异步回调为准；「失败」的退款单可重试（新建退款单，保留原单凭证）</span>
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
            :rowKey="(record) => record.refundNo"
            bordered
            :loading="tableLoading"
            :pagination="false"
        >
            <template #bodyCell="{ text, record, column }">
                <template v-if="column.dataIndex === 'username'">
                    {{ record.username || '用户#' + record.userId }}
                </template>
                <template v-if="column.dataIndex === 'amountFen'">
                    {{ formatAmount(record.amountFen) }}
                </template>
                <template v-if="column.dataIndex === 'reasonType'">
                    {{ record.reasonTypeName || '—' }}
                </template>
                <template v-if="column.dataIndex === 'status'">
                    <a-tag :color="statusTagColor(record.status)">{{ record.statusName || '—' }}</a-tag>
                </template>
                <template v-if="column.dataIndex === 'action'">
                    <div class="smart-table-operate">
                        <a-button v-if="record.status === 2" danger type="link" @click="onRetry(record)">重试</a-button>
                        <span v-else>—</span>
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
    import { activityOrderApi } from '/@/api/business/funcampus/activity-order-api';
    import { defaultTimeRanges } from '/@/lib/default-time-ranges';
    import { PAGE_SIZE_OPTIONS } from '/src/constants/common-const';
    import { smartSentry } from '/src/lib/smart-sentry';
    import TableOperator from '/src/components/support/table-operator/index.vue';

    // 退款状态：0-退款中 1-成功 2-失败
    const statusOptions = [
        { label: '退款中', value: 0 },
        { label: '成功', value: 1 },
        { label: '失败', value: 2 },
    ];

    const STATUS_TAG_COLOR = { 0: 'blue', 1: 'green', 2: 'red' };

    function statusTagColor(status) {
        return STATUS_TAG_COLOR[status] || 'default';
    }

    // 金额分转元展示
    function formatAmount(amountFen) {
        if (amountFen == null) {
            return '—';
        }
        return '¥' + (amountFen / 100).toFixed(2);
    }

    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        {
            title: '退款单号',
            dataIndex: 'refundNo',
            ellipsis: true,
            width: 210,
        },
        {
            title: '订单号',
            dataIndex: 'orderNo',
            ellipsis: true,
            width: 210,
        },
        {
            title: '活动标题',
            dataIndex: 'activityTitle',
            ellipsis: true,
            width: 180,
        },
        {
            title: '退款用户',
            dataIndex: 'username',
            width: 120,
        },
        {
            title: '金额',
            dataIndex: 'amountFen',
            width: 100,
        },
        {
            title: '退款原因类型',
            dataIndex: 'reasonType',
            width: 130,
        },
        {
            title: '状态',
            dataIndex: 'status',
            width: 90,
        },
        {
            title: '退款时间',
            dataIndex: 'refundTime',
            width: 160,
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            width: 160,
        },
        {
            title: '操作',
            dataIndex: 'action',
            fixed: 'right',
            width: 80,
        },
    ]);

    // ---------------------------- 查询数据表单和方法 ----------------------------

    const queryFormState = {
        status: undefined, // 退款状态：0-退款中 1-成功 2-失败
        keyword: undefined, // 关键词（退款单号/订单号/用户名模糊匹配）
        createTime: undefined, // 创建时间范围（日期选择器展示用）
        beginCreateTime: undefined, // 创建时间起（含时分秒）
        endCreateTime: undefined, // 创建时间止（含时分秒）
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
    function onSearch() {
        queryForm.pageNum = 1;
        queryData();
    }

    // 时间范围选择：后端 LocalDateTime 需完整格式 yyyy-MM-dd HH:mm:ss
    function onChangeCreateTime(dates, dateStrings) {
        queryForm.beginCreateTime = dateStrings && dateStrings[0] ? `${dateStrings[0]} 00:00:00` : undefined;
        queryForm.endCreateTime = dateStrings && dateStrings[1] ? `${dateStrings[1]} 23:59:59` : undefined;
    }

    // 查询数据
    async function queryData() {
        tableLoading.value = true;
        try {
            let queryResult = await activityOrderApi.refundQueryPage(queryForm);
            tableData.value = queryResult.data.list;
            total.value = queryResult.data.total;
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            tableLoading.value = false;
        }
    }

    onMounted(queryData);

    // ---------------------------- 退款重试 ----------------------------

    // 重试退款失败的退款单：后端新建退款单并重走渠道受理
    function onRetry(record) {
        Modal.confirm({
            title: '退款失败重试',
            content: `确认重新发起退款？退款单：${record.refundNo}，金额：${formatAmount(record.amountFen)}`,
            onOk: async () => {
                try {
                    const res = await activityOrderApi.refundRetry(record.refundNo);
                    message.success(`已重新发起退款，新退款单号：${res.data}`);
                    queryData();
                } catch (e) {
                    smartSentry.captureError(e);
                }
            },
        });
    }
</script>
