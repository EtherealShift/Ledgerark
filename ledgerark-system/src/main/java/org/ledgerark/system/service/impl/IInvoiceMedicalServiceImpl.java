package org.ledgerark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.system.entity.ocr.InvoiceMedical;
import org.ledgerark.system.mapper.InvoiceMedicalMapper;
import org.ledgerark.system.service.IInvoiceMedicalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 医疗票据类发票扩展表业务实现
 */
@Slf4j
@Service
public class IInvoiceMedicalServiceImpl implements IInvoiceMedicalService {

    @Resource
    private InvoiceMedicalMapper invoiceMedicalMapper;

    @Override
    public InvoiceMedical selectByInvoiceId(Long invoiceId) {
        return invoiceMedicalMapper.selectOne(new LambdaQueryWrapper<InvoiceMedical>()
                .eq(InvoiceMedical::getInvoiceId, invoiceId)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertByInvoiceId(InvoiceMedical entity) {
        if (entity == null) {
            return;
        }
        InvoiceMedical exist = selectByInvoiceId(entity.getInvoiceId());
        if (exist != null) {
            entity.setId(exist.getId());
            invoiceMedicalMapper.updateById(entity);
        } else {
            invoiceMedicalMapper.insert(entity);
        }
    }

    @Override
    public void deleteByInvoiceId(Long invoiceId) {
        invoiceMedicalMapper.delete(new LambdaQueryWrapper<InvoiceMedical>()
                .eq(InvoiceMedical::getInvoiceId, invoiceId));
    }
}
