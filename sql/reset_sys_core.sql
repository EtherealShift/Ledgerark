-- ============================================================================
-- 重置系统核心三表（sys_role / sys_user / sys_user_role）并预置超级管理员
-- ----------------------------------------------------------------------------
-- 用途：删表重建（DROP + CREATE）+ 基础数据（4 个系统角色 + admin 超级管理员 + 关联）。
-- 警告：
--   1. 本脚本会【清空】三表全部数据，执行前请确认或备份；
--   2. 若已执行过 sql/sys_manage.sql（sys_role_menu 外键引用 sys_role），
--      本脚本会自动先 DROP sys_role_menu 以解除外键，需要时重建可参照 sys_manage.sql；
--   3. 之后执行 sql/init_data.sql 不会重复插入（该脚本已幂等化）。
-- 前置：MySQL 8.0+，库 ledgerark。
-- 超级管理员：user_name=admin，密码 admin123（BCrypt，见下方 INSERT 注释），
--             首次登录后请立即修改密码；工号留空由管理员分配。
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 一、删表（顺序：先删引用方，再删被引用方）
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS sys_role_menu;   -- 若存在（引用 sys_role），先解除外键
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_role;

-- ----------------------------------------------------------------------------
-- 二、重建 sys_role 角色表（与 sql/sys_role.sql 一致）
-- ----------------------------------------------------------------------------
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

-- ----------------------------------------------------------------------------
-- 三、重建 sys_user 用户表（与 sql/sys_user.sql 一致）
-- ----------------------------------------------------------------------------
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

-- ----------------------------------------------------------------------------
-- 四、重建 sys_user_role 用户角色关联表（与 sql/sys_user_role.sql 一致）
-- ----------------------------------------------------------------------------
CREATE TABLE sys_user_role (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT      NOT NULL COMMENT '用户 ID',
    role_id     BIGINT      NOT NULL COMMENT '角色 ID',
    update_time DATETIME    NULL     COMMENT '更新时间',
    update_by   VARCHAR(64) NULL     COMMENT '更新者',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表';

-- ----------------------------------------------------------------------------
-- 五、基础数据
-- ----------------------------------------------------------------------------

-- 5.1 预置系统角色（data_scope：1-全部数据 2-本部门；status：0-正常 1-停用）
INSERT INTO sys_role (role_code, role_name, role_key, data_scope, status, remark) VALUES
    (1, '系统管理员', 'ADMIN',    '1', '0', '系统内置角色：全部权限'),
    (2, '档案管理员', 'ARCHIVER', '2', '0', '档案采集/整理/归档管理'),
    (3, '审批人',     'AUDITOR',  '2', '0', '借阅/销毁审批'),
    (4, '普通用户',   'USER',     '2', '0', '默认角色：借阅申请等');

-- 5.2 预置超级管理员
--     账号：admin    密码：admin123（BCrypt hash，已用 BCryptPasswordEncoder.matches 验证）
--     首次登录后请立即修改密码；邮箱可自行更换（uk_email 唯一）；工号留空由管理员分配。
INSERT INTO sys_user (employee_id, user_name, nick_name, password, email, phone_number,
                      sex, status, avatar, user_type, create_by, update_by, remark, del_flag)
VALUES (NULL, 'admin', '系统管理员',
        '$2a$10$wVaxm1jSsMSZYlSgj6GuT.K7z7l5dptIgt9PCuw/hiYPQw0HoxQW6',
        'admin@ledgerark.local', NULL,
        '2', '0', NULL, '1', 'system', NULL, '系统内置超级管理员', 0);

-- 5.3 关联：admin 用户 → ADMIN 角色
INSERT INTO sys_user_role (user_id, role_id, update_time, update_by)
SELECT u.id, r.id, NOW(), 'system'
FROM sys_user u
JOIN sys_role r ON r.role_key = 'ADMIN'
WHERE u.user_name = 'admin';
