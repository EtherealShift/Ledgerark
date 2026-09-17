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
 * 银行回单类发票扩展表（biz_invoice_bank）
 * <p>
 * 适用类型：42 银行回单。仅当发票类型命中银行回单时写入。
 * 注：付款人/收款人名称复用主表 purchaserName / salesName。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_invoice_bank")
public class InvoiceBank extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    // 逻辑删除：0-正常 1-已删除
    @TableLogic
    private Integer delFlag;

    // 所属发票 ID（biz_invoice.id）
    private Long invoiceId;

    // 付款人账号
    private String purchaserNo;

    // 付款人开户行
    private String purchaserBankAdd;

    // 收款人账号
    private String salesNo;

    // 收款人开户行
    private String salesBank;

    // 摘要
    private String summary;

    // 流水号
    private String serialNo;

    // 回单编号
    private String receiptNo;

    // 用途
    private String used;

    // 银行名称
    private String bankName;

    // 附言
    private String postscript;

    // 交易名称
    private String tradeName;

    // 回单种类
    private String bankCategory;
}
