# Fun Campus

<p align="center">
  <img src="https://img.shields.io/badge/SmartAdmin-Framework-blue" alt="SmartAdmin Framework">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen" alt="Spring Boot 3.5.4">
  <img src="https://img.shields.io/badge/Vue-3.4.27-brightgreen" alt="Vue 3.4.27">
</p>


**重要声明：本软件为独立开发，与 PU口袋校园 无任何关联。所有代码、设计均为原创，仅借鉴了校园管理系统的一般功能概念。**


前端我实在写不下去了

## 基于前端的功能描述
首页
- 扫码按钮
- 搜索框
- 消息按钮
- pu签到二维码按钮
- 学分认定按钮
- 二课部落按钮
- 校园生活按钮
- 活动日历按钮
- 问卷调查按钮
- 显示当前学校的二课活动，显示标题，封面，活动开始报名时间和活动结束时间，活动状态
  - 活动详情页
- 对当前显示的二课活动进行筛选的按钮
我的
- 基本信息编辑按钮
- 显示待签到数量，点击进入显示待签到活动
- 显示待签退数量，点击进入显示待签退活动
- 显示待评价数量，点击进入显示待评价活动
- 显示实践积分，点击进入详情页
- 显示诚信分，点击进入详情页
- 我的活动按钮
- 我的部落按钮
- 我的申请按钮
- 我的评价按钮
校园生活
- 敬请期待




- 扫码按钮
- 点击进入后弹出窗口：选择签到签退活动
- 基本扫二维码功能
- 扫描签到签退二维码后可实现被扫的人的活动签到签退
搜索框
- 基本搜索功能
- 历史搜索记录
消息
- 新增校园生活关注
- 校园生活互动消息
- 二课活动状态通知
- 校内通知
pu签到二维码按钮
- 显示uid
- 显示二维码
- 二维码30s刷新
学分认定按钮
- 历史申请记录
  - 显示待审核，已通过，已驳回申请
  - 待审核活动可以编辑或删除
  - 申请详情有标题，申请时间，分类获奖时间为哪个学期，申请内容，审核人，审核状态
  - 申请表单
    - 标题
    - 学期
    - 证明材料：图片
    - 审核人院系/组织
    - 审核人
- 二课部落按钮
  - 显示本校部落，图标，热度
    - 详情页有图标，标题，归属学院组织热度
      - 评分功能
      - 收藏功能
      - 查看所有成员
      - 公告
      - 发起的活动
      - 申请加入按钮（触发表单）
        - 加入理由
        - 兴趣特长
        - 一二三志愿
        - 是否服从调剂
  - 搜索按钮
    - 历史记录
  - 筛选功能
校园生活按钮
  - 暂不考虑
活动日历按钮
  - 当天活动
  - 全部活动
问卷调查
  - 暂不考虑
活动详情页
  - 活动状态
  - 活动封面
  - 标题
  - 分类
  - 可以获得的实践分
  - 距离活动下一阶段开始还有
  - 可参与人数
  - 已报名人数
  - 已签到人数
  - 报名时间段，活动时间段
  - 活动成员
    - 管理员
    - 签到员
  - 归属组织
  - 活动附件
  - 简介
  - 报名是否需要审核
  - 是否需要签退
  - 参与团体
  - 联系活动发起人对话框
  - 分享按钮
  - 收藏按钮
  - 评论
  - 报名按钮
基本信息编辑
  - 上传头像
  - 改昵称

---

## 开发计划

### 当前进度总览

| 层级 | 模块 | 状态 |
|------|------|------|
| 后端 | 活动管理（提交/审核/报名/签到签退/详情/倒计时/首页数据） | ✅ 已完成（接口均带 portal 前缀） |
| 后端 | 基础数据（学校/学院/组织/前台用户/部落用户） | 🟡 接口已实现，但未适配 portal/backend 前缀，请求会被拦截 |
| 后端 | 活动报名范围控制（学院/年级/部落） | 🟡 学院/年级接口已暴露；部落 Controller 为空壳 |
| 后端 | 部落管理 CRUD | 🟡 Service 已实现，Controller 仅暴露 `/portal/tribe/query/simple` |
| 管理后台前端 | 37 个业务页面（活动/基础数据/报名/审核/部落等） | ⚠️ 页面已生成，但 34 个页面 API import 失效、接口约定与后端不一致，暂无页面可完整联调（详见 Phase 0） |
| 移动端前端 | 首页 / 我的 / 活动详情等 | ❌ 未开始 |
| 后端 | 评论 / 收藏 / 分享 / 签到签退二维码 | ✅ 已完成（前端页面暂无） |
| 后端 | 消息通知 / 学分认定 | ❌ 未开始 |
| 后端+管理后台 | 活动付费参加（订单 / 支付 / 退款） | ❌ 未开始 |

