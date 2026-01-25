<template>
  <view class="publish-activity-container">
    <view class="header">
      <text class="title">发布活动</text>
    </view>
    
    <scroll-view class="form-container" scroll-y="true">
      <form @submit="onSubmit">
        <!-- 活动标题 -->
        <view class="form-item">
          <view class="label">活动标题</view>
          <input 
            class="input" 
            v-model="formData.title" 
            placeholder="请输入活动标题"
            maxlength="50"
          />
        </view>

        <!-- 组织或学院选择 -->
        <view class="form-item">
          <view class="label">活动归属</view>
          <radio-group @change="onBelongToTypeChange">
            <label class="radio-item">
              <radio value="organization" :checked="belongToType === 'organization'" color="#ff8c42" />
              <text>组织</text>
            </label>
            <label class="radio-item">
              <radio value="college" :checked="belongToType === 'college'" color="#ff8c42" />
              <text>学院</text>
            </label>
          </radio-group>
        </view>

        <!-- 组织/学院下拉框 -->
        <view class="form-item" v-if="belongToType">
          <view class="label">{{ belongToType === 'organization' ? '选择组织' : '选择学院' }}</view>
          <picker
              @change="onOrganizationOrCollegeChange"
              :value="organizationOrCollegeIndex"
              :range="belongToType === 'organization' ? organizations : colleges"
              range-key="name"
          >
            <view class="picker">{{
                organizationOrCollegeIndex >= 0 && getCurrentBelongTo() && getCurrentBelongTo()[organizationOrCollegeIndex] ?
                    getCurrentBelongTo()[organizationOrCollegeIndex].name :
                    `请选择${belongToType === 'organization' ? '组织' : '学院'}`
              }}</view>
          </picker>
        </view>

        <!-- 审核人 -->
        <view class="form-item">
          <view class="label">审核人</view>
          <picker @change="onReviewerChange" :value="reviewerIndex" :range="getCurrentReviewers() || []" range-key="name">
            <view class="picker">{{
                reviewerIndex >= 0 && getCurrentReviewers() && getCurrentReviewers()?.length > 0 && getCurrentReviewers()[reviewerIndex] ?
                    (getCurrentReviewers()[reviewerIndex].name || '暂无') :
                    '请选择审核人'
              }}</view>
          </picker>
        </view>

        <!-- 活动分类 -->
        <view class="form-item">
          <view class="label">活动分类</view>
          <picker @change="onCategoryChange" :value="categoryIndex" :range="categories" range-key="name">
            <view class="picker">{{
                categoryIndex >= 0 && categories[categoryIndex] ?
                    categories[categoryIndex].name :
                    '请选择活动分类'
              }}</view>
          </picker>
        </view>

        <!-- 活动封面 -->
        <view class="form-item">
          <view class="label">活动封面</view>
          <view class="upload-area" @click="chooseCoverImage">
            <view v-if="!formData.coverImg" class="upload-placeholder">
              <text class="upload-icon">+</text>
              <text>点击上传封面</text>
            </view>
            <image v-else :src="formData.coverImg" class="cover-preview" mode="aspectFill"></image>
          </view>
        </view>

        <!-- 是否需要审核 -->
        <view class="form-item">
          <view class="label">报名需审核</view>
          <radio-group @change="onAuditRequiredChange">
            <label class="radio-item">
              <radio value="true" :checked="formData.enrollNeedReview === true" color="#ff8c42" />
              <text>是</text>
            </label>
            <label class="radio-item">
              <radio value="false" :checked="formData.enrollNeedReview === false" color="#ff8c42" />
              <text>否</text>
            </label>
          </radio-group>
        </view>

        <!-- 参与对象选择 -->
        <view class="form-item">
          <view class="label">参与对象</view>
          <radio-group @change="onParticipationTypeChangeRadio">
            <label class="radio-item">
              <radio value="grade" :checked="participationType === 'grade'" color="#ff8c42" />
              <text>按院系年级参与</text>
            </label>
            <label class="radio-item">
              <radio value="tribe" :checked="participationType === 'tribe'" color="#ff8c42" />
              <text>按部落参与</text>
            </label>
          </radio-group>
        </view>

        <!-- 院系年级选择 (当选择按院系年级参与时显示) -->
        <view v-if="participationType === 'grade'">
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
        <view v-if="participationType === 'tribe'">
          <view class="form-item">
            <view class="label">活动部落</view>
            <view class="search-container">
              <view class="search-input-wrapper">
                <input 
                  class="search-input" 
                  v-model="tribeSearchKeyword" 
                  placeholder="搜索活动部落" 
                  @input="onTribeSearch"
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
        
        <!-- 活动人数 -->
        <view class="form-item">
          <view class="label">活动人数</view>
          <input 
            class="input" 
            v-model.number="formData.enrollNumLimit" 
            type="number"
            placeholder="请输入活动人数限制(0-20000)"
            @input="onEnrollNumLimitInput"
          />
        </view>

        <!-- 活动地点 -->
        <view class="form-item">
          <view class="label">活动地点</view>
          <input 
            class="input" 
            v-model="formData.position"
            placeholder="请输入活动地点"
            maxlength="100"
          />
        </view>

        <!-- PU分 -->
        <view class="form-item">
          <view class="label">获得PU分</view>
          <input 
            class="input" 
            v-model.number="formData.scoreCanGet"
            type="number"
            placeholder="请输入活动可获得的PU分数"
          />
        </view>

        <!-- 活动详细描述 -->
        <view class="form-item">
          <view class="label">活动描述</view>
          <textarea 
            class="textarea" 
            v-model="formData.description" 
            placeholder="请输入活动详细描述"
            maxlength="500"
            auto-height
          />
        </view>

        <!-- 是否需要签退 -->
        <view class="form-item">
          <view class="label">需要签退</view>
          <radio-group @change="onCheckOutRequiredChange">
            <label class="radio-item">
              <radio value="true" :checked="formData.needSignOut" color="#ff8c42" />
              <text>是</text>
            </label>
            <label class="radio-item">
              <radio value="false" :checked="!formData.needSignOut" color="#ff8c42" />
              <text>否</text>
            </label>
          </radio-group>
        </view>

        <!-- 提交按钮 -->
        <view class="button-container">
          <button class="submit-button" form-type="submit">发布活动</button>
        </view>
      </form>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import {ref, computed, onMounted} from 'vue';
