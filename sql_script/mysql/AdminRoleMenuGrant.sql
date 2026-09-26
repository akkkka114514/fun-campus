-- ============================================================================
-- 管理员角色（role_id=1）菜单授权补全 + 未实现菜单隐藏
-- ----------------------------------------------------------------------------
-- 背景：
--   1. admin 角色原仅关联 110 个菜单，校园活动（301）下除「活动管理」(307) 外
--      均未授权，后台接口 @SaCheckPermission 一律返回 30005 无权限。
--   2. 以下 4 个菜单无对应后端模块（无 Java 模块、无数据库表），需隐藏：
--      309 组织账号运营者 / 359 学院的审核员 / 374 组织的审核员 / 379 组织干事用户
-- 执行方式：可直接在 fc_portal 库执行，可重复执行（幂等）。
-- ============================================================================

-- 1) 隐藏无后端模块的菜单
UPDATE t_menu SET visible_flag = 0 WHERE menu_id IN (309, 359, 374, 379);

-- 2) 补全 admin 角色菜单授权（排除上述 4 个菜单及其功能点）
INSERT INTO t_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM t_menu m
LEFT JOIN t_role_menu rm ON rm.menu_id = m.menu_id AND rm.role_id = 1
WHERE rm.role_menu_id IS NULL
  AND m.deleted_flag = 0
  AND m.menu_id NOT IN (309, 359, 374, 379)
  AND (m.parent_id IS NULL OR m.parent_id NOT IN (309, 359, 374, 379));
