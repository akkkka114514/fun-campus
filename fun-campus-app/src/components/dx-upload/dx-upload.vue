<template>
  <view class="dx-upload">
    <template v-if="limit === 1">
      <view class="item flex-center" v-if="!!value">
        <image class="upload-img" :src="value"></image>
        <view class="close">
            <tm-icon name="tmicon-times-circle-fill" color="#fe1b00" @click="value=''"></tm-icon>
        </view>
      </view>
    </template>
    <template v-else>
      <view class="item flex-center" :class="{'ml-15':index%3!==0}" v-for="(item,index) in value" :key="index">
        <image class="upload-img" :src="item"></image>
        <view class="close">
            <tm-icon name="tmicon-times-circle-fill" color="#fe1b00" @click="remove(index)"></tm-icon>
        </view>
      </view>
    </template>
    <view class="item flex-center ml-15" v-if="showUpload" @click="selectImage">
      <view>
        <tm-icon name="tmicon-plus" color="#999999"></tm-icon>
        <view class="tips">上传</view>
      </view>
    </view>
  </view>
</template>
<script lang="ts" setup>
import { ref, watch,computed } from 'vue';
import { multiUpload } from '@/common/index';
const props = withDefaults(defineProps<{
    modelValue: any,
    limit?: number,
}>(), {
    limit: 1,
})
const value = ref(props.limit === 1 ? '' : []);
const showUpload = computed(() => {
    return props.limit === 1 ? !value.value : value.value.length < props.limit;
})
watch(() => props.modelValue, (val) => {
    console.log(val);
    if (props.limit === 1) {
        value.value = val;
    } else {
        value.value = !val ? [] : Array.isArray(val) ? val : val.split(',');
    }
},{
    immediate: true
})
const $emit = defineEmits(['update:modelValue']);
// 删除图片
function remove(index: number) {
    (value.value as string[]).splice(index, 1);
    $emit('update:modelValue', value.value);
}
function selectImage(){
    uni.chooseImage({
        count: props.limit === 1 ? 1 : props.limit - value.value.length,
        success: (e) => {
            multiUpload(e.tempFiles as any).then((res:any) => {
                if (props.limit === 1) {
                    value.value = res[0];
                    $emit('update:modelValue', res[0]);
                } else {
                    value.value = value.value.concat(res);
                    $emit('update:modelValue',value.value);
                }
            })
        }
    })
}
</script>
<style lang="scss" scoped>
.dx-upload{
    display: flex;
    flex-wrap: wrap;
    .item{
        margin-bottom: 15rpx;
        color: #888888;
        width: 180rpx;
        height: 180rpx;
        border: 2rpx dashed #b3c3e0;
        border-radius: 5rpx;
        position: relative;
        .upload-img{
            width: 100%;
            height: 100%;
        }
        .tips{
            font-size: 24rpx;
        }
        .close{
            position: absolute;
            top: 10rpx;
            right: 10rpx;
            z-index: 100;
            background: #ffffff;
            border-radius: 50%;
        }
    }
}
</style>
