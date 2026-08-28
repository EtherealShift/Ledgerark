package org.ledgerark.system.entity.ocr;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;


@Data
@ConfigurationProperties(prefix = "ledgerark.ocr")
@Component
public class OcrProperties {


    // OCR 服务的主机地址
    private String host;
    // OCR 服务的用户名
    private String username;

    private Service voucher;

    private Service invoice;


    @Data
    public static class Service {

        private Integer port;

        private String path;

    }


    public String url(Service svc) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host)
                .port(svc.getPort())
                .path(svc.getPath())
                .build()
                .toUriString();
    }

}
