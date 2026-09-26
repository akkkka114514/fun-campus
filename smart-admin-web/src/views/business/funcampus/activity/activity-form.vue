<!--
  * 活动管理 - 新建/编辑活动（管理端）
  *
  * 说明：
  * 1. 提交结构对应后端 ActivityWithScheduleAddForm / ActivityWithScheduleUpdateForm；
  * 2. 编辑模式下活动归属（学校/组织/学院）不可修改（后端更新时忽略归属字段）；
  * 3. 报名费用表单以「元」录入，提交时换算为「分」。
  *
  * @Author:    akkkka114514
  * @Date:      2025-09-04 13:41:42
  * @Copyright  akkkka114514
-->
<template>
  <a-drawer
    :title="form.id ? '编辑活动' : '新建活动'"
    :width="860"
    :open="visibleFlag"
    @close="onClose"
    :maskClosable="false"
    :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
      <a-divider orientation="left">基本信息</a-divider>
      <a-form-item label="活动标题" name="title" :rules="[{ required: true, message: '请输入活动标题' }]">
        <a-input v-model:value="form.title" placeholder="请输入活动标题" :maxlength="100" />
      </a-form-item>
      <a-form-item label="活动分类" name="categoryId" :rules="[{ required: true, message: '请选择活动分类' }]">
        <a-select v-model:value="form.categoryId" :options="categoryOptions" placeholder="请选择活动分类" allowClear />
      </a-form-item>
      <a-form-item label="活动地点" name="position" :rules="[{ required: true, message: '请输入活动地点' }]">
        <a-input v-model:value="form.position" placeholder="请输入活动地点" :maxlength="200" />
      </a-form-item>
      <a-form-item label="可获得的学分" name="scoreCanGet" :rules="[{ required: true, message: '请输入可获得的学分' }]">
        <a-input-number v-model:value="form.scoreCanGet" :min="0" :max="100" :step="0.5" placeholder="0 ~ 100" style="width: 100%" />
      </a-form-item>
      <a-form-item label="报名人数限制" name="enrollNumLimit" :rules="[{ required: true, message: '请输入报名人数限制' }]">
        <a-input-number v-model:value="form.enrollNumLimit" :min="0" :max="1000" placeholder="0 ~ 1000" style="width: 100%" />
      </a-form-item>
      <a-form-item
        label="所属学校"
        name="activityBelongToSchoolId"
        :rules="[{ required: true, message: '请选择所属学校' }]"
        :extra="isEdit ? '编辑模式不支持修改活动归属' : ''"
      >
        <a-select
          v-model:value="form.activityBelongToSchoolId"
          :options="schoolOptions"
          placeholder="请选择所属学校"
          :disabled="isEdit"
          show-search
          option-filter-prop="label"
          @change="onSchoolChange"
        />
      </a-form-item>
      <a-form-item label="所属组织" name="activityBelongToOrganizationId">
        <a-select
          v-model:value="form.activityBelongToOrganizationId"
          :options="orgOptions"
          placeholder="组织与学院至少选择其一"
          allowClear
          :disabled="isEdit"
        />
      </a-form-item>
      <a-form-item label="所属学院" name="activityBelongToCollegeId">
        <a-select
          v-model:value="form.activityBelongToCollegeId"
          :options="collegeOptions"
          placeholder="组织与学院至少选择其一"
          allowClear
          :disabled="isEdit"
        />
      </a-form-item>
      <a-form-item label="活动管理员" name="activityManagerId">
        <a-select
          v-model:value="form.activityManagerId"
          :options="userOptions"
          placeholder="可不选"
          allowClear
          show-search
          :filter-option="filterOption"
        />
      </a-form-item>
      <a-form-item label="活动封面" name="coverImg" :rules="[{ required: true, message: '请上传活动封面' }]">
        <Upload
          :defaultFileList="coverFileList"
          :maxUploadSize="1"
          :folder="FILE_FOLDER_TYPE_ENUM.COMMON.value"
          buttonText="上传封面"
          accept=".jpg,.jpeg,.png,.gif"
          @change="onCoverChange"
        />
      </a-form-item>
      <a-form-item label="活动描述" name="description">
        <a-textarea v-model:value="form.description" :rows="4" :maxlength="2000" show-count placeholder="活动介绍、报名须知等" />
      </a-form-item>
      <a-form-item label="报名需审核" name="enrollNeedReview">
        <a-switch v-model:checked="form.enrollNeedReview" checked-children="是" un-checked-children="否" />
      </a-form-item>
      <a-form-item label="需要签退" name="needSignOut">
        <a-switch v-model:checked="form.needSignOut" checked-children="是" un-checked-children="否" />
      </a-form-item>
      <a-form-item label="付费活动" name="paidFlag">
        <a-switch v-model:checked="form.paidFlag" checked-children="是" un-checked-children="否" />
      </a-form-item>
      <template v-if="form.paidFlag">
        <a-form-item label="报名费(元)" name="priceYuan" :rules="[{ required: true, message: '请输入报名费' }]">
          <a-input-number v-model:value="form.priceYuan" :min="0.01" :precision="2" placeholder="报名费（元）" style="width: 100%" />
        </a-form-item>
        <a-form-item label="退款政策" name="refundPolicy" :rules="[{ required: true, message: '请选择退款政策' }]">
          <a-select v-model:value="form.refundPolicy" :options="refundPolicyOptions" placeholder="请选择退款政策" />
        </a-form-item>
      </template>
      <a-form-item label="活动附件" name="attachment">
        <Upload
          :defaultFileList="attachmentFileList"
          :maxUploadSize="1"
          :folder="FILE_FOLDER_TYPE_ENUM.COMMON.value"
          buttonText="上传附件"
          listType="text"
          @change="onAttachmentChange"
        />
      </a-form-item>

      <a-divider orientation="left">活动时间表</a-divider>
      <a-form-item label="报名开始时间" name="enrollStartTime" :rules="[{ required: true, message: '请选择报名开始时间' }]">
        <a-date-picker v-model:value="form.enrollStartTime" show-time valueFormat="YYYY-MM-DD HH:mm:ss" placeholder="请选择" style="width: 100%" />
      </a-form-item>
      <a-form-item label="报名结束时间" name="enrollEndTime" :rules="[{ required: true, message: '请选择报名结束时间' }]">
        <a-date-picker v-model:value="form.enrollEndTime" show-time valueFormat="YYYY-MM-DD HH:mm:ss" placeholder="请选择" style="width: 100%" />
      </a-form-item>
      <a-form-item label="活动开始时间" name="activityStartTime" :rules="[{ required: true, message: '请选择活动开始时间' }]">
        <a-date-picker v-model:value="form.activityStartTime" show-time valueFormat="YYYY-MM-DD HH:mm:ss" placeholder="请选择" style="width: 100%" />
      </a-form-item>
      <a-form-item label="活动结束时间" name="activityEndTime" :rules="[{ required: true, message: '请选择活动结束时间' }]">
        <a-date-picker v-model:value="form.activityEndTime" show-time valueFormat="YYYY-MM-DD HH:mm:ss" placeholder="请选择" style="width: 100%" />
      </a-form-item>
      <a-form-item label="签到开始时间" name="signinStartTime" :rules="[{ required: true, message: '请选择签到开始时间' }]">
        <a-date-picker v-model:value="form.signinStartTime" show-time valueFormat="YYYY-MM-DD HH:mm:ss" placeholder="请选择" style="width: 100%" />
      </a-form-item>
      <a-form-item label="签到结束时间" name="signinEndTime" :rules="[{ required: true, message: '请选择签到结束时间' }]">
        <a-date-picker v-model:value="form.signinEndTime" show-time valueFormat="YYYY-MM-DD HH:mm:ss" placeholder="请选择" style="width: 100%" />
      </a-form-item>
      <template v-if="form.needSignOut">
        <a-form-item label="签退开始时间" name="signoutStartTime" :rules="[{ required: true, message: '请选择签退开始时间' }]">
          <a-date-picker v-model:value="form.signoutStartTime" show-time valueFormat="YYYY-MM-DD HH:mm:ss" placeholder="请选择" style="width: 100%" />
        </a-form-item>
        <a-form-item label="签退结束时间" name="signoutEndTime" :rules="[{ required: true, message: '请选择签退结束时间' }]">
          <a-date-picker v-model:value="form.signoutEndTime" show-time valueFormat="YYYY-MM-DD HH:mm:ss" placeholder="请选择" style="width: 100%" />
        </a-form-item>
      </template>

      <a-divider orientation="left">报名范围（至少选择一项）</a-divider>
      <a-form-item label="可报名学院" name="canEnrollCollegeIdList">
        <a-select
          mode="multiple"
          v-model:value="form.canEnrollCollegeIdList"
          :options="collegeOptions"
          placeholder="可多选"
          allowClear
          option-filter-prop="label"
          show-search
        />
      </a-form-item>
      <a-form-item label="可报名年级" name="canEnrollGradeIdList">
        <a-select
          mode="multiple"
          v-model:value="form.canEnrollGradeIdList"
          :options="gradeOptions"
          placeholder="可多选"
          allowClear
          option-filter-prop="label"
          show-search
        />
      </a-form-item>
      <a-form-item label="可报名部落" name="canEnrollTribeIdList">
        <a-select
          mode="multiple"
          v-model:value="form.canEnrollTribeIdList"
          :options="tribeOptions"
          placeholder="可多选"
          allowClear
          option-filter-prop="label"
          show-search
        />
      </a-form-item>

      <a-divider orientation="left">签到员设置</a-divider>
      <a-form-item label="活动签到员" name="activitySigninManagerIdList">
        <a-select
          mode="multiple"
          v-model:value="form.activitySigninManagerIdList"
          :options="userOptions"
          placeholder="从本校用户中选择"
          allowClear
          show-search
          :filter-option="filterOption"
        />
      </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" :loading="saveLoading" @click="onSubmit">保存</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
  import { reactive, ref, onMounted, nextTick } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { activityApi } from '/@/api/business/funcampus/activity-api';
  import { schoolInfoApi } from '/@/api/business/funcampus/school-info-api';
  import { organizationInfoApi } from '/@/api/business/funcampus/organization-info-api';
  import { collegeInfoApi } from '/@/api/business/funcampus/college-info-api';
  import { gradeInfoApi } from '/@/api/business/funcampus/grade-info-api';
  import { activityCategoryApi } from '/@/api/business/funcampus/activity-category-api';
  import { tribeApi } from '/@/api/business/funcampus/tribe-api';
  import { portalUserApi } from '/@/api/business/funcampus/portal-user-api';
  import { activityCanEnrollCollegeApi } from '/@/api/business/funcampus/activity-can-enroll-college-api';
  import { activityCanEnrollGradeApi } from '/@/api/business/funcampus/activity-can-enroll-grade-api';
  import { activityCanEnrollTribeApi } from '/@/api/business/funcampus/activity-can-enroll-tribe-api';
  import { activitySigninManagerApi } from '/@/api/business/funcampus/activity-signin-manager-api';
  import { fileApi } from '/@/api/support/file-api';
  import { FILE_FOLDER_TYPE_ENUM } from '/@/constants/support/file-const';
  import Upload from '/@/components/support/file-upload/index.vue';

  // ------------------------ 事件 ------------------------

  const emits = defineEmits(['reloadList']);

  // ------------------------ 显示与隐藏 ------------------------

  const visibleFlag = ref(false);
  const isEdit = ref(false);
  const saveLoading = ref(false);

  async function show(rowData) {
    Object.assign(form, formDefault);
    coverFileList.value = [];
    attachmentFileList.value = [];
    isEdit.value = !!(rowData && rowData.id);
    visibleFlag.value = true;
    nextTick(() => {
      formRef.value && formRef.value.clearValidate();
    });
    if (isEdit.value) {
      await loadDetail(rowData.id);
    } else {
      await ensureBaseOptions();
    }
  }

  function onClose() {
    Object.assign(form, formDefault);
    isEdit.value = false;
    coverFileList.value = [];
    attachmentFileList.value = [];
    visibleFlag.value = false;
  }

  // ------------------------ 表单 ------------------------

  const formRef = ref();

  const formDefault = {
    id: undefined, // 活动ID（编辑时使用）
    // 基本信息
    title: undefined,
    categoryId: undefined,
    position: undefined,
    scoreCanGet: undefined,
    enrollNumLimit: undefined,
    activityBelongToSchoolId: undefined,
    activityBelongToOrganizationId: undefined,
    activityBelongToCollegeId: undefined,
    activityManagerId: undefined,
    coverImg: undefined,
    description: undefined,
    enrollNeedReview: false,
    needSignOut: false,
    attachment: undefined,
    paidFlag: false,
    priceYuan: undefined, // 报名费（元），提交时换算为分
    refundPolicy: 1,
    // 时间表
    enrollStartTime: undefined,
    enrollEndTime: undefined,
    activityStartTime: undefined,
    activityEndTime: undefined,
    signinStartTime: undefined,
    signinEndTime: undefined,
    signoutStartTime: undefined,
    signoutEndTime: undefined,
    // 报名范围
    canEnrollCollegeIdList: [],
    canEnrollGradeIdList: [],
    canEnrollTribeIdList: [],
    // 签到员
    activitySigninManagerIdList: [],
  };

  let form = reactive({ ...formDefault });

  // 退款政策选项（与后端 RefundPolicy 一致）
  const refundPolicyOptions = [
    { value: 1, label: '报名截止前可退' },
    { value: 2, label: '活动开始前可退' },
    { value: 3, label: '不可退款' },
  ];

  // ------------------------ 下拉数据 ------------------------

  const schoolOptions = ref([]);
  const categoryOptions = ref([]);
  const gradeOptions = ref([]);
  const orgAllList = ref([]); // 全量组织（按学校过滤）
  const collegeAllList = ref([]); // 全量学院（按学校过滤）
  const orgOptions = ref([]);
  const collegeOptions = ref([]);
  const tribeOptions = ref([]);
  const userOptions = ref([]);

  // 封面/附件回显
  const coverFileList = ref([]);
  const attachmentFileList = ref([]);

  function filterOption(input, option) {
    return (option.label || '').toLowerCase().includes(input.toLowerCase());
  }

  // 加载全局下拉（学校/分类/年级/组织/学院）
  async function initBaseOptions() {
    const [schoolRes, categoryRes, gradeRes, orgRes, collegeRes] = await Promise.all([
      schoolInfoApi.queryPage({ pageNum: 1, pageSize: 500 }),
      activityCategoryApi.queryPage({ pageNum: 1, pageSize: 500 }),
      gradeInfoApi.queryPage({ pageNum: 1, pageSize: 500 }),
      organizationInfoApi.queryPage({ pageNum: 1, pageSize: 500 }),
      collegeInfoApi.queryPage({ pageNum: 1, pageSize: 500 }),
    ]);
    schoolOptions.value = (schoolRes.data.list || []).map((e) => ({ value: e.id, label: e.name }));
    categoryOptions.value = (categoryRes.data.list || []).map((e) => ({ value: e.id, label: e.name }));
    gradeOptions.value = (gradeRes.data.list || []).map((e) => ({ value: e.id, label: e.name }));
    orgAllList.value = orgRes.data.list || [];
    collegeAllList.value = collegeRes.data.list || [];
  }

  async function ensureBaseOptions() {
    if (_.isEmpty(schoolOptions.value)) {
      await initBaseOptions();
    }
  }

  // 按学校加载：组织/学院（本地过滤）、部落/用户（远程查询）
  async function loadSchoolOptions(schoolId) {
    if (!schoolId) {
      orgOptions.value = [];
      collegeOptions.value = [];
      tribeOptions.value = [];
      userOptions.value = [];
      return;
    }
    orgOptions.value = orgAllList.value.filter((e) => e.schoolId === schoolId).map((e) => ({ value: e.id, label: e.name }));
    collegeOptions.value = collegeAllList.value.filter((e) => e.schoolId === schoolId).map((e) => ({ value: e.id, label: e.name }));
    const [tribeRes, userRes] = await Promise.all([
      tribeApi.simpleList(schoolId),
      portalUserApi.queryPage({ schoolId: String(schoolId), pageNum: 1, pageSize: 500 }),
    ]);
    tribeOptions.value = (tribeRes.data || []).map((e) => ({ value: e.id, label: e.name }));
    userOptions.value = (userRes.data.list || []).map((e) => ({ value: e.id, label: e.username }));
  }

  // 新建模式：切换学校时清空依赖项并刷新选项
  async function onSchoolChange(schoolId) {
    if (!isEdit.value) {
      form.activityBelongToOrganizationId = undefined;
      form.activityBelongToCollegeId = undefined;
      form.activityManagerId = undefined;
      form.canEnrollCollegeIdList = [];
      form.canEnrollTribeIdList = [];
      form.activitySigninManagerIdList = [];
    }
    await loadSchoolOptions(schoolId);
  }

  // ------------------------ 编辑回显 ------------------------

  async function loadDetail(activityId) {
    SmartLoading.show();
    try {
      await ensureBaseOptions();
      const res = await activityApi.detail(activityId);
      const activity = res.data.activity || {};
      const schedule = res.data.schedule || {};
      // 基本信息
      form.id = activity.id;
      form.title = activity.title;
      form.categoryId = activity.categoryId;
      form.position = activity.position;
      form.scoreCanGet = activity.scoreCanGet;
      form.enrollNumLimit = activity.enrollNumLimit;
      form.activityBelongToSchoolId = activity.activityBelongToSchoolId;
      form.activityBelongToOrganizationId = activity.activityBelongToOrganizationId || undefined;
      form.activityBelongToCollegeId = activity.activityBelongToCollegeId || undefined;
      form.activityManagerId = activity.activityManagerId || undefined;
      form.coverImg = activity.coverImg || undefined;
      form.description = activity.description;
      form.enrollNeedReview = !!activity.enrollNeedReview;
      form.needSignOut = !!activity.needSignOut;
      form.attachment = activity.attachment || undefined;
      form.paidFlag = !!activity.paidFlag;
      form.priceYuan = activity.priceFen != null ? Number(activity.priceFen) / 100 : undefined;
      form.refundPolicy = activity.refundPolicy || 1;
      // 时间表
      form.enrollStartTime = schedule.enrollStartTime;
      form.enrollEndTime = schedule.enrollEndTime;
      form.activityStartTime = schedule.activityStartTime;
      form.activityEndTime = schedule.activityEndTime;
      form.signinStartTime = schedule.signinStartTime;
      form.signinEndTime = schedule.signinEndTime;
      form.signoutStartTime = schedule.signoutStartTime;
      form.signoutEndTime = schedule.signoutEndTime;
      // 先加载依赖学校的选择项，再回显范围与签到员
      await loadSchoolOptions(form.activityBelongToSchoolId);
      await loadScopeAndManagers(activityId);
      await fillFileLists(activity.coverImg, activity.attachment);
      nextTick(() => {
        formRef.value && formRef.value.clearValidate();
      });
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // 回显报名范围与签到员（按活动ID 查询）
  async function loadScopeAndManagers(activityId) {
    const [collegeRes, gradeRes, tribeRes, signinRes] = await Promise.all([
      activityCanEnrollCollegeApi.queryPage({ activityId, pageNum: 1, pageSize: 200 }),
      activityCanEnrollGradeApi.queryPage({ activityId, pageNum: 1, pageSize: 200 }),
      activityCanEnrollTribeApi.queryPage({ activityId, pageNum: 1, pageSize: 200 }),
      activitySigninManagerApi.queryPage({ activityId, pageNum: 1, pageSize: 200 }),
    ]);
    form.canEnrollCollegeIdList = (collegeRes.data.list || []).map((e) => e.canEnrollCollegeId);
    form.canEnrollGradeIdList = (gradeRes.data.list || []).map((e) => e.canEnrollGrade);
    form.canEnrollTribeIdList = (tribeRes.data.list || []).map((e) => e.canEnrollTribe);
    form.activitySigninManagerIdList = (signinRes.data.list || []).map((e) => e.portalUserId).filter((e) => e != null);
  }

  // 回显封面/附件
  async function fillFileLists(coverImg, attachment) {
    if (coverImg) {
      try {
        const res = await fileApi.getUrl(coverImg);
        coverFileList.value = [{ fileKey: coverImg, fileUrl: res.data, fileName: '活动封面' }];
      } catch (e) {
        smartSentry.captureError(e);
      }
    }
    if (attachment) {
      try {
        const res = await fileApi.getUrl(attachment);
        attachmentFileList.value = [{ fileKey: attachment, fileUrl: res.data, fileName: '活动附件' }];
      } catch (e) {
        smartSentry.captureError(e);
      }
    }
  }

  // ------------------------ 上传 ------------------------

  function onCoverChange(fileList) {
    coverFileList.value = fileList;
    form.coverImg = _.isEmpty(fileList) ? undefined : fileList[0].fileKey;
  }

  function onAttachmentChange(fileList) {
    attachmentFileList.value = fileList;
    form.attachment = _.isEmpty(fileList) ? undefined : fileList[0].fileKey;
  }

  // ------------------------ 校验与提交 ------------------------

  // 报名范围至少一项
  function validateEnrollScope() {
    const empty = _.isEmpty(form.canEnrollCollegeIdList) && _.isEmpty(form.canEnrollGradeIdList) && _.isEmpty(form.canEnrollTribeIdList);
    if (empty) {
      message.error('报名范围不能为空，请至少选择学院/年级/部落其一');
      return false;
    }
    return true;
  }

  // 时间顺序（与后端一致）：报名开始<报名结束<活动开始<活动结束<签到开始<签到结束；签退时：签到结束<签退开始<签退结束
  function validateScheduleOrder() {
    const orderOk =
      form.enrollStartTime < form.enrollEndTime &&
      form.enrollEndTime < form.activityStartTime &&
      form.activityStartTime < form.activityEndTime &&
      form.activityEndTime < form.signinStartTime &&
      form.signinStartTime < form.signinEndTime;
    if (!orderOk) {
      message.error('时间顺序应为：报名开始 < 报名结束 < 活动开始 < 活动结束 < 签到开始 < 签到结束');
      return false;
    }
    if (form.needSignOut) {
      if (!form.signoutStartTime || !form.signoutEndTime) {
        message.error('已开启签退，请填写签退开始/结束时间');
        return false;
      }
      if (!(form.signinEndTime < form.signoutStartTime && form.signoutStartTime < form.signoutEndTime)) {
        message.error('签退时间需满足：签到结束 < 签退开始 < 签退结束');
        return false;
      }
    }
    return true;
  }

  async function onSubmit() {
    try {
      await formRef.value.validateFields();
    } catch (err) {
      message.error('参数验证错误，请仔细填写表单数据!');
      return;
    }
    if (!validateEnrollScope()) {
      return;
    }
    if (form.paidFlag && !(Number(form.priceYuan) > 0)) {
      message.error('付费活动必须配置大于0的报名费');
      return;
    }
    if (!validateScheduleOrder()) {
      return;
    }
    await save();
  }

  function buildActivityForm() {
    return {
      title: form.title,
      position: form.position,
      scoreCanGet: form.scoreCanGet,
      enrollNumLimit: form.enrollNumLimit,
      activityBelongToSchoolId: form.activityBelongToSchoolId,
      activityBelongToOrganizationId: form.activityBelongToOrganizationId || null,
      activityBelongToCollegeId: form.activityBelongToCollegeId || null,
      description: form.description,
      enrollNeedReview: !!form.enrollNeedReview,
      needSignOut: !!form.needSignOut,
      attachment: form.attachment || null,
      categoryId: form.categoryId,
      coverImg: form.coverImg,
      activityManagerId: form.activityManagerId || null,
      paidFlag: !!form.paidFlag,
      priceFen: form.paidFlag ? Math.round(Number(form.priceYuan || 0) * 100) : null,
      refundPolicy: form.paidFlag ? form.refundPolicy : null,
    };
  }

  function buildScheduleForm() {
    return {
      enrollStartTime: form.enrollStartTime,
      enrollEndTime: form.enrollEndTime,
      activityStartTime: form.activityStartTime,
      activityEndTime: form.activityEndTime,
      signinStartTime: form.signinStartTime,
      signinEndTime: form.signinEndTime,
      signoutStartTime: form.needSignOut ? form.signoutStartTime : null,
      signoutEndTime: form.needSignOut ? form.signoutEndTime : null,
    };
  }

  async function save() {
    saveLoading.value = true;
    SmartLoading.show();
    try {
      const param = {
        canEnrollCollegeIdList: form.canEnrollCollegeIdList || [],
        canEnrollGradeIdList: form.canEnrollGradeIdList || [],
        canEnrollTribeIdList: form.canEnrollTribeIdList || [],
        activitySigninManagerIdList: form.activitySigninManagerIdList || [],
      };
      if (form.id) {
        param.activityUpdateForm = { ...buildActivityForm(), id: form.id };
        param.activityScheduleUpdateForm = { ...buildScheduleForm(), activityId: form.id };
        await activityApi.update(param);
      } else {
        param.activityAddForm = buildActivityForm();
        param.activityScheduleAddForm = buildScheduleForm();
        await activityApi.add(param);
      }
      message.success('保存成功');
      emits('reloadList');
      onClose();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      saveLoading.value = false;
      SmartLoading.hide();
    }
  }

  onMounted(() => {
    initBaseOptions().catch((e) => smartSentry.captureError(e));
  });

  defineExpose({
    show,
  });
</script>
