-- IM 管理菜单（若依 sys_menu），在已有库执行；menu_id 请按环境调整避免冲突
INSERT INTO sys_menu (menu_name, parent_id, order_num, url, target, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('IM管理', 0, 5, '#', '', 'M', '0', '1', '', 'fa fa-comments', 'admin', sysdate(), '', null, 'chat-ops 业务');

SET @imParent := LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, url, target, menu_type, visible, is_refresh, perms, icon, create_by, create_time, remark)
VALUES ('移动端用户', @imParent, 1, '/im/member', '', 'C', '0', '1', 'member:member:list', 'fa fa-user', 'admin', sysdate(), 'IM会员');

INSERT INTO sys_menu (menu_name, parent_id, order_num, url, target, menu_type, visible, is_refresh, perms, icon, create_by, create_time, remark)
VALUES ('群组管理', @imParent, 2, '/im/group', '', 'C', '0', '1', 'group:group:list', 'fa fa-group', 'admin', sysdate(), 'IM群组');

INSERT INTO sys_menu (menu_name, parent_id, order_num, url, target, menu_type, visible, is_refresh, perms, icon, create_by, create_time, remark)
VALUES ('好友管理', @imParent, 3, '/im/friend', '', 'C', '0', '1', 'friend:friend:list', 'fa fa-handshake-o', 'admin', sysdate(), 'IM好友');
