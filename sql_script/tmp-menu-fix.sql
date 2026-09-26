-- 菜单修复：component 路径对齐前端实际文件、挂载到「校园活动」(301) 目录、补齐排序、隐藏无页面菜单
UPDATE t_menu SET component='/business/funcampus/activity/activity-list.vue', sort=10 WHERE menu_id=307;
UPDATE t_menu SET component='/business/funcampus/activity-category/activity-category-list.vue', sort=20 WHERE menu_id=334;
UPDATE t_menu SET component='/business/funcampus/activity-enrollment/activity-enrollment-list.vue', sort=30 WHERE menu_id=339;
UPDATE t_menu SET component='/business/funcampus/activity-review-log/activity-review-log-list.vue', sort=40 WHERE menu_id=344;
UPDATE t_menu SET component='/business/funcampus/activity-signin-manager/activity-signin-manager-list.vue', sort=50 WHERE menu_id=349;
UPDATE t_menu SET component='/business/funcampus/activity-can-enroll-college/activity-can-enroll-college-list.vue', sort=60 WHERE menu_id=319;
UPDATE t_menu SET component='/business/funcampus/activity-can-enroll-grade/activity-can-enroll-grade-list.vue', sort=70 WHERE menu_id=324;
UPDATE t_menu SET component='/business/funcampus/activity-can-enroll-tribe/activity-can-enroll-tribe-list.vue', sort=80 WHERE menu_id=329;
UPDATE t_menu SET component='/business/funcampus/college-info/college-info-list.vue', sort=90 WHERE menu_id=354;
UPDATE t_menu SET component='/business/funcampus/grade-info/grade-info-list.vue', sort=100 WHERE menu_id=364;
UPDATE t_menu SET component='/business/funcampus/schoolInfo/school-info-list.vue', sort=110 WHERE menu_id=314;
UPDATE t_menu SET component='/business/funcampus/organization-info/organization-info-list.vue', sort=120 WHERE menu_id=369;
UPDATE t_menu SET component='/business/funcampus/tribe/tribe-list.vue', sort=130 WHERE menu_id=389;
UPDATE t_menu SET component='/business/funcampus/tribe-user/tribe-user-list.vue', sort=140 WHERE menu_id=394;
UPDATE t_menu SET component='/business/funcampus/organizer-cadre/organizer-cadre-list.vue', sort=150 WHERE menu_id=379;
UPDATE t_menu SET component='/business/funcampus/portal-user/portal-user-list.vue', sort=160 WHERE menu_id=384;
UPDATE t_menu SET component='/business/funcampus/portalOrganizerUser/portal-organization-user-list.vue', sort=170 WHERE menu_id=309;
UPDATE t_menu SET parent_id=301 WHERE menu_id IN (309,314,319,324,329,334,339,344,349,354,364,369,379,384,389,394,359,374);
UPDATE t_menu SET visible_flag=0 WHERE menu_id IN (359,374);
-- 校验
SELECT menu_id, parent_id, menu_name, sort, visible_flag, component FROM t_menu WHERE parent_id=301 OR menu_id=301 ORDER BY sort;
