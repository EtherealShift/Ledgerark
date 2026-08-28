package org.ledgerark.system.entity.ocr.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import tools.jackson.databind.JsonNode;

import java.util.List;

/**
 * 通用表格 OCR 响应。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoucherRecognitionResponseVO {

    private Message message;

    private Info info;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {

        private Integer status;

        private String value;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {

        private String type;

        private String coreTime;

        private List<PageResult> result;

        private String processedImagePath;

        private String num;

        private String consumeId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PageResult {

        private String path;

        /** 当前页HTML数据 */
        private String table;

        /** OCR 节点结构随识别内容变化，保留为动态 JSON。 */
        private JsonNode ocr;
    }
}
