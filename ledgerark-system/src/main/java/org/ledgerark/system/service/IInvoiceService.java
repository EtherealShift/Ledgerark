package org.ledgerark.system.service;

import org.ledgerark.common.entity.base.PageQuery;
import org.ledgerark.system.entity.ocr.Invoice;
import org.ledgerark.system.entity.ocr.vo.InvoiceRecognitionResponseVO;
import org.ledgerark.system.entity.vo.SysPageResponseVO;

import java.util.List;

/**
 * 发票主表业务层（biz_invoice）
 */
public interface IInvoiceService {

    /**
     * 新增发票（主表），返回自增主键
     */
    Long insertInvoice(Invoice invoice);

    /**
     * 按主键查询发票
     */
    Invoice selectById(Long id);

    /**
     * 按凭证 ID 查询该凭证下的发票列表
     */
    List<Invoice> selectByVoucherId(Long voucherId);

    /**
     * 按发票代码 + 发票号码查询（业务键，命中多条时返回第一条）
     */
    Invoice selectByCodeAndNo(String invoiceCode, String invoiceNumber);

    /**
     * 修改发票（按主键，null 字段不更新）
     */
    void updateInvoice(Invoice invoice);

    /**
     * 分页查询发票（按 id 倒序）
     */
    SysPageResponseVO pageInvoiceList(PageQuery pageQuery);

    /**
     * 删除发票：逻辑删除主表，并级联逻辑删除明细表与 6 张扩展表记录
     */
    void deleteById(Long id);

    /**
     * 保存 OCR 识别结果：转换并落库（主表 + 按类型的扩展表 + 明细，同一事务）
     *
     * @param dataItem OCR 返回的单票数据（data[].item）
     * @return 发票主键；dataItem 为空或缺少 invoice 时返回 null
     */
    Long saveFromRecognition(InvoiceRecognitionResponseVO.DataItem dataItem);
}
