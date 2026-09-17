package org.ledgerark.system.service;

import org.ledgerark.system.entity.ocr.InvoiceDetail;

import java.util.List;

/**
 * 发票明细表业务层（biz_invoice_detail）
 */
public interface IInvoiceDetailService {

    /**
     * 按发票 ID 查询明细列表（按 id 正序）
     */
    List<InvoiceDetail> selectByInvoiceId(Long invoiceId);

    /**
     * 批量新增明细（统一回填 invoiceId）
     */
    void insertBatch(Long invoiceId, List<InvoiceDetail> details);

    /**
     * 整体替换明细：先逻辑删除该发票旧明细，再批量新增
     */
    void replaceBatch(Long invoiceId, List<InvoiceDetail> details);

    /**
     * 逻辑删除某发票的全部明细
     */
    void deleteByInvoiceId(Long invoiceId);
}
