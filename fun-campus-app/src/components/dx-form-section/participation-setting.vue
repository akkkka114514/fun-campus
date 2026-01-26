<template>
  <view class="participation-setting-section">
    <!-- 是否需要审核 -->
    <view class="form-item">
      <view class="label">报名需审核</view>
      <radio-group @change="onAuditRequiredChange">
        <label class="radio-item">
          <radio value="true" :checked="localFormData.enrollNeedReview" color="#ff8c42" />
          <text>是</text>
        </label>
        <label class="radio-item">
          <radio value="false" :checked="!localFormData.enrollNeedReview" color="#ff8c42" />
          <text>否</text>
        </label>
      </radio-group>
    </view>

    <!-- 是否需要签退 -->
    <view class="form-item">
      <view class="label">需要签退</view>
      <radio-group @change="onCheckOutRequiredChange">
        <label class="radio-item">
          <radio value="true" :checked="localFormData.needSignOut" color="#ff8c42" />
          <text>是</text>
        </label>
        <label class="radio-item">
          <radio value="false" :checked="!localFormData.needSignOut" color="#ff8c42" />
          <text>否</text>
        </label>
      </radio-group>
    </view>

    <!-- 参与对象选择 -->
    <view class="form-item">
      <view class="label">参与对象</view>
      <radio-group @change="onParticipationTypeChange">
        <label class="radio-item">
          <radio value="grade" :checked="localParticipationType === 'grade'" color="#ff8c42" />
          <text>按院系年级参与</text>
        </label>
        <label class="radio-item">
          <radio value="tribe" :checked="localParticipationType === 'tribe'" color="#ff8c42" />
          <text>按部落参与</text>
        </label>
      </radio-group>
    </view>

    <!-- 院系年级选择 (当选择按院系年级参与时显示) -->
    <view v-if="localParticipationType === 'grade'">
      <!-- 院系选择 -->
      <view class="form-item">
        <view class="label">选择院系</view>
        <view class="multi-selector" @click="showCollegeMultiSelector = true">
          <view class="picker" :class="{'placeholder': selectedColleges.length === 0}">
            {{
              selectedColleges.length > 0 ? 
              selectedColleges.map(id => {
                const college = colleges.find(c => c.id === id);
                return college ? college.name : '';
              }).join(', ') : 
              '请选择院系(可多选)'
            }}
          </view>
        </view>
      </view>

      <!-- 年级选择 -->
      <view class="form-item">
        <view class="label">选择年级</view>
        <view class="multi-selector" @click="showGradeMultiSelector = true">
          <view class="picker" :class="{'placeholder': selectedGrades.length === 0}">
            {{
              selectedGrades.length > 0 ? 
              selectedGrades.map(id => {
                const grade = grades.find(g => g.id === id);
                return grade ? grade.name : '';
              }).join(', ') : 
              '请选择年级(可多选)'
            }}
          </view>
        </view>
      </view>
    </view>

    <!-- 活动部落 (当选择按部落参与时显示) -->
    <view v-if="localParticipationType === 'tribe'">
      <view class="form-item">
        <view class="label">活动部落</view>
        <view class="search-container">
          <view class="search-input-wrapper">
            <input 
              class="search-input" 
              v-model="tribeSearchKeyword" 
              placeholder="搜索活动部落"
            />
            <button class="search-button" @click="onTribeSearchClick">搜索</button>
          </view>
          <view class="search-results" v-if="filteredTribes.length > 0">
            <view 
              v-for="(tribe, index) in filteredTribes" 
              :key="tribe.id" 
              class="search-result-item"
              @click="selectTribe(tribe.id)"
            >
              <text :class="{'selected': selectedTribes.includes(tribe.id)}">{{ tribe.name }}</text>
            </view>
          </view>
          <view class="selected-tribes">
            <view 
              v-for="id in selectedTribes" 
              :key="'selected-' + id" 
              class="selected-tribe-tag"
            >
              {{ getTribeNameById(id) }}
              <text class="remove-tribe" @click.stop="removeTribe(id)">×</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 院系多选弹窗 -->
    <view v-if="showCollegeMultiSelector" class="selector-popup">
      <view class="popup-overlay" @click="showCollegeMultiSelector = false"></view>
      <view class="popup-content">
        <view class="popup-header">
          <text class="popup-title">选择院系</text>
          <view class="popup-actions">
            <button class="popup-btn" @click="clearSelectedColleges">清空</button>
            <button class="popup-btn confirm-btn" @click="confirmCollegesSelection">确定</button>
          </view>
        </view>
        <scroll-view class="selector-list" scroll-y="true">
          <label v-for="(college, index) in colleges" :key="college.id" class="selector-item">
            <checkbox
              :value="college.id"
              :checked="selectedColleges.includes(college.id)"
              @change="onCollegeCheckboxChange($event, college.id)"
            />
            <text>{{ college.name }}</text>
          </label>
        </scroll-view>
      </view>
    </view>
    
    <!-- 年级多选弹窗 -->
    <view v-if="showGradeMultiSelector" class="selector-popup">
      <view class="popup-overlay" @click="showGradeMultiSelector = false"></view>
      <view class="popup-content">
        <view class="popup-header">
          <text class="popup-title">选择年级</text>
          <view class="popup-actions">
            <button class="popup-btn" @click="clearSelectedGrades">清空</button>
            <button class="popup-btn confirm-btn" @click="confirmGradesSelection">确定</button>
          </view>
        </view>
        <scroll-view class="selector-list" scroll-y="true">
          <label v-for="(grade, index) in grades" :key="grade.id" class="selector-item">
            <checkbox
              :value="grade.id"
              :checked="selectedGrades.includes(grade.id)"
              @change="onGradeCheckboxChange($event, grade.id)"
            />
            <text>{{ grade.name }}</text>
          </label>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, defineEmits, defineProps, computed, watch } from 'vue';
