<template>
  <tm-app>
    <!-- <tm-sheet :margin="[0, 0]" :padding="[0, 0]">
      <view>费用</view>
    </tm-sheet> -->
    <tm-sheet :margin="[0, 0]" :padding="[0, 15]">
      <tm-form @submit="confirm" :margin="[0, 0]" ref="form" v-model="applyForm" :label-width="150">
        <template v-for="(item, index) in fields" :key="index">
          <tm-form-item :margin="[15, 0]" v-if="item.type === 'text'" :required="item.required" :field="item.name" :rules="rulesList(item)" :label="item.title + ':'">
            <tm-input :inputPadding="[20, 0]" :round="20" showClear v-model.lazy="applyForm[item.name]" :placeholder="`请输入${item.title}`" :showBottomBotder="false"> </tm-input>
          </tm-form-item>
          <tm-form-item :margin="[15, 0]" v-else-if="item.type === 'number'" :required="item.required" :field="item.name" :rules="rulesList(item)" :label="item.title + ':'">
            <tm-input
              :inputPadding="[20, 0]"
              type="number"
              showClear
              :round="20"
              v-model.lazy="applyForm[item.name]"
              :placeholder="`请输入${item.title}`"
              :showBottomBotder="false"
            >
            </tm-input>
          </tm-form-item>
          <tm-form-item :margin="[15, 0]" v-else-if="item.type === 'textarea'" :required="item.required" :field="item.name" :rules="rulesList(item)" :label="item.title + ':'">
            <tm-input
              :inputPadding="[24, 15]"
              :height="100"
              showClear
              type="textarea"
              v-model.lazy="applyForm[item.name]"
              :placeholder="`请输入${item.title}`"
              :showBottomBotder="false"
            >
            </tm-input>
          </tm-form-item>
          <tm-form-item :margin="[15, 0]" v-else-if="item.type === 'radio'" :required="item.required" :field="item.name" :rules="rulesList(item)" :label="item.title + ':'">
            <tm-radio-group v-model="applyForm[item.name]">
              <tm-radio v-for="(radio, radioIndex) in item.options" :key="radioIndex" :value="radio.value" :label="radio.label"></tm-radio>
            </tm-radio-group>
          </tm-form-item>
          <tm-form-item :margin="[15, 0]" v-else-if="item.type === 'checkbox'" :required="item.required" :field="item.name" :rules="rulesList(item)" :label="item.title + ':'">
            <tm-checkbox-group v-model="applyForm[item.name]">
              <tm-checkbox v-for="(radio, radioIndex) in item.options" :key="radioIndex" :value="radio.value" :label="radio.label"></tm-checkbox>
            </tm-checkbox-group>
          </tm-form-item>
          <tm-form-item :margin="[15, 0]" v-else-if="item.type === 'select'" :required="item.required" :field="item.name" :rules="rulesList(item)" :label="item.title + ':'">
            <view @click="showSelect[item.name] = true" class="input-select round-3" :class="{ 'no-select': !applyForm[item.name] }">
              {{ strSelect[item.name] || `请选择${item.title}` }}</view
            >
            <tm-picker v-model:show="showSelect[item.name]" mapKey="label" :columns="item.options" v-model="applyForm[item.name]" v-model:model-str="strSelect[item.name]">
            </tm-picker>
          </tm-form-item>
          <tm-form-item :margin="[15, 0]" v-else-if="item.type === 'switch'" :required="item.required" :field="item.name" :rules="rulesList(item)" :label="item.title + ':'">
            <tm-switch v-model="applyForm[item.name]" :defaultValue="!!applyForm[item.name]"></tm-switch>
          </tm-form-item>
          <tm-form-item :margin="[15, 0]" v-else-if="item.type === 'upload'" :required="item.required" :field="item.name" :rules="rulesList(item)" :label="item.title + ':'">
            <dx-upload v-model="applyForm[item.name]"></dx-upload>
          </tm-form-item>
          <tm-form-item :margin="[15, 0]" v-else-if="item.type === 'date'" :required="item.required" :field="item.name" :rules="rulesList(item)" :label="item.title + ':'">
            <view @click="showSelect[item.name] = true" class="input-select round-3" :class="{ 'no-select': !applyForm[item.name] }">
              {{ strSelect[item.name] || `请选择${item.title}` }}</view
            >
            <tm-time-picker
              :showDetail="{ year: true, month: true, day: true}"
              v-model:show="showSelect[item.name]"
              v-model="applyForm[item.name]"
              :defaultValue="applyForm[item.name]"
              format="YYYY年MM月DD日"
              v-model:model-str="strSelect[item.name]"
            ></tm-time-picker>
          </tm-form-item>
          <tm-form-item :margin="[15, 0]" v-else-if="item.type === 'time'" :required="item.required" :field="item.name" :rules="rulesList(item)" :label="item.title + ':'">
            <view @click="showSelect[item.name] = true" class="input-select round-3" :class="{ 'no-select': !applyForm[item.name] }">
              {{ strSelect[item.name] || `请选择${item.title}` }}</view
            >
            <tm-time-picker
              :showDetail="{ year: false, month: false, day: false,hour:true,minute:true,second:true}"
              v-model:show="showSelect[item.name]"
              v-model="applyForm[item.name]"
              :defaultValue="applyForm[item.name]"
              format="HH时mm分ss秒"
              v-model:model-str="strSelect[item.name]"
            ></tm-time-picker>
          </tm-form-item>
          <tm-form-item :margin="[15, 0]" v-else-if="item.type === 'datetime'" :required="item.required" :field="item.name" :rules="rulesList(item)" :label="item.title + ':'">
            <view @click="showSelect[item.name] = true" class="input-select round-3" :class="{ 'no-select': !applyForm[item.name] }">
              {{ strSelect[item.name] || `请选择${item.title}` }}</view
            >
            <tm-time-picker
              :showDetail="{ year: true, month: true, day: true,hour:true,minute:true,second:true}"
              v-model:show="showSelect[item.name]"
              v-model="applyForm[item.name]"
              :defaultValue="applyForm[item.name]"
              format="YYYY年MM月DD日 HH时mm分ss秒"
              v-model:model-str="strSelect[item.name]"
            ></tm-time-picker>
          </tm-form-item>
        </template>
        <tm-form-item :border="false">
          <tm-button :margin="[10]" :shadow="0" :round="20" size="small" form-type="submit" block label="报名"></tm-button>
        </tm-form-item>
      </tm-form>
    </tm-sheet>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref, computed } from 'vue';
