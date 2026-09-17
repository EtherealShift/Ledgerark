package org.ledgerark.system.service;

import org.ledgerark.system.entity.ocr.InvoiceVehicle;

/**
 * 机动车/二手车类发票扩展表业务层（biz_invoice_vehicle，与发票一对一）
 */
public interface IInvoiceVehicleService {

    /**
     * 按发票 ID 查询扩展记录（无则返回 null）
     */
    InvoiceVehicle selectByInvoiceId(Long invoiceId);

    /**
     * 新增或更新扩展记录（按 invoiceId 判断，一对一行）
     */
    void upsertByInvoiceId(InvoiceVehicle entity);

    /**
     * 逻辑删除某发票的扩展记录
     */
    void deleteByInvoiceId(Long invoiceId);
}
