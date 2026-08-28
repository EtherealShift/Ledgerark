package org.ledgerark.system.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.system.entity.ocr.OcrProperties;
import org.ledgerark.system.entity.ocr.vo.InvoiceRecognitionResponseVO;
import org.ledgerark.system.entity.ocr.vo.VoucherRecognitionResponseVO;
import org.ledgerark.system.service.OcrService;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.io.File;


@Slf4j
@Service
public class OcrServiceImpl implements OcrService {


    @Resource
    private OcrProperties ocrProperties;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public VoucherRecognitionResponseVO recognizeVoucher(File file) {
        RestClient restClient = RestClient.builder()
                .baseUrl(ocrProperties.url(ocrProperties.getVoucher()))
                .build();

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("username", ocrProperties.getUsername());
        form.add("file", new FileSystemResource(file));   // 文件流以 Resource 形式放进去
        form.add("nLanguage", 0);
        form.add("typeId", "3004");
        form.add("autoRotation", 1);
        form.add("refactoring", 0);
        form.add("inclineCorrect", 1);
        form.add("layout", 1);


        log.info("Map:{}", form);


        String json = restClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(form)
                .retrieve()
                .body(String.class);

        log.info("OCR 原始响应: {}", json);

        // 把 OCR 服务返回的 JSON 文本反序列化成 VO
        return objectMapper.readValue(json, VoucherRecognitionResponseVO.class);
    }

    @Override
    public InvoiceRecognitionResponseVO recognizeInvoice(File file) {
        RestClient restClient = RestClient.builder()
                .baseUrl(ocrProperties.url(ocrProperties.getInvoice()))
                .build();

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
//        data = {
//                "suffix": "",
//                "userName": HTTPConfig.USER_NAME,
//                "verify": 0,
//                "retain": 0,
//                "redSeal": 0,
//                "cutImage": 0
//        }
        form.add("file", new FileSystemResource(file));   // 文件流以 Resource 形式放进去
        form.add("suffix", "");
        form.add("userName", ocrProperties.getUsername());
        form.add("verify", 0);
        form.add("retain", 0);
        form.add("redSeal", 0);
        form.add("cutImage", 0);


        String json = restClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(form)
                .retrieve()
                .body(String.class);


        log.info("OCR 原始响应: {}", json);

        // 把 OCR 服务返回的 JSON 文本反序列化成 VO
        return objectMapper.readValue(json, InvoiceRecognitionResponseVO.class);
    }
}
