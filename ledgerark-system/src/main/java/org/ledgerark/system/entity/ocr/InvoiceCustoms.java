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
 * 海关专用缴款书类发票扩展表（biz_invoice_customs）
 * <p>
 * 适用类型：35 海关专用缴款书。仅当发票类型命中海关类时写入。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_invoice_customs")
public class InvoiceCustoms extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    // 逻辑删除：0-正常 1-已删除
    @TableLogic
    private Integer delFlag;

    // 所属发票 ID（biz_invoice.id）
    private Long invoiceId;

    // 号码
    private String customsNo;

    // 收入系统
    private String revenueSys;

    // 收入机关
    private String revenueOrg;

    // 科目
    private String subject;

    // 预算级次
    private String budgetLevel;

    // 收入国库
    private String exchequer;

    // 缴款单位名称
    private String corporateName;

    // 交款单位账号
    private String corporateAccountNo;

    // 交款单位开户银行
    private String corporateBank;

    // 申请单位编号
    private String corporateNo;

    // 报关单编号
    private String customsBillNo;

    // 合同(批文)号
    private String contractNo;

    // 运输工具(号)
    private String transport;

    // 缴款期限
    private String endTime;

    // 提/装货单号
    private String loadBillNo;

    // 填制单位
    private String fillingOrg;

    // 制单人
    private String producer;

    // 复核人（海关专用，与主表 recheckName 区分）
    private String reuniteName;
}
