<template>
  <view class="form-page">
    <view class="form-card">
      <!-- 标题 -->
      <view class="form-item">
        <text class="form-label">标题<text class="required">*</text></text>
        <input
          v-model="form.title"
          class="form-input"
          placeholder="如：省级程序设计大赛一等奖"
          :maxlength="255"
          placeholder-class="input-placeholder"
        />
      </view>

      <!-- 学期 -->
      <view class="form-item" @click="openSemester">
        <text class="form-label">学期<text class="required">*</text></text>
        <view class="picker-value">
          <text :class="{ 'picker-placeholder': !form.semester }">
            {{ form.semester || '请选择学期' }}
          </text>
          <text class="form-arrow">></text>
        </view>
      </view>

      <!-- 申请内容 -->
      <view class="form-block">
        <text class="form-label">申请内容</text>
        <textarea
          v-model="form.content"
          class="form-textarea"
          :maxlength="2000"
          placeholder="说明获奖/实践情况、对应学分认定依据等（2000 字以内）"
          placeholder-class="input-placeholder"
        />
      </view>

      <!-- 证明材料 -->
      <view class="form-block">
        <text class="form-label">证明材料（最多 9 张）</text>
        <view class="upload-grid">
          <view v-for="(img, index) in imageList" :key="index" class="upload-cell">
            <image
              class="upload-img"
              :src="resolveFileUrl(img)"
              mode="aspectFill"
              @click="previewImage(index)"
            />
            <view class="upload-remove" @click.stop="removeImage(index)">×</view>
          </view>
          <view
            v-if="imageList.length < 9"
            class="upload-cell upload-add"
            @click="chooseImages"
          >
            <text class="upload-add-icon">+</text>
            <text class="upload-add-text">上传图片</text>
          </view>
        </view>
      </view>

      <!-- 审核人 -->
      <view class="form-item" @click="openReviewer">
        <text class="form-label">审核人<text class="required">*</text></text>
        <view class="picker-value">
          <text :class="{ 'picker-placeholder': !reviewerText }">
            {{ reviewerText || '请选择审核人' }}
          </text>
          <text class="form-arrow">></text>
        </view>
      </view>
    </view>

    <view
      class="submit-btn"
      :class="{ 'submit-disabled': submitting }"
      @click="handleSubmit"
    >
      {{ submitting ? '提交中...' : isEdit ? '保存修改' : '提交申请' }}
    </view>
    <view class="page-tip">提交后可在「学分认定」页查看进度；待审核状态下可编辑或删除</view>

    <!-- 学期选择弹层 -->
    <view v-if="semesterVisible" class="sheet-mask" @click="semesterVisible = false">
      <view class="sheet-body" @click.stop>
        <text class="sheet-title">选择学期</text>
        <view class="option-list">
          <view
            v-for="opt in semesterOptions"
            :key="opt"
            class="option-item"
            :class="{ 'option-item--selected': opt === form.semester }"
            @click="selectSemester(opt)"
          >
            {{ opt }}
          </view>
        </view>
      </view>
    </view>

    <!-- 审核人选择弹层 -->
    <view v-if="reviewerVisible" class="sheet-mask" @click="reviewerVisible = false">
      <view class="sheet-body" @click.stop>
        <text class="sheet-title">选择审核人</text>
        <scroll-view scroll-y class="option-scroll">
          <view v-for="group in reviewerGroups" :key="group.name">
            <text class="group-title">{{ group.name }}</text>
            <view
              v-for="reviewer in group.list"
              :key="reviewer.id"
              class="option-item"
              :class="{ 'option-item--selected': reviewer.id === form.reviewUserId }"
              @click="selectReviewer(reviewer)"
            >
              {{ reviewer.username }}
            </view>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { creditApi } from '@/api/credit-api';
import { uploadFile } from '@/utils/request';
import { getToken } from '@/utils/auth';
import { resolveFileUrl } from '@/utils/format';

const id = ref(null);
const isEdit = computed(() => !!id.value);

const form = reactive({
  title: '',
  semester: '',
  content: '',
  reviewUserId: null,
});
const imageList = ref([]);

const reviewerText = ref('');
const submitting = ref(false);
const inited = ref(false);

// 学期弹层
const semesterVisible = ref(false);
const semesterOptions = buildSemesterOptions();

// 审核人弹层
const reviewerVisible = ref(false);
const reviewerList = ref([]);

