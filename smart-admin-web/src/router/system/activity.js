/*
 * 活动管理路由
 *
 * @Author:    akkkka114514
 * @Date:      2025-09-05 19:25:03
 * @Copyright  akkkka114514
 */
import SmartLayout from '/@/layout/index.vue';

export const activityRouters = [
  {
    path: '/activity',
    name: 'Activity',
    component: SmartLayout,
    redirect: '/activity/list',
    meta: {
      title: '活动管理',
      icon: 'AppstoreOutlined',
    },
    children: [
      {
        path: '/activity/list',
        name: 'ActivityList',
        component: () => import('/@/views/business/funcampus/activity/activity-list.vue'),
        meta: {
          title: '活动列表',
          icon: 'AppstoreOutlined',
        },
      },
    ],
  },
];