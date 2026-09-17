-- ============================================================================
-- biz_* 档案业务表（v2 · 对齐 ledgerark 骨架）
-- ----------------------------------------------------------------------------
-- 前置依赖（按序执行）：
--   1. sql/sys_user.sql、sql/sys_role.sql、sql/sys_user_role.sql（已建）
--   2. sql/sys_manage.sql（可选，仅 sys_dept/sys_menu 等系统扩展表）
--   3. 本文件（biz_ 表之间存在外键依赖，必须按文件内顺序执行）
-- 目标环境：MySQL 8.0+，库 ledgerark（utf8mb4 / utf8mb4_unicode_ci）
--
-- 本文件包含 16 张表（按依赖顺序创建）：
--   1.  biz_archive_category    档案分类表（树形）        ← v1 t_archive_category
--   2.  biz_archive             档案主表（系统核心）       ← v1 t_archive
--   3.  biz_voucher             记账凭证表                ← v1 t_voucher
--   4.  biz_invoice             发票主表（跨类型通用核心字段）← v1 t_invoice
--   5.  biz_invoice_detail      发票明细表                ← v1 t_invoice_detail
--   6.  biz_invoice_travel      交通出行类扩展表          ← v1 t_invoice_transport(重构)
--   7.  biz_invoice_vehicle     机动车/二手车类扩展表     ← 新增
--   8.  biz_invoice_medical     医疗票据类扩展表          ← 新增
--   9.  biz_invoice_customs     海关缴款书类扩展表        ← 新增
--   10. biz_invoice_bank        银行回单类扩展表          ← 新增
--   11. biz_invoice_nontax      非税/财政/完税/通用电子类扩展表 ← 新增
--   12. biz_archive_file        档案文件表（影像/压缩包）  ← v1 t_archive_file
--   13. biz_ocr_task            OCR 识别任务表（流水）    ← v1 t_ocr_task
--   14. biz_borrow              借阅登记表                ← v1 t_borrow
--   15. biz_destruction         档案销毁登记表            ← v1 t_destruction
--   16. biz_approval_record     审批记录表（通用流水）    ← v1 t_approval_record
--
-- 发票表设计说明（4~11）：
--   * 主表只存跨类型通用核心字段；类型特有字段按 invoiceType 写入 6 张扩展表（6~11），
--     仅对应类型出现时才插入，其余扩展表无记录（空着不影响查询），详情按类型 LEFT JOIN；
--   * 金额/日期/税率等一律 VARCHAR 原样留存（OCR 接口存在 "13%"、"*"、"2019年5月14日" 等非标准格式）；
--   * 低频明细（航班/货物运输/旅客运输）以 JSON 存主表 flights_json/goods_json/traveler_json；
--   * 识别附加信息（orientation/coord/region/cutUrlImage/confidence 等）不入库、不留存。
--
-- 字段对齐约定（与骨架 BaseEntity / 逻辑删除方案 B 一致）：
--   * 主键统一 BIGINT AUTO_INCREMENT（对齐骨架，v1 的 BIGINT UNSIGNED 去掉 UNSIGNED）
--   * 逻辑删除：方案 B —— 实体表建 del_flag（0-正常 1-已删除），
--     实体对应字段加 @TableLogic，全局 logic-delete-field: delFlag 自动生效
--   * 审计字段：实体表含 create_by / update_by / create_time / update_time（对齐 BaseEntity）
--   * 流水表（biz_ocr_task、biz_approval_record）不建 del_flag、不建审计字段
--   * 金额 DECIMAL、日期 DATE/DATETIME、OCR 原值 JSON、业务单号全部 UNIQUE
--   * 保留真实外键（与 v1 设计一致），外键引用 sys_user 时使用骨架 BIGINT 主键
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. biz_archive_category 档案分类表（树形：parent_id + path + level）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_archive_category (
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    parent_id      BIGINT       NOT NULL DEFAULT 0      COMMENT '父分类 ID（0=根节点）',
    category_code  VARCHAR(32)  NOT NULL                COMMENT '分类编码（唯一，如 JS/WZ/HT）',
    category_name  VARCHAR(64)  NOT NULL                COMMENT '分类名称（如：记账凭证/发票/合同）',
    path           VARCHAR(255) NULL                    COMMENT '祖先路径（如 /1/5/，冗余加速子树查询）',
    level          TINYINT      NOT NULL DEFAULT 1      COMMENT '层级（1 为根级）',
    sort_no        INT          NOT NULL DEFAULT 0      COMMENT '排序号',
    retention_days INT          NULL                    COMMENT '默认保管期限（天，NULL=永久）',
    status         TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-启用 0-停用',
    del_flag       TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by      VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by      VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark         VARCHAR(500) NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_code (category_code),
    KEY idx_parent_id (parent_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '档案分类表（树形）';

-- ----------------------------------------------------------------------------
-- 2. biz_archive 档案主表（系统核心）
--    档号规则：分类编码-年份-流水号（如 JS-2023-0001），应用层生成，uk 兜底防重
-- ----------------------------------------------------------------------------
CREATE TABLE biz_archive (
    id                   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    archive_code         VARCHAR(64)   NOT NULL                COMMENT '档号（业务唯一，如 JS-2023-0001）',
    category_id          BIGINT        NOT NULL                COMMENT '档案分类 ID（biz_archive_category.id）',
    archive_name         VARCHAR(255)  NOT NULL                COMMENT '案卷题名',
    archive_type         TINYINT       NOT NULL DEFAULT 0      COMMENT '档案形式：0-案卷 1-文件',
    description          VARCHAR(2000) NULL                    COMMENT '内容摘要/说明',
    security_level       TINYINT       NOT NULL DEFAULT 1      COMMENT '密级：1-内部 2-秘密 3-机密',
    retention_type       TINYINT       NOT NULL DEFAULT 1      COMMENT '保管期限：1-永久 2-30年 3-10年',
    retention_start_date DATE          NULL                    COMMENT '保管起始日期（通常=归档日期）',
    retention_end_date   DATE          NULL                    COMMENT '保管到期日期（=起始+期限；永久为 NULL）',
    storage_location     VARCHAR(128)  NULL                    COMMENT '存放位置（库房/柜架/盒号）',
    status               TINYINT       NOT NULL DEFAULT 0      COMMENT '状态：0-草稿 1-已归档 2-已借出 3-已销毁 4-已移交',
    borrow_status        TINYINT       NOT NULL DEFAULT 0      COMMENT '借出状态（冗余）：0-未借出 1-借出中',
    voucher_count        INT           NOT NULL DEFAULT 0      COMMENT '关联凭证数量（冗余统计）',
    file_count           INT           NOT NULL DEFAULT 0      COMMENT '文件数量（冗余统计）',
    archive_user_id      BIGINT        NULL                    COMMENT '归档人（sys_user.id）',
    archive_time         DATETIME      NULL                    COMMENT '归档时间',
    del_flag             TINYINT       NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by            VARCHAR(64)   NULL                    COMMENT '创建者',
    update_by            VARCHAR(64)   NULL                    COMMENT '更新者',
    create_time          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark               VARCHAR(500)  NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_archive_code (archive_code),
    KEY idx_category_id (category_id),
    KEY idx_status (status),
    KEY idx_retention_end (retention_end_date),
    KEY idx_security_level (security_level),
    CONSTRAINT fk_archive_category FOREIGN KEY (category_id) REFERENCES biz_archive_category (id) ON DELETE RESTRICT,
    CONSTRAINT fk_archive_user FOREIGN KEY (archive_user_id) REFERENCES sys_user (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '档案主表';

-- ----------------------------------------------------------------------------
-- 3. biz_voucher 记账凭证表（voucher_no 业务唯一；archive_id 可空=尚未组卷）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_voucher (
    id            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    archive_id    BIGINT        NULL                    COMMENT '所属档案 ID（biz_archive.id，可空=尚未组卷）',
    voucher_no    VARCHAR(32)   NOT NULL                COMMENT '凭证编号（业务唯一，如 2023-14-1910）',
    voucher_date  DATE          NOT NULL                COMMENT '凭证日期（由原 year/month/day 合并）',
    title         VARCHAR(50)   NOT NULL DEFAULT '记账凭证' COMMENT '凭证标题',
    debit_amount  DECIMAL(18,2) NOT NULL DEFAULT 0.00   COMMENT '借方金额合计（元）',
    credit_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00   COMMENT '贷方金额合计（元）',
    annex_path    VARCHAR(255)  NULL                    COMMENT '附件路径（zip 压缩包，相对项目根目录）',
    status        TINYINT       NOT NULL DEFAULT 0      COMMENT '状态：0-未识别 1-已识别 2-已完成 3-异常',
    del_flag      TINYINT       NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by     VARCHAR(64)   NULL                    COMMENT '创建者',
    update_by     VARCHAR(64)   NULL                    COMMENT '更新者',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark        VARCHAR(500)  NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_voucher_no (voucher_no),
    KEY idx_voucher_date (voucher_date),
    KEY idx_archive_id (archive_id),
    CONSTRAINT fk_voucher_archive FOREIGN KEY (archive_id) REFERENCES biz_archive (id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '记账凭证表';

-- ----------------------------------------------------------------------------
-- 4. biz_invoice 发票主表（跨类型通用核心字段）
--    要点：类型特有字段按 invoiceType 写入扩展表（6~11）；识别附加信息不入库；
--          金额/日期/税率等 VARCHAR 原样留存（接口格式不统一）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_invoice (
    id                      BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    voucher_id              BIGINT        NULL                    COMMENT '所属记账凭证 ID（biz_voucher.id），可空=尚未挂接凭证',
    voucher_code            VARCHAR(20)   NULL                    COMMENT '凭证编号 YYYY-mm-XXXX',
    voucher_year            VARCHAR(10)   NULL                    COMMENT '凭证年份',
    voucher_month           VARCHAR(10)   NULL                    COMMENT '凭证月份',
    invoice_type_code       INT           NULL                    COMMENT '发票类型代码（接口 invoiceType 原始值）',
    invoice_type            VARCHAR(50)   NOT NULL                COMMENT '发票类型名称（关联字典 INVOICE_TYPE）',
    title                   VARCHAR(100)  NULL                    COMMENT '发票标题/票面名称',
    administrative_division_name VARCHAR(100) NULL                 COMMENT '所属行政区名称',
    full_invoice_number     VARCHAR(50)   NULL                    COMMENT '全电发票号码',
    invoice_code            VARCHAR(50)   NULL                    COMMENT '发票代码',
    invoice_number          VARCHAR(50)   NULL                    COMMENT '发票号码',
    invoice_number_ocr      VARCHAR(50)   NULL                    COMMENT '发票号码 OCR',
    billing_date            VARCHAR(20)   NULL                    COMMENT '开票日期（原样留存，格式不统一）',
    billing_date_ocr        VARCHAR(20)   NULL                    COMMENT '开票日期 OCR',
    total_amount            VARCHAR(50)   NULL                    COMMENT '不含税金额',
    total_amount_ocr        VARCHAR(50)   NULL                    COMMENT '不含税金额 OCR',
    total_tax               VARCHAR(50)   NULL                    COMMENT '合计税额',
    amount_tax              VARCHAR(50)   NULL                    COMMENT '票面金额（价税合计）',
    amount_tax_ocr          VARCHAR(50)   NULL                    COMMENT '票面金额 OCR',
    amount_tax_cn           VARCHAR(100)  NULL                    COMMENT '票面金额大写',
    check_code              VARCHAR(100)  NULL                    COMMENT '校验码',
    machine_code            VARCHAR(50)   NULL                    COMMENT '机器编号/机器编码',
    password_field          TEXT          NULL                    COMMENT '密码区',
    tax_control_code        TEXT          NULL                    COMMENT '税控码',
    pri_invoice_code        VARCHAR(50)   NULL                    COMMENT '印刷发票代码',
    pri_invoice_number      VARCHAR(50)   NULL                    COMMENT '印刷发票号码',
    aft_invoice_code        VARCHAR(50)   NULL                    COMMENT '打印/机打发票代码',
    aft_invoice_number      VARCHAR(50)   NULL                    COMMENT '打印/机打发票号码',
    kind                    VARCHAR(50)   NULL                    COMMENT '消费类型（水电/餐饮/交通/医疗等）',
    category                VARCHAR(100)  NULL                    COMMENT '种类(机打票)/业务种类(银行回单)',
    amount                  VARCHAR(50)   NULL                    COMMENT '金额(火车退费票/医疗收费明细小计)',
    tax_rate                VARCHAR(20)   NULL                    COMMENT '增值税税率(百分比,如13%)',
    tax_rate_value          VARCHAR(20)   NULL                    COMMENT '增值税税率(数值,如0.09)',
    special_tag             VARCHAR(50)   NULL                    COMMENT '特殊标记(如"通行费")',
    page                    VARCHAR(20)   NULL                    COMMENT '页码(如"1/2")',
    state                   VARCHAR(50)   NULL                    COMMENT '发票状态',
    invoice_form            VARCHAR(50)   NULL                    COMMENT '发票联',
    invoice_form_num        VARCHAR(50)   NULL                    COMMENT '发票联次',
    receiver_name           VARCHAR(100)  NULL                    COMMENT '收款人',
    recheck_name            VARCHAR(100)  NULL                    COMMENT '复核人',
    drawer_name             VARCHAR(100)  NULL                    COMMENT '开票人',
    travel_tax              VARCHAR(50)   NULL                    COMMENT '车船税',
    supervision_seal        VARCHAR(10)   NULL                    COMMENT '监制章标识 0无 1有',
    red_seal                VARCHAR(10)   NULL                    COMMENT '红章标识 0无 1有',
    oil_mark                VARCHAR(10)   NULL                    COMMENT '成品油标志 0非 1是',
    inv_tax_sign            VARCHAR(10)   NULL                    COMMENT '代开标志 1自开 2代开',
    toll_sign               VARCHAR(10)   NULL                    COMMENT '通行费标志 0非 1是',
    seal_mark               VARCHAR(10)   NULL                    COMMENT '销售方(章)标志 0无 1有',
    jiangsu_toll_sign       VARCHAR(10)   NULL                    COMMENT '江苏通行费发票标识',
    hint_info               VARCHAR(255)  NULL                    COMMENT '提示信息',
    signature_flag          INT           NULL                    COMMENT '是否含签名 0/1',
    signature               JSON          NULL                    COMMENT '签名信息',
    pdf_page                VARCHAR(50)   NULL                    COMMENT '文件页码',
    blur_flag               VARCHAR(10)   NULL                    COMMENT '模糊标志 0否 1是',
    purchaser_name          VARCHAR(255)  NULL                    COMMENT '购方名称(银行回单=付款人;医疗=交款人)',
    purchaser_tax_no        VARCHAR(100)  NULL                    COMMENT '购方税号/统一社会信用代码',
    purchaser_address_phone VARCHAR(500)  NULL                    COMMENT '购方地址电话(合并地址+电话)',
    purchaser_bank          VARCHAR(500)  NULL                    COMMENT '购方开户行',
    sales_name              VARCHAR(255)  NULL                    COMMENT '销方名称(银行回单=收款人;医疗=收款单位)',
    sales_tax_no            VARCHAR(100)  NULL                    COMMENT '销方税号',
    sales_address_phone     VARCHAR(500)  NULL                    COMMENT '销方地址电话(合并地址+电话)',
    sales_bank_and_no       VARCHAR(500)  NULL                    COMMENT '销方开户行及账号(合并开户行+账号)',
    flights_json            JSON          NULL                    COMMENT '航班信息列表(JSON数组)',
    goods_json              JSON          NULL                    COMMENT '货物运输明细列表(JSON数组)',
    traveler_json           JSON          NULL                    COMMENT '旅客运输明细列表(JSON数组)',
    del_flag                TINYINT       NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by               VARCHAR(64)   NULL                    COMMENT '创建者',
    update_by               VARCHAR(64)   NULL                    COMMENT '更新者',
    create_time             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark                  VARCHAR(500)  NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_voucher_id (voucher_id),
    KEY idx_voucher_code (voucher_code),
    KEY idx_invoice_type (invoice_type),
    KEY idx_billing_date (billing_date),
    KEY idx_invoice_number (invoice_number),
    CONSTRAINT fk_invoice_voucher FOREIGN KEY (voucher_id) REFERENCES biz_voucher (id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '发票主表（跨类型通用核心字段）';

-- ----------------------------------------------------------------------------
-- 5. biz_invoice_detail 发票明细表（商品/服务行，随发票级联删除）
--    金额/税率等 VARCHAR 原样留存（接口存在 "*"、"13%" 等非标准格式）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_invoice_detail (
    id                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    invoice_id          BIGINT        NOT NULL                COMMENT '所属发票 ID（biz_invoice.id）',
    commodity_no        VARCHAR(100)  NULL                    COMMENT '项目编码',
    commodity_name      VARCHAR(500)  NULL                    COMMENT '商品/服务名称',
    specification_model VARCHAR(255)  NULL                    COMMENT '规格型号(通行费明细=车牌号)',
    unit                VARCHAR(50)   NULL                    COMMENT '单位(通行费明细=车辆类型)',
    quantity            VARCHAR(50)   NULL                    COMMENT '数量(通行费明细=通行日期起)',
    quantity_unit       VARCHAR(50)   NULL                    COMMENT '数量/单位(医疗明细)',
    unit_price          VARCHAR(50)   NULL                    COMMENT '单价(通行费明细=通行日期止)',
    amount              VARCHAR(50)   NULL                    COMMENT '金额',
    tax_rate            VARCHAR(20)   NULL                    COMMENT '税率(可能为*)',
    tax                 VARCHAR(50)   NULL                    COMMENT '税额(可能为*)',
    tax_percentage      VARCHAR(20)   NULL                    COMMENT '税率百分比(兼容taxPercentage/taxRatePercentage)',
    tax_paid            VARCHAR(50)   NULL                    COMMENT '完税价格(海关明细)',
    duty_no             VARCHAR(100)  NULL                    COMMENT '税号(海关明细)',
    standard            VARCHAR(50)   NULL                    COMMENT '收缴标准(非税明细)',
    voucher_number      VARCHAR(100)  NULL                    COMMENT '原凭证号(完税证明明细)',
    time_horizon        VARCHAR(100)  NULL                    COMMENT '税款所属时间',
    storage_date        VARCHAR(20)   NULL                    COMMENT '入(退)库日期',
    tax_categories      VARCHAR(100)  NULL                    COMMENT '税种',
    items_name          VARCHAR(100)  NULL                    COMMENT '品目名称',
    car_type            VARCHAR(50)   NULL                    COMMENT '车型(滴滴)',
    time_get_on         VARCHAR(20)   NULL                    COMMENT '上车时间(滴滴)',
    city                VARCHAR(100)  NULL                    COMMENT '城市(滴滴)',
    from_place          VARCHAR(100)  NULL                    COMMENT '出发地(滴滴)',
    to_place            VARCHAR(100)  NULL                    COMMENT '到达地(滴滴)',
    mileage             VARCHAR(50)   NULL                    COMMENT '里程(滴滴)',
    location_construction_service VARCHAR(500) NULL             COMMENT '建筑服务发生地',
    construction_name   VARCHAR(500)  NULL                    COMMENT '建筑项目名称',
    detail_json         JSON          NULL                    COMMENT '原始明细JSON冗余(可选)',
    del_flag            TINYINT       NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by           VARCHAR(64)   NULL                    COMMENT '创建者',
    update_by           VARCHAR(64)   NULL                    COMMENT '更新者',
    create_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark              VARCHAR(500)  NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_invoice_id (invoice_id),
    CONSTRAINT fk_detail_invoice FOREIGN KEY (invoice_id) REFERENCES biz_invoice (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '发票明细表（商品/服务行）';

-- ----------------------------------------------------------------------------
-- 6. biz_invoice_travel 交通出行类扩展表（20/22/24/25/26/27/28/31/39/61/62）
--    仅交通类发票写入；火车票/铁路电子客票的备注存 remark 列
-- ----------------------------------------------------------------------------
CREATE TABLE biz_invoice_travel (
    id                        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    invoice_id                BIGINT       NOT NULL                COMMENT '所属发票 ID（biz_invoice.id）',
    number                    VARCHAR(50)  NULL                    COMMENT '火车票编号',
    train_number              VARCHAR(50)  NULL                    COMMENT '车次(火车票/客运/铁路电子客票)',
    traveler_from             VARCHAR(100) NULL                    COMMENT '出发地/始发站/入口',
    traveler_to               VARCHAR(100) NULL                    COMMENT '到达地/终点站/出口',
    carriage_number           VARCHAR(50)  NULL                    COMMENT '车厢/座位号',
    seat                      VARCHAR(50)  NULL                    COMMENT '座位类型',
    seat_number               VARCHAR(50)  NULL                    COMMENT '席位',
    air_sign                  VARCHAR(50)  NULL                    COMMENT '空调特征',
    name                      VARCHAR(100) NULL                    COMMENT '乘客姓名/旅客姓名',
    user_card_no              VARCHAR(100) NULL                    COMMENT '证件号码/身份证号',
    salestation               VARCHAR(100) NULL                    COMMENT '发售车站',
    serial_number             VARCHAR(100) NULL                    COMMENT '序列号',
    ticket_check              VARCHAR(50)  NULL                    COMMENT '检票口',
    billing_time              VARCHAR(20)  NULL                    COMMENT '乘车/开票时间',
    fare                      VARCHAR(50)  NULL                    COMMENT '票价/车费',
    mileage                   VARCHAR(50)  NULL                    COMMENT '里程',
    time_geton                VARCHAR(20)  NULL                    COMMENT '上车时间(出租车/滴滴)',
    time_getoff               VARCHAR(20)  NULL                    COMMENT '下车时间',
    unit_price                VARCHAR(50)  NULL                    COMMENT '单价',
    license_plate             VARCHAR(50)  NULL                    COMMENT '车牌号',
    fuel_surcharge            VARCHAR(50)  NULL                    COMMENT '燃油附加费',
    other_surcharge           VARCHAR(50)  NULL                    COMMENT '另收附加费',
    other_callcharge          VARCHAR(50)  NULL                    COMMENT '另收电召费',
    bus_insurance             VARCHAR(50)  NULL                    COMMENT '汽车保险费',
    date_start                VARCHAR(20)  NULL                    COMMENT '行程开始时间(滴滴)',
    date_end                  VARCHAR(20)  NULL                    COMMENT '行程结束时间(滴滴)',
    phone                     VARCHAR(50)  NULL                    COMMENT '行程人手机号(滴滴)',
    provider                  VARCHAR(100) NULL                    COMMENT '服务商(滴滴)',
    province                  VARCHAR(100) NULL                    COMMENT '省份',
    city                      VARCHAR(100) NULL                    COMMENT '城市',
    currency_code             VARCHAR(50)  NULL                    COMMENT '币种',
    highway_flag              VARCHAR(10)  NULL                    COMMENT '高速标志',
    official_serial_number    VARCHAR(100) NULL                    COMMENT '公务机票序列号',
    print_number              VARCHAR(100) NULL                    COMMENT '印刷序号',
    endorsement               VARCHAR(255) NULL                    COMMENT '签注',
    caac_development_fund     VARCHAR(50)  NULL                    COMMENT '民航发展基金',
    tax_fee                   VARCHAR(50)  NULL                    COMMENT '其他税费',
    eticket_number            VARCHAR(100) NULL                    COMMENT '电子客票号码',
    insurance                 VARCHAR(50)  NULL                    COMMENT '保险费',
    agent_code                VARCHAR(100) NULL                    COMMENT '销售单位代号/销售网点代号',
    issue_by                  VARCHAR(255) NULL                    COMMENT '填开单位',
    international_flag        VARCHAR(20)  NULL                    COMMENT '国内国际标签',
    riding_date               VARCHAR(20)  NULL                    COMMENT '乘车日期(格式不统一)',
    riding_time               VARCHAR(20)  NULL                    COMMENT '乘车时间',
    original_invoice_number   VARCHAR(50)  NULL                    COMMENT '原发票号码',
    amount_tax_type           VARCHAR(50)  NULL                    COMMENT '金额类型',
    re_fund                   VARCHAR(50)  NULL                    COMMENT '退费',
    rebook                    VARCHAR(50)  NULL                    COMMENT '改签',
    replace                   VARCHAR(50)  NULL                    COMMENT '换开',
    cancle                    VARCHAR(50)  NULL                    COMMENT '红冲',
    del_flag                  TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by                 VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by                 VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark                    VARCHAR(500) NULL                    COMMENT '备注(火车票/铁路电子客票专用remark)',
    PRIMARY KEY (id),
    KEY idx_invoice_id (invoice_id),
    CONSTRAINT fk_travel_invoice FOREIGN KEY (invoice_id) REFERENCES biz_invoice (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '交通出行类发票扩展表';

-- ----------------------------------------------------------------------------
-- 7. biz_invoice_vehicle 机动车/二手车类扩展表（3/15/63/64/93/94）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_invoice_vehicle (
    id                      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    invoice_id              BIGINT       NOT NULL                COMMENT '所属发票 ID（biz_invoice.id）',
    id_card_no              VARCHAR(100) NULL                    COMMENT '购买方身份证号码/组织机构代码',
    vehicle_type            VARCHAR(100) NULL                    COMMENT '车辆类型',
    brand_model             VARCHAR(200) NULL                    COMMENT '厂牌型号',
    origin_place            VARCHAR(100) NULL                    COMMENT '产地',
    certificate_no          VARCHAR(100) NULL                    COMMENT '合格证号',
    import_certificate_no   VARCHAR(100) NULL                    COMMENT '进口证明书号',
    inspection_list_no      VARCHAR(100) NULL                    COMMENT '商检单号',
    engine_no               VARCHAR(100) NULL                    COMMENT '发动机号',
    vehicle_no              VARCHAR(100) NULL                    COMMENT '车辆识别代号/车架号码',
    car_number              VARCHAR(50)  NULL                    COMMENT '车牌照号',
    registration_number     VARCHAR(100) NULL                    COMMENT '登记证号',
    vehicle_place_name      VARCHAR(200) NULL                    COMMENT '转入地车辆车管所名称',
    used_car_name           VARCHAR(255) NULL                    COMMENT '二手车市场名称',
    used_car_tax_no         VARCHAR(100) NULL                    COMMENT '二手车市场纳税人识别号',
    used_car_address        VARCHAR(500) NULL                    COMMENT '二手车市场地址',
    used_car_bank           VARCHAR(500) NULL                    COMMENT '二手车市场开户银行及账号',
    used_car_phone          VARCHAR(50)  NULL                    COMMENT '二手车市场电话',
    auction_address         VARCHAR(500) NULL                    COMMENT '经营/拍卖单位地址',
    auction_name            VARCHAR(255) NULL                    COMMENT '经营/拍卖单位名称',
    auction_phone           VARCHAR(50)  NULL                    COMMENT '经营/拍卖单位电话',
    auction_tax_no          VARCHAR(100) NULL                    COMMENT '经营/拍卖单位纳税人识别号',
    auction_bank            VARCHAR(500) NULL                    COMMENT '经营/拍卖单位开户银行及账号',
    tax_authority_name      VARCHAR(255) NULL                    COMMENT '主管税务名称',
    tax_authority_no        VARCHAR(100) NULL                    COMMENT '主管税务机关',
    payment_voucher_no      VARCHAR(100) NULL                    COMMENT '完税凭证号码',
    tonnage                 VARCHAR(50)  NULL                    COMMENT '车船吨位',
    passengers_limited      VARCHAR(50)  NULL                    COMMENT '限乘人数',
    del_flag                TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by               VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by               VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark                  VARCHAR(500) NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_invoice_id (invoice_id),
    CONSTRAINT fk_vehicle_invoice FOREIGN KEY (invoice_id) REFERENCES biz_invoice (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '机动车/二手车类发票扩展表';

-- ----------------------------------------------------------------------------
-- 8. biz_invoice_medical 医疗票据类扩展表（38/43）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_invoice_medical (
    id                         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    invoice_id                 BIGINT       NOT NULL                COMMENT '所属发票 ID（biz_invoice.id）',
    ele_invoice_code           VARCHAR(100) NULL                    COMMENT '电子票据代码',
    ele_invoice_number         VARCHAR(100) NULL                    COMMENT '电子票据号码',
    payer                      VARCHAR(100) NULL                    COMMENT '交款人',
    outpatient_number          VARCHAR(100) NULL                    COMMENT '门诊号',
    visit_date                 VARCHAR(20)  NULL                    COMMENT '就诊日期',
    institution_type           VARCHAR(100) NULL                    COMMENT '医疗机构类型',
    insurance_type             VARCHAR(100) NULL                    COMMENT '医保类型',
    insurance_number           VARCHAR(100) NULL                    COMMENT '医保编号',
    gender                     VARCHAR(10)  NULL                    COMMENT '性别',
    insurance_fund_payment     VARCHAR(50)  NULL                    COMMENT '医保统筹基金支付',
    personal_payment           VARCHAR(50)  NULL                    COMMENT '个人账户支付',
    business_number            VARCHAR(100) NULL                    COMMENT '业务流水号',
    other_payment              VARCHAR(50)  NULL                    COMMENT '其他支付',
    personal_cash_payment      VARCHAR(50)  NULL                    COMMENT '个人现金支付',
    personal_self_payment      VARCHAR(50)  NULL                    COMMENT '个人自付',
    personal_self_funded       VARCHAR(50)  NULL                    COMMENT '个人自费',
    medical_record_number      VARCHAR(100) NULL                    COMMENT '病历号',
    admission_number           VARCHAR(100) NULL                    COMMENT '住院号',
    inpatient_department       VARCHAR(100) NULL                    COMMENT '住院科别',
    hospital_stay              VARCHAR(100) NULL                    COMMENT '住院时间',
    ele_medical                VARCHAR(10)  NULL                    COMMENT '电子医疗票标识 0否 1是',
    del_flag                   TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by                  VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by                  VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time                DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time                DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark                     VARCHAR(500) NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_invoice_id (invoice_id),
    CONSTRAINT fk_medical_invoice FOREIGN KEY (invoice_id) REFERENCES biz_invoice (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医疗票据类发票扩展表';

-- ----------------------------------------------------------------------------
-- 9. biz_invoice_customs 海关缴款书类扩展表（35）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_invoice_customs (
    id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    invoice_id           BIGINT       NOT NULL                COMMENT '所属发票 ID（biz_invoice.id）',
    customs_no           VARCHAR(100) NULL                    COMMENT '号码',
    revenue_sys          VARCHAR(100) NULL                    COMMENT '收入系统',
    revenue_org          VARCHAR(100) NULL                    COMMENT '收入机关',
    subject              VARCHAR(100) NULL                    COMMENT '科目',
    budget_level         VARCHAR(50)  NULL                    COMMENT '预算级次',
    exchequer            VARCHAR(100) NULL                    COMMENT '收入国库',
    corporate_name       VARCHAR(255) NULL                    COMMENT '缴款单位名称',
    corporate_account_no VARCHAR(100) NULL                    COMMENT '交款单位账号',
    corporate_bank       VARCHAR(255) NULL                    COMMENT '交款单位开户银行',
    corporate_no         VARCHAR(100) NULL                    COMMENT '申请单位编号',
    customs_bill_no      VARCHAR(100) NULL                    COMMENT '报关单编号',
    contract_no          VARCHAR(100) NULL                    COMMENT '合同(批文)号',
    transport            VARCHAR(100) NULL                    COMMENT '运输工具(号)',
    end_time             VARCHAR(100) NULL                    COMMENT '缴款期限',
    load_bill_no         VARCHAR(100) NULL                    COMMENT '提/装货单号',
    filling_org          VARCHAR(255) NULL                    COMMENT '填制单位',
    producer             VARCHAR(100) NULL                    COMMENT '制单人',
    reunite_name         VARCHAR(100) NULL                    COMMENT '复核人(海关专用)',
    del_flag             TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by            VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by            VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark               VARCHAR(500) NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_invoice_id (invoice_id),
    CONSTRAINT fk_customs_invoice FOREIGN KEY (invoice_id) REFERENCES biz_invoice (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '海关缴款书类发票扩展表';

-- ----------------------------------------------------------------------------
-- 10. biz_invoice_bank 银行回单类扩展表（42）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_invoice_bank (
    id                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    invoice_id          BIGINT       NOT NULL                COMMENT '所属发票 ID（biz_invoice.id）',
    purchaser_no        VARCHAR(100) NULL                    COMMENT '付款人账号',
    purchaser_bank_add  VARCHAR(255) NULL                    COMMENT '付款人开户行',
    sales_no            VARCHAR(100) NULL                    COMMENT '收款人账号',
    sales_bank          VARCHAR(255) NULL                    COMMENT '收款人开户行',
    summary             VARCHAR(255) NULL                    COMMENT '摘要',
    serial_no           VARCHAR(100) NULL                    COMMENT '流水号',
    receipt_no          VARCHAR(100) NULL                    COMMENT '回单编号',
    used                VARCHAR(255) NULL                    COMMENT '用途',
    bank_name           VARCHAR(255) NULL                    COMMENT '银行名称',
    postscript          VARCHAR(255) NULL                    COMMENT '附言',
    trade_name          VARCHAR(255) NULL                    COMMENT '交易名称',
    bank_category       VARCHAR(100) NULL                    COMMENT '回单种类',
    del_flag            TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by           VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by           VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark              VARCHAR(500) NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_invoice_id (invoice_id),
    CONSTRAINT fk_bank_invoice FOREIGN KEY (invoice_id) REFERENCES biz_invoice (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行回单类发票扩展表';

-- ----------------------------------------------------------------------------
-- 11. biz_invoice_nontax 非税/财政/完税/通用电子类扩展表（34/36/37/40）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_invoice_nontax (
    id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    invoice_id           BIGINT       NOT NULL                COMMENT '所属发票 ID（biz_invoice.id）',
    collection_user      VARCHAR(100) NULL                    COMMENT '取票用户(通用电子36)',
    pay_info             VARCHAR(255) NULL                    COMMENT '支付信息(通用电子36)',
    merchant_no          VARCHAR(100) NULL                    COMMENT '商户号(通用电子36)',
    order_no             VARCHAR(100) NULL                    COMMENT '订单号(通用电子36)',
    payment_code         VARCHAR(100) NULL                    COMMENT '缴款码(非税40)',
    receiving_code       VARCHAR(100) NULL                    COMMENT '执收单位编码(非税40)',
    receiving_name       VARCHAR(255) NULL                    COMMENT '执收单位名称(非税40)',
    payer_name           VARCHAR(255) NULL                    COMMENT '付款人全称(非税40)',
    payer_number         VARCHAR(100) NULL                    COMMENT '付款人账号(非税40)',
    payer_bank           VARCHAR(255) NULL                    COMMENT '付款人开户行(非税40)',
    payee_name           VARCHAR(255) NULL                    COMMENT '收款人全称(非税40)',
    payee_number         VARCHAR(100) NULL                    COMMENT '收款人账号(非税40)',
    payee_bank           VARCHAR(255) NULL                    COMMENT '收款人开户行(非税40)',
    seal_receiving       VARCHAR(100) NULL                    COMMENT '执收单位(盖章)(非税40)',
    ele_nontax_invoice   VARCHAR(10)  NULL                    COMMENT '电子票标识 0纸票 1电子票(财政34)',
    voucher_number       VARCHAR(100) NULL                    COMMENT '原凭证号(完税37)',
    auction_tax_name     VARCHAR(255) NULL                    COMMENT '纳税人名称(完税37)',
    auction_tax_no       VARCHAR(100) NULL                    COMMENT '纳税人识别号(完税37)',
    tax_authority_no     VARCHAR(100) NULL                    COMMENT '税务机关(完税37)',
    del_flag             TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by            VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by            VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark               VARCHAR(500) NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_invoice_id (invoice_id),
    CONSTRAINT fk_nontax_invoice FOREIGN KEY (invoice_id) REFERENCES biz_invoice (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '非税/财政/完税/通用电子类发票扩展表';

-- ----------------------------------------------------------------------------
-- 7. biz_archive_file 档案文件表（影像/OCR 结果/压缩包；file_hash 去重）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_archive_file (
    id           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    archive_id   BIGINT        NOT NULL                COMMENT '所属档案 ID（biz_archive.id）',
    file_name    VARCHAR(255)  NOT NULL                COMMENT '原始文件名',
    file_type    TINYINT       NOT NULL DEFAULT 1      COMMENT '文件类型：1-影像图 2-OCR结果 3-压缩包 4-其他',
    storage_path VARCHAR(512)  NOT NULL                COMMENT '存储相对路径（按 年/月/档号 分目录）',
    file_size    BIGINT        NOT NULL DEFAULT 0      COMMENT '文件大小（字节）',
    file_hash    CHAR(64)      NULL                    COMMENT 'SHA-256 文件哈希（去重/完整性校验）',
    ocr_status   TINYINT       NOT NULL DEFAULT 0      COMMENT '识别状态：0-待识别 1-识别中 2-成功 3-失败',
    sort_no      INT           NOT NULL DEFAULT 0      COMMENT '排序号',
    del_flag     TINYINT       NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by    VARCHAR(64)   NULL                    COMMENT '创建者',
    update_by    VARCHAR(64)   NULL                    COMMENT '更新者',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_archive_id (archive_id),
    KEY idx_file_hash (file_hash),
    CONSTRAINT fk_archive_file_archive FOREIGN KEY (archive_id) REFERENCES biz_archive (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '档案文件表';

-- ----------------------------------------------------------------------------
-- 8. biz_ocr_task OCR 识别任务表（任务流水，不软删）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_ocr_task (
    id           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    task_no      VARCHAR(32)   NOT NULL                COMMENT '任务编号（唯一）',
    file_id      BIGINT        NOT NULL                COMMENT '来源文件（biz_archive_file.id）',
    source_type  TINYINT       NOT NULL DEFAULT 1      COMMENT '来源类型：1-凭证zip 2-单张图片',
    status       TINYINT       NOT NULL DEFAULT 0      COMMENT '状态：0-排队 1-识别中 2-成功 3-失败',
    ocr_engine   VARCHAR(32)   NULL                    COMMENT '识别引擎（如 paddleocr/tesseract）',
    ocr_version  VARCHAR(32)   NULL                    COMMENT '引擎/模型版本',
    result_json  JSON          NULL                    COMMENT '识别结果（结构化 JSON，含置信度）',
    error_msg    VARCHAR(1000) NULL                    COMMENT '失败原因',
    cost_ms      INT           NULL                    COMMENT '识别耗时（毫秒）',
    retry_count  INT           NOT NULL DEFAULT 0      COMMENT '重试次数',
    finish_time  DATETIME      NULL                    COMMENT '完成时间',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_no (task_no),
    KEY idx_file_id (file_id),
    KEY idx_status (status),
    CONSTRAINT fk_ocr_task_file FOREIGN KEY (file_id) REFERENCES biz_archive_file (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OCR 识别任务表';

-- ----------------------------------------------------------------------------
-- 9. biz_borrow 借阅登记表（状态机：0待审批 1已批准/借出 2已归还 3已驳回 4已逾期）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_borrow (
    id                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    borrow_no          VARCHAR(32)  NOT NULL                COMMENT '借阅单号（唯一，如 JY20260820-0001）',
    archive_id         BIGINT       NOT NULL                COMMENT '被借档案 ID（biz_archive.id）',
    borrower_user_id   BIGINT       NOT NULL                COMMENT '借阅人（sys_user.id）',
    borrow_reason      VARCHAR(500) NULL                    COMMENT '借阅事由',
    borrow_date        DATE         NOT NULL                COMMENT '借出日期',
    plan_return_date   DATE         NULL                    COMMENT '计划归还日期',
    actual_return_date DATE         NULL                    COMMENT '实际归还日期',
    status             TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0-待审批 1-已批准(借出) 2-已归还 3-已驳回 4-已逾期',
    approve_user_id    BIGINT       NULL                    COMMENT '审批人（sys_user.id）',
    approve_time       DATETIME     NULL                    COMMENT '审批时间',
    approve_comment    VARCHAR(500) NULL                    COMMENT '审批意见',
    del_flag           TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by          VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by          VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark             VARCHAR(500) NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_borrow_no (borrow_no),
    KEY idx_archive_id (archive_id),
    KEY idx_borrower (borrower_user_id),
    KEY idx_status (status),
    CONSTRAINT fk_borrow_archive FOREIGN KEY (archive_id) REFERENCES biz_archive (id) ON DELETE RESTRICT,
    CONSTRAINT fk_borrow_user FOREIGN KEY (borrower_user_id) REFERENCES sys_user (id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '借阅登记表';

-- ----------------------------------------------------------------------------
-- 10. biz_destruction 档案销毁登记表（双审批留痕，监销人 witness_user_id）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_destruction (
    id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    destruction_no   VARCHAR(32)  NOT NULL                COMMENT '销毁单号（唯一）',
    archive_id       BIGINT       NOT NULL                COMMENT '待销毁档案 ID（biz_archive.id）',
    apply_user_id    BIGINT       NOT NULL                COMMENT '申请人（sys_user.id）',
    apply_reason     VARCHAR(500) NULL                    COMMENT '销毁理由（如保管期满）',
    apply_time       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    approve_status   TINYINT      NOT NULL DEFAULT 0      COMMENT '审批状态：0-待审批 1-已批准 2-已驳回',
    approve_user_id  BIGINT       NULL                    COMMENT '审批人（sys_user.id）',
    approve_time     DATETIME     NULL                    COMMENT '审批时间',
    approve_comment  VARCHAR(500) NULL                    COMMENT '审批意见',
    destroy_time     DATETIME     NULL                    COMMENT '实际销毁时间',
    destroy_method   VARCHAR(100) NULL                    COMMENT '销毁方式（碎纸/焚烧/数据清除）',
    witness_user_id  BIGINT       NULL                    COMMENT '监销人（两人监销制，sys_user.id）',
    del_flag         TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-已删除',
    create_by        VARCHAR(64)  NULL                    COMMENT '创建者',
    update_by        VARCHAR(64)  NULL                    COMMENT '更新者',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark           VARCHAR(500) NULL                    COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_destruction_no (destruction_no),
    KEY idx_archive_id (archive_id),
    CONSTRAINT fk_destruction_archive FOREIGN KEY (archive_id) REFERENCES biz_archive (id) ON DELETE RESTRICT,
    CONSTRAINT fk_destruction_user FOREIGN KEY (apply_user_id) REFERENCES sys_user (id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '档案销毁登记表';

-- ----------------------------------------------------------------------------
-- 11. biz_approval_record 审批记录表（通用审批流水，支撑多级审批扩展，不软删）
-- ----------------------------------------------------------------------------
CREATE TABLE biz_approval_record (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    biz_type        TINYINT      NOT NULL                COMMENT '业务类型：1-借阅 2-销毁 3-移交',
    biz_id          BIGINT       NOT NULL                COMMENT '业务单 ID（如 biz_borrow.id / biz_destruction.id）',
    approve_user_id BIGINT       NOT NULL                COMMENT '审批人（sys_user.id）',
    approve_action  TINYINT      NOT NULL                COMMENT '动作：1-同意 2-驳回',
    approve_comment VARCHAR(500) NULL                    COMMENT '审批意见',
    approve_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_biz (biz_type, biz_id),
    KEY idx_approve_user (approve_user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '审批记录表（通用）';