---

### Phase 0：管理后台「前端已有、后端缺失」接口对齐（优先级：高）

> 背景：管理后台前端已生成 37 个业务页面（含 API 封装与菜单 SQL），但排查代码后发现：部分模块后端完全缺失，部分 Controller 为空壳（仅有 `@Resource` 注入、无任何接口方法），部分接口路径与前端约定不一致。本阶段目标是把「前端已画好、后端接不上」的缺口逐个补齐。

#### 0.1 后端模块完全缺失（前端页面 + API + 菜单均已存在）

- [ ] 组织干事用户（organizer-cadre）：前端有 `organizer-cadre-list/form.vue`、`organizer-cadre-api.js`（调用 `/organizationCadre/*`）、菜单 SQL（`OrganizerCadreMenu.sql`）；后端无任何模块、数据库无 `organizer_cadre` 表 —— 需建表 + 补 entity/service/controller，或确认该功能废弃并清理前端
- [ ] 组织账号运营者（portal-organizer-user）：前端有 `portal-organization-user-list/form.vue`、`portal-organization-user-api.js`（调用 `/portalOrganizationUser/*`）；`portal_organization_user` 表已存在；后端无对应 service/controller，需补齐

#### 0.2 后端 Controller 为空壳（需补 CRUD 接口）

> 以下模块已有 domain / dao / manager / service 骨架，但 Controller 里没有暴露任何接口。

- [ ] 活动分类管理：`ActivityCategoryController` 空壳，Service 仅有 `getNameById/getAll`；前端调用 `/activityCategory/{queryPage,add,update,delete/{id},batchDelete}`
- [ ] 年级信息管理：`GradeInfoController` 空壳，Service 仅有 `getAll/getNameById`；前端调用 `/gradeInfo/*`
- [ ] 活动签到管理员管理：`ActivitySigninManagerController` 空壳，Service 仅有活动创建流程用的内部批处理方法；前端调用 `/activitySigninManager/*`
- [ ] 活动可报名部落管理：`ActivityCanEnrollTribeController` 空壳，情况同上；前端调用 `/activityCanEnrollTribe/*`

#### 0.3 Service 已实现、仅需暴露 Controller

- [ ] 部落管理：`TribeService` 已含完整 CRUD（queryPage/add/update/batchDelete/delete），但 `TribeController` 只暴露了 `/portal/tribe/query/simple`；需按前端约定补齐 `/tribe/*`，或统一调整为 `/backend/tribe/*` 并同步前端

#### 0.4 接口路径 / 方法不一致（需前后端对齐）

- [ ] 活动管理：前端 `activity-api.js` 调用 `/activity/{queryPage,add,delete/{id}}`，后端实际为 `/portal/activity/{query,submit,delete(POST)}` —— 建议前端对齐后端
- [ ] 活动时间表：前端 `activity-schedule-api.js` 调用独立 `/activitySchedule/*`，后端无此模块（时间表随 `/portal/activity/*` 组合接口一起维护）—— 需确认独立页面去留
- [ ] 活动报名管理：前端需 `/activityEnrollment/{queryPage,add,update,delete,batchDelete}` 管理接口，后端仅有报名 + 扫码（enroll / signIn / signOut）4 个接口，管理端分页查询与 CRUD 待补
- [ ] 活动审核日志：前端需 `/activityReviewLog/queryPage`，后端仅有 `add/update/review/initial/latest/{activityId}`
- [ ] `activityWithSchedule` 的 `/activity/draft/submit` 仅有 TODO（触发状态机后未落库），`getReviewProposal` 方法未加 mapping 注解（疑似未完成）

#### 0.5 URL 前缀适配（portal / backend 双端体系）

