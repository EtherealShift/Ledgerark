package org.ledgerark.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ledgerark.system.entity.ocr.InvoiceTravel;

/**
 * 交通出行类发票扩展表 Mapper（biz_invoice_travel）
 */
@Mapper
public interface InvoiceTravelMapper extends BaseMapper<InvoiceTravel> {
}
