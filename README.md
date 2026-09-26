# Fun Campus

<p align="center">
  <img src="https://img.shields.io/badge/SmartAdmin-Framework-blue" alt="SmartAdmin Framework">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen" alt="Spring Boot 3.5.4">
  <img src="https://img.shields.io/badge/Vue-3.4.27-brightgreen" alt="Vue 3.4.27">
</p>


**重要声明：本软件为独立开发，与 PU口袋校园 无任何关联。所有代码、设计均为原创，仅借鉴了校园管理系统的一般功能概念。**


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
| 后端 | 活动核心链路（发布/审核/报名/签到签退/详情/倒计时/范围控制/状态自动推进） | ✅ 已完成（portal/backend 双前缀） |
| 后端 | 基础数据（学校/学院/组织/前台用户/部落用户） | ✅ 已完成（CRUD 全量 + 双前缀适配） |
| 后端 | 互动功能（评论 + 热门排行 / 收藏 / 分享 / 评价） | ✅ 已完成 |
| 后端 | 消息通知（门户+后台收发、未读计数、业务节点自动站内信） | ✅ 已完成 |
| 后端 | 学分认定（门户申请/编辑/删除 + 管理端审核） | ✅ 已完成 |
| 后端 | 部落（列表/详情/成员/活动/加入申请+审核） | ✅ 已完成 |
| 后端 | 活动付费链路（下单锁座 / 模拟支付回调 / 退款 / 超时关单） | ✅ 已完成（Mock 渠道） |
| 后端 | AI 校园助手（SSE 流式问答 / 找活动 / RAG / 降级） | ✅ 已完成（学生端 App 待集成） |
| 管理后台前端 | 37 个业务页面（活动/基础数据/报名/审核/部落等） | 🟡 import 与菜单已修复，多数页面后端就绪可联调；活动表单保存、组织干事/组织账号运营者等少数未接通 |
| 学生端前端 | fun-campus-app（uni-app，登录/首页/详情/支付/收银台/签到码/消息/我的） | 🟡 核心页面已实现；部落为占位页，订单列表/我的活动等入口显示"开发中" |
| 质量保障 | 单元测试 | 🟡 13 个测试类（活动/报名/审核/状态推进/登录/消息） |

---

### Phase 0：管理后台「前端已有、后端缺失」接口对齐（优先级：高）

> 背景：管理后台前端已生成 37 个业务页面（含 API 封装与菜单 SQL），但排查代码后发现：部分模块后端完全缺失，部分 Controller 为空壳（仅有 `@Resource` 注入、无任何接口方法），部分接口路径与前端约定不一致。本阶段目标是把「前端已画好、后端接不上」的缺口逐个补齐。

#### 0.1 后端模块完全缺失（前端页面 + API + 菜单均已存在）

- [ ] 组织干事用户（organizer-cadre）：仍未补齐 —— 前端有 `organizer-cadre-list/form.vue`、`organizer-cadre-api.js`（调用无前缀的 `/organizationCadre/*`）、菜单已入库；后端无任何模块、数据库无 `organizer_cadre` 表 —— 需建表 + 补 entity/service/controller，或确认该功能废弃并清理前端
- [ ] 组织账号运营者（portal-organizer-user）：仍未补齐 —— 前端有 `portal-organization-user-list/form.vue`、`portal-organization-user-api.js`（调用无前缀的 `/portalOrganizationUser/*`）；`portal_organization_user` 表已存在；后端无对应 service/controller

#### 0.2 后端 Controller 为空壳（需补 CRUD 接口）—— ✅ 已全部补齐

> 以下模块已有 domain / dao / manager / service 骨架，但 Controller 里没有暴露任何接口。

- [✅] 活动分类管理：`ActivityCategoryController` 已补齐 CRUD（`/backend/activityCategory/{queryPage,add,update,delete/{id},batchDelete}`）
- [✅] 年级信息管理：`GradeInfoController` 已补齐 CRUD（`/backend/gradeInfo/*`，前端已同步加前缀）
- [✅] 活动签到管理员管理：`ActivitySigninManagerController` 已补齐 CRUD（`/backend/activitySigninManager/*`）
- [✅] 活动可报名部落管理：`ActivityCanEnrollTribeController` 已补齐 CRUD（`/backend/activityCanEnrollTribe/*`）

