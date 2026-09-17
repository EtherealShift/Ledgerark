package org.ledgerark.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 发票类型（对应 OCR 接口 invoiceType 代码，见接口文档"一、发票类型"）
 * <p>
 * 与旧版 Python 项目 ProcessConfigModel.invoice_types 一致；
 * 未知代码由 {@link #fromCode(Integer)} 返回 null，调用方自行兜底。
 */
@Getter
@AllArgsConstructor
public enum InvoiceType {

    VAT_SPECIAL(1, "增值税专用发票"),
    MOTOR_VEHICLE(3, "机动车统一销售发票"),
    VAT_NORMAL(4, "增值税普通发票"),
    VAT_ELECTRONIC_SPECIAL(8, "增值税电子专用发票"),
    ELECTRONIC_SPECIAL(9, "电子发票(增值税专用发票)"),
    VAT_NORMAL_ELECTRONIC(10, "增值税普通发票(电子)"),
    VAT_NORMAL_ROLL(11, "增值税普通发票(卷式)"),
    VAT_NORMAL_TOLL(14, "增值税普通发票(通行费)"),
    USED_CAR(15, "二手车统一销售发票"),
    TRAIN(20, "火车票"),
    BLOCKCHAIN(21, "区块链发票"),
    SHIP(22, "船票"),
    FIXED_AMOUNT(23, "定额发票"),
    GENERAL_MACHINE_PRINTED(24, "通用机打发票"),
    TAXI(25, "出租车发票"),
    COACH(26, "客运汽车票"),
    AIR_ITINERARY(27, "航空运输电子客票行程单"),
    TOLL(28, "过路费发票"),
    DIDI(31, "滴滴出行行程单"),
    SALES_LIST(33, "销货清单"),
    FISCAL(34, "财政电子票据"),
    CUSTOMS(35, "海关专用缴款书"),
    GENERAL_ELECTRONIC(36, "通用电子发票"),
    TAX_CERTIFICATE(37, "完税证明"),
    MEDICAL(38, "医疗票据"),
    TRAIN_REFUND(39, "火车退费票"),
    NONTAX(40, "非税收入一般缴款书(电子)"),
    VEHICLE_TOLL(41, "车辆通行费通用(电子)"),
    BANK_RECEIPT(42, "银行回单"),
    MEDICAL_DETAIL(43, "医疗收费明细(电子)"),
    ELECTRONIC_AIR(61, "电子发票(航空运输电子客票行程单)"),
    ELECTRONIC_RAIL(62, "电子发票(铁路电子客票)"),
    ELECTRONIC_MOTOR_VEHICLE(63, "电子发票(机动车销售统一发票)"),
    ELECTRONIC_USED_CAR(64, "电子发票(二手车销售统一发票)"),
    ELECTRONIC_NORMAL_TOLL(72, "电子发票(普通发票)-通行费"),
    ELECTRONIC_NORMAL(83, "电子发票(普通发票)"),
    FULL_ELECTRONIC_SPECIAL(91, "全电纸票(增值税专用发票)"),
    FULL_ELECTRONIC_NORMAL(92, "全电纸票(增值税普通发票)"),
    FULL_ELECTRONIC_MOTOR_VEHICLE(93, "全电纸票(机动车销售统一发票)"),
    FULL_ELECTRONIC_USED_CAR(94, "全电纸票(二手车销售统一发票)"),
    OTHER(99, "其他票据");


    // 发票类型代码
    private final Integer code;

    // 发票类型名称
    private final String name;


    /**
     * 根据代码获取发票类型；未知代码返回 null（不抛异常，避免识别流程中断）
     */
    public static InvoiceType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (InvoiceType type : InvoiceType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

}
