$mapperDir = 'D:\project\web4x\web4x\web4x-system\src\main\resources\mapper\system'
$files = Get-ChildItem $mapperDir -Filter '*.xml' | Where-Object { $_.Name -ne 'SysConfigMapper.xml' }

$rules = @(
    @{ from = 'sys_user_role'; to = 'sys_user_role_ry' }
    @{ from = 'sys_role_menu'; to = 'sys_role_menu_ry' }
    @{ from = 'sys_notice_read'; to = 'sys_notice_read_ry' }
    @{ from = 'sys_dict_type'; to = 'sys_dict_type_ry' }
    @{ from = 'sys_user'; to = 'sys_user_ry' }
    @{ from = 'sys_role'; to = 'sys_role_ry' }
    @{ from = 'sys_menu'; to = 'sys_menu_ry' }
    @{ from = 'sys_post'; to = 'sys_post_ry' }
)

foreach ($f in $files) {
    $text = [IO.File]::ReadAllText($f.FullName)
    $orig = $text
    foreach ($r in $rules) {
        $text = $text.Replace($r.from, $r.to)
    }
    # undo accidental double-suffix / wrong compounds
    $text = $text.Replace('sys_user_ry_online', 'sys_user_online')
    $text = $text.Replace('sys_user_ry_post', 'sys_user_post')
    $text = $text.Replace('sys_role_ry_dept', 'sys_role_dept')
    if ($text -ne $orig) {
        [IO.File]::WriteAllText($f.FullName, $text, (New-Object Text.UTF8Encoding $false))
        Write-Host ('patched ' + $f.Name)
    }
}