#### 0.3 Service 已实现、仅需暴露 Controller —— ✅ 已完成

- [✅] 部落管理：已拆分为 `TribeAdminController`（`/backend/tribe/*` 管理端 CRUD）+ `TribeController`（`/portal/tribe/*` 门户：queryPage/detail/member/activity/simple）+ `TribeApplicationController`（加入申请），前端 `tribe-api.js` 已同步

#### 0.4 接口路径 / 方法不一致（需前后端对齐）

- [✅] 活动管理：前端 `activity-api.js` 已对齐 `/backend/activity/{query,delete,batchDelete}`
- [ ] 活动时间表：前端 `activity-schedule-api.js` 仍调用无前缀的 `/activitySchedule/*`，后端仍无该独立模块（时间表随 `/portal/activity/*` 组合接口维护）—— 需确认独立页面去留
- [✅] 活动报名管理：新增 `ActivityEnrollmentAdminController`，已提供 `/backend/activityEnrollment/{queryPage,delete,batchDelete}`，前端已同步
- [✅] 活动审核日志：`/backend/activityReviewLog/{queryPage,add,update,review/initial,latest/{activityId}}` 已就绪
- [ ] `activityWithSchedule.getReviewProposal` 方法仍未挂 mapping 注解（未完成）；`/portal/activity/submit` 已可正常提交（含 Validator + `submitDraft` 落库）

#### 0.5 URL 前缀适配（portal / backend 双端体系）—— ✅ 已全量适配

- [✅] 原 9 个未适配模块已全部改造：`activityEnrollment`（门户 + `/backend` 管理端双 Controller）、`activityCanEnrollCollege`、`activityCanEnrollGrade`、`activityReviewLog`、`collegeInfo`、`organizationInfo`、`portalUser`（`PortalUserController=/backend`、`PortalUserSelfController=/portal`）、`schoolInfo`、`tribeUser` 均带 `backend`/`portal` 段
- [✅] 原空壳模块（见 0.2）补 CRUD 时已按 `/backend/*` 前缀实现
- [✅] 前端 API 封装已同步：funcampus 全部 api 文件走 `/backend/*` 或 `/portal/*`（仅 0.1 的两个无后端模块与活动时间表 3 个历史 API 例外）

#### 0.6 前端页面修复（技术债）—— ✅ 已全部修复

- [✅] 37/37 页面 API import 已统一为 `/@/api/business/funcampus/...`（全量扫描无旧路径残留）
- [✅] 菜单 SQL 组件路径已修正为 `/business/funcampus/{module}/...`，并统一合并进 `sql_script/mysql/fun-campus-merged.sql`（原分散的 `*Menu.sql` 已删除）
- [✅] `activity-enrollment-list.vue`（278 行）/ `activity-enrollment-form.vue`（125 行）已重写为单模板单脚本

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

#### 1.4 消息通知系统 —— ✅ 已完成（活动状态通知节点在途）
- [✅] 复用既有消息表重构消息服务：`MessageService.sendTemplateMessage`（模板化 + 群发，`MessageTemplateEnum` 多模板）；门户接口：`POST /portal/message/queryMyMessage`、`GET /portal/message/getUnreadCount`、`GET /portal/message/read/{id}`
- [ ] 活动状态变更通知（报名开始、即将开始、已结束）—— 活动状态 Job 尚未接入站内信
- [✅] 审核结果通知（报名审核通过/驳回 → `ActivityReviewLogService` 已发站内信）
- [ ] 校内公告通知列表接口（门户侧；后管侧 `t_notice` 体系已有）
- [✅] 未读消息计数接口（`GET /portal/message/getUnreadCount`）
- [✅] 额外落地：报名结果、支付成功、退款到账、订单超时关闭 均自动发送站内信（`ActivityEnrollmentService` / `PaymentService` / `ActivityOrderTimeoutJob`）