import { querySimpleTribeList } from "@/common/Api";

interface FormData {
  enrollNeedReview: boolean;
  needSignOut: boolean;
  canEnrollCollegeIdList: number[];
  canEnrollGradeIdList: number[];
}

interface Tribe {
  id: number;
  name: string;
}

interface Grade {
  id: number;
  name: string;
}

interface College {
  id: number;
  name: string;
}

interface Props {
  modelValue: FormData;
  participationType: string;
  grades: Grade[];
  colleges: College[];
}

interface Emits {
  (e: 'update:modelValue', value: FormData): void;
  (e: 'update:participationType', value: string): void;
  (e: 'change', value: FormData): void;
  (e: 'participationTypeChange', value: string): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const localFormData = ref<FormData>({...props.modelValue});
const localParticipationType = ref<string>(props.participationType);

// 监听外部值的变化
watch(() => props.modelValue, (newValue) => {
  localFormData.value = {...newValue};
}, { deep: true });

watch(() => props.participationType, (newValue) => {
  localParticipationType.value = newValue;
});

// 监听内部值的变化并同步到父组件
watch(localFormData, (newValue) => {
  emit('update:modelValue', {...newValue});
  emit('change', {...newValue});
}, { deep: true });

watch(localParticipationType, (newValue) => {
  emit('update:participationType', newValue);
  emit('participationTypeChange', newValue);
}, { immediate: true });

// 已选择的院系和年级
const selectedColleges = ref<number[]>(localFormData.value.canEnrollCollegeIdList || []);
const selectedGrades = ref<number[]>(localFormData.value.canEnrollGradeIdList || []);

// 已选择的部落
const selectedTribes = ref<number[]>([]);

// 控制多选弹窗显示
const showCollegeMultiSelector = ref(false);
const showGradeMultiSelector = ref(false);

// 搜索部落的关键词
const tribeSearchKeyword = ref('');

// 活动部落数据（从API获取）
const tribes = ref<Tribe[]>([]);

// 计算属性：过滤后的部落列表
const filteredTribes = computed(() => {
  if (!tribeSearchKeyword.value) {
    return tribes.value.filter(tribe => !selectedTribes.value.includes(tribe.id));
  }
  return tribes.value.filter(tribe => 
    !selectedTribes.value.includes(tribe.id) &&
    tribe.name.toLowerCase().includes(tribeSearchKeyword.value.toLowerCase())
  );
});

// 报名审核需求改变事件
const onAuditRequiredChange = (e: any) => {
  localFormData.value.enrollNeedReview = e.detail.value === 'true';
};

// 签退需求改变事件
const onCheckOutRequiredChange = (e: any) => {
  localFormData.value.needSignOut = e.detail.value === 'true';
};

// 参与类型改变事件
const onParticipationTypeChange = (e: any) => {
  localParticipationType.value = e.detail.value;
};

// 院系复选框改变事件
const onCollegeCheckboxChange = (e: any, id: number) => {
  const checked = e.detail.checked;
  if (checked) {
    if (!selectedColleges.value.includes(id)) {
      selectedColleges.value.push(id);
    }
  } else {
    const index = selectedColleges.value.indexOf(id);
    if (index > -1) {
      selectedColleges.value.splice(index, 1);
    }
  }
  // 同步到formData
  localFormData.value.canEnrollCollegeIdList = [...selectedColleges.value];
};

// 年级复选框改变事件
const onGradeCheckboxChange = (e: any, id: number) => {
  const checked = e.detail.checked;
  if (checked) {
    if (!selectedGrades.value.includes(id)) {
      selectedGrades.value.push(id);
    }
  } else {
    const index = selectedGrades.value.indexOf(id);
    if (index > -1) {
      selectedGrades.value.splice(index, 1);
    }
  }
  // 同步到formData
  localFormData.value.canEnrollGradeIdList = [...selectedGrades.value];
};

// 确认院系选择
const confirmCollegesSelection = () => {
  showCollegeMultiSelector.value = false;
};

// 确认年级选择
const confirmGradesSelection = () => {
  showGradeMultiSelector.value = false;
};

// 清空院系选择
const clearSelectedColleges = () => {
  selectedColleges.value = [];
  localFormData.value.canEnrollCollegeIdList = [];
};

// 清空年级选择
const clearSelectedGrades = () => {
  selectedGrades.value = [];
  localFormData.value.canEnrollGradeIdList = [];
};

// 根据ID获取部落名称
const getTribeNameById = (id: number) => {
  const tribe = tribes.value.find(t => t.id === id);
  return tribe ? tribe.name : '';
};

// 选择部落
const selectTribe = (id: number) => {
  if (!selectedTribes.value.includes(id)) {
    selectedTribes.value.push(id);
  }
  // 清空搜索关键词
  tribeSearchKeyword.value = '';
};

// 移除已选择的部落
const removeTribe = (id: number) => {
  const index = selectedTribes.value.indexOf(id);
  if (index > -1) {
    selectedTribes.value.splice(index, 1);
  }
};

// 部落搜索点击事件
const onTribeSearchClick = async () => {
  if (tribeSearchKeyword.value.trim()) {
    try {
      const response = await querySimpleTribeList(tribeSearchKeyword.value);
      if (response.data.flag) {
        // 过滤掉已选择的部落
        tribes.value = response.data.data.filter((tribe: any) => 
          !selectedTribes.value.includes(tribe.id)
        );
      } else {
        console.error('获取部落列表失败:', response.data.msg);
        tribes.value = [];
      }
    } catch (error) {
      console.error('搜索部落时发生错误:', error);
      tribes.value = [];
    }
  } else {
    // 如果搜索关键词为空，则清空搜索结果
    tribes.value = [];
  }
};
</script>

<style lang="scss" scoped>
.form-item {
  margin-bottom: 40rpx;

  .label {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }

  .radio-item {
    display: inline-block;
    margin-right: 40rpx;
    font-size: 30rpx;
    color: #333;
    align-items: center;

    radio {
      transform: scale(0.8);
    }
  }
}

/* 多选器样式 */
.multi-selector {
  position: relative;
}

.multi-selector .picker {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 12rpx;
  padding: 0 20rpx;
  font-size: 30rpx;
  color: #333;
  display: flex;
  align-items: center;
  background: #fafafa;
}

.multi-selector .picker.placeholder {
  color: #999;
}

/* 弹窗样式 */
.selector-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
}

