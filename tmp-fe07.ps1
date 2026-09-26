$ErrorActionPreference = 'Stop'
$base = 'http://127.0.0.1:1024'

# 登录（张三：UTF-8 字节构造避免 PS5.1 中文字面量乱码）
$cap = (Invoke-WebRequest -Uri "$base/portal/login/getCaptcha" -UseBasicParsing).Content | ConvertFrom-Json
$zhang = [Text.Encoding]::UTF8.GetString([byte[]]@(0xE5,0xBC,0xA0,0xE4,0xB8,0x89))
$loginBody = '{"username":"' + $zhang + '","password":"admin123","captchaCode":"' + $cap.data.captchaText + '","captchaUuid":"' + $cap.data.captchaUuid + '","loginDevice":4}'
$resp = Invoke-WebRequest -Uri "$base/portal/login" -Method Post -Body ([Text.Encoding]::UTF8.GetBytes($loginBody)) -ContentType 'application/json; charset=utf-8' -UseBasicParsing
$token = ($resp.Content | ConvertFrom-Json).data.token
if (-not $token) { Write-Host 'LOGIN FAIL'; exit 1 }
$headers = @{ Authorization = 'Bearer ' + $token }
Write-Host 'LOGIN OK'

# 1. 签到码
$qr = (Invoke-WebRequest -Uri "$base/portal/activityEnrollment/signIn/QRCode" -Headers $headers -UseBasicParsing).Content | ConvertFrom-Json
Write-Host ('QR code=' + $qr.code + ' expire=' + $qr.data.expireSeconds + ' userId=' + $qr.data.userId + ' tokenLen=' + $qr.data.token.Length + ' imgPrefix=' + $qr.data.qrCodeImage.Substring(0,22) + ' imgLen=' + $qr.data.qrCodeImage.Length)

# 2. 消息列表
$msgBody = '{"pageNum":1,"pageSize":15,"searchCount":true}'
$msg = (Invoke-WebRequest -Uri "$base/portal/message/queryMyMessage" -Method Post -Headers $headers -Body ([Text.Encoding]::UTF8.GetBytes($msgBody)) -ContentType 'application/json; charset=utf-8' -UseBasicParsing).Content | ConvertFrom-Json
Write-Host ('MSG code=' + $msg.code + ' total=' + $msg.data.total + ' listCount=' + @($msg.data.list).Count)
@($msg.data.list) | ForEach-Object { Write-Host ('  id=' + $_.messageId + ' type=' + $_.messageType + ' read=' + $_.readFlag + ' title=' + $_.title) }

# 3. 未读数
$unread = (Invoke-WebRequest -Uri "$base/portal/message/getUnreadCount" -Headers $headers -UseBasicParsing).Content | ConvertFrom-Json
Write-Host ('UNREAD=' + $unread.data)

# 4. 分享生成 + 解析
$share = (Invoke-WebRequest -Uri "$base/portal/activity/share/generate" -Method Post -Headers $headers -Body ([Text.Encoding]::UTF8.GetBytes('6')) -ContentType 'application/json; charset=utf-8' -UseBasicParsing).Content | ConvertFrom-Json
Write-Host ('SHARE url=' + $share.data.shareUrl)
$resolve = (Invoke-WebRequest -Uri ("$base/portal/activity/share/resolve/" + $share.data.shareToken) -Headers $headers -UseBasicParsing).Content | ConvertFrom-Json
Write-Host ('RESOLVE activityId=' + $resolve.data)

# 5. 标记已读 + 复验未读数
$first = @($msg.data.list) | Where-Object { -not $_.readFlag } | Select-Object -First 1
if ($first) {
  $read = (Invoke-WebRequest -Uri ("$base/portal/message/read/" + $first.messageId) -Headers $headers -UseBasicParsing).Content | ConvertFrom-Json
  Write-Host ('READ id=' + $first.messageId + ' code=' + $read.code)
  $unread2 = (Invoke-WebRequest -Uri "$base/portal/message/getUnreadCount" -Headers $headers -UseBasicParsing).Content | ConvertFrom-Json
  Write-Host ('UNREAD AFTER=' + $unread2.data)
} else {
  Write-Host 'NO UNREAD MESSAGE IN LIST'
}
Write-Host 'DONE'