#### 1.5 学分认定模块 —— ✅ 已完成
- [✅] 学分认定申请表 `credit_application`（已并入 `fun-campus-merged.sql`）
- [✅] 门户 CRUD 接口（apply/update/delete/detail/myList/reviewer/query，待审核可编辑可删）
- [✅] 审核流程（状态机 0 待审 → 1 通过 / 2 驳回；管理端 `/backend/creditApplication/{queryPage,review}`）
- [✅] 历史记录查询（按状态筛选 + 详情/审核人信息）

---

### Phase 2：移动端前端 — 首页 & 活动（优先级：高）

> 目标：实现移动端首页和活动浏览核心功能

#### 2.1 项目脚手架 —— ✅ 已完成
- [✅] uni-app（Vue3 + Vite）项目 `fun-campus-app`，UI 用 uview-plus，状态用 pinia
- [✅] pages.json 路由（4 tab：首页/部落/消息/我的，外加登录/详情/支付/收银台/签到码）、user store、`uni.request` promise 封装（对齐 axios 手感）
- [✅] 对接门户登录（验证码 → 登录 → token 存储 → 失效自动踢回登录页）

#### 2.2 首页 —— ✅ 核心已完成
- [✅] 首页布局：本校/全局切换、扫码入口、消息入口（未读角标）
- [✅] 二课活动列表（本校 / 全局切换，分页 + 下拉刷新）
- [✅] 活动卡片展示（标题、封面、时间、状态标签、价格）
- [ ] 活动筛选功能（按分类、状态、时间筛选）—— 未做
- [ ] 搜索功能（关键词搜索 + 历史记录）—— 未做（后端暂无关键词参数）

#### 2.3 活动详情页 —— ✅ 核心已完成
- [✅] 活动基本信息展示（封面、标题、分类、实践分、报名范围提示、需审核标签）
- [ ] 活动状态与阶段倒计时 —— 未做（后端 `remainingSeconds` 接口已就绪）
- [✅] 报名人数 / 签到人数统计
- [✅] 报名时间段 / 活动时间段展示
- [✅] 活动成员列表（报名同学头像墙，前 12 位 + 总数）
- [✅] 活动附件查看（复制链接）
- [✅] 活动简介展示（纯文本）
- [✅] 报名按钮（含报名审核提示、付费分流、取消报名）
- [✅] 收藏 / 取消收藏
- [✅] 评论列表与发表评论（回复、点赞、撤销点赞）
- [✅] 分享功能（生成分享链接 + 分享码解析进入）
- [ ] 联系活动发起人入口 —— 未做

---

### Phase 3：移动端前端 — 签到签退 & 报名（优先级：高）

> 目标：完成活动报名与签到签退的移动端闭环

#### 3.1 报名流程 —— ✅ 已完成
- [✅] 活动报名接口对接（详情接口带学院/年级/部落范围校验结果，前端展示提示文案）
- [✅] 报名状态展示（未报名 / 已报名 / 已签到 / 待审核，基于详情接口 `currentUserEnrollment`）
- [✅] 取消报名（免费活动前端直调；付费活动后端拦截并引导走退款）

#### 3.2 扫码签到签退 —— ✅ 已完成（学生亮码被扫模式）
- [✅] 扫码按钮：App/小程序走 `uni.scanCode`、H5 降级粘贴分享链接（一期用于分享码 → 活动详情）
- [✅] 签到员端：管理后台 `signin-qrcode.vue` 调试页（UID + 二维码，30s 自动刷新，扫码后选活动提交签到/签退）
- [✅] 学员端：我的签到码页（个人二维码 30s 刷新，签到签退通用码）
- [✅] 签到/签退结果反馈提示

> 流程约定：学生亮码 → 签到员扫码选择活动提交 → 后端按时间窗口校验执行（非"学员扫码"模式）。

---

