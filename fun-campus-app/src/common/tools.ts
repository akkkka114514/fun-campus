import * as dayjs from "@/tmui/tool/dayjs/esm/index"
import { computed } from "vue";
//导航
export const openLink = (url: string, type?: number) => {
  url = url.startsWith('/') ? url : `/${url}`;
  if (type) {
    uni.redirectTo({
      url,
    });
  } else {
    uni.navigateTo({
      url,
    });
  }
};

/**
 * 转树形
 * @param items
 * @param idField
 * @param pidField
 * @returns
 */
export const arrayToTree = function (items: any, idField = '_id', pidField = 'parent_id') {
  const result: any = []; // 存放结果集
  if (!items) return result;
  const itemMap: any = {}; //
  for (const item of items) {
    const id = item[idField];
    const pid = item[pidField];
    if (!itemMap[id]) {
      itemMap[id] = {
        children: [],
      };
    }
    itemMap[id] = {
      ...item,
      children: itemMap[id]['children'],
    };
    const treeItem = itemMap[id];
    if (!pid) {
      result.push(treeItem);
    } else {
      if (!itemMap[pid]) {
        itemMap[pid] = {
          children: [],
        };
      }
      itemMap[pid].children.push(treeItem);
    }
  }
  return result;
};

/**
 * 格式化时间
 */
export const timeText = computed(() => (time: number) => {
  // 4月25日 周二
  const DayJs = dayjs.default;
  let str = DayJs(time).format('MM月DD日-d');
  let arr = str.split('-');
  let week = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return arr[0] + ' ' + week[Number(arr[1])]
})