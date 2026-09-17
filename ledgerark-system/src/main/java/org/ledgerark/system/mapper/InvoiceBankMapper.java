package org.ledgerark.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ledgerark.system.entity.ocr.InvoiceBank;

/**
 * 银行回单类发票扩展表 Mapper（biz_invoice_bank）
 */
@Mapper
public interface InvoiceBankMapper extends BaseMapper<InvoiceBank> {
}
