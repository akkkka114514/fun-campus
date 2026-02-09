<template>
  <view class="participation-setting-section" @click="hideSearchResults">
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

    <!-- 时间设置 -->
    <view class="form-item">
      <view class="label">报名开始时间</view>
      <tm-time-picker
        v-model="localFormData.enrollStartTime"
        :start="minDate"
        :end="maxDate"
        title="选择报名开始时间"
        :showDetail="{year: true, month: true, day: true, hour: true, minute: true}"
        format="YYYY/MM/DD HH:mm"
      >
        <view class="picker-display" :class="{'placeholder': !localFormData.enrollStartTime}">
          {{ localFormData.enrollStartTime ? formatDate(localFormData.enrollStartTime) : '请选择报名开始时间' }}
        </view>
      </tm-time-picker>
    </view>

    <view class="form-item">
      <view class="label">报名结束时间</view>
      <tm-time-picker
        v-model="localFormData.enrollEndTime"
        :start="localFormData.enrollStartTime || minDate"
        :end="maxDate"
        title="选择报名结束时间"
        :showDetail="{year: true, month: true, day: true, hour: true, minute: true}"
        format="YYYY/MM/DD HH:mm"
        :disabled="!localFormData.enrollStartTime"
        @confirm="validateEnrollEndTime"
      >
        <view class="picker-display" :class="{'placeholder': !localFormData.enrollEndTime}">
          {{ localFormData.enrollEndTime ? formatDate(localFormData.enrollEndTime) : '请选择报名结束时间' }}
        </view>
      </tm-time-picker>
    </view>

    <view class="form-item">
      <view class="label">活动开始时间</view>
      <tm-time-picker
        v-model="localFormData.activityStartTime"
        :start="localFormData.enrollEndTime || minDate"
        :end="maxDate"
        title="选择活动开始时间"
        :showDetail="{year: true, month: true, day: true, hour: true, minute: true}"
        format="YYYY/MM/DD HH:mm"
        :disabled="!localFormData.enrollEndTime"
        @confirm="validateActivityStartTime"
      >
        <view class="picker-display" :class="{'placeholder': !localFormData.activityStartTime}">
          {{ localFormData.activityStartTime ? formatDate(localFormData.activityStartTime) : '请选择活动开始时间' }}
        </view>
      </tm-time-picker>
    </view>

    <view class="form-item">
      <view class="label">活动结束时间</view>
      <tm-time-picker
        v-model="localFormData.activityEndTime"
        :start="localFormData.activityStartTime || minDate"
        :end="maxDate"
        title="选择活动结束时间"
        :showDetail="{year: true, month: true, day: true, hour: true, minute: true}"
        format="YYYY/MM/DD HH:mm"
        :disabled="!localFormData.activityStartTime"
        @confirm="validateActivityEndTime"
      >
        <view class="picker-display" :class="{'placeholder': !localFormData.activityEndTime}">
          {{ localFormData.activityEndTime ? formatDate(localFormData.activityEndTime) : '请选择活动结束时间' }}
        </view>
      </tm-time-picker>
    </view>

    <view class="form-item">
      <view class="label">签到开始时间</view>
      <tm-time-picker
        v-model="localFormData.checkInStartTime"
        :start="localFormData.activityEndTime || minDate"
        :end="maxDate"
        title="选择签到开始时间"
        :showDetail="{year: true, month: true, day: true, hour: true, minute: true}"
        format="YYYY/MM/DD HH:mm"
        :disabled="!localFormData.activityEndTime"
        @confirm="validateCheckInStartTime"
      >
        <view class="picker-display" :class="{'placeholder': !localFormData.checkInStartTime}">
          {{ localFormData.checkInStartTime ? formatDate(localFormData.checkInStartTime) : '请选择签到开始时间' }}
        </view>
      </tm-time-picker>
    </view>

    <view class="form-item">
      <view class="label">签到结束时间</view>
      <tm-time-picker
        v-model="localFormData.checkInEndTime"
        :start="localFormData.checkInStartTime || minDate"
        :end="maxDate"
        title="选择签到结束时间"
        :showDetail="{year: true, month: true, day: true, hour: true, minute: true}"
        format="YYYY/MM/DD HH:mm"
        :disabled="!localFormData.checkInStartTime"
        @confirm="validateCheckInEndTime"
      >
        <view class="picker-display" :class="{'placeholder': !localFormData.checkInEndTime}">
          {{ localFormData.checkInEndTime ? formatDate(localFormData.checkInEndTime) : '请选择签到结束时间' }}
        </view>
      </tm-time-picker>
    </view>

    <!-- 签退时间设置 (当需要签退时显示) -->
    <view v-if="localFormData.needSignOut">
      <view class="form-item">
        <view class="label">签退开始时间</view>
        <tm-time-picker
          v-model="localFormData.checkOutStartTime"
          :start="localFormData.checkInEndTime || minDate"
          :end="maxDate"
          title="选择签退开始时间"
          :showDetail="{year: true, month: true, day: true, hour: true, minute: true}"
          format="YYYY/MM/DD HH:mm"
          :disabled="!localFormData.checkInEndTime"
          @confirm="validateCheckOutStartTime"
        >
          <view class="picker-display" :class="{'placeholder': !localFormData.checkOutStartTime}">
            {{ localFormData.checkOutStartTime ? formatDate(localFormData.checkOutStartTime) : '请选择签退开始时间' }}
          </view>
        </tm-time-picker>
      </view>

      <view class="form-item">
        <view class="label">签退结束时间</view>
        <tm-time-picker
          v-model="localFormData.checkOutEndTime"
          :start="localFormData.checkOutStartTime || minDate"
          :end="maxDate"
          title="选择签退结束时间"
          :showDetail="{year: true, month: true, day: true, hour: true, minute: true}"
          format="YYYY/MM/DD HH:mm"
          :disabled="!localFormData.checkOutStartTime"
          @confirm="validateCheckOutEndTime"
        >
          <view class="picker-display" :class="{'placeholder': !localFormData.checkOutEndTime}">
            {{ localFormData.checkOutEndTime ? formatDate(localFormData.checkOutEndTime) : '请选择签退结束时间' }}
          </view>
        </tm-time-picker>
      </view>
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
        <view class="picker-trigger" @click="showCollegeSelector = true">
          <view class="picker-display" :class="{'placeholder': selectedColleges.length === 0}">
            {{
              selectedColleges.length > 0 ? 
              selectedColleges.map(id => getCollegeNameById(id)).join(', ') : 
              '请选择院系(可多选)'
            }}
          </view>
          <view class="selected-tags">
            <view 
              v-for="id in selectedColleges" 
              :key="'selected-college-' + id" 
              class="tag-item"
            >
              {{ getCollegeNameById(id) }}
              <text class="remove-tag" @click.stop="removeCollege(id)">×</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 年级选择 -->
      <view class="form-item">
        <view class="label">选择年级</view>
        <view class="picker-trigger" @click="showGradeSelector = true">
          <view class="picker-display" :class="{'placeholder': selectedGrades.length === 0}">
            {{
              selectedGrades.length > 0 ? 
              selectedGrades.map(id => getGradeNameById(id)).join(', ') : 
              '请选择年级(可多选)'
            }}
          </view>
          <view class="selected-tags">
            <view 
              v-for="id in selectedGrades" 
              :key="'selected-grade-' + id" 
              class="tag-item"
            >
              {{ getGradeNameById(id) }}
              <text class="remove-tag" @click.stop="removeGrade(id)">×</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 活动部落 (当选择按部落参与时显示) -->
    <view v-if="localParticipationType === 'tribe'">
      <view class="form-item">
        <view class="label">活动部落</view>
        <view class="search-container" @click.stop>
          <view class="search-input-wrapper">
            <input 
              class="search-input" 
              v-model="tribeSearchKeyword" 
              placeholder="搜索活动部落"
              @focus="showSearchResults = true"
            />
            <button class="search-button" @click="onTribeSearchClick">搜索</button>
          </view>
          <view class="search-results" v-if="showSearchResults && filteredTribes.length > 0">
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
    
    <!-- 院系选择抽屉 -->
    <tm-drawer 
      v-model:show="showCollegeSelector"
      title="选择院系"
      placement="bottom"
      :mask="true"
      :overlayClick="true"
      :closeable="true"
      :teleport="true"
      :inContent="false"
    >
      <view class="drawer-content">
        <scroll-view class="checkbox-list" scroll-y="true">
          <tm-checkbox-group v-model="tempSelectedColleges">
            <tm-checkbox
              v-for="(college, index) in colleges"
              :key="college.id"
              :value="college.id"
              :label="college.name"
            />
          </tm-checkbox-group>
          <tm-button label="确定" @click="confirmCollegeSelection" />
        </scroll-view>
      </view>
    </tm-drawer>
    
    <!-- 年级选择抽屉 -->
    <tm-drawer 
      v-model:show="showGradeSelector"
      title="选择年级"
      :overlayClick="true"
      :closeable="true"
      :teleport="true"
      :inContent="false"
      placement="bottom"
    >
      <view class="drawer-content">
        <scroll-view class="checkbox-list" scroll-y="true">
          <tm-checkbox-group v-model="tempSelectedGrades">
            <tm-checkbox
              v-for="(grade, index) in grades"
              :key="grade.id"
              :value="grade.id"
              :label="grade.name"
            />
          </tm-checkbox-group>
          <tm-button label="确定" @click="confirmGradeSelection"></tm-button>
        </scroll-view>
      </view>
    </tm-drawer>
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
  enrollStartTime: string | number | Date;
  enrollEndTime: string | number | Date;
  activityStartTime: string | number | Date;
  activityEndTime: string | number | Date;
  checkInStartTime: string | number | Date;
  checkInEndTime: string | number | Date;
  checkOutStartTime: string | number | Date;
  checkOutEndTime: string | number | Date;
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
  (e: 'collegeSelectionComplete', value: number[]): void;
  (e: 'gradeSelectionComplete', value: number[]): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const localFormData = ref<FormData>({...props.modelValue});
