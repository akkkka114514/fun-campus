# 品牌化：smartAdmin* DOM id / 变量 & smart_admin_ 存储前缀 -> funcampus
$root = 'f:\ideaWorkspace\fun-campus\smart-admin-web\src'
$map = [ordered]@{
  'smartAdminWaterMarkIntervalId' = 'funcampusWaterMarkIntervalId'
  'smartAdminLayoutContent'       = 'funcampusLayoutContent'
  'smartAdminHeader'              = 'funcampusHeader'
  'smartAdminAvatar'              = 'funcampusAvatar'
  'smartAdminPageTag'             = 'funcampusPageTag'
  'smartAdminMenu'                = 'funcampusMenu'
  'smartAdminMain'                = 'funcampusMain'
  'smart_admin_'                  = 'fun_campus_'
}
$enc = New-Object System.Text.UTF8Encoding($false)
$changed = @()
Get-ChildItem -Path $root -Recurse -File -Include *.vue, *.js | ForEach-Object {
  $file = $_.FullName
  $text = [System.IO.File]::ReadAllText($file, [System.Text.Encoding]::UTF8)
  $original = $text
  foreach ($k in $map.Keys) {
    if ($text.Contains($k)) {
      $text = $text.Replace($k, $map[$k])
    }
  }
  if ($text -ne $original) {
    [System.IO.File]::WriteAllText($file, $text, $enc)
    $changed += $file.Replace('f:\ideaWorkspace\fun-campus\smart-admin-web\', '')
  }
}
Write-Output "CHANGED: $($changed.Count)"
$changed | ForEach-Object { Write-Output "  $_" }
# 残留检查
$left = @()
Get-ChildItem -Path $root -Recurse -File -Include *.vue, *.js | ForEach-Object {
  $n = 0
  foreach ($line in [System.IO.File]::ReadAllLines($_.FullName)) {
    $n++
    if ($line -match 'smartAdmin|smart_admin_') {
      $left += "$($_.FullName):$n : $($line.Trim())"
    }
  }
}
Write-Output "REMAINING: $($left.Count)"
$left | ForEach-Object { Write-Output "  $_" }
