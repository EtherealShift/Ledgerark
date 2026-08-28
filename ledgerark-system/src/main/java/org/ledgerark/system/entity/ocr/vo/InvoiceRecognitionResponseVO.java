package org.ledgerark.system.entity.ocr.vo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import tools.jackson.databind.JsonNode;

import java.util.List;

/**
 * 发票 OCR / 核验响应（慧票通）。
 * <p>
 * 顶层结构固定：{ "code": 0, "data": [ ...一张票一个元素... ], "msg": "调用完成" }
 * <p>
 * 注意：data[].invoice 的字段随发票类型（invoiceType）变化，手册中有 30+ 种类型
 * （增值税专/普票、火车票、出租车票、航空行程单、滴滴行程单……），
 * 因此 invoice 用 JsonNode 动态承接，按需 path(...) 读取，避免反序列化报错或漏字段。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceRecognitionResponseVO {

    /** 识别状态码：0 表示成功 */
    private Integer code;

    private String msg;

    /** 识别结果数组，每个元素是一张票 */
    private List<DataItem> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataItem {

        /** 多票状态码：0=识别成功 */
        private Integer code;

        /** ★ 发票数据（核心）。字段随 invoiceType 变化，按需 path(...) 读取 */
        private JsonNode invoice;

        /** 二维码区域识别结果 */
        private JsonNode qrLists;

    }
}