const localParticipationType = ref<string>(props.participationType);

// 设置最小日期为当前时间
const minDate = new Date();
// 设置最大日期为当前时间往后一年
const maxDate = new Date();
maxDate.setFullYear(maxDate.getFullYear() + 1);

// 格式化日期显示
const formatDate = (date: string | number | Date) => {
  if (!date) return '';
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hour = String(d.getHours()).padStart(2, '0');
  const minute = String(d.getMinutes()).padStart(2, '0');
  return `${year}/${month}/${day} ${hour}:${minute}`;
};

// 已选择的院系和年级
const selectedColleges = ref<number[]>(localFormData.value.canEnrollCollegeIdList || []);
const selectedGrades = ref<number[]>(localFormData.value.canEnrollGradeIdList || []);

// 临时选择状态（用于弹窗确认前的临时存储）
const tempSelectedColleges = ref<number[]>([...selectedColleges.value]);
const tempSelectedGrades = ref<number[]>([...selectedGrades.value]);

// 控制弹窗显示
const showCollegeSelector = ref(false);
const showGradeSelector = ref(false);

// 已选择的部落
const selectedTribes = ref<number[]>([]);

// 搜索部落的关键词
const tribeSearchKeyword = ref('');

// 控制搜索结果显示
const showSearchResults = ref(false);

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
    if (!tempSelectedColleges.value.includes(id)) {
      tempSelectedColleges.value.push(id);
    }
  } else {
    const index = tempSelectedColleges.value.indexOf(id);
    if (index > -1) {
      tempSelectedColleges.value.splice(index, 1);
    }
  }
};

