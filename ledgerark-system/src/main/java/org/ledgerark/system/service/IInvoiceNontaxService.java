package org.ledgerark.system.service;

import org.ledgerark.system.entity.ocr.InvoiceNontax;

/**
 * 非税/财政/完税/通用电子类发票扩展表业务层（biz_invoice_nontax，与发票一对一）
 */
public interface IInvoiceNontaxService {

    /**
     * 按发票 ID 查询扩展记录（无则返回 null）
     */
    InvoiceNontax selectByInvoiceId(Long invoiceId);

    /**
     * 新增或更新扩展记录（按 invoiceId 判断，一对一行）
     */
    void upsertByInvoiceId(InvoiceNontax entity);

    /**
     * 逻辑删除某发票的扩展记录
     */
    void deleteByInvoiceId(Long invoiceId);
}