// 审核人按院系/组织分组
const reviewerGroups = computed(() => {
  const map = new Map();
  reviewerList.value.forEach((reviewer) => {
    const name = reviewer.organizationName || '学校/其他';
    if (!map.has(name)) {
      map.set(name, []);
    }
    map.get(name).push(reviewer);
  });
  return Array.from(map.entries()).map(([name, list]) => ({ name, list }));
});

onLoad(async (options) => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  id.value = options && options.id ? Number(options.id) : null;
  if (id.value) {
    await prefillFromDetail();
  } else {
    // 新建默认选中当前学期
    form.semester = defaultSemester();
  }
  inited.value = true;
});

/** 生成近年学期选项（9 月起算新学年） */
function buildSemesterOptions() {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + 1;
  const startYear = month >= 9 ? year : year - 1;
  const options = [];
  for (let i = -1; i <= 1; i++) {
    const y = startYear + i;
    options.push(`${y}-${y + 1}学年第一学期`);
    options.push(`${y}-${y + 1}学年第二学期`);
  }
  return options;
}

function defaultSemester() {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + 1;
  const startYear = month >= 9 ? year : year - 1;
  return `${startYear}-${startYear + 1}学年第一学期`;
}

async function prefillFromDetail() {
  uni.showLoading({ title: '加载中...' });
  try {
    const { data } = await creditApi.detail(id.value);
    if (!data) {
      return;
    }
    form.title = data.title || '';
    form.semester = data.semester || '';
    form.content = data.content || '';
    form.reviewUserId = data.reviewUserId || null;
    imageList.value = data.imageList || [];
    if (data.reviewUserId) {
      reviewerText.value = `${data.reviewOrganizationName || '学校'} · ${data.reviewUserName || '审核人'}`;
    }
  } catch (e) {
    // 请求层已统一 toast
  } finally {
    uni.hideLoading();
  }
}

function openSemester() {
  semesterVisible.value = true;
}

function selectSemester(value) {
  form.semester = value;
  semesterVisible.value = false;
}

async function openReviewer() {
  if (!reviewerList.value.length) {
    uni.showLoading({ title: '加载审核人...' });
    try {
      const { data } = await creditApi.reviewerList();
      reviewerList.value = data || [];
    } catch (e) {
      // 请求层已统一 toast
    } finally {
      uni.hideLoading();
    }
  }
  if (!reviewerList.value.length) {
    uni.showToast({ title: '暂无可选审核人', icon: 'none' });
    return;
  }
  reviewerVisible.value = true;
}

function selectReviewer(reviewer) {
  form.reviewUserId = reviewer.id;
  reviewerText.value = `${reviewer.organizationName || '学校'} · ${reviewer.username}`;
  reviewerVisible.value = false;
}

// 选图 → 逐张上传（folder=1 通用），收集 fileKey
function chooseImages() {
  const remain = 9 - imageList.value.length;
  if (remain <= 0) {
    return;
  }
  uni.chooseImage({
    count: remain,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const filePaths = res.tempFilePaths || [];
      if (!filePaths.length) {
        return;
      }
      uni.showLoading({ title: '上传中...' });
      const uploaded = [];
      try {
        for (const filePath of filePaths) {
          const { data } = await uploadFile(filePath, {
            url: '/portal/file/upload',
            formData: { folder: 1 },
          });
          if (data && data.fileKey) {
            uploaded.push(data.fileKey);
          }
        }
        imageList.value = imageList.value.concat(uploaded);
        uni.showToast({ title: `已上传 ${uploaded.length} 张`, icon: 'none' });
      } catch (e) {
        // 请求层已统一 toast；已成功的先保留
        imageList.value = imageList.value.concat(uploaded);
      } finally {
        uni.hideLoading();
      }
    },
  });
}

function removeImage(index) {
  imageList.value.splice(index, 1);
}

function previewImage(index) {
  uni.previewImage({
    urls: imageList.value.map((img) => resolveFileUrl(img)),
    current: index,
  });
}

