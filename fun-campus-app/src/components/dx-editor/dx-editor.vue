<template>
  <view class="container" ref="editorRef">
    <view class="page-body">
      <view class="wrapper">
        <view class="toolbar" @tap="format" style="height: 150px;overflow-y: auto;">
          <view :class="formats.bold ? 'ql-active' : ''" class="iconfont icon-zitijiacu" data-name="bold"></view>
          <view :class="formats.italic ? 'ql-active' : ''" class="iconfont icon-zitixieti" data-name="italic"></view>
          <view :class="formats.underline ? 'ql-active' : ''" class="iconfont icon-zitixiahuaxian" data-name="underline"></view>
          <view :class="formats.strike ? 'ql-active' : ''" class="iconfont icon-zitishanchuxian" data-name="strike"></view>
          <view :class="formats.align === 'left' ? 'ql-active' : ''" class="iconfont icon-zuoduiqi" data-name="align" data-value="left"></view>
          <view :class="formats.align === 'center' ? 'ql-active' : ''" class="iconfont icon-juzhongduiqi" data-name="align" data-value="center"></view>
          <view :class="formats.align === 'right' ? 'ql-active' : ''" class="iconfont icon-youduiqi" data-name="align" data-value="right"></view>
          <view :class="formats.align === 'justify' ? 'ql-active' : ''" class="iconfont icon-zuoyouduiqi" data-name="align" data-value="justify"></view>
          <view :class="formats.lineHeight ? 'ql-active' : ''" class="iconfont icon-line-height" data-name="lineHeight" data-value="2"></view>
          <view :class="formats.letterSpacing ? 'ql-active' : ''" class="iconfont icon-Character-Spacing" data-name="letterSpacing" data-value="2em"></view>
          <view :class="formats.marginTop ? 'ql-active' : ''" class="iconfont icon-722bianjiqi_duanqianju" data-name="marginTop" data-value="20px"></view>
          <view :class="formats.previewarginBottom ? 'ql-active' : ''" class="iconfont icon-723bianjiqi_duanhouju" data-name="marginBottom" data-value="20px"></view>
          <view class="iconfont icon-clearedformat" @tap="removeFormat"></view>
          <view :class="formats.fontFamily ? 'ql-active' : ''" class="iconfont icon-font" data-name="fontFamily" data-value="Pacifico"></view>
          <view :class="formats.fontSize === '24px' ? 'ql-active' : ''" class="iconfont icon-fontsize" data-name="fontSize" data-value="24px"></view>

          <view :class="formats.color === '#0000ff' ? 'ql-active' : ''" class="iconfont icon-text_color" data-name="color" data-value="#0000ff"></view>
          <view :class="formats.backgroundColor === '#00ff00' ? 'ql-active' : ''" class="iconfont icon-fontbgcolor" data-name="backgroundColor" data-value="#00ff00"></view>

          <view class="iconfont icon-date" @tap="insertDate"></view>
          <view class="iconfont icon--checklist" data-name="list" data-value="check"></view>
          <view :class="formats.list === 'ordered' ? 'ql-active' : ''" class="iconfont icon-youxupailie" data-name="list" data-value="ordered"></view>
          <view :class="formats.list === 'bullet' ? 'ql-active' : ''" class="iconfont icon-wuxupailie" data-name="list" data-value="bullet"></view>
          <view class="iconfont icon-undo" @tap="undo"></view>
          <view class="iconfont icon-redo" @tap="redo"></view>

          <view class="iconfont icon-outdent" data-name="indent" data-value="-1"></view>
          <view class="iconfont icon-indent" data-name="indent" data-value="+1"></view>
          <view class="iconfont icon-fengexian" @tap="insertDivider"></view>
          <view class="iconfont icon-charutupian" @tap="insertImage"></view>
          <view :class="formats.header === 1 ? 'ql-active' : ''" class="iconfont icon-format-header-1" data-name="header" :data-value="1"></view>
          <view :class="formats.script === 'sub' ? 'ql-active' : ''" class="iconfont icon-zitixiabiao" data-name="script" data-value="sub"></view>
          <view :class="formats.script === 'super' ? 'ql-active' : ''" class="iconfont icon-zitishangbiao" data-name="script" data-value="super"></view>
          <view class="iconfont icon-shanchu" @tap="clear"></view>
          <view :class="formats.direction === 'rtl' ? 'ql-active' : ''" class="iconfont icon-direction-rtl" data-name="direction" data-value="rtl"></view>
        </view>

        <view class="editor-wrapper">
          <editor
            id="editor"
            class="ql-container"
            placeholder="开始输入..."
            showImgSize
            showImgToolbar
            showImgResize
            @statuschange="onStatusChange"
            :read-only="readOnly"
            @ready="onEditorReady"
			@input="onEditorInput"
          >
          </editor>
        </view>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import {upload} from '@/common/index'
