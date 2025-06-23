<?php
/**
 * phpMyAdmin 自定义配置文件
 * 用于jshERP数据库管理
 */

// 增加内存限制
ini_set('memory_limit', '512M');

// 增加最大执行时间
ini_set('max_execution_time', 600);

// 增加上传文件大小限制
ini_set('upload_max_filesize', '100M');
ini_set('post_max_size', '100M');

// 设置时区
date_default_timezone_set('Asia/Shanghai');

// 配置服务器连接
$cfg['Servers'][1]['host'] = 'jsherp-mysql-dev';
$cfg['Servers'][1]['port'] = '3306';
$cfg['Servers'][1]['socket'] = '';
$cfg['Servers'][1]['connect_type'] = 'tcp';
$cfg['Servers'][1]['extension'] = 'mysqli';
$cfg['Servers'][1]['auth_type'] = 'cookie';
$cfg['Servers'][1]['user'] = '';
$cfg['Servers'][1]['password'] = '';
$cfg['Servers'][1]['AllowNoPassword'] = false;

// 数据库和表的默认设置
$cfg['DefaultLang'] = 'zh_CN';
$cfg['DefaultCharset'] = 'utf8mb4';
$cfg['DefaultCollation'] = 'utf8mb4_unicode_ci';

// 界面设置
$cfg['ThemeDefault'] = 'pmahomme';
$cfg['NavigationTreeDefaultTabTable'] = 'structure';
$cfg['NavigationTreeDefaultTabTable2'] = 'sql';

// 导入导出设置
$cfg['Import']['charset'] = 'utf8mb4';
$cfg['Export']['charset'] = 'utf8mb4';
$cfg['Export']['compression'] = 'gzip';

// SQL查询设置
$cfg['SQLQuery']['Edit'] = true;
$cfg['SQLQuery']['Explain'] = true;
$cfg['SQLQuery']['ShowAsPHP'] = true;
$cfg['SQLQuery']['Validate'] = false;
$cfg['SQLQuery']['Refresh'] = true;

// 安全设置
$cfg['CheckConfigurationPermissions'] = false;
$cfg['AllowArbitraryServer'] = true;

// 会话设置
$cfg['LoginCookieValidity'] = 3600 * 24; // 24小时
$cfg['LoginCookieRecall'] = true;
$cfg['LoginCookieStore'] = 0;
$cfg['LoginCookieDeleteAll'] = true;

// Cookie安全设置 - 解决HTTP环境下的cookie问题
$cfg['CookieSameSite'] = 'Lax';
$cfg['SessionSavePath'] = '';

// 解决HTTP环境下的session问题
ini_set('session.cookie_secure', '0');
ini_set('session.cookie_httponly', '1');
ini_set('session.cookie_samesite', 'Lax');

// 显示设置
$cfg['MaxRows'] = 100;
$cfg['Order'] = 'ASC';
$cfg['RowActionLinks'] = 'left';
$cfg['ShowAll'] = false;
$cfg['ShowSQL'] = true;
$cfg['RetainQueryBox'] = true;

// 编辑设置
$cfg['ProtectBinary'] = 'blob';
$cfg['ShowFunctionFields'] = true;
$cfg['ShowFieldTypesInDataEditView'] = true;
$cfg['InsertRows'] = 2;
$cfg['ForeignKeyDropdownOrder'] = array('content-id', 'id-content');

// 浏览设置
$cfg['RepeatCells'] = 100;
$cfg['LimitChars'] = 50;
$cfg['BrowseMIME'] = true;
$cfg['BrowseMarkerEnable'] = true;

// 特殊功能
$cfg['EnableAutocompleteForTablesAndColumns'] = true;
$cfg['CodemirrorEnable'] = true;
$cfg['LintEnable'] = true;

// jshERP特定设置
$cfg['Servers'][1]['verbose'] = 'jshERP Database';
$cfg['Servers'][1]['hide_db'] = '^(information_schema|performance_schema|mysql|sys)$';

// 自定义CSS（可选）
$cfg['ThemePath'] = './themes';

// 错误报告
$cfg['Error_Handler']['display'] = true;
$cfg['Error_Handler']['gather'] = true;

// 备份设置
$cfg['Export']['method'] = 'quick';
$cfg['Export']['format'] = 'sql';
$cfg['Export']['compression'] = 'gzip';
$cfg['Export']['charset'] = 'utf8mb4';

// 中文界面优化
$cfg['Lang'] = 'zh_CN';
$cfg['FilterLanguages'] = 'zh_CN';

?>
