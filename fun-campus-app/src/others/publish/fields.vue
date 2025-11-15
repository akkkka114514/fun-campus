<template>
  <tm-app>
    <tm-sheet :margin="[0, 0]" :padding="[30, 30]" :round="3" :shadow="2">
      <view class="flex">
        <view class="title">标题</view>
        <view class="name mx-15">名称</view>
        <view class="operate">操作</view>
      </view>
      <view class="flex content mt-30" v-for="(item,index) in fieldList" :key="index">
        <view class="title">
          <tm-input v-model="item.title" showClear placeholder="请输入标题"></tm-input>
        </view>
        <view class="name mx-15">
          <tm-input v-model="item.name" showClear placeholder="请输入名称"></tm-input>
        </view>
        <view class="flex operate ml-15">
          <view>
            <tm-icon name="tmicon-cog" color="#999" @click="setting(item)"></tm-icon>
          </view>
          <view class="mx-20">
            <tm-icon name="tmicon-times-circle" color="#999" @click="remove(index)"></tm-icon>
          </view>
          <view>
            <tm-icon :name="isDirection(index)?'tmicon-long-arrow-up':'tmicon-long-arrow-down'" color="#999" @click="toSort(index)"></tm-icon>
          </view>
        </view>
      </view>
      <!--  -->
      <view class="flex footer-btn">
        <view class="flex-1">
          <tm-button :margin="[15]" :shadow="0" :round="20" outlined size="small" form-type="submit" block label="添加一个" @click="add"></tm-button>
        </view>
        <view class="flex-1">
          <tm-button :margin="[15]" :shadow="0" :round="20" size="small" form-type="submit" block label="确定保存" @click="submit"></tm-button>
        </view>
      </view>
    </tm-sheet>
    <tm-drawer ref="calendarView" :width="550" placement="right" :round="0" :hideHeader="true" v-model:show="showWin">
      <view class="pa-30">
        <view> 数据类型 </view>
        <view class="mt-15">
          <tm-radio-group v-model="currentRow.type">
            <view class="left">
              <tm-radio value="number" label="数字"></tm-radio>
            </view>
            <view class="right">
              <tm-radio value="text" label="单行文本"></tm-radio>
            </view>
            <view class="left">
              <tm-radio value="textarea" label="多行文本"></tm-radio>
            </view>
            <view class="right">
              <tm-radio value="radio" label="单选框"></tm-radio>
            </view>
            <view class="left">
              <tm-radio value="checkbox" label="复选框"></tm-radio>
            </view>
            <view class="right">
              <tm-radio value="select" label="弹窗选择"></tm-radio>
            </view>
            <view class="left">
              <tm-radio value="switch" label="开关"></tm-radio>
            </view>
            <view class="right">
              <tm-radio value="upload" label="图片上传"></tm-radio>
            </view>
            <view class="left">
              <tm-radio value="date" label="日期"></tm-radio>
            </view>
            <view class="right">
              <tm-radio value="time" label="时间"></tm-radio>
            </view>
            <view class="left">
              <tm-radio value="datetime" label="日期时间"></tm-radio>
            </view>
          </tm-radio-group>
        </view>
        <template v-if="['radio','checkbox','select'].includes(currentRow.type)">
            <view class="flex-row-center-between mt-30">
                <view class="label">选项名</view>
                <view class="value mx-20">选项值</view>
                <view>
                    <tm-icon name="tmicon-plus" :font-size="35" color="#3c8af8" @click="addOption"></tm-icon>
                </view>
            </view>
            <view class="flex-row-center-between mt-20" v-for="(row,rowIndex) in currentRow.options" :key="rowIndex">
                <view class="label">
                    <tm-input v-model="row.label" showClear placeholder="选项名"></tm-input>
                </view>
                <view class="value mx-20">
                    <tm-input v-model="row.value" showClear placeholder="选项值"></tm-input>
                </view>
                <view>
                    <tm-icon name="tmicon-delete" color="#f56c6c" @click="delOption(rowIndex)"></tm-icon>
                </view>
            </view>
        </template>
        <view class="mt-30">
            提示文字
        </view>
        <view class="mt-20">
            <tm-input v-model="currentRow.tips" showClear placeholder="请输入提示文字"></tm-input>
        </view>
        <view class="mt-30">
            是否必填
        </view>
        <view class="mt-20">
            <tm-switch v-model="currentRow.required" :defaultValue="currentRow.required"></tm-switch>
        </view>
      </view>
    </tm-drawer>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref,computed } from 'vue'
import {useActivityStore} from '@/stores/activity'
const showWin = ref(false)
const fieldList = ref<any>([]);
const currentRow = ref<any>({});
const activityStore = useActivityStore();
const isDirection = computed(()=>(index:number)=>{
    return fieldList.value.length-1 === index
})
if(fieldList.value.length === 0 && activityStore.fieldList.length===0){
   add()
}else if(activityStore.fieldList.length>0){
    fieldList.value = activityStore.fieldList
}
function toSort(index:number){
    if(fieldList.value.length === 1){
        return
    }
    if(isDirection.value(index)){
        // 向上一个
        fieldList.value.splice(index-1,0,fieldList.value.splice(index,1)[0])
    }else{
        // 向下一个
        fieldList.value.splice(index+1,0,fieldList.value.splice(index,1)[0])
    }
}
function add() {
  fieldList.value.push({
    title: '',
    name: '',
    value: '',
    type: 'text',
    tips: '',
    options: [],
    required: false,
})
}
function remove(index:number) {
  fieldList.value.splice(index, 1)
}
function setting(row:any){
    currentRow.value = row
    showWin.value = true
}
function addOption(){
    currentRow.value.options.push({
        label:'',
        value:''
    })
}
function delOption(index:number){
    currentRow.value.options.splice(index,1)
}
function submit(){
    // 标题不能为空
    const titleReg = fieldList.value.every((item:any)=>item.title)
    if(!titleReg){
        return uni.$tm.u.toast('标题不能为空')
    }
    // 名称不能为空
    const nameReg = fieldList.value.every((item:any)=>item.name)
    if(!nameReg){
        return uni.$tm.u.toast('名称不能为空')
    }
    // 校验标题和字段名是否重复
    const titleList = fieldList.value.map((item:any)=>item.title)
    const nameList = fieldList.value.map((item:any)=>item.name)
    const titleSet = new Set(titleList)
    const nameSet = new Set(nameList)
    if(titleSet.size !== titleList.length){
        return uni.$tm.u.toast('标题不能重复')
    }
    if(nameSet.size !== nameList.length){
        return uni.$tm.u.toast('名称不能重复')
    }
    // 名称必须是字母和数字组合且不能以数字开头
    const reg = /^[a-zA-Z][a-zA-Z0-9]*$/;
    const nameReg2 = nameList.every((item:string)=>reg.test(item))
    if(!nameReg2){
        return uni.$tm.u.toast('名称必须是字母和数字组合且不能以数字开头')
    }
    activityStore.setFieldList(fieldList.value);
    uni.navigateBack()
}
</script>
<style lang="scss" scoped>
.title,.name{
    width: 33vw;
}
.content{
    align-items: center;
}
.footer-btn{
    margin-top: 100rpx;
}
.left{
    width: 220rpx;
}
.left,.right{
    margin-top: 10rpx;
}
.label,.value{
    width: 45%;
}
</style>
