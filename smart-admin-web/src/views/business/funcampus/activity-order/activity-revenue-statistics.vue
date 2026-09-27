<!--
  * 活动收入统计（按活动汇总报名费，管理端）
  *
  * @Author:    akkkka114514
  * @Date:      2026-09-27 10:00:00
  * @Copyright  akkkka114514
-->
<template>
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
            <a-form-item label="支付时间" class="smart-query-form-item">
                <a-range-picker v-model:value="queryForm.payTime" :presets="defaultTimeRanges" style="width: 240px" @change="onChangePayTime" />
            </a-form-item>
            <a-form-item class="smart-query-form-item">
                <a-button type="primary" @click="queryData">
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

    <!---------- 合计统计卡 begin ----------->
    <a-row :gutter="16">
        <a-col :span="8">
            <a-card size="small" :bordered="false">
                <a-statistic title="报名费总额" :value="totalPaidYuan" :precision="2" prefix="¥" />
            </a-card>
        </a-col>
        <a-col :span="8">
            <a-card size="small" :bordered="false">
                <a-statistic title="退款总额" :value="totalRefundYuan" :precision="2" prefix="¥" />
            </a-card>
        </a-col>
        <a-col :span="8">
            <a-card size="small" :bordered="false">
                <a-statistic title="净收入" :value="totalNetYuan" :precision="2" prefix="¥" />
            </a-card>
        </a-col>
    </a-row>
    <!---------- 合计统计卡 end ----------->

    <a-card size="small" :bordered="false" :hoverable="true" class="smart-margin-top10">
        <!---------- 表格操作行 begin ----------->
        <a-row class="smart-table-btn-block">
            <div class="smart-table-operate-block">
                <span>
                    口径：报名费计「曾支付成功」订单（已支付/退款中/已退款/退款失败），退款计这些订单中已成功的退款单；净收入 = 报名费 - 退款，按支付时间筛选
                </span>
            </div>
            <div class="smart-table-setting-block">
                <TableOperator v-model="columns" :tableId="null" :refresh="queryData" />
            </div>
        </a-row>
        <!---------- 表格操作行 end ----------->

        <!---------- 表格 begin ----------->
        <a-table
            size="small"
            :dataSource="tableData"
            :columns="columns"
            :rowKey="(record) => record.activityId"
            bordered
            :loading="tableLoading"
            :pagination="false"
        >
            <template #bodyCell="{ record, column }">
                <template v-if="column.dataIndex === 'paidAmountFen'">
                    {{ formatAmount(record.paidAmountFen) }}
                </template>
                <template v-if="column.dataIndex === 'refundAmountFen'">
                    {{ formatAmount(record.refundAmountFen) }}
                </template>
                <template v-if="column.dataIndex === 'netAmountFen'">
                    <span :style="{ color: (record.netAmountFen || 0) >= 0 ? '#3f8600' : '#cf1322' }">{{ formatAmount(record.netAmountFen) }}</span>
                </template>
            </template>
        </a-table>
        <!---------- 表格 end ----------->
    </a-card>
</template>
<script setup>
    import { reactive, ref, onMounted, computed } from 'vue';
    import { activityOrderApi } from '/@/api/business/funcampus/activity-order-api';
    import { defaultTimeRanges } from '/@/lib/default-time-ranges';
    import { smartSentry } from '/src/lib/smart-sentry';
    import TableOperator from '/src/components/support/table-operator/index.vue';

    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        {
            title: '活动ID',
            dataIndex: 'activityId',
            width: 90,
        },
        {
            title: '活动标题',
            dataIndex: 'activityTitle',
            ellipsis: true,
        },
        {
            title: '付费订单数',
            dataIndex: 'paidOrderCount',
            width: 110,
        },
        {
            title: '报名费总额',
            dataIndex: 'paidAmountFen',
            width: 130,
        },
        {
            title: '退款单数',
            dataIndex: 'refundOrderCount',
            width: 100,
        },
        {
            title: '退款总额',
            dataIndex: 'refundAmountFen',
            width: 130,
        },
        {
            title: '净收入',
            dataIndex: 'netAmountFen',
            width: 130,
        },
    ]);

    // ---------------------------- 查询数据表单和方法 ----------------------------

    const queryFormState = {
        payTime: undefined, // 支付时间范围（日期选择器展示用）
        beginPayTime: undefined, // 支付时间起（含时分秒）
        endPayTime: undefined, // 支付时间止（含时分秒）
    };
    // 查询表单form
    const queryForm = reactive({ ...queryFormState });
    // 表格加载loading
    const tableLoading = ref(false);
    // 表格数据
    const tableData = ref([]);

    // 合计（单位：元；按当前查询结果行汇总）
    const totalPaidYuan = computed(() => sumYuan('paidAmountFen'));
    const totalRefundYuan = computed(() => sumYuan('refundAmountFen'));
    const totalNetYuan = computed(() => sumYuan('netAmountFen'));

    function sumYuan(field) {
        const fen = tableData.value.reduce((sum, row) => sum + (row[field] || 0), 0);
        return fen / 100;
    }

    // 金额分转元展示
    function formatAmount(amountFen) {
        if (amountFen == null) {
            return '—';
        }
        return '¥' + (amountFen / 100).toFixed(2);
    }

    // 重置查询条件
    function resetQuery() {
        Object.assign(queryForm, queryFormState);
        queryData();
    }

    // 时间范围选择：后端 LocalDateTime 需完整格式 yyyy-MM-dd HH:mm:ss（按订单支付时间过滤）
    function onChangePayTime(dates, dateStrings) {
        queryForm.beginPayTime = dates && dateStrings[0] ? `${dateStrings[0]} 00:00:00` : undefined;
        queryForm.endPayTime = dates && dateStrings[1] ? `${dateStrings[1]} 23:59:59` : undefined;
    }

    // 查询数据（按活动汇总，无分页）
    async function queryData() {
        tableLoading.value = true;
        try {
            const res = await activityOrderApi.statisticsByActivity({
                beginPayTime: queryForm.beginPayTime,
                endPayTime: queryForm.endPayTime,
            });
            tableData.value = res.data || [];
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            tableLoading.value = false;
        }
    }

    onMounted(queryData);
</script>
