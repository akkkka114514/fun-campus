/**
 * 展示格式化 & 文件 URL 工具
 */

// 文件访问前缀：非私有 folder 的 fileKey 直接拼接即可访问
const FILE_URL = import.meta.env.VITE_APP_FILE_URL || '';

/**
 * fileKey → 可访问 URL（已是完整 URL / base64 的原样返回）
 */
export function resolveFileUrl(fileKey) {
  if (!fileKey) {
    return '';
  }
  if (
    fileKey.startsWith('http://') ||
    fileKey.startsWith('https://') ||
    fileKey.startsWith('data:')
  ) {
    return fileKey;
  }
  return FILE_URL + fileKey;
}

/**
 * 后端 LocalDateTime（'2026-09-25T14:00:00'）→ '2026-09-25 14:00'
 */
export function formatDateTime(value) {
  if (!value) {
    return '';
  }
  return String(value).replace('T', ' ').slice(0, 16);
}

/**
 * → '2026-09-25'
 */
export function formatDate(value) {
  if (!value) {
    return '';
  }
  return String(value).replace('T', ' ').slice(0, 10);
}

/**
 * 时间区间：'09-25 14:00 ~ 16:00'（同一天）/'09-25 14:00 ~ 09-26 16:00'
 */
export function formatTimeRange(start, end) {
  if (!start) {
    return '';
  }
  const s = formatDateTime(start);
  const e = end ? formatDateTime(end) : '';
  if (!e) {
    return s;
  }
  const sameDay = s.slice(0, 10) === e.slice(0, 10);
  return sameDay ? `${s} ~ ${e.slice(11)}` : `${s} ~ ${e}`;
}

/**
 * 分 → 元字符串（'12.00'）
 */
export function fenToYuan(fen) {
  if (fen == null) {
    return '0.00';
  }
  return (Number(fen) / 100).toFixed(2);
}

/**
 * 性别文案：true-男 false-女
 */
export function genderText(gender) {
  if (gender === true) {
    return '男';
  }
  if (gender === false) {
    return '女';
  }
  return '未设置';
}
