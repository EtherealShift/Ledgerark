-- =============================================
-- sys_role 角色表（完整建表脚本）
-- 数据库：ledgerark
-- 说明：字段与 SysRole 实体 + BaseEntity 对齐（逻辑删除字段 del_flag，仅需要的表才建）
-- 若本地表已存在：先核对 SHOW CREATE TABLE sys_role; 或 DROP 后执行；
-- 已存在且缺 del_flag 列时执行：
--   ALTER TABLE sys_role ADD COLUMN del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志 0正常 1已删除';
-- =============================================
CREATE TABLE sys_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_code   BIGINT       NULL     COMMENT '角色编码',
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    role_key    VARCHAR(100) NULL     COMMENT '角色权限字符串',
    data_scope  CHAR(1)      NOT NULL DEFAULT '1' COMMENT '数据权限范围',
    status      CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态 0正常 1停用',
    create_by   VARCHAR(64)  NULL     COMMENT '创建者',
    update_by   VARCHAR(64)  NULL     COMMENT '更新者',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(500) NULL     COMMENT '备注',
    del_flag    TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志 0正常 1已删除',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表';
