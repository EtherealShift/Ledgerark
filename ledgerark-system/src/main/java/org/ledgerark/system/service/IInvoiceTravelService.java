package org.ledgerark.system.service;

import org.ledgerark.system.entity.ocr.InvoiceTravel;

/**
 * 交通出行类发票扩展表业务层（biz_invoice_travel，与发票一对一）
 */
public interface IInvoiceTravelService {

    /**
     * 按发票 ID 查询扩展记录（无则返回 null）
     */
    InvoiceTravel selectByInvoiceId(Long invoiceId);

    /**
     * 新增或更新扩展记录（按 invoiceId 判断，一对一行）
     */
    void upsertByInvoiceId(InvoiceTravel entity);

    /**
     * 逻辑删除某发票的扩展记录
     */
    void deleteByInvoiceId(Long invoiceId);
}
