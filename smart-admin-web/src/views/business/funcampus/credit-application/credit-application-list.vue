<!--
  * 学分认定审核（管理端）
  *
  * @Author:    akkkka114514
  * @Date:      2026-09-26 10:00:00
  * @Copyright  akkkka114514
-->
<template>
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
            <a-form-item label="审核状态" class="smart-query-form-item">
                <a-select style="width: 160px" v-model:value="queryForm.status" placeholder="全部" allowClear :options="statusOptions" />
            </a-form-item>
            <a-form-item label="关键词" class="smart-query-form-item">
                <a-input style="width: 220px" v-model:value="queryForm.keyword" placeholder="标题 / 申请人" @pressEnter="onSearch" />
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
                <span>仅展示指派给您审核的学分认定申请</span>
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
            :rowKey="(record) => record.id"
            bordered
            :loading="tableLoading"
            :pagination="false"
        >
            <template #bodyCell="{ text, record, column }">
                <template v-if="column.dataIndex === 'status'">
                    <a-tag :color="statusTagColor(record.status)">{{ record.statusName }}</a-tag>
                </template>
                <template v-if="column.dataIndex === 'action'">
                    <div class="smart-table-operate">
                        <a-button @click="openDetail(record)" type="link">详情</a-button>
                        <a-button v-if="record.status === 0" @click="openDetail(record)" type="link">审核</a-button>
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

    <!---------- 详情 / 审核 弹窗 begin ----------->
    <a-modal v-model:open="detailModalVisible" :title="detailModalTitle" :width="720" :footer="null">
        <a-descriptions bordered size="small" :column="2">
            <a-descriptions-item label="标题" :span="2">{{ currentRecord.title }}</a-descriptions-item>
            <a-descriptions-item label="学期">{{ currentRecord.semester }}</a-descriptions-item>
            <a-descriptions-item label="申请人">{{ currentRecord.applicantUsername }}</a-descriptions-item>
            <a-descriptions-item label="审核人">{{ currentRecord.reviewUserName }}</a-descriptions-item>
            <a-descriptions-item label="审核人院系/组织">{{ currentRecord.reviewOrganizationName || '—' }}</a-descriptions-item>
            <a-descriptions-item label="审核状态">
                <a-tag :color="statusTagColor(currentRecord.status)">{{ currentRecord.statusName }}</a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="申请时间">{{ currentRecord.createTime }}</a-descriptions-item>
            <a-descriptions-item label="审核时间" :span="2" v-if="currentRecord.reviewTime">{{ currentRecord.reviewTime }}</a-descriptions-item>
            <a-descriptions-item label="审核意见" :span="2" v-if="currentRecord.status !== 0">{{ currentRecord.reviewRemark || '无' }}</a-descriptions-item>
        </a-descriptions>

        <div class="detail-block">
            <div class="detail-block-title">申请内容</div>
            <div class="detail-block-content">{{ currentRecord.content || '无' }}</div>
        </div>

        <div class="detail-block">
            <div class="detail-block-title">证明材料</div>
            <div v-if="imageUrlList.length > 0" class="detail-block-images">
                <a-image v-for="(url, index) in imageUrlList" :key="index" :src="url" :width="110" />
            </div>
            <div v-else class="detail-block-content">无</div>
        </div>

        <div class="detail-block" v-if="currentRecord.status === 0">
            <div class="detail-block-title">审核操作</div>
            <a-form layout="vertical">
                <a-form-item label="审核结果">
                    <a-radio-group v-model:value="reviewForm.approved">
                        <a-radio :value="true">通过</a-radio>
                        <a-radio :value="false">驳回</a-radio>
                    </a-radio-group>
                </a-form-item>
                <a-form-item :label="reviewForm.approved ? '审核意见（可选）' : '驳回原因（必填）'">
                    <a-textarea
                        v-model:value="reviewForm.reviewRemark"
                        :maxlength="500"
                        show-count
                        :rows="3"
                        :placeholder="reviewForm.approved ? '可填写审核意见' : '请填写驳回原因，将展示给申请人'"
                    />
                </a-form-item>
                <a-button type="primary" :loading="reviewLoading" @click="submitReview">提交审核</a-button>
            </a-form>
        </div>
    </a-modal>
    <!---------- 详情 / 审核 弹窗 end ----------->
