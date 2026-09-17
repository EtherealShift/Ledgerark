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
 * 医疗票据类发票扩展表（biz_invoice_medical）
 * <p>
 * 适用类型：38 医疗票据 / 43 医疗收费明细(电子)。仅当发票类型命中医疗类时写入。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_invoice_medical")
public class InvoiceMedical extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    // 逻辑删除：0-正常 1-已删除
    @TableLogic
    private Integer delFlag;

    // 所属发票 ID（biz_invoice.id）
    private Long invoiceId;

    // 电子票据代码
    private String eleInvoiceCode;

    // 电子票据号码
    private String eleInvoiceNumber;

    // 交款人
    private String payer;

    // 门诊号
    private String outpatientNumber;

    // 就诊日期
    private String visitDate;

    // 医疗机构类型
    private String institutionType;

    // 医保类型
    private String insuranceType;

    // 医保编号
    private String insuranceNumber;

    // 性别
    private String gender;

    // 医保统筹基金支付
    private String insuranceFundPayment;

    // 个人账户支付
    private String personalPayment;

    // 业务流水号
    private String businessNumber;

    // 其他支付
    private String otherPayment;

    // 个人现金支付
    private String personalCashPayment;

    // 个人自付
    private String personalSelfPayment;

    // 个人自费
    private String personalSelfFunded;

    // 病历号
    private String medicalRecordNumber;

    // 住院号
    private String admissionNumber;

    // 住院科别
    private String inpatientDepartment;

    // 住院时间
    private String hospitalStay;

    // 电子医疗票标识 0否 1是
    private String eleMedical;
}
