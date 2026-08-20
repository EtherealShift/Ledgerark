-- =============================================
-- sys_user_role 表结构调整脚本（表已存在，执行以下 ALTER 修改）
-- 数据库：ledgerark
-- 说明：不使用外键约束，数据完整性由代码逻辑保证
--       （删除用户/角色时，需在代码中同步清理关联数据）
-- =============================================

-- 1. 删除原复合主键 (user_id, role_id)
ALTER TABLE `sys_user_role` DROP PRIMARY KEY;

-- 2. 新增自增主键 id、update_time 列，并重建唯一索引防止重复授权
ALTER TABLE `sys_user_role`
  ADD COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID' FIRST,
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD COLUMN `update_time` DATETIME DEFAULT NULL COMMENT '更新时间' AFTER `role_id`,
  ADD UNIQUE KEY `uk_user_role` (`user_id`, `role_id`) USING BTREE;

-- =============================================
-- 以下语句仅在尚未执行过时运行（若 sys_user.role_type 已改名可跳过）
-- =============================================
-- ALTER TABLE `sys_user` CHANGE COLUMN `role_type` `user_type` CHAR(1) DEFAULT '2' COMMENT '用户类型 (1-超级管理员 2-普通用户)';
