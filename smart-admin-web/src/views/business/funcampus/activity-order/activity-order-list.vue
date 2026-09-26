<!--
  * 活动报名订单管理（管理端）
  *
  * @Author:    akkkka114514
  * @Date:      2026-09-26 10:00:00
  * @Copyright  akkkka114514
-->
<template>
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
            <a-form-item label="订单状态" class="smart-query-form-item">
                <a-select style="width: 140px" v-model:value="queryForm.status" placeholder="全部" allowClear :options="statusOptions" />
            </a-form-item>
            <a-form-item label="关键词" class="smart-query-form-item">
                <a-input style="width: 240px" v-model:value="queryForm.keyword" placeholder="订单号 / 活动标题 / 用户名" @pressEnter="onSearch" />
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
                <span>付费报名订单全量查询；退款失败订单可在「退款管理」中重试</span>
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
            :rowKey="(record) => record.orderNo"
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
                <template v-if="column.dataIndex === 'status'">
                    <a-tag :color="statusTagColor(record.status)">{{ statusName(record.status) }}</a-tag>
                </template>
                <template v-if="column.dataIndex === 'payChannel'">
                    {{ payChannelName(record.payChannel) }}
                </template>
                <template v-if="column.dataIndex === 'action'">
                    <div class="smart-table-operate">
                        <a-button @click="openDetail(record)" type="link">详情</a-button>
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

    <!---------- 订单详情弹窗 begin ----------->
    <a-modal v-model:open="detailModalVisible" title="订单详情" :width="720" :footer="null">
        <a-descriptions bordered size="small" :column="2">
            <a-descriptions-item label="订单号" :span="2">{{ currentRecord.orderNo }}</a-descriptions-item>
            <a-descriptions-item label="活动标题" :span="2">{{ currentRecord.activityTitle || '—' }}</a-descriptions-item>
            <a-descriptions-item label="下单用户">
                {{ currentRecord.username || '—' }}（id: {{ currentRecord.userId }}）
            </a-descriptions-item>
            <a-descriptions-item label="订单金额">{{ formatAmount(currentRecord.amountFen) }}</a-descriptions-item>
            <a-descriptions-item label="订单状态">
                <a-tag :color="statusTagColor(currentRecord.status)">{{ statusName(currentRecord.status) }}</a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="支付渠道">{{ payChannelName(currentRecord.payChannel) }}</a-descriptions-item>
            <a-descriptions-item label="渠道订单号" :span="2">{{ currentRecord.channelOrderNo || '—' }}</a-descriptions-item>
            <a-descriptions-item label="支付时间">{{ currentRecord.payTime || '—' }}</a-descriptions-item>
            <a-descriptions-item label="支付截止时间">{{ currentRecord.expireTime || '—' }}</a-descriptions-item>
            <a-descriptions-item label="关闭时间">{{ currentRecord.closeTime || '—' }}</a-descriptions-item>
            <a-descriptions-item label="创建时间">{{ currentRecord.createTime || '—' }}</a-descriptions-item>
        </a-descriptions>
    </a-modal>
    <!---------- 订单详情弹窗 end ----------->
</template>
<script setup>
    import { reactive, ref, onMounted } from 'vue';
    import { activityOrderApi } from '/@/api/business/funcampus/activity-order-api';
    import { defaultTimeRanges } from '/@/lib/default-time-ranges';
    import { PAGE_SIZE_OPTIONS } from '/src/constants/common-const';
    import { smartSentry } from '/src/lib/smart-sentry';
    import TableOperator from '/src/components/support/table-operator/index.vue';

    // 订单状态：0-待支付 1-已支付 2-已关闭 3-退款中 4-已退款 5-退款失败
    const statusOptions = [
        { label: '待支付', value: 0 },
        { label: '已支付', value: 1 },
        { label: '已关闭', value: 2 },
        { label: '退款中', value: 3 },
        { label: '已退款', value: 4 },
        { label: '退款失败', value: 5 },
    ];

    const STATUS_TAG_COLOR = { 0: 'orange', 1: 'green', 2: 'default', 3: 'blue', 4: 'purple', 5: 'red' };
    const STATUS_NAME = { 0: '待支付', 1: '已支付', 2: '已关闭', 3: '退款中', 4: '已退款', 5: '退款失败' };
    const PAY_CHANNEL_NAME = { 1: '微信', 2: '支付宝', 3: 'Mock' };

    function statusTagColor(status) {
        return STATUS_TAG_COLOR[status] || 'default';
    }

    function statusName(status) {
        return STATUS_NAME[status] || '—';
    }

    function payChannelName(payChannel) {
        return PAY_CHANNEL_NAME[payChannel] || '—';
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
            title: '订单号',
            dataIndex: 'orderNo',
            ellipsis: true,
            width: 210,
        },
        {
            title: '活动标题',
            dataIndex: 'activityTitle',
            ellipsis: true,
            width: 200,
        },
        {
            title: '下单用户',
            dataIndex: 'username',
            width: 130,
        },
        {
            title: '金额',
            dataIndex: 'amountFen',
            width: 100,
        },
        {
            title: '状态',
            dataIndex: 'status',
            width: 90,
        },
        {
            title: '支付渠道',
            dataIndex: 'payChannel',
            width: 90,
        },
        {
            title: '支付时间',
            dataIndex: 'payTime',
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
        status: undefined, // 订单状态：0-待支付 1-已支付 2-已关闭 3-退款中 4-已退款 5-退款失败
        keyword: undefined, // 关键词（订单号/活动标题/用户名模糊匹配）
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
            let queryResult = await activityOrderApi.queryPage(queryForm);
            tableData.value = queryResult.data.list;
            total.value = queryResult.data.total;
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            tableLoading.value = false;
        }
    }

    onMounted(queryData);

    // ---------------------------- 订单详情弹窗 ----------------------------

    // 当前查看的记录
    const currentRecord = ref({});
    // 弹窗可见性
    const detailModalVisible = ref(false);

    // 打开详情：先展示行数据，再拉取最新详情覆盖
    async function openDetail(record) {
        currentRecord.value = { ...record };
        detailModalVisible.value = true;
        try {
            const res = await activityOrderApi.detail(record.orderNo);
            currentRecord.value = res.data;
        } catch (e) {
            smartSentry.captureError(e);
        }
    }
</script>