### Phase 4：移动端前端 — 我的 & 个人中心（优先级：中）

> 目标：实现「我的」页面及个人中心功能

#### 4.1 我的页面 —— 🟡 部分完成
- [ ] 待签到 / 待签退 / 待评价数量统计卡片 —— 未做
- [✅] 实践学分 & 信誉分展示（`/portal/portalUser/current`）
- [🟡] 功能入口：我的签到码已通；我的活动 / 我的订单 / 资料编辑为占位入口（"开发中"）

#### 4.2 我的活动 —— 待开发（后端已就绪）
- [ ] 活动列表（已报名 / 已签到 / 已完成 分类 Tab）—— 后端 `POST /portal/activityEnrollment/queryMy` 已就绪
- [ ] 点击进入活动详情

#### 4.3 待办事项 —— 待开发（后端数据已具备）
- [ ] 待签到活动列表（一键签到入口）
- [ ] 待签退活动列表（一键签退入口）
- [ ] 待评价活动列表（后端 pending/list、pending/count 接口已就绪）

#### 4.4 积分详情 —— 未开始
- [ ] 实践积分明细列表
- [ ] 信誉分变动记录列表

#### 4.5 基本信息编辑 —— 待开发（后端已就绪）
- [ ] 上传/更换头像（`POST /portal/portalUser/updateProfile` + 文件上传已就绪）
- [ ] 修改昵称

---

### Phase 5：移动端前端 — 部落系统（优先级：中）

> 目标：实现二课部落的浏览、搜索、加入功能

#### 5.1 部落列表 —— 待开发（后端已就绪）
- [ ] 本校部落列表展示（图标、标题、热度）
- [ ] 搜索功能（关键词 + 历史记录）
- [ ] 筛选功能（按学院、热度排序）

#### 5.2 部落详情 —— 待开发（后端已就绪）
- [ ] 基本信息（图标、标题、归属学院/组织、热度）
- [ ] 成员列表查看
- [ ] 部落公告展示
- [ ] 部落发起的活动列表
- [ ] 评分功能 / 收藏功能 —— 二期

#### 5.3 加入部落 —— 后端一期已就绪（简化版：理由 + 审核）
- [ ] 申请加入表单（一期：加入理由；`POST /portal/tribe/application/apply`）
- [ ] 申请状态查询（`GET /portal/tribe/application/myList`）

> 说明：`fun-campus-app` 部落页当前为占位页，门户接口（queryPage/detail/member/activity/application）均已就绪即可开发。

---

### Phase 6：移动端前端 — 消息 & 学分认定（优先级：中）

#### 6.1 消息中心 —— ✅ 已完成
- [✅] 消息列表页面（活动通知、审核通知、支付/退款通知；分页 + 下拉刷新）
- [✅] 未读消息标记与计数（tabBar 角标 + 已读标记）
- [ ] 校园生活关注消息 —— 未做（对应功能未规划）

#### 6.2 学分认定 —— 待开发（后端已就绪）
- [ ] 历史申请记录列表（待审核 / 已通过 / 已驳回 Tab；`GET /portal/creditApplication/myList` 已就绪）
- [ ] 申请详情页（标题、时间、学期、内容、审核人、状态）
- [ ] 申请表单（标题、学期、证明材料图片上传、审核人选择）
- [ ] 待审核状态可编辑/删除（后端已支持）

---

### Phase 7：移动端前端 — 活动日历 & 其他（优先级：低）

#### 7.1 活动日历 —— 待开发（后端已就绪）
- [ ] 日历视图展示当天活动（`GET /portal/activity/calendar` 已就绪，区间 ≤62 天）
- [ ] 全部活动列表（按日期筛选）

#### 7.2 PU 签到二维码 —— ✅ 已完成
- [✅] 显示 UID / 用户信息
- [✅] 展示个人签到二维码（30s 刷新，签到签退通用码）

---

### Phase 8：管理后台前端完善（优先级：低）

> 目标：Phase 0 补齐后端接口与修复页面 import 后，联调并完善管理后台页面

