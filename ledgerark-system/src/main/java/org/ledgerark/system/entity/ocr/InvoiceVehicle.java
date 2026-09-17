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
 * 机动车/二手车类发票扩展表（biz_invoice_vehicle）
 * <p>
 * 适用类型：3 机动车统一销售发票 / 15 二手车统一销售发票 /
 * 63 电子发票(机动车) / 64 电子发票(二手车) / 93 全电纸票(机动车) / 94 全电纸票(二手车)。
 * 仅当发票类型命中机动车类时写入。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_invoice_vehicle")
public class InvoiceVehicle extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    // 逻辑删除：0-正常 1-已删除
    @TableLogic
    private Integer delFlag;

    // 所属发票 ID（biz_invoice.id）
    private Long invoiceId;

    // 购买方身份证号码/组织机构代码
    private String idCardNo;

    // 车辆类型
    private String vehicleType;

    // 厂牌型号
    private String brandModel;

    // 产地
    private String originPlace;

    // 合格证号
    private String certificateNo;

    // 进口证明书号
    private String importCertificateNo;

    // 商检单号
    private String inspectionListNo;

    // 发动机号
    private String engineNo;

    // 车辆识别代号/车架号码
    private String vehicleNo;

    // 车牌照号
    private String carNumber;

    // 登记证号
    private String registrationNumber;

    // 转入地车辆车管所名称
    private String vehiclePlaceName;

    // 二手车市场名称
    private String usedCarName;

    // 二手车市场纳税人识别号
    private String usedCarTaxNo;

    // 二手车市场地址
    private String usedCarAddress;

    // 二手车市场开户银行及账号
    private String usedCarBank;

    // 二手车市场电话
    private String usedCarPhone;

    // 经营/拍卖单位地址
    private String auctionAddress;

    // 经营/拍卖单位名称
    private String auctionName;

    // 经营/拍卖单位电话
    private String auctionPhone;

    // 经营/拍卖单位纳税人识别号
    private String auctionTaxNo;

    // 经营/拍卖单位开户银行及账号
    private String auctionBank;

    // 主管税务名称
    private String taxAuthorityName;

    // 主管税务机关
    private String taxAuthorityNo;

    // 完税凭证号码
    private String paymentVoucherNo;

    // 车船吨位
    private String tonnage;

    // 限乘人数
    private String passengersLimited;
}
