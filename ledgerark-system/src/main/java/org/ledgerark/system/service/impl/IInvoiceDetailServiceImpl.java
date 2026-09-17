package org.ledgerark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.system.entity.ocr.InvoiceDetail;
import org.ledgerark.system.mapper.InvoiceDetailMapper;
import org.ledgerark.system.service.IInvoiceDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 发票明细表业务实现
 */
@Slf4j
@Service
public class IInvoiceDetailServiceImpl implements IInvoiceDetailService {

    @Resource
    private InvoiceDetailMapper invoiceDetailMapper;

    @Override
    public List<InvoiceDetail> selectByInvoiceId(Long invoiceId) {
        return invoiceDetailMapper.selectList(new LambdaQueryWrapper<InvoiceDetail>()
                .eq(InvoiceDetail::getInvoiceId, invoiceId)
                .orderByAsc(InvoiceDetail::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBatch(Long invoiceId, List<InvoiceDetail> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        for (InvoiceDetail detail : details) {
            detail.setId(null);
            detail.setInvoiceId(invoiceId);
            invoiceDetailMapper.insert(detail);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceBatch(Long invoiceId, List<InvoiceDetail> details) {
        // 先逻辑删除旧明细，再插入新明细（整票替换语义）
        deleteByInvoiceId(invoiceId);
        insertBatch(invoiceId, details);
    }

    @Override
    public void deleteByInvoiceId(Long invoiceId) {
        invoiceDetailMapper.delete(new LambdaQueryWrapper<InvoiceDetail>()
                .eq(InvoiceDetail::getInvoiceId, invoiceId));
    }
}