#### 8.1 状态与待办
- [✅] 活动分类 / 年级信息 / 签到管理员 / 报名范围（学院、年级、部落）页面：后端 CRUD 已就绪（Phase 0.2），可直接联调
- [✅] 活动报名关系 / 审核日志页面：后端管理接口已就绪（Phase 0.4）
- [ ] 活动新增/编辑表单未接通：`activity-list.vue` 无新增/编辑入口，`activity-api.js` 缺 `add/update` 方法（后端提交/草稿接口已就绪）
- [ ] 活动时间表页面：待 Phase 0.4「活动时间表」结论（独立模块去留）
- [ ] 学分认定审核管理页面：后端已就绪（`/backend/creditApplication/{queryPage,review}`），前端页面待建
- [ ] 付费配置与订单/退款管理页面：见 Phase 9.5
- [ ] 组织干事用户 / 组织账号运营者页面：后端缺失（见 Phase 0.1）

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

- [✅] SQL 脚本统一维护在 `sql_script/mysql/fun-campus-merged.sql`（全量合并版，建库初始化唯一入口；活动/订单/退款/评价/学分认定/AI 等新表均已并入）

#### 9.2 后端 — 订单与支付 —— ✅ 已完成

- [✅] 新建 `activityOrder` 模块（controller / service / manager / dao / domain，结构参考 `activityEnrollment`）
- [✅] 创建订单 `POST /activityOrder/create`：校验付费活动 + 报名时间内 + 报名范围 + 无未关闭订单 → 事务内 `increaseEnrollNum` 锁座 + 写入待支付订单（`expire_time = now + 15min`）
- [✅] 订单查重：同一用户同一活动存在「待支付 / 已支付」订单时拒绝重复下单
- [✅] 订单号生成（`OrderNoUtil`）与订单详情、我的订单分页接口（`detail` / `query`）
- [✅] 取消订单 `POST /activityOrder/cancel`：仅待支付可取消，关单并 `decreaseEnrollNum` 释放名额
- [✅] 支付渠道抽象 `PaymentChannel`：`prepay()` 预下单、`refund()` 退款、`parsePayCallback()` / `parseRefundCallback()` 验签解析
- [✅] 支付回调 `POST /payment/callback/{channel}`：验签 → 校验金额 → CAS 更新订单（`WHERE status = 0` 幂等）→ 写入 `activity_enrollment` 报名记录 → 发送通知
- [✅] `MockPaymentChannel`：`prepay()` 返回「模拟收银台」链接；`parsePayCallback()` 直接解析模拟回调参数（不做验签）
- [✅] 模拟收银台页面（dev/test 专用）：`GET /payment/mock/cashier?orderNo=xxx`，后端简易 HTML 页，含「支付成功 / 支付失败」按钮，移动端页面未开发也能全链路联调
- [✅] 模拟支付回调：复用 `POST /payment/callback/{channel}` 入口（`channel=mock`），构造 `PayNotifyResult` 后收敛到 `handlePayNotify`；业务代码禁止 `Thread.sleep`
- [✅] 可选延时到账：`MockPayCallbackConfig` 配置 + `TaskScheduler` 延时投递回调，模拟异步通知的不确定性
- [✅] 获取支付参数接口（`prepay`，重新调起支付）
- [✅] `ActivityOrderTimeoutJob`：SmartJob 扫描超时未支付订单 → 批量关单 + 释放名额（参考 `ActivityStatusUpdateJob` 写法）

> 实际访问路径带前缀：门户为 `/portal/activityOrder/*`、`/portal/payment/*`（如 `/portal/payment/mock/cashier`）。

