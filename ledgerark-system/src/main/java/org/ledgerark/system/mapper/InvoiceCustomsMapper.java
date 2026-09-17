package org.ledgerark.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ledgerark.system.entity.ocr.InvoiceCustoms;

/**
 * 海关缴款书类发票扩展表 Mapper（biz_invoice_customs）
 */
@Mapper
public interface InvoiceCustomsMapper extends BaseMapper<InvoiceCustoms> {
}
