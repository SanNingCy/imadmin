# Generate ruoyi-compat-chat_platform.sql from ruoyi.sql
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$ruoyiPath = Join-Path $root 'ruoyi.sql'
$destPath = Join-Path $root 'ruoyi-compat-chat_platform.sql'
$bt = [char]96

$rename = @{
    'sys_config' = 'sys_param_config'
    'sys_user' = 'sys_user_ry'
    'sys_role' = 'sys_role_ry'
    'sys_menu' = 'sys_menu_ry'
    'sys_post' = 'sys_post_ry'
    'sys_notice_read' = 'sys_notice_read_ry'
    'sys_dict_type' = 'sys_dict_type_ry'
    'sys_role_menu' = 'sys_role_menu_ry'
    'sys_user_role' = 'sys_user_role_ry'
}

$header = @'
-- =============================================================================
-- RuoYi + IM same DB (chat_platform_db) - safe install script
-- No DROP TABLE; does not modify IM tables (sys_config, sys_user, sys_menu, ...)
-- RuoYi uses: sys_param_config, sys_user_ry, sys_menu_ry, ... see RUOYI-COMPAT.md
-- =============================================================================
USE chat_platform_db;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

'@

function Wrap-Table([string]$name) { return "$bt$name$bt" }

$lines = Get-Content -Path $ruoyiPath -Encoding UTF8
$out = [System.Collections.Generic.List[string]]::new()
[void]$out.Add($header.TrimEnd())

$i = 0
while ($i -lt $lines.Count) {
    $line = $lines[$i]

    if ($line -match '^\s*DROP TABLE IF EXISTS ' + $bt + '([^' + $bt + ']+)' + $bt) {
        $t = $Matches[1]
        if ($t -like 'QRTZ_*' -or $t -like 'qrtz_*') {
            [void]$out.Add('-- skip quartz ' + (Wrap-Table $t))
        }
        elseif ($rename.ContainsKey($t)) {
            [void]$out.Add('-- skip DROP protect IM ' + (Wrap-Table $t) + ' ruoyi=' + (Wrap-Table $rename[$t]))
        }
        else {
            [void]$out.Add('-- skip DROP ' + (Wrap-Table $t))
        }
        $i++
        continue
    }

    if ($line -match '^\s*CREATE TABLE ' + $bt + '([^' + $bt + ']+)' + $bt) {
        $t = $Matches[1]
        if ($t -like 'QRTZ_*' -or $t -like 'qrtz_*') {
            [void]$out.Add('-- skip create ' + (Wrap-Table $t))
            $i++
            while ($i -lt $lines.Count -and -not $lines[$i].Trim().EndsWith(';')) { $i++ }
            if ($i -lt $lines.Count) { $i++ }
            continue
        }
        $nt = if ($rename.ContainsKey($t)) { $rename[$t] } else { $t }
        $old = 'CREATE TABLE ' + (Wrap-Table $t)
        $new = 'CREATE TABLE IF NOT EXISTS ' + (Wrap-Table $nt)
        [void]$out.Add($line.Replace($old, $new))
        $i++
        while ($i -lt $lines.Count) {
            if ($lines[$i].Trim().EndsWith(';')) {
                [void]$out.Add($lines[$i])
                $i++
                break
            }
            if ($lines[$i] -match '^\s*DROP TABLE' -or $lines[$i] -match '^\s*CREATE TABLE') { break }
            [void]$out.Add($lines[$i])
            $i++
        }
        [void]$out.Add('')
        continue
    }

    if ($line -match '^\s*INSERT INTO ' + $bt + '([^' + $bt + ']+)' + $bt) {
        $t = $Matches[1]
        if ($t -like 'QRTZ_*' -or $t -like 'qrtz_*') {
            $i++
            continue
        }
        $nt = if ($rename.ContainsKey($t)) { $rename[$t] } else { $t }
        $old = 'INSERT INTO ' + (Wrap-Table $t)
        $new = 'INSERT IGNORE INTO ' + (Wrap-Table $nt)
        [void]$out.Add($line.Replace($old, $new))
        $i++
        continue
    }

    if ($line -match '^\s*(BEGIN|COMMIT)\s*;?\s*$') {
        [void]$out.Add($line)
    }
    $i++
}

[void]$out.Add('SET FOREIGN_KEY_CHECKS = 1;')
[void]$out.Add('')
[void]$out.Add('-- done: ruoyi login uses sys_user_ry (default admin)')

$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllLines($destPath, $out, $utf8NoBom)
$info = Get-Item $destPath
Write-Host ('Wrote ' + $destPath + ' size=' + $info.Length + ' lines=' + $out.Count)
