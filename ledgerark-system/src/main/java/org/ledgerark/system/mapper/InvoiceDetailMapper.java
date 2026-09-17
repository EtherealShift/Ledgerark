package org.ledgerark.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ledgerark.system.entity.ocr.InvoiceDetail;

/**
 * 发票明细表 Mapper（biz_invoice_detail）
 */
@Mapper
public interface InvoiceDetailMapper extends BaseMapper<InvoiceDetail> {
}
