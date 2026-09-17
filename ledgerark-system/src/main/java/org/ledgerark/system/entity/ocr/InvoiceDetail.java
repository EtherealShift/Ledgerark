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
 * 发票明细子表（biz_invoice_detail）
 * <p>
 * 所有含 invoiceLists 的票据类型写入；一票多行。
 * 金额/税率等一律 String 原样存储（接口存在 "*"、"13%" 等非标准格式）。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_invoice_detail")
public class InvoiceDetail extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    // 逻辑删除：0-正常 1-已删除
    @TableLogic
    private Integer delFlag;

    // 所属发票 ID（biz_invoice.id）
    private Long invoiceId;

    // 项目编码
    private String commodityNo;

    // 项目/货物名称
    private String commodityName;

    // 规格型号（通行费明细=车牌号）
    private String specificationModel;

    // 单位（通行费明细=车辆类型）
    private String unit;

    // 数量（通行费明细=通行日期起）
    private String quantity;

    // 数量/单位（医疗明细，如 "1项"）
    private String quantityUnit;

    // 单价（通行费明细=通行日期止）
    private String unitPrice;

    // 金额
    private String amount;

    // 税率（可能为 "*"）
    private String taxRate;

    // 税额（可能为 "*"）
    private String tax;

    // 税率百分比（兼容接口 taxPercentage 与 taxRatePercentage 两个键）
    private String taxPercentage;

    // 完税价格（海关明细）
    private String taxPaid;

    // 税号（海关明细）
    private String dutyNo;

    // 收缴标准（非税明细）
    private String standard;

    // 原凭证号（完税证明明细）
    private String voucherNumber;

    // 税款所属时间
    private String timeHorizon;

    // 入(退)库日期
    private String storageDate;

    // 税种
    private String taxCategories;

    // 品目名称
    private String itemsName;

    // 车型（滴滴明细）
    private String carType;

    // 上车时间（滴滴明细）
    private String timeGetOn;

    // 城市（滴滴明细）
    private String city;

    // 出发地（滴滴明细）
    private String fromPlace;

    // 到达地（滴滴明细）
    private String toPlace;

    // 里程（滴滴明细）
    private String mileage;

    // 建筑服务发生地
    private String locationConstructionService;

    // 建筑项目名称
    private String constructionName;

    // 原始明细 JSON 冗余（可选，便于类型特有字段不丢失）
    private String detailJson;
}