import {
  initActivityPublishPage,
  presignUploadUrl,
  generatePresignedUploadUrl,
  querySimpleTribeList
} from "@/common/Api";
import axios from 'axios'

// 表单数据
const formData = ref({
  title: '',                    // 活动标题
  position: '',                 // 活动地点
  scoreCanGet: 0.0,                  // PU分
  enrollNumLimit: 0,            // 活动人数限制
  activityBelongToSchoolId: -1,      // 学校ID
  activityBelongToOrganizationId: -1,              // 组织ID
  activityBelongToCollegeId: -1,    // 院系ID
  description: '',              // 活动描述
  enrollNeedReview: false,         // 是否需要审核
  needSignOut: false,      // 是否需要签退
  attachment: '',               // 附件
  categoryId: -1,               // 活动分类ID
  coverImg: '',                    // 活动封面
  activityManagerId: -1,          // 活动管理员ID
  enrollStartTime: '',           // 活动开始时间
  enrollEndTime: '',           // 活动结束时间
  activityStartTime: '',          // 活动开始时间
  activityEndTime: '',          // 活动结束时间
  signinStartTime: '',          // 签到开始时间
  signinEndTime: '',            // 签到结束时间
  initialReviewer:-1,            //初审人id
  canEnrollGradeIdList: [],       // 活动可报名的班级ID列表
  canEnrollCollegeIdList: [],      // 活动可报名的院系ID列表
});

const belongToType = ref('');
const participationType = ref('');

// 活动部落数据（从API获取）
const tribes = ref<any[]>([]);

// 模拟院系数据
const colleges = ref([
  { id: 1, name: '计算机学院' },
  { id: 2, name: '电子信息学院' },
  { id: 3, name: '机械工程学院' },
  { id: 4, name: '经济管理学院' },
  { id: 5, name: '外国语学院' }
]);

// 模拟年级数据
const grades = ref([
  { id: 1, name: '2020级' },
  { id: 2, name: '2021级' },
  { id: 3, name: '2022级' },
  { id: 4, name: '2023级' },
  { id: 5, name: '2024级' }
]);

// 已选择的院系和年级
const selectedColleges = ref<number[]>([]);
const selectedGrades = ref<number[]>([]);