// 年级复选框改变事件
const onGradeCheckboxChange = (e: any, id: number) => {
  const checked = e.detail.checked;
  if (checked) {
    if (!tempSelectedGrades.value.includes(id)) {
      tempSelectedGrades.value.push(id);
    }
  } else {
    const index = tempSelectedGrades.value.indexOf(id);
    if (index > -1) {
      tempSelectedGrades.value.splice(index, 1);
    }
  }
};

// 确认院系选择
const confirmCollegeSelection = () => {
  console.log(tempSelectedColleges.value)
  selectedColleges.value = [...tempSelectedColleges.value];
  localFormData.value.canEnrollCollegeIdList = [...tempSelectedColleges.value];
  emit('collegeSelectionComplete', [...tempSelectedColleges.value]);
  showCollegeSelector.value = false;
};

// 确认年级选择
const confirmGradeSelection = () => {
  selectedGrades.value = [...tempSelectedGrades.value];
  localFormData.value.canEnrollGradeIdList = [...tempSelectedGrades.value];
  console.log('年级选择完成:'+tempSelectedGrades.value)
  emit('gradeSelectionComplete', [...tempSelectedGrades.value]);
  showGradeSelector.value = false;
};

// 清空院系选择
const clearSelectedColleges = () => {
  tempSelectedColleges.value = [];
};