async function handleSubmit() {
  if (submitting.value) {
    return;
  }
  const title = form.title.trim();
  if (!title) {
    uni.showToast({ title: '请填写标题', icon: 'none' });
    return;
  }
  if (!form.semester) {
    uni.showToast({ title: '请选择学期', icon: 'none' });
    return;
  }
  if (!form.reviewUserId) {
    uni.showToast({ title: '请选择审核人', icon: 'none' });
    return;
  }

  const payload = {
    title,
    semester: form.semester,
    content: form.content.trim() || undefined,
    imageList: imageList.value.length ? imageList.value : undefined,
    reviewUserId: form.reviewUserId,
  };

  submitting.value = true;
  try {
    if (isEdit.value) {
      await creditApi.update({ ...payload, id: id.value });
    } else {
      await creditApi.apply(payload);
    }
    uni.showToast({ title: isEdit.value ? '修改已保存' : '申请已提交', icon: 'success' });
    setTimeout(() => {
      uni.navigateBack();
    }, 700);
  } catch (e) {
    // 请求层已统一 toast
  } finally {
    submitting.value = false;
  }
}
</script>

<style lang="scss" scoped>
.form-page {
  min-height: 100vh;
  background: $fc-bg;
  padding: 24rpx;
  box-sizing: border-box;
}

.form-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 0 28rpx;
}

.form-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 0;
  border-bottom: 1rpx solid #f5f6f8;
}

.form-item:last-child {
  border-bottom: none;
}

.form-block {
  padding: 28rpx 0;
  border-bottom: 1rpx solid #f5f6f8;
}

.form-block:last-child {
  border-bottom: none;
}

.form-label {
  flex-shrink: 0;
  font-size: 30rpx;
  color: $fc-text-main;
  margin-right: 24rpx;
}

.required {
  color: #fa3534;
  margin-left: 4rpx;
}

.form-input {
  flex: 1;
  font-size: 30rpx;
  color: $fc-text-main;
  text-align: right;
}

.input-placeholder {
  color: #c0c4cc;
}

.picker-value {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.picker-value text {
  font-size: 30rpx;
  color: $fc-text-main;
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.picker-placeholder {
  color: #c0c4cc !important;
}

.form-arrow {
  margin-left: 12rpx;
  font-size: 28rpx;
  color: #c0c4cc;
}

.form-textarea {
  width: 100%;
  height: 240rpx;
  margin-top: 20rpx;
  padding: 20rpx;
  box-sizing: border-box;
  background: #f5f6f8;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: $fc-text-main;
}

.upload-grid {
  display: flex;
  flex-wrap: wrap;
  margin-top: 20rpx;
}

.upload-cell {
  position: relative;
  width: 200rpx;
  height: 200rpx;
  margin: 0 16rpx 16rpx 0;
  border-radius: 12rpx;
  overflow: hidden;
  background: #f5f6f8;
}

.upload-img {
  width: 100%;
  height: 100%;
}

.upload-remove {
  position: absolute;
  top: 0;
  right: 0;
  width: 44rpx;
  height: 44rpx;
  line-height: 40rpx;
  text-align: center;
  background: rgba(0, 0, 0, 0.5);
  color: #ffffff;
  font-size: 32rpx;
  border-bottom-left-radius: 12rpx;
}

.upload-add {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2rpx dashed #c0c4cc;
  box-sizing: border-box;
}

.upload-add-icon {
  font-size: 56rpx;
  color: #c0c4cc;
  line-height: 1;
}

.upload-add-text {
  margin-top: 12rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.submit-btn {
  margin-top: 48rpx;
  background: $fc-primary;
  border-radius: 44rpx;
  text-align: center;
  padding: 26rpx 0;
  font-size: 32rpx;
  color: #ffffff;
}

.submit-disabled {
  opacity: 0.6;
}

.page-tip {
  text-align: center;
  font-size: 24rpx;
  color: $fc-text-sub;
  padding: 24rpx 16rpx;
  line-height: 1.6;
}

/* 弹层 */
.sheet-mask {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 100;
  display: flex;
  align-items: flex-end;
}

.sheet-body {
  width: 100%;
  background: #ffffff;
  border-radius: 28rpx 28rpx 0 0;
  padding: 36rpx 32rpx calc(36rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
}

.sheet-title {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: $fc-text-main;
  margin-bottom: 12rpx;
}

.option-list {
  padding-bottom: 8rpx;
}

.option-scroll {
  max-height: 560rpx;
}

.group-title {
  display: block;
  margin-top: 24rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.option-item {
  padding: 26rpx 0;
  font-size: 30rpx;
  color: $fc-text-main;
  border-bottom: 1rpx solid #f5f6f8;
}

.option-item--selected {
  color: $fc-primary;
  font-weight: 600;
}
</style>
