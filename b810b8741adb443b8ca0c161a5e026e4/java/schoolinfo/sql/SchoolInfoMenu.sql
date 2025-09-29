# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '学校信息表', 2, 0, '/school-info/list', '/business/school-info/school-info-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '学校信息表';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'schoolInfo:query', 'schoolInfo:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'schoolInfo:add', 'schoolInfo:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'schoolInfo:update', 'schoolInfo:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'schoolInfo:delete', 'schoolInfo:delete', @parent_id, 1 );
