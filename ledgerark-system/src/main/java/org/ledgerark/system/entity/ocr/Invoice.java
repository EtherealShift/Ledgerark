package org.ledgerark.system.entity.ocr;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ledgerark.common.entity.base.BaseEntity;

import java.io.Serial;

/**
 * 发票识别结果主表（biz_invoice）
 * <p>
 * 只存跨类型通用核心字段；类型特有字段按 invoiceType 存入对应扩展表：
 * {@link InvoiceTravel}（交通）/ {@link InvoiceVehicle}（机动车）/
 * {@link InvoiceMedical}（医疗）/ {@link InvoiceCustoms}（海关）/
 * {@link InvoiceBank}（银行回单）/ {@link InvoiceNontax}（非税/财政/完税/通用电子），
 * 发票明细行存 {@link InvoiceDetail}。
 * <p>
 * 设计约定：
 * 1. 金额/日期/税率等一律 String 原样存储（OCR 接口存在 "13%"、"*"、"2019年5月14日" 等非标准格式）；
 * 2. 低频明细（航班/货物运输/旅客运输）以 JSON 文本存于 flightsJson / goodsJson / travelerJson；
 * 3. 识别附加信息（orientation/coord/region/cutUrlImage/confidence 等）不入库。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_invoice")
public class Invoice extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    // 逻辑删除：0-正常 1-已删除
    @TableLogic
    private Integer delFlag;

    // ==================== 凭证/归档关联 ====================
    // 所属记账凭证 ID（biz_voucher.id），可空=尚未挂接凭证
    private Long voucherId;

    // 凭证编号 YYYY-mm-XXXX
    private String voucherCode;

    // 凭证年份
    private String voucherYear;

    // 凭证月份
    private String voucherMonth;

    // ==================== 发票基本通用信息 ====================
    // 发票类型代码（接口 invoiceType 原始值，如 1/20/91）
    private Integer invoiceTypeCode;

    // 发票类型名称（如"增值税专用发票"，由 invoiceType 映射）
    private String invoiceType;

    // 发票标题
    private String title;

    // 所属行政区名称
    private String administrativeDivisionName;

    // 全电发票号码
    private String fullInvoiceNumber;

    // 发票代码
    private String invoiceCode;

    // 发票号码
    private String invoiceNumber;

    // 发票号码 OCR
    private String invoiceNumberOcr;

    // 开票日期（格式不统一，如 "2023-04-28" / "2019年5月14日"）
    private String billingDate;

    // 开票日期 OCR
    private String billingDateOcr;

    // 不含税金额
    private String totalAmount;

    // 不含税金额 OCR
    private String totalAmountOcr;

    // 合计税额
    private String totalTax;

    // 票面金额（价税合计）
    private String amountTax;

    // 票面金额 OCR
    private String amountTaxOcr;

    // 票面金额（大写）
    private String amountTaxCn;

    // 校验码
    private String checkCode;

    // 机器编号/机器编码
    private String machineCode;

    // 密码区
    private String passwordField;

    // 税控码
    private String taxControlCode;

    // 印刷发票代码
    private String priInvoiceCode;

    // 印刷发票号码
    private String priInvoiceNumber;

    // 打印/机打发票代码
    private String aftInvoiceCode;

    // 打印/机打发票号码
    private String aftInvoiceNumber;

    // 消费类型（水电/餐饮/交通/医疗等）
    private String kind;

    // 种类（机打票）/业务种类（银行回单），跨类共用
    private String category;

    // 金额（火车退费票金额；医疗收费明细小计），跨类共用
    private String amount;

    // 增值税税率（百分比，如 "13%"），机动车/铁路电子共用
    private String taxRate;

    // 增值税税率（数值，如 "0.09"）
    private String taxRateValue;

    // 特殊标记（如 "通行费"）
    private String specialTag;

    // 页码（如 "1/2"）
    private String page;

    // 发票状态
    private String state;

    // 发票联
    private String invoiceForm;

    // 发票联次
    private String invoiceFormNum;

    // 收款人
    private String receiverName;

    // 复核人
    private String recheckName;

    // 开票人
    private String drawerName;

    // 车船税
    private String travelTax;

    // 监制章标识 0无 1有
    private String supervisionSeal;

    // 红章标识 0无 1有
    private String redSeal;

    // 成品油标志 0非 1是
    private String oilMark;

    // 代开标志 1自开 2代开
    private String invTaxSign;

    // 通行费标志 0非 1是
    private String tollSign;

    // 销售方(章)标志 0无 1有
    private String sealMark;

    // 江苏通行费发票标识
    private String jiangsuTollSign;

    // 提示信息
    private String hintInfo;

    // 是否含签名 0/1
    private Integer signatureFlag;

    // 签名信息（JSON 文本）
    private String signature;

    // 文件页码
    private String pdfPage;

    // 模糊标志 0否 1是
    private String blurFlag;

    // ==================== 购方信息（通用合并字段） ====================
    // 购方名称（银行回单=付款人名称；医疗=交款人）
    private String purchaserName;

    // 购方税号/统一社会信用代码
    private String purchaserTaxNo;

    // 购方地址电话（合并 purchaserAddress + purchaserPhone）
    private String purchaserAddressPhone;

    // 购方开户行
    private String purchaserBank;

    // ==================== 销方信息（通用合并字段） ====================
    // 销方名称（银行回单=收款人名称；医疗=收款单位）
    private String salesName;

    // 销方税号
    private String salesTaxNo;

    // 销方地址电话（合并 salesAddress + salesPhone）
    private String salesAddressPhone;

    // 销方开户行及账号（合并 salesBankNo + salesBank）
    private String salesBankAndNo;

    // ==================== 低频明细 JSON 列 ====================
    // 航班信息列表（JSON 数组，元素见 Invoice.FlightItem 结构）
    private String flightsJson;

    // 货物运输明细列表（JSON 数组，元素：transportationType/travelerID/travelerFrom/travelerTo/goodsName）
    private String goodsJson;

    // 旅客运输明细列表（JSON 数组，元素：travelerName/travelerID/travelerDate/travelerFrom/travelerTo/travelerLevel/transportationType）
    private String travelerJson;
}
