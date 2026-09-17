package org.ledgerark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.system.entity.ocr.InvoiceTravel;
import org.ledgerark.system.mapper.InvoiceTravelMapper;
import org.ledgerark.system.service.IInvoiceTravelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 交通出行类发票扩展表业务实现
 */
@Slf4j
@Service
public class IInvoiceTravelServiceImpl implements IInvoiceTravelService {

    @Resource
    private InvoiceTravelMapper invoiceTravelMapper;

    @Override
    public InvoiceTravel selectByInvoiceId(Long invoiceId) {
        return invoiceTravelMapper.selectOne(new LambdaQueryWrapper<InvoiceTravel>()
                .eq(InvoiceTravel::getInvoiceId, invoiceId)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertByInvoiceId(InvoiceTravel entity) {
        if (entity == null) {
            return;
        }
        InvoiceTravel exist = selectByInvoiceId(entity.getInvoiceId());
        if (exist != null) {
            entity.setId(exist.getId());
            invoiceTravelMapper.updateById(entity);
        } else {
            invoiceTravelMapper.insert(entity);
        }
    }

    @Override
    public void deleteByInvoiceId(Long invoiceId) {
        invoiceTravelMapper.delete(new LambdaQueryWrapper<InvoiceTravel>()
                .eq(InvoiceTravel::getInvoiceId, invoiceId));
    }
}