</template>
<script setup>
    import { computed, reactive, ref, onMounted } from 'vue';
    import { message } from 'ant-design-vue';
    import { creditApplicationApi } from '/@/api/business/funcampus/credit-application-api';
    import { fileApi } from '/@/api/support/file-api';
    import { PAGE_SIZE_OPTIONS } from '/src/constants/common-const';
    import { smartSentry } from '/src/lib/smart-sentry';
    import TableOperator from '/src/components/support/table-operator/index.vue';

    // 审核状态：0-待审核 1-已通过 2-已驳回
    const statusOptions = [
        { label: '待审核', value: 0 },
        { label: '已通过', value: 1 },
        { label: '已驳回', value: 2 },
    ];

    const STATUS_TAG_COLOR = { 0: 'orange', 1: 'green', 2: 'red' };

    function statusTagColor(status) {
        return STATUS_TAG_COLOR[status] || 'default';
    }

    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        {
            title: '标题',
            dataIndex: 'title',
            ellipsis: true,
            width: 220,
        },
        {
            title: '学期',
            dataIndex: 'semester',
            width: 120,
        },
        {
            title: '申请人',
            dataIndex: 'applicantUsername',
            width: 120,
        },
        {
            title: '审核人院系/组织',
            dataIndex: 'reviewOrganizationName',
            ellipsis: true,
        },
        {
            title: '状态',
            dataIndex: 'status',
            width: 90,
        },
        {
            title: '申请时间',
            dataIndex: 'createTime',
            width: 160,
        },
        {
            title: '审核时间',
            dataIndex: 'reviewTime',
            width: 160,
        },
        {
            title: '操作',
            dataIndex: 'action',
            fixed: 'right',
            width: 120,
        },
    ]);

    // ---------------------------- 查询数据表单和方法 ----------------------------

    const queryFormState = {
        status: undefined, // 审核状态：0-待审核 1-已通过 2-已驳回
        keyword: undefined, // 关键词（标题/申请人模糊匹配）
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

    // 查询数据
    async function queryData() {
        tableLoading.value = true;
        try {
            let queryResult = await creditApplicationApi.queryPage(queryForm);
            tableData.value = queryResult.data.list;
            total.value = queryResult.data.total;
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            tableLoading.value = false;
        }
    }

    onMounted(queryData);

    // ---------------------------- 详情 / 审核 弹窗 ----------------------------

    // 当前查看的记录
    const currentRecord = ref({});
    // 弹窗可见性
    const detailModalVisible = ref(false);
    // 证明材料图片地址列表
    const imageUrlList = ref([]);
    // 审核提交loading
    const reviewLoading = ref(false);

    // 审核表单
    const reviewForm = reactive({
        id: undefined,
        approved: true,
        reviewRemark: '',
    });

    // 弹窗标题：待审核时可操作，已审核时仅查看
    const detailModalTitle = computed(() => (currentRecord.value.status === 0 ? '学分认定审核' : '申请详情'));

    // 打开详情（待审核记录可直接在弹窗内审核）
    async function openDetail(record) {
        currentRecord.value = record;
        imageUrlList.value = [];
        reviewForm.id = record.id;
        reviewForm.approved = true;
        reviewForm.reviewRemark = '';
        detailModalVisible.value = true;
        await loadImageUrls(record.imageList);
    }

    // 证明材料 fileKey 列表 -> 可预览的URL列表
    async function loadImageUrls(imageList) {
        if (!imageList || imageList.length === 0) {
            return;
        }
        try {
            const urls = await Promise.all(
                imageList.map(async (fileKey) => {
                    const res = await fileApi.getUrl(fileKey);
                    return res.data;
                })
            );
            imageUrlList.value = urls.filter((url) => !!url);
        } catch (e) {
            smartSentry.captureError(e);
        }
    }

    // 提交审核
    async function submitReview() {
        if (reviewForm.id == null) {
            return;
        }
        const reviewRemark = String(reviewForm.reviewRemark || '').trim();
        if (!reviewForm.approved && !reviewRemark) {
            message.warning('驳回时请填写驳回原因');
            return;
        }
        reviewLoading.value = true;
        try {
            await creditApplicationApi.review({
                id: reviewForm.id,
                approved: reviewForm.approved,
                reviewRemark: reviewRemark || undefined,
            });
            message.success(reviewForm.approved ? '已通过' : '已驳回');
            detailModalVisible.value = false;
            queryData();
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            reviewLoading.value = false;
        }
    }
</script>
<style scoped lang="less">
    .detail-block {
        margin-top: 16px;
    }

    .detail-block-title {
        font-weight: 600;
        margin-bottom: 8px;
    }

    .detail-block-content {
        white-space: pre-wrap;
        word-break: break-all;
        background: #fafafa;
        border-radius: 4px;
        padding: 8px 12px;
        min-height: 40px;
    }

    .detail-block-images {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
    }
</style>
