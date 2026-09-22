<!--
  * 签到签退二维码管理
  *
  * @Author:    akkkka114514
  * @Date:      2026-08-12
  * @Copyright  akkkka114514
-->
<template>
    <a-card size="small" :bordered="false" :hoverable="true">
        <a-row :gutter="24">
            <!-- 左侧：二维码展示区 -->
            <a-col :span="12">
                <a-card title="签到/签退二维码" :bordered="true">
                    <div class="qr-code-container">
                        <div v-if="qrCodeLoading" class="qr-loading">
                            <a-spin size="large" />
                            <p>正在生成二维码...</p>
                        </div>
                        <div v-else-if="qrCodeData" class="qr-code-wrapper">
                            <img :src="qrCodeData.qrCodeImage" alt="签到/签退二维码" class="qr-code-img" />
                            <div class="qr-info">
                                <p><strong>UID：</strong>{{ qrCodeData.userId }}</p>
                                <p><strong>Token：</strong>{{ qrCodeData.token }}</p>
                                <p class="expire-info">
                                    <strong>有效期：</strong>
                                    <span :class="{ 'expire-warning': countdown <= 10 }">{{ countdown }}秒后过期</span>
                                </p>
                            </div>
                            <a-button type="primary" @click="refreshQRCode" :loading="qrCodeLoading">
                                <template #icon><ReloadOutlined /></template>
                                刷新二维码
                            </a-button>
                        </div>
                        <div v-else class="qr-empty">
                            <p>点击下方按钮生成二维码</p>
                            <a-button type="primary" @click="generateQRCode" :loading="qrCodeLoading">
                                <template #icon><QrcodeOutlined /></template>
                                生成二维码
                            </a-button>
                        </div>
                    </div>
                </a-card>
            </a-col>

            <!-- 右侧：手动签到/签退操作区 -->
            <a-col :span="12">
                <a-card title="手动签到/签退" :bordered="true">
                    <a-form :model="signInForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
                        <a-form-item label="活动ID" required>
                            <a-input-number
                                v-model:value="signInForm.activityId"
                                placeholder="请输入活动ID"
                                style="width: 100%"
                                :min="1"
                            />
                        </a-form-item>
                        <a-form-item label="目标用户ID" required>
                            <a-input-number
                                v-model:value="signInForm.targetUserId"
                                placeholder="请输入被签到/签退的用户ID"
                                style="width: 100%"
                                :min="1"
                            />
                        </a-form-item>
                        <a-form-item label="Token" required>
                            <a-input
                                v-model:value="signInForm.token"
                                placeholder="请输入二维码中的Token"
                            />
                        </a-form-item>
                        <a-form-item :wrapper-col="{ offset: 6, span: 16 }">
                            <a-space>
                                <a-button type="primary" @click="handleSignIn" :loading="signInLoading">
                                    签到
                                </a-button>
                                <a-button type="primary" @click="handleSignOut" :loading="signOutLoading" danger>
                                    签退
                                </a-button>
                            </a-space>
                        </a-form-item>
                    </a-form>
                </a-card>

                <a-card title="使用说明" :bordered="true" style="margin-top: 16px">
                    <a-typography-paragraph>
                        <ol>
                            <li>左侧二维码为当前账号自己的签到/签退二维码（含 UID 与 Token），有效期 30 秒，可手动刷新</li>
                            <li>学员端展示各自的二维码，扫码后解析出学员的 UID 与 Token</li>
                            <li>在右侧表单填入活动 ID、学员 UID 与学员二维码中的 Token</li>
                            <li>点击「签到」或「签退」完成操作（需处于该活动对应时间窗口，且当前账号为该活动签到员）</li>
                            <li>签到/签退成功后该 Token 即作废，需学员刷新二维码后再次操作</li>
                            <li>二维码过期后需重新生成</li>
                        </ol>
                    </a-typography-paragraph>
                </a-card>
            </a-col>
        </a-row>
    </a-card>
</template>
<script setup>
    import { reactive, ref, onMounted, onUnmounted } from 'vue';
    import { message } from 'ant-design-vue';
    import { SmartLoading } from '/@/components/framework/smart-loading';
    import { activityEnrollmentApi } from '/@/api/business/funcampus/activity-enrollment-api';
    import { smartSentry } from '/@/lib/smart-sentry';

    // ---------------------------- 二维码相关 ----------------------------
    const qrCodeData = ref(null);
    const qrCodeLoading = ref(false);
    const countdown = ref(0);
    let countdownTimer = null;

    // 生成二维码
    async function generateQRCode() {
        qrCodeLoading.value = true;
        try {
            const result = await activityEnrollmentApi.signInQRCode();
            qrCodeData.value = result.data;
            countdown.value = result.data.expireSeconds;
            startCountdown();
            message.success('二维码生成成功');
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            qrCodeLoading.value = false;
        }
    }

    // 刷新二维码
    async function refreshQRCode() {
        stopCountdown();
        await generateQRCode();
    }

    // 开始倒计时
    function startCountdown() {
        stopCountdown();
        countdownTimer = setInterval(() => {
            countdown.value--;
            if (countdown.value <= 0) {
                stopCountdown();
                message.warning('二维码已过期，请刷新');
            }
        }, 1000);
    }

    // 停止倒计时
    function stopCountdown() {
        if (countdownTimer) {
            clearInterval(countdownTimer);
            countdownTimer = null;
        }
    }

    // ---------------------------- 签到/签退表单 ----------------------------
    const signInForm = reactive({
        activityId: undefined,
        targetUserId: undefined,
        token: '',
    });

    const signInLoading = ref(false);
    const signOutLoading = ref(false);

    // 签到
    async function handleSignIn() {
        if (!signInForm.activityId || !signInForm.targetUserId || !signInForm.token) {
            message.error('请填写完整的签到信息');
            return;
        }
        signInLoading.value = true;
        try {
            await activityEnrollmentApi.signInByQRCode({
                activityId: signInForm.activityId,
                targetUserId: signInForm.targetUserId,
                token: signInForm.token,
            });
            message.success('签到成功');
            resetForm();
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            signInLoading.value = false;
        }
    }

    // 签退
    async function handleSignOut() {
        if (!signInForm.activityId || !signInForm.targetUserId || !signInForm.token) {
            message.error('请填写完整的签退信息');
            return;
        }
        signOutLoading.value = true;
        try {
            await activityEnrollmentApi.signOutByQRCode({
                activityId: signInForm.activityId,
                targetUserId: signInForm.targetUserId,
                token: signInForm.token,
            });
            message.success('签退成功');
            resetForm();
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            signOutLoading.value = false;
        }
    }

    // 重置表单
    function resetForm() {
        signInForm.activityId = undefined;
        signInForm.targetUserId = undefined;
        signInForm.token = '';
    }

    // ---------------------------- 生命周期 ----------------------------
    onMounted(() => {
        // 页面加载时自动生成二维码
        generateQRCode();
    });

    onUnmounted(() => {
        stopCountdown();
    });
</script>
<style scoped>
    .qr-code-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 20px 0;
    }

    .qr-loading {
        text-align: center;
    }

    .qr-code-wrapper {
        text-align: center;
    }

    .qr-code-img {
        width: 250px;
        height: 250px;
        border: 1px solid #f0f0f0;
        border-radius: 8px;
    }

    .qr-info {
        margin: 16px 0;
        text-align: left;
        display: inline-block;
    }

    .qr-info p {
        margin: 4px 0;
    }

    .expire-info .expire-warning {
        color: #ff4d4f;
        font-weight: bold;
    }

    .qr-empty {
        text-align: center;
        padding: 40px 0;
    }
</style>