> 订单状态机：`待支付(0) → 超时/取消 → 已关闭(2)`；`待支付(0) → 支付成功 → 已支付(1) → 申请退款 → 退款中(3) → 已退款(4)`；`退款中(3) → 渠道失败 → 退款失败(5)`（支持重试）
>
> Mock 支付约定：模拟的是「渠道异步回调」链路（下单 → 模拟收银台付款 → 回调入账），而不是服务端同步等待支付结果；模拟回调与真实回调汇聚到同一个 `handlePayNotify` 入口（CAS 幂等 → 写报名记录），后续接入真实渠道仅新增 `PaymentChannel` 实现类，业务代码零改动；mock 渠道与模拟收银台通过 `pay.channel=mock`（`@ConditionalOnProperty`）或 `@Profile("dev")` 注册，生产环境不存在；真实渠道暂不接入（后续需要时可用支付宝沙箱免费联调，无需商户资质）。

#### 9.3 后端 — 退款 —— ✅ 主体已完成

- [✅] 用户退款申请 `POST /activityOrder/refundApply`：按活动 `refund_policy` 校验（报名截止前 / 活动开始前），创建退款单并发起渠道退款
- [✅] 退款回调 `POST /payment/callback/{channel}/refund`：更新退款单与订单状态 → 逻辑删除报名记录 + `decreaseEnrollNum` + 通知
- [✅] 系统自动退款场景（部分）：
  - [✅] 报名审核剔除已支付用户（`ActivityReviewLogService.reviewEnroll` → `ActivityRefundService` 系统退款，reasonType=审核未通过，不受退款政策限制）
  - [ ] 活动取消（管理端对全部已支付订单批量退款）—— 未实现
- [ ] 退款失败重试（SmartJob 定时重试或管理端手动重试）—— 未实现

#### 9.4 与现有模块的整合点 —— ✅ 已完成

- [✅] `enroll` 接口拦截付费活动（提示走支付流程，取消报名亦引导走退款）
- [✅] 抽取「写入报名记录」公共逻辑供支付回调复用（免费活动 enroll 保持原逻辑）
- [✅] `reviewEnroll` 名单变更联动退款（见 9.3）
- [✅] 活动详情接口补充付费信息：价格、退款规则、当前用户订单状态（`currentUserOrder`）
- [✅] 消息通知：支付成功、退款到账、订单超时关闭（已接入消息模板站内信）

#### 9.5 管理后台前端 —— 未开始（活动表单尚未接通保存）

- [ ] `activity-form.vue` 增加「付费参加」开关、价格、退款规则配置
- [ ] 订单管理页：分页查询（活动 / 状态 / 用户 / 时间筛选）、订单详情、导出 Excel
- [ ] 退款管理页：退款单列表、失败重试
- [ ] 活动收入统计（按活动汇总报名费）

#### 9.6 移动端前端 —— ✅ 核心已完成

- [✅] 报名按钮分流：免费「立即报名」/ 付费「立即支付」（详情页 `onMainAction` 分流）
- [✅] 支付确认页：金额、待支付状态、打开模拟收银台、2.5s×24 次轮询回查（60s 上限）、取消订单
- [ ] 订单列表（待支付 / 已支付 / 退款 Tab）与订单详情 —— 后端 `query` 已就绪，App 入口暂为"开发中"占位
- [ ] 退款申请入口与规则说明 —— 后端 `refundApply` 已就绪，App API 未封装

#### 9.7 安全与幂等要点

- [✅] 金额一律以服务端 `activity.price_fen` 为准，忽略前端传入金额（下单金额由后端计算）
- [✅] 回调 + CAS 幂等更新，重复回调直接返回成功
- [✅] 订单归属校验：仅本人可查询 / 取消 / 申请退款
- [ ] 支付、退款关键操作留痕（日志已有；订单操作记录表未建）

---

### Phase 10：质量保障 & 优化（优先级：低）

#### 10.1 后端
- [ ] 补全报名接口幂等性（`ActivityEnrollmentService` 仍留 `//todo做幂等` 标记）
- [✅] 接口参数校验完善（各业务模块已具备 Form 校验 + Validator，如 `ActivityScheduleValidator`、`ActivityReviewLogValidator`、`PortalUserValidator`）
- [✅] 关键业务单元测试（13 个测试类：评论/报名/审核日志/状态推进 Job/登录/消息等）
- [ ] 性能优化（活动列表查询、首页数据加载）
- [ ] 支付链路单元测试（订单状态机、回调幂等、超时关单）

