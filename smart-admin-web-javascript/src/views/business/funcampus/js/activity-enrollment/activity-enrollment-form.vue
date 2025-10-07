<!--
  * 活动报名关系
  *
  * @Author:    akkkka114514
  * @Date:      2025-10-02 13:54:42
  * @Copyright  akkkka114514
-->
<template>
  <a-modal
      :title="form.activityId ? '编辑' : '添加'"
      :width="30"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="活动主键"  name="activityId">
          <a-input-number style="width: 100%" v-model:value="form.activityId" placeholder="活动主键" />
        </a-form-item>
        <a-form-item label="用户主键"  name="userId">
          <a-input-number style="width: 100%" v-model:value="form.userId" placeholder="用户主键" />
        </a-form-item>
        <a-form-item label="是否已签到，1-》是，0-》否"  name="signInStatus">
          <BooleanSelect v-model:value="form.signInStatus" style="width: 100%" />
        </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit">保存</a-button>
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
  import { reactive, ref, nextTick } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { activityEnrollmentApi } from '/@/api/business/activity-enrollment/activity-enrollment-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import BooleanSelect from '/@/components/framework/boolean-select/index.vue';

  // ------------------------ 事件 ------------------------

  const emits = defineEmits(['reloadList']);

  // ------------------------ 显示与隐藏 ------------------------
  // 是否显示
  const visibleFlag = ref(false);

  function show(rowData) {
    Object.assign(form, formDefault);
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    // 使用字典时把下面这注释修改成自己的字典字段 有多个字典字段就复制多份同理修改 不然打开表单时不显示字典初始值
    // if (form.status && form.status.length > 0) {
    //   form.status = form.status.map((e) => e.valueCode);
    // }
    visibleFlag.value = true;
    nextTick(() => {
      formRef.value.clearValidate();
    });
  }

  function onClose() {
    Object.assign(form, formDefault);
    visibleFlag.value = false;
  }

  // ------------------------ 表单 ------------------------

  // 组件ref
  const formRef = ref();

  const formDefault = {
      activityId: undefined, //活动主键
      userId: undefined, //用户主键
      signInStatus: undefined, //是否已签到，1-》是，0-》否
  };

  let form = reactive({ ...formDefault });

  const rules = {
      activityId: [{ required: true, message: '活动主键 必填' }],
      userId: [{ required: true, message: '用户主键 必填' }],
      signInStatus: [{ required: true, message: '是否已签到，1-》是，0-》否 必填' }],
  };

  // 点击确定，验证表单
  async function onSubmit() {
    try {
      await formRef.value.validateFields();
      save();
    } catch (err) {
      message.error('参数验证错误，请仔细填写表单数据!');
    }
  }

  // 新建、编辑API
  async function save() {
    SmartLoading.show();
    try {
      if (form.activityId) {
        await activityEnrollmentApi.update(form);
      } else {
        await activityEnrollmentApi.add(form);
      }
      message.success('操作成功');
      emits('reloadList');
      onClose();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  defineExpose({
    show,
  });
</script>
<!--
  * 活动报名关系
  *
  * @Author:    akkkka114514
  * @Date:      2025-10-02 13:54:42
  * @Copyright  akkkka114514
-->
<template>
  <a-modal
      :title="form.activityId ? '编辑' : '添加'"
      :width="30"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="活动主键"  name="activityId">
          <a-input-number style="width: 100%" v-model:value="form.activityId" placeholder="活动主键" />
        </a-form-item>
        <a-form-item label="用户主键"  name="userId">
          <a-input-number style="width: 100%" v-model:value="form.userId" placeholder="用户主键" />
        </a-form-item>
        <a-form-item label="是否已签到，1-》是，0-》否"  name="signInStatus">
          <BooleanSelect v-model:value="form.signInStatus" style="width: 100%" />
        </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit">保存</a-button>
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
  import { reactive, ref, nextTick } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { activityEnrollmentApi } from '/@/api/business/activity-enrollment/activity-enrollment-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import BooleanSelect from '/@/components/framework/boolean-select/index.vue';

  // ------------------------ 事件 ------------------------

  const emits = defineEmits(['reloadList']);

  // ------------------------ 显示与隐藏 ------------------------
  // 是否显示
  const visibleFlag = ref(false);

  function show(rowData) {
    Object.assign(form, formDefault);
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    // 使用字典时把下面这注释修改成自己的字典字段 有多个字典字段就复制多份同理修改 不然打开表单时不显示字典初始值
    // if (form.status && form.status.length > 0) {
    //   form.status = form.status.map((e) => e.valueCode);
    // }
    visibleFlag.value = true;
    nextTick(() => {
      formRef.value.clearValidate();
    });
  }

  function onClose() {
    Object.assign(form, formDefault);
    visibleFlag.value = false;
  }

  // ------------------------ 表单 ------------------------

  // 组件ref
  const formRef = ref();

  const formDefault = {
      activityId: undefined, //活动主键
      userId: undefined, //用户主键
      signInStatus: undefined, //是否已签到，1-》是，0-》否
  };

  let form = reactive({ ...formDefault });

  const rules = {
      activityId: [{ required: true, message: '活动主键 必填' }],
      userId: [{ required: true, message: '用户主键 必填' }],
      signInStatus: [{ required: true, message: '是否已签到，1-》是，0-》否 必填' }],
  };

  // 点击确定，验证表单
  async function onSubmit() {
    try {
      await formRef.value.validateFields();
      save();
    } catch (err) {
      message.error('参数验证错误，请仔细填写表单数据!');
    }
  }

  // 新建、编辑API
  async function save() {
    SmartLoading.show();
    try {
      if (form.activityId) {
        await activityEnrollmentApi.update(form);
      } else {
        await activityEnrollmentApi.add(form);
      }
      message.success('操作成功');
      emits('reloadList');
      onClose();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  defineExpose({
    show,
  });
</script>
<!--
  * 活动报名关系
  *
  * @Author:    akkkka114514
  * @Date:      2025-10-02 13:54:42
  * @Copyright  akkkka114514
-->
<template>
  <a-modal
      :title="form.activityId ? '编辑' : '添加'"
      :width="30"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="活动主键"  name="activityId">
          <a-input-number style="width: 100%" v-model:value="form.activityId" placeholder="活动主键" />
        </a-form-item>
        <a-form-item label="用户主键"  name="userId">
          <a-input-number style="width: 100%" v-model:value="form.userId" placeholder="用户主键" />
        </a-form-item>
        <a-form-item label="是否已签到，1-》是，0-》否"  name="signInStatus">
          <BooleanSelect v-model:value="form.signInStatus" style="width: 100%" />
        </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit">保存</a-button>
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
  import { reactive, ref, nextTick } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { activityEnrollmentApi } from '/@/api/business/activity-enrollment/activity-enrollment-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import BooleanSelect from '/@/components/framework/boolean-select/index.vue';

  // ------------------------ 事件 ------------------------

  const emits = defineEmits(['reloadList']);

  // ------------------------ 显示与隐藏 ------------------------
  // 是否显示
  const visibleFlag = ref(false);

  function show(rowData) {
    Object.assign(form, formDefault);
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    // 使用字典时把下面这注释修改成自己的字典字段 有多个字典字段就复制多份同理修改 不然打开表单时不显示字典初始值
    // if (form.status && form.status.length > 0) {
    //   form.status = form.status.map((e) => e.valueCode);
    // }
    visibleFlag.value = true;
    nextTick(() => {
      formRef.value.clearValidate();
    });
  }

  function onClose() {
    Object.assign(form, formDefault);
    visibleFlag.value = false;
  }

  // ------------------------ 表单 ------------------------

  // 组件ref
  const formRef = ref();

  const formDefault = {
      activityId: undefined, //活动主键
      userId: undefined, //用户主键
      signInStatus: undefined, //是否已签到，1-》是，0-》否
  };

  let form = reactive({ ...formDefault });

  const rules = {
      activityId: [{ required: true, message: '活动主键 必填' }],
      userId: [{ required: true, message: '用户主键 必填' }],
      signInStatus: [{ required: true, message: '是否已签到，1-》是，0-》否 必填' }],
  };

  // 点击确定，验证表单
  async function onSubmit() {
    try {
      await formRef.value.validateFields();
      save();
    } catch (err) {
      message.error('参数验证错误，请仔细填写表单数据!');
    }
  }

  // 新建、编辑API
  async function save() {
    SmartLoading.show();
    try {
      if (form.activityId) {
        await activityEnrollmentApi.update(form);
      } else {
        await activityEnrollmentApi.add(form);
      }
      message.success('操作成功');
      emits('reloadList');
      onClose();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  defineExpose({
    show,
  });
</script>
<!--
  * 活动报名关系
  *
  * @Author:    akkkka114514
  * @Date:      2025-10-02 13:54:42
  * @Copyright  akkkka114514
-->
<template>
  <a-modal
      :title="form.activityId ? '编辑' : '添加'"
      :width="30"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="活动主键"  name="activityId">
          <a-input-number style="width: 100%" v-model:value="form.activityId" placeholder="活动主键" />
        </a-form-item>
        <a-form-item label="用户主键"  name="userId">
          <a-input-number style="width: 100%" v-model:value="form.userId" placeholder="用户主键" />
        </a-form-item>
        <a-form-item label="是否已签到，1-》是，0-》否"  name="signInStatus">
          <BooleanSelect v-model:value="form.signInStatus" style="width: 100%" />
        </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit">保存</a-button>
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
  import { reactive, ref, nextTick } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { activityEnrollmentApi } from '/@/api/business/activity-enrollment/activity-enrollment-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import BooleanSelect from '/@/components/framework/boolean-select/index.vue';

  // ------------------------ 事件 ------------------------

  const emits = defineEmits(['reloadList']);

  // ------------------------ 显示与隐藏 ------------------------
  // 是否显示
  const visibleFlag = ref(false);

  function show(rowData) {
    Object.assign(form, formDefault);
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    // 使用字典时把下面这注释修改成自己的字典字段 有多个字典字段就复制多份同理修改 不然打开表单时不显示字典初始值
    // if (form.status && form.status.length > 0) {
    //   form.status = form.status.map((e) => e.valueCode);
    // }
    visibleFlag.value = true;
    nextTick(() => {
      formRef.value.clearValidate();
    });
  }

  function onClose() {
    Object.assign(form, formDefault);
    visibleFlag.value = false;
  }

  // ------------------------ 表单 ------------------------

  // 组件ref
  const formRef = ref();

  const formDefault = {
      activityId: undefined, //活动主键
      userId: undefined, //用户主键
      signInStatus: undefined, //是否已签到，1-》是，0-》否
  };

  let form = reactive({ ...formDefault });

  const rules = {
      activityId: [{ required: true, message: '活动主键 必填' }],
      userId: [{ required: true, message: '用户主键 必填' }],
      signInStatus: [{ required: true, message: '是否已签到，1-》是，0-》否 必填' }],
  };

  // 点击确定，验证表单
  async function onSubmit() {
    try {
      await formRef.value.validateFields();
      save();
    } catch (err) {
      message.error('参数验证错误，请仔细填写表单数据!');
    }
  }

  // 新建、编辑API
  async function save() {
    SmartLoading.show();
    try {
      if (form.activityId) {
        await activityEnrollmentApi.update(form);
      } else {
        await activityEnrollmentApi.add(form);
      }
      message.success('操作成功');
      emits('reloadList');
      onClose();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  defineExpose({
    show,
  });
</script>