// 已选择的部落
const selectedTribes = ref<number[]>([]);

// 控制多选弹窗显示
const showCollegeMultiSelector = ref(false);
const showGradeMultiSelector = ref(false);
const showTribeMultiSelector = ref(false);

// 搜索部落的关键词
const tribeSearchKeyword = ref('');

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

// 模拟组织/学院数据
const organizations = ref([
  { id: 1, name: '学生会' },
  { id: 2, name: '社团联合会' },
  { id: 3, name: '志愿者协会' },
  { id: 4, name: '科技创新社' }
]);

// 活动分类
const categories = ref([
  { id: 1, name: '学术讲座' },
  { id: 2, name: '文艺活动' },
  { id: 3, name: '体育竞赛' },
  { id: 4, name: '志愿服务' },
  { id: 5, name: '技能培训' }
]);

// 审核人列表 - 使用 Map 结构
const collegeReviewers = ref<Map<number, Array<{id: number, name: string}>>>(
    new Map()
);

const organizationReviewers = ref<Map<number, Array<{id: number, name: string}>>>(
    new Map()
);




// 计算属性：当前选中的组织或学院索引
const organizationOrCollegeIndex = computed(() => {
  if (belongToType.value === 'organization' && formData.value.activityBelongToOrganizationId !== -1) {
    return organizations.value.findIndex(item => item.id === formData.value.activityBelongToOrganizationId);
  } else if (belongToType.value === 'college' && formData.value.activityBelongToCollegeId !== -1) {
    return colleges.value.findIndex(item => item.id === formData.value.activityBelongToCollegeId);
  }
  return -1;
});


// 获取当前组织/学院列表
const getCurrentBelongTo = () => {
  return belongToType.value === 'organization' ? organizations.value : colleges.value;
};

// 活动归属类型选择改变事件
const onBelongToTypeChange = (e: any) => {
  belongToType.value = e.detail.value;
  // 清除之前的选择
  formData.value.activityBelongToOrganizationId = -1;
  formData.value.activityBelongToCollegeId = -1;
  // 同时清除审核人选择
  formData.value.initialReviewer = -1;
};

// 组织或学院选择改变事件
const onOrganizationOrCollegeChange = (e: any) => {
  const index = parseInt(e.detail.value);
  const currentList = getCurrentBelongTo();
  if (index >= 0 && currentList[index]) {
    if (belongToType.value === 'organization') {
      formData.value.activityBelongToOrganizationId = currentList[index].id;
      // 重置审核人选择
      formData.value.initialReviewer = -1;
    } else if (belongToType.value === 'college') {
      formData.value.activityBelongToCollegeId = currentList[index].id;
      // 重置审核人选择
      formData.value.initialReviewer = -1;
    }
  }
};

// 计算属性：当前选中的分类索引
const categoryIndex = computed(() => {
  if (!formData.value.categoryId) return -1;
  return categories.value.findIndex(item => item.id === formData.value.categoryId);
});

// 计算属性：当前选中的审核人索引
const reviewerIndex = computed(() => {
  if (formData.value.initialReviewer === -1) return -1;
  const currentReviewerList = getCurrentReviewers();
  console.log(currentReviewerList)
  if (!currentReviewerList) return -1;
  return currentReviewerList.findIndex(item => item.id === formData.value.initialReviewer);
});

// 获取当前审核人列表
const getCurrentReviewers = () => {
   if(belongToType.value==='organization'){
     if(organizationReviewers.value instanceof Map) {
       const reviewers = organizationReviewers.value.get(formData.value.activityBelongToOrganizationId);
       console.log('获取组织审核人:', formData.value.activityBelongToOrganizationId, reviewers);
       return reviewers || [];
     }
   }
   if(belongToType.value==='college'){
     if(collegeReviewers.value instanceof Map) {
       const reviewers = collegeReviewers.value.get(formData.value.activityBelongToCollegeId);
       console.log('获取学院审核人:', formData.value.activityBelongToCollegeId, reviewers);
       return reviewers || [];
     }
   }
   return [];
};


// 计算属性：当前选中的院系索引
const selectedCollegeIndex = computed(() => {
  if (!selectedColleges.value[0]) return -1;
  return colleges.value.findIndex(college => college.id === selectedColleges.value[0]);
});