import { formAndCost,applySave } from '@/common/index'
import { onLoad } from '@dcloudio/uni-app';
import { openLink } from '@/common/tools';
const id = ref('');
const applyForm = ref<any>({});
const fields = ref<any>([]);
const showSelect = ref<any>({});
const strSelect = ref<any>({});
const rulesList = computed(() => (row: any) => {
    const rules = [];
    if (row.required) {
        rules.push({
            required: true,
            message: '请输入' + row.title,
        })
    }
    return rules;
})

onLoad((e: any) => {
    if (e.id) {
        id.value = e.id;
        formAndCost({ id: e.id }).then(res => {
            console.log(res)
            if (res.code === 1000) {
                const list = res.data.form_list;
                list.map((item: any) => {
                    if (item.type === 'select' || item.type === 'checkbox') {
                        applyForm.value[item.name] = item.value || [];
                    } else {
                        applyForm.value[item.name] = item.value || '';
                    }
                    showSelect.value[item.name] = false;
                    strSelect.value[item.name] = '';
                })
                fields.value = list;

            }
        })
    }
})
function confirm(e: any) {
    if (e.validate) {
        applySave({
            id: id.value,
            form: e.data,
        }).then(res => {
            console.log(res);
            uni.$tm.u.toast(res.message)
            if(res.code === 1000){
                openLink('others/activity/info?id='+id.value,1)
            }
        });
    }
}
</script>
