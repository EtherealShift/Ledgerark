-- ============================================================================
-- 初始化数据（可选执行）：预置系统角色 + 数据字典
-- ----------------------------------------------------------------------------
-- 前置依赖：已执行 sql/sys_user.sql、sql/sys_role.sql、sql/sys_user_role.sql、
--           sql/sys_manage.sql（需 sys_dict_type / sys_dict_item 表）
--
-- 说明：
--   1. 角色预置 4 个（对应设计文档 §7 权限模型中的角色规划）；
--      role_code 为 BIGINT（对齐骨架 sys_role 实体），role_key 为权限标识。
--   2. 字典预置发票类型/密级/保管期限等常用字典（沿用 v1 数据）。
--   3. 系统管理员账号【不】预置：注册密码需 BCrypt 加密，不适合脚本硬编码，
--      请通过注册接口创建首个管理员后，在数据库中将 user_type 置为 1；
--      若使用 sql/reset_sys_core.sql，其中已包含 admin 超级管理员。
--   4. 本脚本已【幂等】：所有 INSERT 均带 WHERE NOT EXISTS 判断，
--      可重复执行，也不会与 reset_sys_core.sql 预置的角色重复。
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. 预置系统角色（data_scope：1-全部数据 2-本部门；status：0-正常 1-停用）
-- ----------------------------------------------------------------------------
INSERT INTO sys_role (role_code, role_name, role_key, data_scope, status, remark)
SELECT 1, '系统管理员', 'ADMIN',    '1', '0', '系统内置角色：全部权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'ADMIN');

INSERT INTO sys_role (role_code, role_name, role_key, data_scope, status, remark)
SELECT 2, '档案管理员', 'ARCHIVER', '2', '0', '档案采集/整理/归档管理'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'ARCHIVER');

INSERT INTO sys_role (role_code, role_name, role_key, data_scope, status, remark)
SELECT 3, '审批人',     'AUDITOR',  '2', '0', '借阅/销毁审批'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'AUDITOR');

INSERT INTO sys_role (role_code, role_name, role_key, data_scope, status, remark)
SELECT 4, '普通用户',   'USER',     '2', '0', '默认角色：借阅申请等'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'USER');

-- ----------------------------------------------------------------------------
-- 2. 字典类型
-- ----------------------------------------------------------------------------
INSERT INTO sys_dict_type (dict_code, dict_name, status, remark)
SELECT 'INVOICE_TYPE',   '发票类型', 1, '发票/票据类型'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_code = 'INVOICE_TYPE');

INSERT INTO sys_dict_type (dict_code, dict_name, status, remark)
SELECT 'ARCHIVE_STATUS', '档案状态', 1, '档案状态机（0草稿/1归档/2借出/3销毁/4移交）'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_code = 'ARCHIVE_STATUS');

INSERT INTO sys_dict_type (dict_code, dict_name, status, remark)
SELECT 'SECURITY_LEVEL', '密级',     1, '档案密级'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_code = 'SECURITY_LEVEL');

INSERT INTO sys_dict_type (dict_code, dict_name, status, remark)
SELECT 'RETENTION_TYPE', '保管期限', 1, '档案保管期限'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_code = 'RETENTION_TYPE');

INSERT INTO sys_dict_type (dict_code, dict_name, status, remark)
SELECT 'TRANSPORT_TYPE', '运输类型', 1, '运输票据类型'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_code = 'TRANSPORT_TYPE');

INSERT INTO sys_dict_type (dict_code, dict_name, status, remark)
SELECT 'BORROW_STATUS',  '借阅状态', 1, '借阅状态机（0待批/1借出/2归还/3驳回/4逾期）'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_code = 'BORROW_STATUS');

