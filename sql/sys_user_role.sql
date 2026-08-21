-- =============================================
-- sys_user_role 用户角色关联表（完整建表脚本）
-- 数据库：ledgerark
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
