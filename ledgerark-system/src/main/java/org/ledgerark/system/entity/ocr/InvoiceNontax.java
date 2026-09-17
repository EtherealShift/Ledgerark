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
 * 非税/财政/完税/通用电子类发票扩展表（biz_invoice_nontax）
 * <p>
 * 适用类型：34 财政电子票据 / 36 通用电子发票 / 37 完税证明 / 40 非税收入一般缴款书(电子)。
 * 仅当发票类型命中该类时写入。
 * <p>
 * 注：auctionTaxNo / taxAuthorityNo 与 {@link InvoiceVehicle} 中含义不同（本表=完税证明的
 * 纳税人识别号/税务机关），各自存储。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_invoice_nontax")
public class InvoiceNontax extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    // 逻辑删除：0-正常 1-已删除
    @TableLogic
    private Integer delFlag;

    // 所属发票 ID（biz_invoice.id）
    private Long invoiceId;

    // ==================== 通用电子发票（36） ====================
    // 取票用户
    private String collectionUser;

    // 支付信息
    private String payInfo;

    // 商户号
    private String merchantNo;

    // 订单号
    private String orderNo;

    // ==================== 非税收入缴款书（40） ====================
    // 缴款码
    private String paymentCode;

    // 执收单位编码
    private String receivingCode;

    // 执收单位名称
    private String receivingName;

    // 付款人全称
    private String payerName;

    // 付款人账号
    private String payerNumber;

    // 付款人开户行
    private String payerBank;

    // 收款人全称
    private String payeeName;

    // 收款人账号
    private String payeeNumber;

    // 收款人开户行
    private String payeeBank;

    // 执收单位(盖章)
    private String sealReceiving;

    // ==================== 财政电子票据（34） ====================
    // 电子票标识 0纸票 1电子票
    private String eleNontaxInvoice;

    // ==================== 完税证明（37） ====================
    // 原凭证号
    private String voucherNumber;

    // 纳税人名称
    private String auctionTaxName;

    // 纳税人识别号
    private String auctionTaxNo;

    // 税务机关
    private String taxAuthorityNo;
}
