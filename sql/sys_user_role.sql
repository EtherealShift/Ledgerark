-- =============================================
-- sys_user_role 用户角色关联表（完整建表脚本）
-- 数据库：ledgerark
-- 说明：结构对应原 sql/sys_role.sql（ALTER 调整脚本）的最终结果：
--       自增 id 主键 + uk_user_role 唯一键，支持一个用户多角色。
-- 原 sys_role.sql 中针对 sys_user_role 的 ALTER 语句已由本脚本取代。
-- 若本地表已存在：先核对 SHOW CREATE TABLE sys_user_role; 或 DROP 后执行。
-- =============================================
CREATE TABLE sys_user_role (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT      NOT NULL COMMENT '用户 ID',
    role_id     BIGINT      NOT NULL COMMENT '角色 ID',
    update_time DATETIME    NULL     COMMENT '更新时间',
    update_by   VARCHAR(64) NULL     COMMENT '更新者',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表';
