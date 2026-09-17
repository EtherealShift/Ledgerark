package org.ledgerark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.system.entity.ocr.InvoiceVehicle;
import org.ledgerark.system.mapper.InvoiceVehicleMapper;
import org.ledgerark.system.service.IInvoiceVehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 机动车/二手车类发票扩展表业务实现
 */
@Slf4j
@Service
public class IInvoiceVehicleServiceImpl implements IInvoiceVehicleService {

    @Resource
    private InvoiceVehicleMapper invoiceVehicleMapper;

    @Override
    public InvoiceVehicle selectByInvoiceId(Long invoiceId) {
        return invoiceVehicleMapper.selectOne(new LambdaQueryWrapper<InvoiceVehicle>()
                .eq(InvoiceVehicle::getInvoiceId, invoiceId)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertByInvoiceId(InvoiceVehicle entity) {
        if (entity == null) {
            return;
        }
        InvoiceVehicle exist = selectByInvoiceId(entity.getInvoiceId());
        if (exist != null) {
            entity.setId(exist.getId());
            invoiceVehicleMapper.updateById(entity);
        } else {
            invoiceVehicleMapper.insert(entity);
        }
    }

    @Override
    public void deleteByInvoiceId(Long invoiceId) {
        invoiceVehicleMapper.delete(new LambdaQueryWrapper<InvoiceVehicle>()
                .eq(InvoiceVehicle::getInvoiceId, invoiceId));
    }
}