// 清空年级选择
const clearSelectedGrades = () => {
  tempSelectedGrades.value = [];
};

// 互斥打开drawer的函数
const openCollegeDrawer = () => {
  // 关闭其他drawer
  showGradeSelector.value = false;
  // 打开当前drawer
  showCollegeSelector.value = true;
};

const openGradeDrawer = () => {
  // 关闭其他drawer
  showCollegeSelector.value = false;
  // 打开当前drawer
  showGradeSelector.value = true;
};

// 处理院系drawer取消事件（包括外部点击）
const handleCollegeCancel = () => {
  showCollegeSelector.value = false;
};

// 处理年级drawer取消事件（包括外部点击）
const handleGradeCancel = () => {
  showGradeSelector.value = false;
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
      if (response.data.ok) {
        // 过滤掉已选择的部落
        tribes.value = response.data.data.filter((tribe: any) => 
          !selectedTribes.value.includes(tribe.id)
        );
        showSearchResults.value = true;
      } else {
        console.error('获取部落列表失败:', response.data.msg);
        tribes.value = [];
        showSearchResults.value = false;
      }
    } catch (error) {
      console.error('搜索部落时发生错误:', error);
      tribes.value = [];
      showSearchResults.value = false;
    }
  } else {
    // 如果搜索关键词为空，则清空搜索结果
    tribes.value = [];
    showSearchResults.value = false;
  }
};

// 点击其他地方隐藏搜索结果
const hideSearchResults = () => {
  showSearchResults.value = false;
};

// 根据ID获取院系名称
const getCollegeNameById = (id: number) => {
  const college = props.colleges.find(c => c.id === id);
  return college ? college.name : '';
};

// 根据ID获取年级名称
const getGradeNameById = (id: number) => {
  const grade = props.grades.find(g => g.id === id);
  return grade ? grade.name : '';
};

// 验证报名结束时间
const validateEnrollEndTime = (value: any) => {
  if (localFormData.value.enrollStartTime && value) {
    const startTime = new Date(localFormData.value.enrollStartTime);
    const endTime = new Date(value);
    if (endTime <= startTime) {
      uni.showToast({
        title: '报名结束时间必须晚于报名开始时间',
        icon: 'none'
      });
      localFormData.value.enrollEndTime = '';
    }
  }
};

// 验证活动开始时间
const validateActivityStartTime = (value: any) => {
  if (localFormData.value.enrollEndTime && value) {
    const enrollEndTime = new Date(localFormData.value.enrollEndTime);
    const activityStartTime = new Date(value);
    if (activityStartTime <= enrollEndTime) {
      uni.showToast({
        title: '活动开始时间必须晚于报名结束时间',
        icon: 'none'
      });
      localFormData.value.activityStartTime = '';
    }
  }
};

// 验证活动结束时间
const validateActivityEndTime = (value: any) => {
  if (localFormData.value.activityStartTime && value) {
    const startTime = new Date(localFormData.value.activityStartTime);
    const endTime = new Date(value);
    if (endTime <= startTime) {
      uni.showToast({
        title: '活动结束时间必须晚于活动开始时间',
        icon: 'none'
      });
      localFormData.value.activityEndTime = '';
    }
  }
};

// 验证签到开始时间
const validateCheckInStartTime = (value: any) => {
  if (localFormData.value.activityEndTime && value) {
    const activityEndTime = new Date(localFormData.value.activityEndTime);
    const checkInStartTime = new Date(value);
    if (checkInStartTime <= activityEndTime) {
      uni.showToast({
        title: '签到开始时间必须晚于活动结束时间',
        icon: 'none'
      });
      localFormData.value.checkInStartTime = '';
    }
  }
};