- [ ] `AdminInterceptor` 按 URL 路径段分派用户体系，不含 `portal` 或 `backend` 段的请求会被直接拒绝；以下已实现接口的模块未适配，目前请求都会被拦截：`activityEnrollment`、`activityCanEnrollCollege`、`activityCanEnrollGrade`、`activityReviewLog`、`collegeInfo`、`organizationInfo`、`portalUser`、`schoolInfo`、`tribeUser`
- [ ] 以下空壳模块（见 0.2）补 CRUD 接口时需一并适配前缀：`activityCategory`、`gradeInfo`、`activitySigninManager`、`activityCanEnrollTribe`
- [ ] 已适配的参考：`activityWithSchedule` / `activityComment` / `activityFavorite` / `activityShare`（`@RequestMapping("portal")`）、`portalLogin`（`/portal/login`）
- [ ] 前端对应 API 封装同步加前缀（管理后台统一 `/backend/*`，门户统一 `/portal/*`），`signin-qrcode.vue` 调试页依赖的 `/activityEnrollment/*` 一并归入前缀规范

#### 0.6 前端页面修复（技术债）

- [ ] 34 个页面的 API import 路径失效：API 文件已迁移到 `src/api/business/funcampus/`，但页面仍引用旧路径（如 `/@/api/business/college-info/college-info-api`），页面加载即报错；目前仅 `activity-list` / `activity-form` / `signin-qrcode` 3 个页面用了新路径
- [ ] 菜单 SQL 组件路径失效：`sql_script/mysql/*Menu.sql` 中 component 写的是 `/business/{module}/...`，实际文件在 `/business/funcampus/{module}/...`，需修订 SQL 或调整目录
- [ ] `activity-enrollment-list.vue` / `activity-enrollment-form.vue` 内容重复拼接（各含 4 份 template + script），无法编译，需清理重写

---

### Phase 1：后端核心业务补全（优先级：高）

> 目标：补齐移动端所需的核心后端接口

#### 1.1 活动详情接口完善
- [✅] 完善 `activityWithSchedule/detail` 接口，返回完整活动详情（封面、附件、简介、管理员/签到员信息、报名人数、签到人数）
- [✅] 活动阶段倒计时接口（距离下一阶段开始剩余时间）
- [✅] 活动收藏 / 取消收藏接口
- [✅] 活动分享链接生成接口

#### 1.2 评论系统
- [✅] 设计 `activity_comment` 评论表（已建表，需补充 `activity_comment_hot` 热门评论逻辑）
- [✅] 评论 CRUD 接口（发表、删除、点赞）
- [✅] 热门评论排行接口
- [✅] 评论分页查询接口

#### 1.3 签到二维码
- [✅] 签到/签退通用二维码生成接口（含 UUID Token，30s 过期，重新生成即刷新）
- [✅] 扫码签到接口（校验 Token → 签到时间窗口 → 执行签到）
- [✅] 扫码签退接口（复用同一二维码：校验 Token → 活动需签退 → 签退时间窗口 → 执行签退）
- [✅] 二维码 Token Redis 缓存管理（用户维度 Key、30s 过期、成功后即作废）

> 设计约定：二维码**不绑定活动、一人一码**，内容为 `userId + token`（30s 过期，重新生成覆盖刷新，签到/签退成功后作废）。扫码者（该活动签到员）选择活动后提交，后端按**活动时间表的签到/签退时间窗口**校验当前是否可操作（状态机仅推进 0~4，5~8 由时间窗口实时判定）。
> 接口：生成 `GET /activityEnrollment/signIn/QRCode`；扫码签到 `POST /activityEnrollment/signIn/byQRCode`；扫码签退 `POST /activityEnrollment/signOut/byQRCode`。管理后台调试页见 `signin-qrcode.vue`。

#### 1.4 消息通知系统
- [ ] 利用已有 `notice_message` / `message_group` / `chat_message` 表设计消息接口，可以改造表设计或提出新表
- [ ] 活动状态变更通知（报名开始、即将开始、已结束）
- [ ] 审核结果通知（报名审核通过/驳回）
- [ ] 校内公告通知列表接口
- [ ] 未读消息计数接口

#### 1.5 学分认定模块
- [ ] 新建学分认定申请表（`credit_application`）
- [ ] 学分认定申请 CRUD 接口
- [ ] 申请审核流程（待审核 → 通过/驳回）
- [ ] 历史记录查询接口（按状态筛选）

---

### Phase 2：移动端前端 — 首页 & 活动（优先级：高）