#### 10.2 前端
- [ ] 移动端适配与兼容性测试
- [ ] 图片懒加载与缓存策略
- [ ] 接口请求错误处理与用户提示优化
- [ ] 管理后台交互体验优化

---

### AI 校园助手（已完成）

> 面向门户学生的智能问答与找活动助手。独立支撑模块 `fun-campus-support-ai`（Spring AI 1.1.8），业务侧通过端口反转接入（`ActivityQueryPort` ← `funcampus/ai/ActivityQueryPortImpl`），AI 模块不反向依赖业务模块。

- 门户接口：`POST /portal/ai/chat`（SSE 流式：meta → delta → done）、`GET /portal/ai/conversation/list`、`GET /portal/ai/message/list`、`POST /portal/ai/conversation/delete`
- 能力：
  - 流式问答：DeepSeek `deepseek-chat`（OpenAI 兼容协议），单轮携带最多 10 条历史
  - 找活动工具调用：`ActivityAgentTools` 按关键词/分类/时间检索活动并组织回答
  - RAG 知识库：`ai_knowledge` 表 + 阿里百炼 `text-embedding-v3`（topK 3 / 相似度阈值 0.5 可配）
  - 会话落库：`ai_conversation` / `ai_message`
  - 降级设计：未配置 API Key 或开关关闭时返回降级提示流，不影响启动与其它功能
  - 频控：每用户每日 100 次提问（Redis 计数）
- 配置：`fun-campus.ai.*`（enabled / maxHistory / chat / embedding / rag / rateLimit）；API Key 经环境变量注入：`FUN_CAMPUS_AI_CHAT_API_KEY`（DeepSeek）、`FUN_CAMPUS_AI_EMBEDDING_API_KEY`（百炼）
- 调试页：`http://localhost:1024/ai-chat-test.html`（后管静态页，可直接联调流式对话）
- [ ] 学生端 App 集成（聊天页面与入口）—— 待开发

---

### 技术决策备忘

| 决策项 | 方案 | 备注 |
|--------|------|------|
| 移动端框架 | uni-app（Vue3 + Vite）+ uview-plus + pinia | ✅ 已落地：`fun-campus-app`（H5/App/小程序多端） |
| 消息推送 | 站内消息（复用 smart-admin 消息表）+ 未读角标轮询 | WebSocket 未引入 |
| 二维码方案 | 后端 ZXing 生成 PNG（base64）+ Redis Token | 30s 过期自动刷新；一人一码、不绑活动 |
| 双端 URL 前缀 | 门户 `/portal/*`、管理后台 `/backend/*`，由 `AdminInterceptor` 按 URL 段分派用户体系 | ✅ 已全量适配（Phase 0.5） |
| 文件存储 | MinIO（本地 9005 端口，`funcampus` 桶） | 头像、活动封面、证明材料 |
| 付费占座 | ✅ 已实现：下单锁座，15 分钟未支付自动关单释放 | 复用 `increaseEnrollNum` / `decreaseEnrollNum` |
| 支付渠道 | `PaymentChannel` 抽象接口，Mock 渠道已实现，真实渠道暂不接入 | 后续切换仅新增实现类（支付宝沙箱可免费联调，无需商户资质） |
| Mock 支付 | ✅ 已实现：模拟收银台 + 模拟回调，与真实回调同一入口 | 不使用 `Thread.sleep`；支持可选延时回调配置 |
| 金额存储 | int 存「分」（`price_fen` / `amount_fen`） | 避免浮点精度误差 |
| AI 助手 | Spring AI 1.1.8 + DeepSeek（对话）+ 阿里百炼（向量），RAG + 工具调用 + 无 Key 自动降级 | API Key 环境变量注入；频控 100 次/日/人 |

---

## 许可证

本项目采用 MIT License 开源协议，详情请查看 [LICENSE](LICENSE) 文件。

---