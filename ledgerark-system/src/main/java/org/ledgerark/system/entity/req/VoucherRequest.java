package org.ledgerark.system.entity.req;


import lombok.Data;

/**
 * 凭证 OCR 服务的表单参数。
 *
 * <p>这是服务端到 OCR 引擎的请求模型，不作为 Web Controller 的入参，
 * 避免客户端覆盖白名单用户和引擎策略。
 */
@Data
public class VoucherRequest {
}