> 目标：实现移动端首页和活动浏览核心功能

#### 2.1 项目脚手架
- [ ] 初始化移动端前端项目（Vue 3 + Vant 4 / uni-app）
- [ ] 配置路由、状态管理、请求封装
- [ ] 对接后端登录接口，完成 Token 管理

#### 2.2 首页
- [ ] 首页布局：扫码按钮、搜索框、消息按钮、功能入口网格
- [ ] 二课活动列表（本校 / 全局切换，分页加载）
- [ ] 活动卡片展示（标题、封面、报名时间、状态标签）
- [ ] 活动筛选功能（按分类、状态、时间筛选）
- [ ] 搜索功能（关键词搜索 + 历史记录）

#### 2.3 活动详情页
- [ ] 活动基本信息展示（封面、标题、分类、实践分、组织）
- [ ] 活动状态与阶段倒计时
- [ ] 报名人数 / 签到人数统计
- [ ] 报名时间段 / 活动时间段展示
- [ ] 活动成员列表（管理员、签到员）
- [ ] 活动附件查看（图片、文件）
- [ ] 活动简介富文本展示
- [ ] 报名按钮（含报名审核提示）
- [ ] 收藏 / 取消收藏
- [ ] 评论列表与发表评论
- [ ] 分享功能
- [ ] 联系活动发起人入口

---

### Phase 3：移动端前端 — 签到签退 & 报名（优先级：高）

> 目标：完成活动报名与签到签退的移动端闭环

#### 3.1 报名流程
- [ ] 活动报名接口对接（含学院/年级/部落范围校验提示）
- [ ] 报名状态展示（已报名 / 待审核 / 已通过）
- [ ] 取消报名功能

#### 3.2 扫码签到签退
- [ ] 扫码按钮 → 调用摄像头扫描二维码
- [ ] 签到员端：生成签到二维码（UID + 二维码，30s 自动刷新）
- [ ] 签到员端：选择签到/签退活动弹窗
- [ ] 学员端：扫码后自动完成签到/签退
- [ ] 签到/签退结果反馈提示

---

### Phase 4：移动端前端 — 我的 & 个人中心（优先级：中）

> 目标：实现「我的」页面及个人中心功能

#### 4.1 我的页面
- [ ] 待签到 / 待签退 / 待评价数量统计卡片
- [ ] 实践积分 & 诚信分展示
- [ ] 功能入口：我的活动、我的部落、我的申请、我的评价

#### 4.2 我的活动
- [ ] 活动列表（已报名 / 已签到 / 已完成 分类 Tab）
- [ ] 点击进入活动详情

#### 4.3 待办事项
- [ ] 待签到活动列表（一键签到入口）
- [ ] 待签退活动列表（一键签退入口）
- [ ] 待评价活动列表（评价表单）

#### 4.4 积分详情
- [ ] 实践积分明细列表
- [ ] 诚信分变动记录列表

#### 4.5 基本信息编辑
- [ ] 上传/更换头像
- [ ] 修改昵称

---

### Phase 5：移动端前端 — 部落系统（优先级：中）

> 目标：实现二课部落的浏览、搜索、加入功能

#### 5.1 部落列表
- [ ] 本校部落列表展示（图标、标题、热度）
- [ ] 搜索功能（关键词 + 历史记录）
- [ ] 筛选功能（按学院、热度排序）

#### 5.2 部落详情
- [ ] 基本信息（图标、标题、归属学院/组织、热度）
- [ ] 成员列表查看
- [ ] 部落公告展示
- [ ] 部落发起的活动列表
- [ ] 评分功能
- [ ] 收藏功能

#### 5.3 加入部落
- [ ] 申请加入表单（理由、兴趣特长、志愿选择、是否服从调剂）
- [ ] 申请状态查询

---

### Phase 6：移动端前端 — 消息 & 学分认定（优先级：中）

#### 6.1 消息中心
- [ ] 消息列表页面（活动通知、审核通知、校内公告）
- [ ] 未读消息标记与计数
- [ ] 校园生活关注消息

#### 6.2 学分认定
- [ ] 历史申请记录列表（待审核 / 已通过 / 已驳回 Tab）
- [ ] 申请详情页（标题、时间、学期、内容、审核人、状态）
- [ ] 申请表单（标题、学期、证明材料图片上传、审核人选择）
- [ ] 待审核状态可编辑/删除

