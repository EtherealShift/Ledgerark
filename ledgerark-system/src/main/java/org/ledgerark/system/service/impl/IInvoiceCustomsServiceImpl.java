package org.ledgerark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.system.entity.ocr.InvoiceCustoms;
import org.ledgerark.system.mapper.InvoiceCustomsMapper;
import org.ledgerark.system.service.IInvoiceCustomsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 海关缴款书类发票扩展表业务实现
 */
@Slf4j
@Service
public class IInvoiceCustomsServiceImpl implements IInvoiceCustomsService {

    @Resource
    private InvoiceCustomsMapper invoiceCustomsMapper;

    @Override
    public InvoiceCustoms selectByInvoiceId(Long invoiceId) {
        return invoiceCustomsMapper.selectOne(new LambdaQueryWrapper<InvoiceCustoms>()
                .eq(InvoiceCustoms::getInvoiceId, invoiceId)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertByInvoiceId(InvoiceCustoms entity) {
        if (entity == null) {
            return;
        }
        InvoiceCustoms exist = selectByInvoiceId(entity.getInvoiceId());
        if (exist != null) {
            entity.setId(exist.getId());
            invoiceCustomsMapper.updateById(entity);
        } else {
            invoiceCustomsMapper.insert(entity);
        }
    }

    @Override
    public void deleteByInvoiceId(Long invoiceId) {
        invoiceCustomsMapper.delete(new LambdaQueryWrapper<InvoiceCustoms>()
                .eq(InvoiceCustoms::getInvoiceId, invoiceId));
    }
}