// 验证签到结束时间
const validateCheckInEndTime = (value: any) => {
  if (localFormData.value.checkInStartTime && value) {
    const startTime = new Date(localFormData.value.checkInStartTime);
    const endTime = new Date(value);
    if (endTime <= startTime) {
      uni.showToast({
        title: '签到结束时间必须晚于签到开始时间',
        icon: 'none'
      });
      localFormData.value.checkInEndTime = '';
    }
  }
};

// 验证签退开始时间
const validateCheckOutStartTime = (value: any) => {
  if (localFormData.value.checkInEndTime && value) {
    const checkInEndTime = new Date(localFormData.value.checkInEndTime);
    const checkOutStartTime = new Date(value);
    if (checkOutStartTime <= checkInEndTime) {
      uni.showToast({
        title: '签退开始时间必须晚于签到结束时间',
        icon: 'none'
      });
      localFormData.value.checkOutStartTime = '';
    }
  }
};

// 验证签退结束时间
const validateCheckOutEndTime = (value: any) => {
  if (localFormData.value.checkOutStartTime && value) {
    const startTime = new Date(localFormData.value.checkOutStartTime);
    const endTime = new Date(value);
    if (endTime <= startTime) {
      uni.showToast({
        title: '签退结束时间必须晚于签退开始时间',
        icon: 'none'
      });
      localFormData.value.checkOutEndTime = '';
    }
  }
};

// 移除已选择的院系
const removeCollege = (id: number) => {
  const index = selectedColleges.value.indexOf(id);
  if (index > -1) {
    selectedColleges.value.splice(index, 1);
    tempSelectedColleges.value = [...selectedColleges.value];
    // 同步到formData
    localFormData.value.canEnrollCollegeIdList = [...selectedColleges.value];
  }
};

// 移除已选择的年级
const removeGrade = (id: number) => {
  const index = selectedGrades.value.indexOf(id);
  if (index > -1) {
    selectedGrades.value.splice(index, 1);
    tempSelectedGrades.value = [...selectedGrades.value];
    // 同步到formData
    localFormData.value.canEnrollGradeIdList = [...selectedGrades.value];
  }
};

// 监听弹窗显示状态，同步临时选择状态
watch(showCollegeSelector, (newVal) => {
  if (newVal) {
    tempSelectedColleges.value = [...selectedColleges.value];
  }
});

watch(showGradeSelector, (newVal) => {
  if (newVal) {
    tempSelectedGrades.value = [...selectedGrades.value];
  }
});

</script>

<style lang="scss" scoped>
.form-item {
  margin-bottom: 40rpx;

  .label {
    font-size: 32rpx;
    font-weight: bold;
    color: #e0e0e0;
    margin-bottom: 20rpx;
  }

  .radio-item {
    display: inline-block;
    margin-right: 40rpx;
    font-size: 30rpx;
    color: #e0e0e0;
    align-items: center;

    radio {
      transform: scale(0.8);
    }
  }
}

/* Picker容器样式 */
.picker-container {
  position: relative;
}

.picker-display {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #555;
  border-radius: 12rpx;
  padding: 0 20rpx;
  font-size: 30rpx;
  color: #e0e0e0;
  display: flex;
  align-items: center;
  background: #2a2a2a;
  justify-content: space-between;
}

.picker-display.placeholder {
  color: #888;
}

.picker-display::after {
  content: '▼';
  color: #888;
  font-size: 24rpx;
}

/* 选中标签样式 */
.selected-tags {
  margin-top: 20rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 15rpx;
}

.tag-item {
  display: flex;
  align-items: center;
  padding: 8rpx 16rpx;
  background: linear-gradient(135deg, #ff8c42, #ff6b35);
  border-radius: 20rpx;
  font-size: 24rpx;
  color: white;
  box-shadow: 0 2rpx 8rpx rgba(255, 140, 66, 0.3);
}

.remove-tag {
  margin-left: 8rpx;
  font-size: 32rpx;
  font-weight: bold;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
}

/* 部落搜索相关样式 */
.search-container {
  position: relative;
  width: 100%;
}

/* 点击容器外隐藏搜索结果 */
.search-container:focus-within .search-results {
  display: block;
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
  color: #ffffff;
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
  border-bottom: 1rpx solid #444;
  cursor: pointer;
  color: #000000;
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