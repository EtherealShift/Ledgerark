package org.ledgerark.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ledgerark.system.entity.ocr.Invoice;

/**
 * 发票主表 Mapper（biz_invoice）
 */
@Mapper
public interface InvoiceMapper extends BaseMapper<Invoice> {
}
