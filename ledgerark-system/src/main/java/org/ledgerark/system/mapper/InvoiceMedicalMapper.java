package org.ledgerark.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ledgerark.system.entity.ocr.InvoiceMedical;

/**
 * 医疗票据类发票扩展表 Mapper（biz_invoice_medical）
 */
@Mapper
public interface InvoiceMedicalMapper extends BaseMapper<InvoiceMedical> {
}
