package org.ledgerark.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ledgerark.system.entity.ocr.InvoiceVehicle;

/**
 * 机动车/二手车类发票扩展表 Mapper（biz_invoice_vehicle）
 */
@Mapper
public interface InvoiceVehicleMapper extends BaseMapper<InvoiceVehicle> {
}