// 计算属性：当前选中的年级索引
const selectedGradeIndex = computed(() => {
  if (!selectedGrades.value[0]) return -1;
  return grades.value.findIndex(grade => grade.id === selectedGrades.value[0]);
});

// 计算属性：当前选中的组织索引

// 参与类型改变事件 (radio)
const onParticipationTypeChangeRadio = (e: any) => {
  participationType.value = e.detail.value;
};

// 院系选择改变事件
const onCollegeChange = (e: any) => {
  const index = parseInt(e.detail.value);
  if (index >= 0 && colleges.value[index]) {
    selectedColleges.value = [colleges.value[index].id]; // 只允许选择一个院系
  }
};

// 年级选择改变事件
const onGradeChange = (e: any) => {
  const index = parseInt(e.detail.value);
  if (index >= 0 && grades.value[index]) {
    selectedGrades.value = [grades.value[index].id]; // 只允许选择一个年级
  }
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
};

// 部落复选框改变事件
const onTribeCheckboxChange = (e: any, id: number) => {
  const checked = e.detail.checked;
  if (checked) {
    if (!selectedTribes.value.includes(id)) {
      selectedTribes.value.push(id);
    }
  } else {
    const index = selectedTribes.value.indexOf(id);
    if (index > -1) {
      selectedTribes.value.splice(index, 1);
    }
  }
};

// 确认院系选择
const confirmCollegesSelection = () => {
  showCollegeMultiSelector.value = false;
};

// 确认年级选择
const confirmGradesSelection = () => {
  showGradeMultiSelector.value = false;
};

// 确认部落选择
const confirmTribesSelection = () => {
  showTribeMultiSelector.value = false;
};

// 清空院系选择
const clearSelectedColleges = () => {
  selectedColleges.value = [];
};

// 清空年级选择
const clearSelectedGrades = () => {
  selectedGrades.value = [];
};

// 清空部落选择
const clearSelectedTribes = () => {
  selectedTribes.value = [];
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

// 部落搜索事件
const onTribeSearch = (e: any) => {
  tribeSearchKeyword.value = e.detail.value;
  querySimpleTribeList()
};

// 活动人数输入事件
const onEnrollNumLimitInput = (e: any) => {
  let value = parseInt(e.detail.value) || 0;
  if (value < 0) value = 0;
  if (value > 20000) value = 20000;
  formData.value.enrollNumLimit = value;
};



// 报名审核需求改变事件
const onAuditRequiredChange = (e: any) => {
  formData.value.enrollNeedReview = e.detail.value === 'true';
};

// 签退需求改变事件
const onCheckOutRequiredChange = (e: any) => {
  formData.value.needSignOut = e.detail.value === 'true';
};

// 分类选择改变事件
const onCategoryChange = (e: any) => {
  const index = parseInt(e.detail.value);
  if (index >= 0 && categories.value[index]) {
    formData.value.categoryId = categories.value[index].id;
  }
};

// 审核人选择改变事件
const onReviewerChange = (e: any) => {
  const index = parseInt(e.detail.value);
  const currentReviewerList = getCurrentReviewers();
  if (currentReviewerList && index >= 0 && currentReviewerList[index]) {
    formData.value.initialReviewer = currentReviewerList[index].id;
  }
};

// 选择封面图片
const chooseCoverImage = async () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const filePath = res.tempFilePaths[0];
      
      // 获取文件扩展名
      const fileName = filePath.split('/').pop();
      const fileExt = fileName.split('.').pop().toLowerCase();
      
      // 验证文件类型
      const allowedTypes = ['jpg', 'jpeg', 'png', 'gif'];
      if (!allowedTypes.includes(fileExt)) {
        uni.showToast({
          title: '请选择图片文件(jpg/jpeg/png/gif)',
          icon: 'none'
        });
        return;
      }
      
      try {
        // 生成预签名上传URL
        uni.showLoading({
          title: '准备上传...'
        });
        
        // 调用后端生成预签名上传URL，使用公共文件夹（值为1）
        const response = await presignUploadUrl(fileName, 1);
        
        if (response.data.flag) {
          const { url, fileKey } = response.data.data;
          
          // 将本地文件读取为ArrayBuffer并上传到S3兼容存储
          uni.getFileSystemManager().readFile({
            filePath: filePath,
            success: (readRes) => {
              axios({
                url: url, // 预签名URL
                method: 'PUT',
                data: readRes.data,
                header: {
                  'Content-Type': `image/${fileExt}`
                },
                success: (uploadRes) => {
                  if (uploadRes.statusCode >= 200 && uploadRes.statusCode < 300) {
                    // 上传成功，保存文件路径
                    formData.value.coverImg = `http://localhost:1024/portal/file/getFileUrl?fileKey=${encodeURIComponent(fileKey)}`; // 使用获取文件URL的接口
                    console.log('封面上传成功:', fileKey);
                    
                    uni.showToast({
                      title: '上传成功',
                      icon: 'success'
                    });
                  } else {
                    console.error('上传失败:', uploadRes);
                    uni.showToast({
                      title: '上传失败',
                      icon: 'none'
                    });
                  }
                },
                fail: (uploadErr) => {
                  console.error('上传失败:', uploadErr);
                  uni.showToast({
                    title: '上传失败',
                    icon: 'none'
                  });
                },
                complete: () => {
                  uni.hideLoading();
                }
              });
            },
            fail: (readErr) => {
              console.error('读取文件失败:', readErr);
              uni.hideLoading();
              uni.showToast({
                title: '读取文件失败',
                icon: 'none'
              });
            }
          });
        } else {
          uni.hideLoading();
          uni.showToast({
            title: response.data.msg || '获取上传地址失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('上传封面图片失败:', error);
        uni.hideLoading();
        uni.showToast({
          title: '上传失败',
          icon: 'none'
        });
      }
    },
    fail: (error) => {
      console.error('选择图片失败:', error);
    }
  });
};

