-- =============================================
-- sys_user 用户表（完整建表脚本）
-- 数据库：ledgerark
-- 说明：字段与 SysUser 实体 + BaseEntity 对齐（逻辑删除字段 del_flag，仅需要的表才建）
-- =============================================
CREATE TABLE sys_user (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    employee_id  VARCHAR(32)  NULL     COMMENT '工号',
    user_name    VARCHAR(50)  NOT NULL COMMENT '用户账号',
    nick_name    VARCHAR(50)  NULL     COMMENT '用户昵称',
    password     VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    email        VARCHAR(100) NULL     COMMENT '邮箱',
    phone_number VARCHAR(20)  NULL     COMMENT '手机号',
    sex          CHAR(1)      NOT NULL DEFAULT '2' COMMENT '性别 0男 1女 2未知',
    status       CHAR(1)      NOT NULL DEFAULT '0' COMMENT '账号状态 0正常 1停用',
    avatar       VARCHAR(255) NULL     COMMENT '头像路径',
    user_type    CHAR(1)      NOT NULL DEFAULT '2' COMMENT '用户类型 1超级管理员 2普通用户',
    create_by    VARCHAR(64)  NULL     COMMENT '创建者',
    update_by    VARCHAR(64)  NULL     COMMENT '更新者',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark       VARCHAR(500) NULL     COMMENT '备注',
    del_flag     TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志 0正常 1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_name (user_name),
    UNIQUE KEY uk_email (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';
