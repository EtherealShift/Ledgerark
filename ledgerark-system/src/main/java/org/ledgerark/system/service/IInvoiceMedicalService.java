package org.ledgerark.system.service;

import org.ledgerark.system.entity.ocr.InvoiceMedical;

/**
 * 医疗票据类发票扩展表业务层（biz_invoice_medical，与发票一对一）
 */
public interface IInvoiceMedicalService {

    /**
     * 按发票 ID 查询扩展记录（无则返回 null）
     */
    InvoiceMedical selectByInvoiceId(Long invoiceId);

    /**
     * 新增或更新扩展记录（按 invoiceId 判断，一对一行）
     */
    void upsertByInvoiceId(InvoiceMedical entity);

    /**
     * 逻辑删除某发票的扩展记录
     */
    void deleteByInvoiceId(Long invoiceId);
}