-- ----------------------------------------------------------------------------
-- 3. 字典项
-- ----------------------------------------------------------------------------
INSERT INTO sys_dict_item (dict_type_id, item_code, item_name, sort_no, status)
SELECT t.id, 'VAT_SPECIAL', '增值税专用发票', 1, 1 FROM sys_dict_type t
WHERE t.dict_code = 'INVOICE_TYPE'
  AND NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_type_id = t.id AND i.item_code = 'VAT_SPECIAL');

INSERT INTO sys_dict_item (dict_type_id, item_code, item_name, sort_no, status)
SELECT t.id, 'VAT_NORMAL', '增值税普通发票', 2, 1 FROM sys_dict_type t
WHERE t.dict_code = 'INVOICE_TYPE'
  AND NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_type_id = t.id AND i.item_code = 'VAT_NORMAL');

INSERT INTO sys_dict_item (dict_type_id, item_code, item_name, sort_no, status)
SELECT t.id, 'TRAIN', '火车票', 3, 1 FROM sys_dict_type t
WHERE t.dict_code = 'INVOICE_TYPE'
  AND NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_type_id = t.id AND i.item_code = 'TRAIN');

INSERT INTO sys_dict_item (dict_type_id, item_code, item_name, sort_no, status)
SELECT t.id, 'FLIGHT', '机票', 4, 1 FROM sys_dict_type t
WHERE t.dict_code = 'INVOICE_TYPE'
  AND NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_type_id = t.id AND i.item_code = 'FLIGHT');

INSERT INTO sys_dict_item (dict_type_id, item_code, item_name, sort_no, status)
SELECT t.id, 'TAXI', '出租车票', 5, 1 FROM sys_dict_type t
WHERE t.dict_code = 'INVOICE_TYPE'
  AND NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_type_id = t.id AND i.item_code = 'TAXI');

INSERT INTO sys_dict_item (dict_type_id, item_code, item_name, sort_no, status)
SELECT t.id, 'TOLL', '通行费发票', 6, 1 FROM sys_dict_type t
WHERE t.dict_code = 'INVOICE_TYPE'
  AND NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_type_id = t.id AND i.item_code = 'TOLL');

INSERT INTO sys_dict_item (dict_type_id, item_code, item_name, sort_no, status)
SELECT t.id, 'INTERNAL', '内部', 1, 1 FROM sys_dict_type t
WHERE t.dict_code = 'SECURITY_LEVEL'
  AND NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_type_id = t.id AND i.item_code = 'INTERNAL');

INSERT INTO sys_dict_item (dict_type_id, item_code, item_name, sort_no, status)
SELECT t.id, 'SECRET', '秘密', 2, 1 FROM sys_dict_type t
WHERE t.dict_code = 'SECURITY_LEVEL'
  AND NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_type_id = t.id AND i.item_code = 'SECRET');

INSERT INTO sys_dict_item (dict_type_id, item_code, item_name, sort_no, status)
SELECT t.id, 'TOP_SECRET', '机密', 3, 1 FROM sys_dict_type t
WHERE t.dict_code = 'SECURITY_LEVEL'
  AND NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_type_id = t.id AND i.item_code = 'TOP_SECRET');

INSERT INTO sys_dict_item (dict_type_id, item_code, item_name, sort_no, status)
SELECT t.id, 'PERMANENT', '永久', 1, 1 FROM sys_dict_type t
WHERE t.dict_code = 'RETENTION_TYPE'
  AND NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_type_id = t.id AND i.item_code = 'PERMANENT');

INSERT INTO sys_dict_item (dict_type_id, item_code, item_name, sort_no, status)
SELECT t.id, 'Y30', '30年', 2, 1 FROM sys_dict_type t
WHERE t.dict_code = 'RETENTION_TYPE'
  AND NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_type_id = t.id AND i.item_code = 'Y30');

INSERT INTO sys_dict_item (dict_type_id, item_code, item_name, sort_no, status)
SELECT t.id, 'Y10', '10年', 3, 1 FROM sys_dict_type t
WHERE t.dict_code = 'RETENTION_TYPE'
  AND NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_type_id = t.id AND i.item_code = 'Y10');
