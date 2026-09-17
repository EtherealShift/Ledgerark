package org.ledgerark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ledgerark.common.entity.PageParam;
import org.ledgerark.common.entity.base.PageQuery;
import org.ledgerark.system.converter.InvoiceOcrConverter;
import org.ledgerark.system.entity.ocr.Invoice;
import org.ledgerark.system.entity.ocr.InvoiceBank;
import org.ledgerark.system.entity.ocr.InvoiceCustoms;
import org.ledgerark.system.entity.ocr.InvoiceMedical;
import org.ledgerark.system.entity.ocr.InvoiceNontax;
import org.ledgerark.system.entity.ocr.InvoiceTravel;
import org.ledgerark.system.entity.ocr.InvoiceVehicle;
import org.ledgerark.system.entity.ocr.vo.InvoiceRecognitionResponseVO;
import org.ledgerark.system.entity.vo.SysPageResponseVO;
import org.ledgerark.system.mapper.InvoiceMapper;
import org.ledgerark.system.service.IInvoiceBankService;
import org.ledgerark.system.service.IInvoiceCustomsService;
import org.ledgerark.system.service.IInvoiceDetailService;
import org.ledgerark.system.service.IInvoiceMedicalService;
import org.ledgerark.system.service.IInvoiceNontaxService;
import org.ledgerark.system.service.IInvoiceService;
import org.ledgerark.system.service.IInvoiceTravelService;
import org.ledgerark.system.service.IInvoiceVehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.util.List;

/**
 * 发票主表业务实现
 */
@Slf4j
@Service
public class IInvoiceServiceImpl implements IInvoiceService {

    @Resource
    private InvoiceMapper invoiceMapper;

    @Resource
    private IInvoiceDetailService invoiceDetailService;

    @Resource
    private IInvoiceTravelService invoiceTravelService;

    @Resource
    private IInvoiceVehicleService invoiceVehicleService;

    @Resource
    private IInvoiceMedicalService invoiceMedicalService;

    @Resource
    private IInvoiceCustomsService invoiceCustomsService;

    @Resource
    private IInvoiceBankService invoiceBankService;

    @Resource
    private IInvoiceNontaxService invoiceNontaxService;

    @Resource
    private InvoiceOcrConverter invoiceOcrConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long insertInvoice(Invoice invoice) {
        invoiceMapper.insert(invoice);
        return invoice.getId();
    }

    @Override
    public Invoice selectById(Long id) {
        return invoiceMapper.selectById(id);
    }

    @Override
    public List<Invoice> selectByVoucherId(Long voucherId) {
        return invoiceMapper.selectList(new LambdaQueryWrapper<Invoice>()
                .eq(Invoice::getVoucherId, voucherId)
                .orderByAsc(Invoice::getId));
    }

    @Override
    public Invoice selectByCodeAndNo(String invoiceCode, String invoiceNumber) {
        return invoiceMapper.selectOne(new LambdaQueryWrapper<Invoice>()
                .eq(StringUtils.isNotBlank(invoiceCode), Invoice::getInvoiceCode, invoiceCode)
                .eq(StringUtils.isNotBlank(invoiceNumber), Invoice::getInvoiceNumber, invoiceNumber)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInvoice(Invoice invoice) {
        invoiceMapper.updateById(invoice);
    }

    @Override
    public SysPageResponseVO pageInvoiceList(PageQuery pageQuery) {
        Page<Invoice> page = PageParam.of(pageQuery).toPage();
        Page<Invoice> result = invoiceMapper.selectPage(page,
                new LambdaQueryWrapper<Invoice>().orderByDesc(Invoice::getId));
        return SysPageResponseVO.builder()
                .records(result.getRecords())
                .total(result.getTotal())
                .size(result.getSize())
                .current(result.getCurrent())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (id == null || id < 1) {
            log.warn("删除发票参数非法：id={}", id);
            return;
        }
        // 主表逻辑删除
        invoiceMapper.deleteById(id);
        // 级联逻辑删除明细与各扩展表记录（扩展表可能无记录，删除 0 行无副作用）
        invoiceDetailService.deleteByInvoiceId(id);
        invoiceTravelService.deleteByInvoiceId(id);
        invoiceVehicleService.deleteByInvoiceId(id);
        invoiceMedicalService.deleteByInvoiceId(id);
        invoiceCustomsService.deleteByInvoiceId(id);
        invoiceBankService.deleteByInvoiceId(id);
        invoiceNontaxService.deleteByInvoiceId(id);
        log.info("发票已删除（逻辑删除），id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveFromRecognition(InvoiceRecognitionResponseVO.DataItem dataItem) {
        if (dataItem == null || dataItem.getInvoice() == null) {
            log.warn("OCR 结果缺少 invoice 数据，跳过落库");
            return null;
        }
        JsonNode node = dataItem.getInvoice();
        Invoice invoice = invoiceOcrConverter.toInvoice(node, dataItem.getQrLists());
        if (invoice == null) {
            return null;
        }

        // 主表落库，先取得主键
        Long invoiceId = insertInvoice(invoice);

        // 按发票类型路由扩展表（仅对应类型写入，其余扩展表不落数据）
        Integer type = invoice.getInvoiceTypeCode();
        if (type != null) {
            switch (type) {
                case 20, 22, 24, 25, 26, 27, 28, 31, 39, 61, 62 -> {
                    InvoiceTravel ext = invoiceOcrConverter.toTravel(node);
                    if (ext != null) {
                        ext.setInvoiceId(invoiceId);
                        invoiceTravelService.upsertByInvoiceId(ext);
                    }
                }
                case 3, 15, 63, 64, 93, 94 -> {
                    InvoiceVehicle ext = invoiceOcrConverter.toVehicle(node);
                    if (ext != null) {
                        ext.setInvoiceId(invoiceId);
                        invoiceVehicleService.upsertByInvoiceId(ext);
                    }
                }
                case 38, 43 -> {
                    InvoiceMedical ext = invoiceOcrConverter.toMedical(node);
                    if (ext != null) {
                        ext.setInvoiceId(invoiceId);
                        invoiceMedicalService.upsertByInvoiceId(ext);
                    }
                }
                case 35 -> {
                    InvoiceCustoms ext = invoiceOcrConverter.toCustoms(node);
                    if (ext != null) {
                        ext.setInvoiceId(invoiceId);
                        invoiceCustomsService.upsertByInvoiceId(ext);
                    }
                }
                case 42 -> {
                    InvoiceBank ext = invoiceOcrConverter.toBank(node);
                    if (ext != null) {
                        ext.setInvoiceId(invoiceId);
                        invoiceBankService.upsertByInvoiceId(ext);
                    }
                }
                case 34, 36, 37, 40 -> {
                    InvoiceNontax ext = invoiceOcrConverter.toNontax(node);
                    if (ext != null) {
                        ext.setInvoiceId(invoiceId);
                        invoiceNontaxService.upsertByInvoiceId(ext);
                    }
                }
                default -> log.debug("发票类型 {} 无扩展表，仅落主表", type);
            }
        }

        // 明细落库
        invoiceDetailService.insertBatch(invoiceId, invoiceOcrConverter.toDetails(node));
        log.info("OCR 发票落库成功，id={}, type={}", invoiceId, type);
        return invoiceId;
    }
}