---

### Phase 7：移动端前端 — 活动日历 & 其他（优先级：低）

#### 7.1 活动日历
- [ ] 日历视图展示当天活动
- [ ] 全部活动列表（按日期筛选）

#### 7.2 PU 签到二维码
- [ ] 显示 UID
- [ ] 展示个人签到二维码（30s 刷新）

---

### Phase 8：管理后台前端完善（优先级：低）

> 目标：Phase 0 补齐后端接口与修复页面 import 后，联调并完善管理后台页面

#### 8.1 待完善页面
- [ ] 活动签到管理员管理页面：前端页面已生成，等 Phase 0.2 后端接口补齐后联调
- [ ] 活动分类管理页面：同上
- [ ] 活动报名范围管理（学院/年级/部落）页面：前端页面已生成，等 Phase 0.2 / 0.3 接口补齐后联调
- [ ] 活动报名 / 审核日志 / 活动时间表页面：按 Phase 0.4 结论调整（前端对齐后端组合接口，或补后端独立接口）
- [ ] 学分认定审核管理页面（配合 Phase 1.5）

---

### Phase 9：活动付费参加功能（优先级：高）

> 目标：支持部分二课活动收取报名费，建立「创建订单 → 支付 → 报名生效 → 退款」完整链路；免费活动保持现有流程不变。
> 注意：9.1 与 9.4 会改动报名核心链路，建议在移动端报名页面（Phase 3）开发前完成设计定稿，避免返工。

#### 9.0 关键设计决策

| 决策项 | 方案 | 理由 |
|--------|------|------|
| 支付时机 | 下单即锁座，支付成功报名生效 | 避免超卖和「付了钱没名额」 |
| 需审核活动 | 支付成功 ≠ 报名成功，进入待审核；审核剔除时自动退款 | 与现有报名审核流程兼容 |
| 金额单位 | int 存「分」（`price_fen` / `amount_fen`） | 避免浮点精度问题 |
| 支付渠道 | 抽象 `PaymentChannel` 接口，Mock 渠道先行 | 本地联调方便；真实渠道（支付宝沙箱 / 微信支付 v3）后续按需接入，仅新增实现类 |
| Mock 支付 | 模拟「异步回调」链路：模拟收银台 + 模拟回调，不使用 `Thread.sleep` | sleep 阻塞请求线程与连接、与 15 分钟关单任务产生竞态，且模拟的是不存在的同步等待；回调链路才能验证 CAS 幂等 |
| 订单超时 | 15 分钟未支付自动关单并释放名额 | 防止名额被锁死 |
| 退款 | 仅全额退款；系统退款（审核未通过等）不受活动退款规则限制 | 校园场景足够 |
| 报名记录 | 支付成功后才写 `activity_enrollment` | 现有签到/审核代码零侵入 |

#### 9.1 数据库变更

**`activity` 表新增付费配置字段**

```sql
ALTER TABLE `activity`
  ADD COLUMN `paid_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否付费活动',
  ADD COLUMN `price_fen` int NULL COMMENT '报名费（分，免费活动为 NULL）',
  ADD COLUMN `refund_policy` tinyint NULL COMMENT '退款规则：1-报名截止前可退 2-活动开始前可退 3-不可退款';