.popup-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
}

.popup-content {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  max-height: 70vh;
  background: #fff;
  border-radius: 20rpx 20rpx 0 0;
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #eee;
}

.popup-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.popup-actions {
  display: flex;
  gap: 20rpx;
}

.popup-btn {
  padding: 10rpx 20rpx;
  font-size: 28rpx;
  border: 1rpx solid #ddd;
  border-radius: 8rpx;
  background: #f5f5f5;
  color: #333;
}

.confirm-btn {
  border-color: #ff8c42;
  background: #ff8c42;
  color: white;
}

.selector-list {
  flex: 1;
  padding: 20rpx;
}

.selector-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.selector-item:last-child {
  border-bottom: none;
}

.selector-item checkbox {
  margin-right: 20rpx;
}

/* 部落搜索相关样式 */
.search-container {
  position: relative;
  width: 100%;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.search-input {
  flex: 1;
  height: 80rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 12rpx 0 0 12rpx;
  padding: 0 20rpx;
  font-size: 30rpx;
  color: #333;
  transition: all 0.3s ease;
}

.search-input:focus {
  border-color: #ff8c42;
  box-shadow: 0 0 10rpx rgba(255, 140, 66, 0.3);
  outline: none;
}

.search-button {
  height: 80rpx;
  padding: 0 30rpx;
  background: linear-gradient(to right, #ff8c42, #ff6b35);
  color: white;
  border: none;
  border-radius: 0 12rpx 12rpx 0;
  font-size: 30rpx;
  font-weight: bold;
  box-shadow: 0 4rpx 8rpx rgba(255, 140, 66, 0.3);
  transition: all 0.3s ease;
}

.search-button:active {
  transform: translateY(2rpx);
  box-shadow: 0 2rpx 4rpx rgba(255, 140, 66, 0.3);
}

.search-results {
  position: absolute;
  top: calc(80rpx + 4rpx);
  left: 0;
  right: 0;
  background: white;
  border: 2rpx solid #e0e0e0;
  border-top: none;
  border-radius: 0 0 12rpx 12rpx;
  max-height: 300rpx;
  overflow-y: auto;
  z-index: 100;
}

.search-result-item {
  padding: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
  cursor: pointer;
}

.search-result-item:last-child {
  border-bottom: none;
}

.search-result-item:hover,
.search-result-item.selected {
  background-color: #f0f8ff;
}

.selected-tribes {
  margin-top: 20rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
}

.selected-tribe-tag {
  display: flex;
  align-items: center;
  padding: 8rpx 16rpx;
  background: #e1f3d8;
  border-radius: 20rpx;
  font-size: 24rpx;
  color: #2e7d32;
}

.remove-tribe {
  margin-left: 8rpx;
  font-size: 32rpx;
  font-weight: bold;
  color: #c62828;
  cursor: pointer;
}
</style>