// 提交表单
const onSubmit = () => {
  console.log('提交活动信息:', formData.value);
  
  // 这里可以调用API提交数据
  // 示例验证
  if (!formData.value.title.trim()) {
    uni.showToast({
      title: '请输入活动标题',
      icon: 'none'
    });
    return;
  }
  
  if (!formData.value.position.trim()) {
    uni.showToast({
      title: '请输入活动地点',
      icon: 'none'
    });
    return;
  }
  
  if (formData.value.scoreCanGet < 0) {
    uni.showToast({
      title: 'PU分不能为负数',
      icon: 'none'
    });
    return;
  }
  
  if (formData.value.enrollNumLimit < 0 || formData.value.enrollNumLimit > 20000) {
    uni.showToast({
      title: '活动人数必须在0-20000之间',
      icon: 'none'
    });
    return;
  }
  
  if (!belongToType.value) {
    uni.showToast({
      title: '请选择活动归属',
      icon: 'none'
    });
    return;
  }
  
  if (belongToType.value === 'organization' && formData.value.activityBelongToOrganizationId === -1) {
    uni.showToast({
      title: '请选择组织',
      icon: 'none'
    });
    return;
  }
  if (belongToType.value === 'college' && formData.value.activityBelongToCollegeId === -1) {
    uni.showToast({
      title: '请选择学院',
      icon: 'none'
    });
    return;
  }
  
  if (!formData.value.description.trim()) {
    uni.showToast({
      title: '请输入活动描述',
      icon: 'none'
    });
    return;
  }
  
  if (!formData.value.categoryId) {
    uni.showToast({
      title: '请选择活动分类',
      icon: 'none'
    });
    return;
  }
  
  if (formData.value.initialReviewer === -1) {
    uni.showToast({
      title: '请选择审核人',
      icon: 'none'
    });
    return;
  }
  
  // 验证参与类型相关字段
  if (participationType.value === 'grade') {
    if (selectedColleges.value.length === 0) {
      uni.showToast({
        title: '请选择院系',
        icon: 'none'
      });
      return;
    }
    if (selectedGrades.value.length === 0) {
      uni.showToast({
        title: '请选择年级',
        icon: 'none'
      });
      return;
    }
  } else if (participationType.value === 'tribe') {
    if (selectedTribes.value.length === 0) {
      uni.showToast({
        title: '请选择活动部落',
        icon: 'none'
      });
      return;
    }
  }
  
  if (!formData.value.coverImg) {
    uni.showToast({
      title: '请上传活动封面',
      icon: 'none'
    });
    return;
  }
  
  // 如果验证通过，提交数据
  uni.showLoading({
    title: '发布中...'
  });
  
  // 模拟API调用
  setTimeout(() => {
    uni.hideLoading();
    uni.showToast({
      title: '活动发布成功！',
      icon: 'success'
    });
  }, 1500);
};

