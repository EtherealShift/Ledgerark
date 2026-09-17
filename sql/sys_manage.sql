-- ============================================================================
-- sys_* 系统管理扩展表（v2 · 对齐 ledgerark 骨架）
-- ----------------------------------------------------------------------------
-- 前置依赖：已执行 sql/sys_user.sql、sql/sys_role.sql、sql/sys_user_role.sql
--           （sys_role_menu 引用 sys_role；sys_menu 自引用树，无外部依赖）
-- 目标环境：MySQL 8.0+，库 ledgerark（utf8mb4 / utf8mb4_unicode_ci）
--
-- 本文件包含 6 张表（按依赖顺序创建）：
--   1. sys_dept        部门表（← v1 t_dept）
--   2. sys_menu        菜单/权限表（← v1 t_permission，RuoYi 风格，perms 承载权限码）
--   3. sys_role_menu   角色-菜单关联表（← v1 t_role_permission）
--   4. sys_dict_type   字典类型表（← v1 t_dict_type）
--   5. sys_dict_item   字典项表（← v1 t_dict_item）
--   6. sys_oper_log    操作日志表（← v1 t_operation_log，审计流水，不软删）
--
-- 字段对齐约定（与骨架 BaseEntity / 逻辑删除方案 B 一致）：
--   * 主键统一 BIGINT AUTO_INCREMENT（对齐骨架，v1 的 BIGINT UNSIGNED 去掉 UNSIGNED）
--   * 逻辑删除：方案 B —— 仅"需要软删"的表建 del_flag（0-正常 1-已删除），
--     实体对应字段加 @TableLogic，全局 logic-delete-field: delFlag 自动生效
--   * 审计字段：实体表含 create_by / update_by / create_time / update_time（对齐 BaseEntity）
--   * 关联/流水表（sys_role_menu、sys_oper_log）不建 del_flag
--   * 保留真实外键（与 v1 设计一致），执行必须按文件内顺序
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. sys_dept 部门表（树形，引用自身上级部门）
-- ----------------------------------------------------------------------------
CREATE TABLE sys_dept (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    parent_id   BIGINT       NOT NULL DEFAULT 0      COMMENT '上级部门 ID（0=根节点）',
    dept_name   VARCHAR(64)  NOT NULL                COMMENT '部门名称',
    sort_no     INT          NOT NULL DEFAULT 0      COMMENT '排序号',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-启用 0-停用',
    del_flag    TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by   VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by   VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(500) NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门表（树形）';

-- ----------------------------------------------------------------------------
-- 2. sys_menu 菜单/权限表（RuoYi 风格）
--    由 v1 t_permission（perm_code/perm_name/perm_type/path/sort_no）映射：
--      perm_code  → perms（权限码，如 archive:borrow:apply）
--      perm_name  → menu_name
--      perm_type  → menu_type（1-目录 2-菜单 3-按钮/接口）
--      path/sort_no 保留
-- ----------------------------------------------------------------------------
CREATE TABLE sys_menu (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    parent_id   BIGINT       NOT NULL DEFAULT 0      COMMENT '父菜单 ID（0=根，菜单树）',
    menu_name   VARCHAR(64)  NOT NULL                COMMENT '菜单/权限名称',
    menu_type   TINYINT      NOT NULL DEFAULT 2      COMMENT '类型：1-目录 2-菜单 3-按钮（接口权限）',
    path        VARCHAR(255) NULL                    COMMENT '前端路由/接口路径',
    perms       VARCHAR(128) NULL                    COMMENT '权限码（如 archive:borrow:apply，按钮/接口级）',
    icon        VARCHAR(64)  NULL                    COMMENT '图标',
    sort_no     INT          NOT NULL DEFAULT 0      COMMENT '排序号',
    visible     TINYINT      NOT NULL DEFAULT 1      COMMENT '是否可见：1-显示 0-隐藏',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-启用 0-停用',
    del_flag    TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by   VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by   VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(500) NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_perms (perms),
    KEY idx_parent_id (parent_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单/权限表（RuoYi 风格）';

-- ----------------------------------------------------------------------------
-- 3. sys_role_menu 角色-菜单关联表
-- ----------------------------------------------------------------------------
CREATE TABLE sys_role_menu (
    id         BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_id    BIGINT NOT NULL COMMENT '角色 ID（sys_role.id）',
    menu_id    BIGINT NOT NULL COMMENT '菜单 ID（sys_menu.id）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    KEY idx_menu_id (menu_id),
    CONSTRAINT fk_role_menu_role FOREIGN KEY (role_id) REFERENCES sys_role (id) ON DELETE CASCADE,
    CONSTRAINT fk_role_menu_menu FOREIGN KEY (menu_id) REFERENCES sys_menu (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色-菜单关联表';

-- ----------------------------------------------------------------------------
-- 4. sys_dict_type 字典类型表
-- ----------------------------------------------------------------------------
CREATE TABLE sys_dict_type (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    dict_code   VARCHAR(64)  NOT NULL                COMMENT '字典类型编码（唯一，如 INVOICE_TYPE/ARCHIVE_STATUS）',
    dict_name   VARCHAR(64)  NOT NULL                COMMENT '字典类型名称',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-启用 0-停用',
    del_flag    TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by   VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by   VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(500) NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_code (dict_code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典类型表';

-- ----------------------------------------------------------------------------
-- 5. sys_dict_item 字典项表
-- ----------------------------------------------------------------------------
CREATE TABLE sys_dict_item (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    dict_type_id BIGINT       NOT NULL                COMMENT '所属字典类型（sys_dict_type.id）',
    item_code    VARCHAR(32)  NOT NULL                COMMENT '字典项编码（如 VAT_SPECIAL）',
    item_name    VARCHAR(64)  NOT NULL                COMMENT '字典项名称（如：增值税专用发票）',
    sort_no      INT          NOT NULL DEFAULT 0      COMMENT '排序号',
    status       TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-启用 0-停用',
    del_flag     TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by    VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by    VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark       VARCHAR(500) NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_item (dict_type_id, item_code),
    CONSTRAINT fk_dict_item_type FOREIGN KEY (dict_type_id) REFERENCES sys_dict_type (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典项表';

-- ----------------------------------------------------------------------------
-- 6. sys_oper_log 操作日志表（审计流水，不软删；params 存脱敏后的请求参数）
-- ----------------------------------------------------------------------------
CREATE TABLE sys_oper_log (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT        NULL                    COMMENT '操作人（sys_user.id，可空=匿名/系统）',
    username    VARCHAR(64)   NULL                    COMMENT '操作人账号（冗余，用户删除后仍可追溯）',
    module      VARCHAR(64)   NULL                    COMMENT '模块（如：借阅管理）',
    action      VARCHAR(64)   NULL                    COMMENT '动作（如：借阅申请）',
    method      VARCHAR(128)  NULL                    COMMENT '请求方法（类.方法）',
    params      JSON          NULL                    COMMENT '请求参数摘要（敏感字段脱敏）',
    ip          VARCHAR(64)   NULL                    COMMENT '操作 IP',
    cost_ms     INT           NULL                    COMMENT '耗时（毫秒）',
    status      TINYINT       NOT NULL DEFAULT 1      COMMENT '状态：1-成功 0-失败',
    error_msg   VARCHAR(1000) NULL                    COMMENT '异常信息',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time),
    KEY idx_module (module)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表（审计）';
