package org.ledgerark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.system.entity.ocr.InvoiceNontax;
import org.ledgerark.system.mapper.InvoiceNontaxMapper;
import org.ledgerark.system.service.IInvoiceNontaxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 非税/财政/完税/通用电子类发票扩展表业务实现
 */
@Slf4j
@Service
public class IInvoiceNontaxServiceImpl implements IInvoiceNontaxService {

    @Resource
    private InvoiceNontaxMapper invoiceNontaxMapper;

    @Override
    public InvoiceNontax selectByInvoiceId(Long invoiceId) {
        return invoiceNontaxMapper.selectOne(new LambdaQueryWrapper<InvoiceNontax>()
                .eq(InvoiceNontax::getInvoiceId, invoiceId)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertByInvoiceId(InvoiceNontax entity) {
        if (entity == null) {
            return;
        }
        InvoiceNontax exist = selectByInvoiceId(entity.getInvoiceId());
        if (exist != null) {
            entity.setId(exist.getId());
            invoiceNontaxMapper.updateById(entity);
        } else {
            invoiceNontaxMapper.insert(entity);
        }
    }

    @Override
    public void deleteByInvoiceId(Long invoiceId) {
        invoiceNontaxMapper.delete(new LambdaQueryWrapper<InvoiceNontax>()
                .eq(InvoiceNontax::getInvoiceId, invoiceId));
    }
}
