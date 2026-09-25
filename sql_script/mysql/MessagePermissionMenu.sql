-- ----------------------------
-- 消息管理：补充按钮权限点并授权
-- 背景：菜单“消息管理”(menu_id=300) 已存在，但 system:message:send/query/delete
--       三个权限点从未注册，导致 @SaCheckPermission 校验必然 403。
-- 说明：接口权限来自 t_menu.api_perms（perms_type=1），并需通过 t_role_menu 授予角色。
-- 本脚本可重复执行（带 NOT EXISTS 保护）。
-- ----------------------------

-- 定位“消息管理”父菜单
SET @message_menu_id = NULL;
SELECT menu_id INTO @message_menu_id FROM t_menu WHERE menu_name = '消息管理' AND menu_type = 2 LIMIT 1;

-- 发送消息 system:message:send
INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
SELECT '发送消息', 3, @message_menu_id, false, false, true, false, 1, 'system:message:send', 'system:message:send', @message_menu_id, 1
FROM DUAL
WHERE @message_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM (SELECT menu_id FROM t_menu WHERE api_perms = 'system:message:send' AND menu_type = 3) t);

-- 查询消息 system:message:query
INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
SELECT '查询消息', 3, @message_menu_id, false, false, true, false, 1, 'system:message:query', 'system:message:query', @message_menu_id, 1
FROM DUAL
WHERE @message_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM (SELECT menu_id FROM t_menu WHERE api_perms = 'system:message:query' AND menu_type = 3) t);

-- 删除消息 system:message:delete
INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
SELECT '删除消息', 3, @message_menu_id, false, false, true, false, 1, 'system:message:delete', 'system:message:delete', @message_menu_id, 1
FROM DUAL
WHERE @message_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM (SELECT menu_id FROM t_menu WHERE api_perms = 'system:message:delete' AND menu_type = 3) t);

-- 授权给角色1（超级管理员），已授权则跳过
INSERT INTO t_role_menu ( role_id, menu_id, create_time, update_time )
SELECT 1, m.menu_id, NOW(), NOW()
FROM t_menu m
WHERE m.menu_type = 3
  AND m.api_perms IN ('system:message:send', 'system:message:query', 'system:message:delete')
  AND NOT EXISTS (
      SELECT 1 FROM (SELECT role_id, menu_id FROM t_role_menu) rm
      WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id
  );
