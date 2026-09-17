package org.ledgerark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.system.entity.ocr.InvoiceBank;
import org.ledgerark.system.mapper.InvoiceBankMapper;
import org.ledgerark.system.service.IInvoiceBankService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 银行回单类发票扩展表业务实现
 */
@Slf4j
@Service
public class IInvoiceBankServiceImpl implements IInvoiceBankService {

    @Resource
    private InvoiceBankMapper invoiceBankMapper;

    @Override
    public InvoiceBank selectByInvoiceId(Long invoiceId) {
        return invoiceBankMapper.selectOne(new LambdaQueryWrapper<InvoiceBank>()
                .eq(InvoiceBank::getInvoiceId, invoiceId)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertByInvoiceId(InvoiceBank entity) {
        if (entity == null) {
            return;
        }
        InvoiceBank exist = selectByInvoiceId(entity.getInvoiceId());
        if (exist != null) {
            entity.setId(exist.getId());
            invoiceBankMapper.updateById(entity);
        } else {
            invoiceBankMapper.insert(entity);
        }
    }

    @Override
    public void deleteByInvoiceId(Long invoiceId) {
        invoiceBankMapper.delete(new LambdaQueryWrapper<InvoiceBank>()
                .eq(InvoiceBank::getInvoiceId, invoiceId));
    }
}
