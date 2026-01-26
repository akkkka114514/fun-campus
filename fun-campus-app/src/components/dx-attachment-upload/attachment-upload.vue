<template>
  <view class="attachment-upload-component">
    <view class="attachment-upload-area" @click="chooseAttachment">
      <view class="attachment-upload-placeholder">
        <text class="attachment-upload-icon">📁</text>
        <text>点击上传附件（支持PDF、Word、Excel、PPT、TXT等）</text>
      </view>
    </view>
    <view class="attachment-list" v-if="attachmentList.length > 0">
      <view class="attachment-item" v-for="(item, index) in attachmentList" :key="index">
        <text class="attachment-name">{{ item.fileName }}</text>
        <text class="attachment-size">({{ formatFileSize(item.fileSize) }})</text>
        <text class="remove-attachment" @click.stop="removeAttachment(index)">×</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, defineEmits, defineProps } from 'vue';
import { presignUploadUrl } from "@/common/Api";

interface Attachment {
  fileName: string;
  fileSize: number;
  fileKey: string;
}

interface Props {
  modelValue?: string; // 附件文件key列表，以逗号分隔
}

interface Emits {
  (e: 'update:modelValue', value: string): void;
  (e: 'change', value: Attachment[]): void;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: ''
});

const emit = defineEmits<Emits>();

const attachmentList = ref<Attachment[]>([]);

// 初始化附件列表
if (props.modelValue) {
  // 这里应该从后端获取附件信息，暂时简化处理
  console.log('初始化附件列表:', props.modelValue);
}

// 验证附件文件类型
const validateAttachmentFile = (fileName: string) => {
  const allowedTypes = ['.pdf', '.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx', '.txt', '.zip', '.rar'];
  const lowerFileName = fileName.toLowerCase();
  
  const isValid = allowedTypes.some(type => lowerFileName.endsWith(type));
  
  if (!isValid) {
    uni.showToast({
      title: '不支持的文件类型，请上传PDF、Word、Excel、PPT、TXT、ZIP或RAR文件',
      icon: 'none'
    });
    return false;
  }
  
  return true;
};

// 格式化文件大小
const formatFileSize = (sizeInBytes: number): string => {
  if (sizeInBytes < 1024) {
    return sizeInBytes + ' B';
  } else if (sizeInBytes < 1024 * 1024) {
    return (sizeInBytes / 1024).toFixed(1) + ' KB';
  } else if (sizeInBytes < 1024 * 1024 * 1024) {
    return (sizeInBytes / (1024 * 1024)).toFixed(1) + ' MB';
  } else {
    return (sizeInBytes / (1024 * 1024 * 1024)).toFixed(1) + ' GB';
  }
};

// 选择附件
const chooseAttachment = async () => {
  uni.chooseMessageFile({
    count: 5, // 最多选择5个文件
    type: 'file',
    extension: ['.pdf', '.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx', '.txt', '.zip', '.rar'],
    success: async (res) => {
      for (const file of res.tempFiles) {
        // 验证文件类型
        if (!validateAttachmentFile(file.name)) {
          continue; // 跳过不支持的文件类型
        }
        
        // 检查文件大小 (限制为50MB)
        if (file.size > 50 * 1024 * 1024) {
          uni.showToast({
            title: `文件 ${file.name} 超过50MB限制`,
            icon: 'none'
          });
          continue;
        }
        
        // 上传文件
        try {
          const response = await presignUploadUrl(file.name, 1);
          if (response.data.ok) {
            const {url, fileKey} = response.data.data;
            
            // 使用uni.uploadFile上传文件
            uni.uploadFile({
              url: url,
              filePath: file.path,
              name: 'file',
              success: (uploadRes) => {
                console.log('附件上传成功:', uploadRes);
                // 添加到附件列表
                attachmentList.value.push({
                  fileName: file.name,
                  fileSize: file.size,
                  fileKey: fileKey
                });
                
                // 更新v-model值
                const attachmentKeys = attachmentList.value.map(item => item.fileKey);
                emit('update:modelValue', attachmentKeys.join(','));
                emit('change', [...attachmentList.value]);
              },
              fail: (uploadError) => {
                console.error('附件上传失败:', uploadError);
                uni.showToast({
                  title: `附件上传失败: ${file.name}`,
                  icon: 'none'
                });
              }
            });
          } else {
            uni.showToast({
              title: `获取上传地址失败: ${file.name}`,
              icon: 'none'
            });
          }
        } catch (error) {
          console.error('获取预签名URL失败:', error);
          uni.showToast({
            title: `获取上传地址失败: ${file.name}`,
            icon: 'none'
          });
        }
      }
    },
    fail: (error) => {
      console.error('选择附件失败:', error);
      uni.showToast({
        title: '选择附件失败',
        icon: 'none'
      });
    }
  });
};

// 移除附件
const removeAttachment = (index: number) => {
  const removedItem = attachmentList.value.splice(index, 1)[0];
  
  // 更新v-model值
  const attachmentKeys = attachmentList.value.map(item => item.fileKey);
  emit('update:modelValue', attachmentKeys.join(','));
  emit('change', [...attachmentList.value]);
  
  uni.showToast({
    title: '附件已移除',
    icon: 'none'
  });
};
</script>

<style lang="scss">
.attachment-upload-component {
  .attachment-upload-area {
    width: 100%;
    height: 120rpx;
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
  
  .attachment-upload-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    color: #999;
    
    .attachment-upload-icon {
      font-size: 40rpx;
      margin-bottom: 10rpx;
    }
  }
  
  .attachment-list {
    margin-top: 20rpx;
  }
  
  .attachment-item {
    display: flex;
    align-items: center;
    padding: 16rpx;
    background: #f5f5f5;
    border-radius: 8rpx;
    margin-bottom: 10rpx;
    justify-content: space-between;
  }
  
  .attachment-name {
    flex: 1;
    font-size: 28rpx;
    color: #333;
    margin-right: 10rpx;
    word-break: break-all;
  }
  
  .attachment-size {
    font-size: 24rpx;
    color: #999;
    margin-right: 10rpx;
  }
  
  .remove-attachment {
    font-size: 36rpx;
    color: #ff3b30;
    cursor: pointer;
    width: 40rpx;
    text-align: center;
  }
}
</style>