import { onMounted,ref,watch } from 'vue';
const props = defineProps<{
	modelValue: any
}>()
const $emit = defineEmits(['update:modelValue']);
const readOnly = ref(false)
const formats = ref<any>({})
const editorCtx = ref()
const editorRef = ref()
const onEditorInput = (e:any) => {
	$emit('update:modelValue', e.detail.html)
}
// const readOnlyChange = () => {
// 	readOnly.value = !readOnly.value
// }
watch(() => props.modelValue, (val) => {
	if (val !== editorCtx.value?.html) {
		editorCtx.value?.setContents({
			html: val || '',
			success: () => {
				console.log('insert html success')
			}
		})
	}
})
const onEditorReady = () => {
	// #ifdef MP-WEIXIN
	uni.createSelectorQuery().in(editorRef.value).select('#editor').context((res:any) => {
		editorCtx.value = res?.context || '';
	}).exec()
	// #endif
	// #ifndef MP-WEIXIN
	uni.createSelectorQuery().select('#editor').context((res:any) => {
		editorCtx.value = res?.context || '';
	}).exec()
	// #endif
}
const undo = () => {
	editorCtx.value?.undo()
}
const redo = () => {
	editorCtx.value?.redo()
}
const format = (e:any) => {
	const { name, value } = e.target.dataset
	if (!name) return
	editorCtx.value?.format(name, value)
}
const onStatusChange = (e:any) => {
	formats.value = e.detail
}
const insertDivider = () => {
	editorCtx.value?.insertDivider({
		success: () => {
			console.log('insert divider success')
		}
	})
}
const clear = () => {
	editorCtx.value?.clear({
		success: () => {
			console.log('clear success')
		}
	})
}
const removeFormat = () => {
	editorCtx.value?.removeFormat({
		success: () => {
			console.log('removeFormat success')
		}
	})
}
const insertDate = () => {
	const date = new Date()
	const formatDate = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
	editorCtx.value?.insertText({
		text: formatDate
	})
}
const insertImage = () => {
	uni.chooseImage({
		count: 1,
		success: (res:any) => {
			upload(res.tempFiles[0]).then(url => {
				editorCtx.value?.insertImage({
					src: url,
					alt: '图像',
					success: () => {
						console.log('insert image success')
					}
				})
			})
		}
	})
}
onMounted(() => {
	  console.log('mounted')
})
</script>

<style lang="scss" scoped>
@import "./editor-icon.css";

.wrapper {
	height: 100%;
}

.editor-wrapper {
	height: 550rpx;
	background: #fff;
}

.iconfont {
	display: inline-block;
	padding: 16rpx;
	width: 65rpx;
	height: 65rpx;
	cursor: pointer;
	font-size: 20px;
}

.toolbar {
	box-sizing: border-box;
	border-bottom: 1px solid #eee;
	font-family: 'Helvetica Neue', 'Helvetica', 'Arial', sans-serif;
}


.ql-container {
	box-sizing: border-box;
	padding: 12px 15px;
	width: 100%;
	min-height: 200rpx;
	height: 100%;
	font-size: 16px;
	line-height: 1.5;
}

.ql-active {
	color: #06c;
}
</style>