```

**新表 `activity_order`（报名订单）**

```sql
CREATE TABLE `activity_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `user_id` bigint NOT NULL COMMENT '报名用户id',
  `amount_fen` int NOT NULL COMMENT '订单金额（分）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待支付 1-已支付 2-已关闭 3-退款中 4-已退款 5-退款失败',
  `pay_channel` tinyint NULL COMMENT '支付渠道：1-微信 2-支付宝 3-Mock',
  `channel_order_no` varchar(64) NULL COMMENT '渠道订单号',
  `pay_time` datetime NULL COMMENT '支付时间',
  `expire_time` datetime NOT NULL COMMENT '支付截止时间',
  `close_time` datetime NULL COMMENT '关闭时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no`(`order_no`),
  KEY `idx_activity_user`(`activity_id`, `user_id`),
  KEY `idx_status_expire`(`status`, `expire_time`)
) COMMENT = '活动报名订单';
```

**新表 `activity_refund`（退款记录）**

```sql
CREATE TABLE `activity_refund` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `refund_no` varchar(32) NOT NULL COMMENT '退款单号',
  `order_id` bigint NOT NULL COMMENT '订单id',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `amount_fen` int NOT NULL COMMENT '退款金额（分）',
  `reason_type` tinyint NOT NULL COMMENT '原因：1-用户申请 2-活动取消 3-报名审核未通过 4-其它',
  `reason` varchar(255) NULL COMMENT '备注',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-退款中 1-成功 2-失败',
  `channel_refund_no` varchar(64) NULL COMMENT '渠道退款单号',
  `refund_time` datetime NULL COMMENT '退款完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no`(`refund_no`),
  KEY `idx_order_id`(`order_id`)
) COMMENT = '活动报名退款记录';
```

- [ ] SQL 脚本放入 `sql_script/mysql/`，并同步维护到 `fun-campus-merged.sql`

#### 9.2 后端 — 订单与支付

- [ ] 新建 `activityOrder` 模块（controller / service / manager / dao / domain，结构参考 `activityEnrollment`）
- [ ] 创建订单 `POST /activityOrder/create`：校验付费活动 + 报名时间内 + 报名范围 + 无未关闭订单 → 事务内 `increaseEnrollNum` 锁座 + 写入待支付订单（`expire_time = now + 15min`）
- [ ] 订单查重：同一用户同一活动存在「待支付 / 已支付」订单时拒绝重复下单
- [ ] 订单号生成（时间戳 + 随机数）与订单详情、我的订单分页接口
- [ ] 取消订单 `POST /activityOrder/cancel`：仅待支付可取消，关单并 `decreaseEnrollNum` 释放名额
- [ ] 支付渠道抽象 `PaymentChannel`：`prepay()` 预下单、`refund()` 退款、`parsePayCallback()` / `parseRefundCallback()` 验签解析
- [ ] 支付回调 `POST /payment/callback/{channel}`：验签 → 校验金额 → CAS 更新订单（`WHERE status = 0` 幂等）→ 写入 `activity_enrollment` 报名记录 → 发送通知
- [ ] `MockPaymentChannel`：`prepay()` 返回「模拟收银台」链接；`parsePayCallback()` 直接解析模拟回调参数（不做验签）
- [ ] 模拟收银台页面（dev/test 专用）：`GET /payment/mock/cashier?orderNo=xxx`，后端简易 HTML 页，含「支付成功 / 支付失败」按钮，移动端页面未开发也能全链路联调
- [ ] 模拟支付回调：复用 `POST /payment/callback/{channel}` 入口（`channel=mock`），构造 `PayNotifyResult` 后收敛到 `handlePayNotify`；业务代码禁止 `Thread.sleep`
- [ ] 可选延时到账：`mock.pay.callback-delay-seconds` 配置 + `TaskScheduler` 延时投递回调，模拟异步通知的不确定性
- [ ] 获取支付参数接口（重新调起支付 / 展示收款二维码）
- [ ] `ActivityOrderTimeoutJob`：SmartJob 扫描超时未支付订单 → 批量关单 + 释放名额（参考 `ActivityStatusUpdateJob` 写法）

> 订单状态机：`待支付(0) → 超时/取消 → 已关闭(2)`；`待支付(0) → 支付成功 → 已支付(1) → 申请退款 → 退款中(3) → 已退款(4)`；`退款中(3) → 渠道失败 → 退款失败(5)`（支持重试）
>
> Mock 支付约定：模拟的是「渠道异步回调」链路（下单 → 模拟收银台付款 → 回调入账），而不是服务端同步等待支付结果；模拟回调与真实回调汇聚到同一个 `handlePayNotify` 入口（CAS 幂等 → 写报名记录），后续接入真实渠道仅新增 `PaymentChannel` 实现类，业务代码零改动；mock 渠道与模拟收银台通过 `pay.channel=mock`（`@ConditionalOnProperty`）或 `@Profile("dev")` 注册，生产环境不存在；真实渠道暂不接入（后续需要时可用支付宝沙箱免费联调，无需商户资质）。

#### 9.3 后端 — 退款

- [ ] 用户退款申请 `POST /activityOrder/refundApply`：按活动 `refund_policy` 校验（报名截止前 / 活动开始前），创建退款单并发起渠道退款
- [ ] 退款回调 `POST /payment/callback/{channel}/refund`：更新退款单与订单状态 → 逻辑删除报名记录 + `decreaseEnrollNum` + 通知
- [ ] 系统自动退款场景：
  - 报名审核剔除已支付用户（`reviewEnroll` 名单筛选移除时联动）
  - 活动取消（管理端操作对全部已支付订单批量退款）
- [ ] 退款失败重试（SmartJob 定时重试或管理端手动重试）

#### 9.4 与现有模块的整合点

- [ ] `enroll` 接口拦截付费活动（`paid_flag = 1` 时提示走支付流程）
- [ ] 抽取「写入报名记录」公共逻辑供支付回调复用（免费活动 enroll 保持原逻辑）
- [ ] `reviewEnroll` 名单变更联动退款（见 9.3）
- [ ] 活动详情接口补充付费信息：价格、退款规则、当前用户订单状态
- [ ] 消息通知：支付成功、退款到账、订单超时关闭（依赖 Phase 1.4，可先直接写 `notice_message`）

#### 9.5 管理后台前端

- [ ] `activity-form.vue` 增加「付费参加」开关、价格、退款规则配置
- [ ] 订单管理页：分页查询（活动 / 状态 / 用户 / 时间筛选）、订单详情、导出 Excel
- [ ] 退款管理页：退款单列表、失败重试
- [ ] 活动收入统计（按活动汇总报名费）

#### 9.6 移动端前端（依赖 Phase 2 脚手架）

- [ ] 报名按钮分流：免费「立即报名」/ 付费「立即支付」
- [ ] 支付确认页：活动信息、金额、支付方式选择、待支付倒计时
- [ ] 订单列表（待支付 / 已支付 / 退款 Tab）与订单详情
- [ ] 待支付订单取消入口、退款申请入口与规则说明

#### 9.7 安全与幂等要点

- [ ] 金额一律以服务端 `activity.price_fen` 为准，忽略前端传入金额
- [ ] 回调验签 + CAS 幂等更新，重复回调直接返回成功
- [ ] 订单归属校验：仅本人可查询 / 取消 / 申请退款
- [ ] 支付、退款关键操作留痕（日志 + 订单操作记录），便于对账排查

---

### Phase 10：质量保障 & 优化（优先级：低）

#### 10.1 后端
- [ ] 补全报名接口幂等性（代码中已有 todo 标记）
- [ ] 接口参数校验完善
- [ ] 关键业务单元测试
- [ ] 性能优化（活动列表查询、首页数据加载）
- [ ] 支付链路单元测试（订单状态机、回调幂等、超时关单）

#### 10.2 前端
- [ ] 移动端适配与兼容性测试
- [ ] 图片懒加载与缓存策略
- [ ] 接口请求错误处理与用户提示优化
- [ ] 管理后台交互体验优化

---

### 技术决策备忘

| 决策项 | 方案 | 备注 |
|--------|------|------|
| 移动端框架 | 待定（Vue3 + Vant 4 / uni-app） | 需考虑是否需要跨平台 |
| 消息推送 | WebSocket / 轮询 | 根据实时性需求决定 |
| 二维码方案 | 前端 Canvas 生成 + Redis Token | 30s 过期自动刷新 |
| 双端 URL 前缀 | 门户 `/portal/*`、管理后台 `/backend/*`，由 `AdminInterceptor` 按 URL 段分派用户体系 | 9 个已实现接口的历史模块未适配前缀（见 Phase 0.5），前端 API 封装需同步调整 |
| 文件存储 | 待定（OSS / 本地） | 头像、活动封面、证明材料 |
| 付费占座 | 下单锁座，15 分钟未支付自动关单释放 | 复用 `increaseEnrollNum` / `decreaseEnrollNum` |
| 支付渠道 | 抽象 `PaymentChannel` 接口，Mock 先行，真实渠道暂不接入 | 后续切换仅新增实现类（支付宝沙箱可免费联调，无需商户资质） |
| Mock 支付 | 模拟收银台 + 模拟回调，与真实回调同一入口 | 不使用 `Thread.sleep`；可确定性复现成功 / 失败 / 关单竞态 |
| 金额存储 | int 存「分」（`price_fen` / `amount_fen`） | 避免浮点精度误差 |

---

## 许可证

本项目采用 MIT License 开源协议，详情请查看 [LICENSE](LICENSE) 文件。

---