<template>
  <view class="cover-upload-component">
    <view class="cover-preview-container">
      <image :src="coverImgPath" class="cover-preview" mode="aspectFill" v-if="coverImgPath"></image>
      <view class="cover-placeholder" v-else>
        <text>暂无封面</text>
      </view>
    </view>
    <view class="upload-area" @click="chooseCoverImage">
      <view class="upload-placeholder">
        <text class="upload-icon">+</text>
        <text>点击上传封面</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, defineEmits, defineProps, watch } from 'vue';
import { presignUploadUrl } from "@/common/Api";
import { blobToDataURI } from "@/common/ImageUtils";

interface Props {
  modelValue?: string; // 封面图片的fileKey
}

interface Emits {
  (e: 'update:modelValue', value: string): void;
  (e: 'change', value: string): void;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: ''
});

const emit = defineEmits<Emits>();

const coverImgPath = ref<string>('');

// 当外部值变化时同步内部值
watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    // 如果需要根据fileKey获取实际图片URL，这里可以调用API
    console.log('封面图片fileKey更新:', newValue);
  }
}, { immediate: true });

// 验证图片文件类型
const validateImageFile = (filePath: string) => {
  let fileName = '';
  let fileExt = '';
  
  // 检查是否为blob URL（常见于H5环境）
  if (filePath.startsWith('blob:')) {
    // 对于H5环境，uni.chooseImage已经限制了只能选择图片文件
    // 所以我们可以安全地继续，不需要验证扩展名
    console.log('检测到blob URL，假设为有效图片文件');
    fileName = `temp_image_${Date.now()}.jpg`;
    fileExt = 'jpg'; // 设置一个默认值
  } else {
    // 非H5环境，使用正常的路径处理
    const pathParts = filePath.split(/[\\/\\\\]/);
    fileName = pathParts[pathParts.length - 1]; // 获取路径的最后一部分

    console.log('提取的文件名:', fileName);

    const lastDotIndex = fileName.lastIndexOf('.');
    console.log('最后点的位置:', lastDotIndex);

    if (lastDotIndex > 0 && lastDotIndex < fileName.length - 1) {
      fileExt = fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    console.log('提取的扩展名:', fileExt);

    // 验证文件类型
    const allowedTypes = ['jpg', 'jpeg', 'png', 'gif'];
    console.log('允许的类型:', allowedTypes);
    if (!fileExt || !allowedTypes.includes(fileExt)) {  // 如果没有扩展名或扩展名不在允许列表中
      uni.showToast({
        title: '请选择图片文件(jpg/jpeg/png/gif)，当前文件扩展名：' + (fileExt || '无'),
        icon: 'none'
      });
      return null; // 返回null表示验证失败
    }
  }
  
  return { fileName, fileExt }; // 返回验证成功的文件信息
};

// 选择封面图片
const chooseCoverImage = async () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const filePath = res.tempFilePaths[0];

      // 使用抽取的验证函数
      const validationResult = validateImageFile(filePath);
      if (!validationResult) {
        return; // 验证失败，直接返回
      }
      
      // 直接使用临时文件路径进行预览，避免尺寸问题
      coverImgPath.value = filePath;

      const {fileName, fileExt} = validationResult;

      try {
        // 调用后端生成预签名上传URL，使用公共文件夹（值为1）
        const response = await presignUploadUrl(fileName, 1);
        console.log('预签名response:', response);

        if (response.data.ok) {
          const {url, fileKey} = response.data.data;
          console.log('上传URL:', url)
          
          // 使用uni.uploadFile上传文件
          uni.uploadFile({
            url: url,
            filePath: filePath,
            name: 'file',
            success: (uploadRes) => {
              console.log('封面上传成功:', uploadRes);
              // 更新v-model值
              emit('update:modelValue', fileKey);
              emit('change', fileKey);
            },
            fail: (uploadError) => {
              console.error('封面上传失败:', uploadError);
              uni.showToast({
                title: '封面上传失败',
                icon: 'none'
              });
            }
          });
        }
      } catch (error) {
        console.error('预签名URL获取失败:', error);
        uni.showToast({
          title: '获取上传地址失败',
          icon: 'none'
        });
      }
    },
    fail: (error) => {
      console.error('选择图片失败:', error);
      uni.showToast({
        title: '请选择图片文件',
        icon: 'none'
      });
    }
  })
};
</script>

<style lang="scss">
.cover-upload-component {
  .cover-preview-container {
    width: 100%;
    height: 300rpx;
    overflow: hidden;
    border-radius: 12rpx;
    border: 2rpx solid #eee;
    margin-bottom: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f8f8f8;
  }
  
  .cover-preview {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .cover-placeholder {
    color: #999;
    font-size: 28rpx;
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
}
</style>