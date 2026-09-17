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
 * 交通出行类发票扩展表（biz_invoice_travel）
 * <p>
 * 适用类型：20 火车票 / 22 船票 / 24 通用机打票 / 25 出租车 / 26 客运汽车票 /
 * 27 航空行程单 / 28 过路费 / 31 滴滴行程单 / 39 火车退费票 / 61 电子航空 / 62 电子铁路。
 * 仅当发票类型命中交通类时写入；备注字段复用 BaseEntity.remark。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_invoice_travel")
public class InvoiceTravel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    // 逻辑删除：0-正常 1-已删除
    @TableLogic
    private Integer delFlag;

    // 所属发票 ID（biz_invoice.id）
    private Long invoiceId;

    // 火车票编号
    private String number;

    // 车次（火车票/客运/铁路电子客票）
    private String trainNumber;

    // 出发地/始发站/入口
    private String travelerFrom;

    // 到达地/终点站/出口
    private String travelerTo;

    // 车厢/座位号
    private String carriageNumber;

    // 座位类型（二等座/软卧/Y 等）
    private String seat;

    // 席位
    private String seatNumber;

    // 空调特征（如"新空调"）
    private String airSign;

    // 乘客姓名/旅客姓名
    private String name;

    // 证件号码/身份证号
    private String userCardNo;

    // 发售车站
    private String salestation;

    // 序列号
    private String serialNumber;

    // 检票口
    private String ticketCheck;

    // 乘车/开票时间
    private String billingTime;

    // 票价/车费
    private String fare;

    // 里程
    private String mileage;

    // 上车时间（出租车/滴滴）
    private String timeGeton;

    // 下车时间
    private String timeGetoff;

    // 单价
    private String unitPrice;

    // 车牌号
    private String licensePlate;

    // 燃油附加费
    private String fuelSurcharge;

    // 另收附加费
    private String otherSurcharge;

    // 另收电召费
    private String otherCallcharge;

    // 汽车保险费
    private String busInsurance;

    // 行程开始时间（滴滴）
    private String dateStart;

    // 行程结束时间（滴滴）
    private String dateEnd;

    // 行程人手机号（滴滴）
    private String phone;

    // 服务商（滴滴）
    private String provider;

    // 省份
    private String province;

    // 城市
    private String city;

    // 币种
    private String currencyCode;

    // 高速标志
    private String highwayFlag;

    // ==================== 航空行程单专用（27/61） ====================
    // 公务机票序列号
    private String officialSerialNumber;

    // 印刷序号
    private String printNumber;

    // 签注
    private String endorsement;

    // 民航发展基金
    private String caacDevelopmentFund;

    // 其他税费
    private String taxFee;

    // 电子客票号码
    private String eticketNumber;

    // 保险费
    private String insurance;

    // 销售单位代号/销售网点代号
    private String agentCode;

    // 填开单位
    private String issueBy;

    // 国内国际标签
    private String internationalFlag;

    // ==================== 铁路电子客票专用（62） ====================
    // 乘车日期（格式不统一，如"2022年03月01日"）
    private String ridingDate;

    // 乘车时间
    private String ridingTime;

    // 原发票号码
    private String originalInvoiceNumber;

    // 金额类型
    private String amountTaxType;

    // 退费
    private String reFund;

    // 改签
    private String rebook;

    // 换开
    private String replace;

    // 红冲
    private String cancle;
}