onMounted(()=> {
  initActivityPublishPage().then((res)=>{
    console.log(res.data)
    colleges.value = res.data.data.collegeInfoVOList
    categories.value = res.data.data.activityCategoryVOList
    grades.value = res.data.data.gradeInfoVOList
    organizations.value = res.data.data.organizationInfoVOList
    
    // 将对象转换为Map，以便通过ID进行查找
    if (res.data.data.collegeReviewerList && typeof res.data.data.collegeReviewerList === 'object') {
      const collegeReviewerMap = new Map();
      Object.keys(res.data.data.collegeReviewerList).forEach(key => {
        // 转换username为name以适配picker组件
        const reviewersWithNames = res.data.data.collegeReviewerList[key].map(reviewer => ({
          id: reviewer.id,
          name: reviewer.username
        }));
        collegeReviewerMap.set(Number(key), reviewersWithNames);
      });
      collegeReviewers.value = collegeReviewerMap;
    }
    
    if (res.data.data.organizationReviewerList && typeof res.data.data.organizationReviewerList === 'object') {
      const organizationReviewerMap = new Map();
      Object.keys(res.data.data.organizationReviewerList).forEach(key => {
        // 转换username为name以适配picker组件
        const reviewersWithNames = res.data.data.organizationReviewerList[key].map(reviewer => ({
          id: reviewer.id,
          name: reviewer.username
        }));
        organizationReviewerMap.set(Number(key), reviewersWithNames);
      });
      organizationReviewers.value = organizationReviewerMap;
    }
  }).catch( error=> {
    console.log(error)
  })
  console.log('colleges',colleges.value)
})

</script>

<style lang="scss">
.publish-activity-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #ff8c42, #ff6b35);
  padding: 20rpx;

  .header {
    text-align: center;
    margin-bottom: 40rpx;

    .title {
      font-size: 48rpx;
      font-weight: bold;
      color: #fff;
      text-shadow: 2rpx 2rpx 4rpx rgba(0, 0, 0, 0.3);
    }
  }

  .form-container {
    background: #fff;
    border-radius: 20rpx;
    padding: 40rpx;
    box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);

    .form-item {
      margin-bottom: 40rpx;

      .label {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 20rpx;
      }

      .input {
        width: 100%;
        height: 80rpx;
        border: 2rpx solid #e0e0e0;
        border-radius: 12rpx;
        padding: 0 20rpx;
        font-size: 30rpx;
        color: #333;

        &:focus {
          border-color: #ff8c42;
          box-shadow: 0 0 10rpx rgba(255, 140, 66, 0.3);
        }
      }

      .textarea {
        width: 100%;
        min-height: 200rpx;
        border: 2rpx solid #e0e0e0;
        border-radius: 12rpx;
        padding: 20rpx;
        font-size: 30rpx;
        color: #333;
        line-height: 1.5;

        &:focus {
          border-color: #ff8c42;
          box-shadow: 0 0 10rpx rgba(255, 140, 66, 0.3);
        }
      }

      .picker {
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
      
      .upload-area {
        width: 100%;
        height: 200rpx;
        border: 2rpx dashed #e0e0e0;
        border-radius: 12rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: #fafafa;
        overflow: hidden;
        
        &.active {
          border-color: #ff8c42;
          background-color: rgba(255, 140, 66, 0.1);
        }
      }
      
      .upload-placeholder {
        display: flex;
        flex-direction: column;
        align-items: center;
        color: #999;
        
        .upload-icon {
          font-size: 60rpx;
          margin-bottom: 10rpx;
        }
      }
      
      .cover-preview {
        width: 100%;
        height: 100%;
      }
    }
  }

  .button-container {
    margin-top: 60rpx;
    padding: 0 40rpx 40rpx;

    .submit-button {
      width: 100%;
      height: 100rpx;
      background: linear-gradient(to right, #ff8c42, #ff6b35);
      color: white;
      border: none;
      border-radius: 50rpx;
      font-size: 36rpx;
      font-weight: bold;
      box-shadow: 0 10rpx 20rpx rgba(255, 140, 66, 0.4);

      &:active {
        transform: translateY(2rpx);
        box-shadow: 0 5rpx 10rpx rgba(255, 140, 66, 0.4);
      }
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