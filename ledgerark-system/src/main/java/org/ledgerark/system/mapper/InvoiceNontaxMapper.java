package org.ledgerark.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ledgerark.system.entity.ocr.InvoiceNontax;

/**
 * 非税/财政/完税/通用电子类发票扩展表 Mapper（biz_invoice_nontax）
 */
@Mapper
public interface InvoiceNontaxMapper extends BaseMapper<InvoiceNontax> {
}
