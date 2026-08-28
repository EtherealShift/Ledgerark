package org.ledgerark.admin.web.controller.test;


import jakarta.annotation.Resource;

import org.ledgerark.common.entity.Result;
import org.ledgerark.system.entity.ocr.vo.InvoiceRecognitionResponseVO;
import org.ledgerark.system.entity.ocr.vo.VoucherRecognitionResponseVO;
import org.ledgerark.system.service.OcrService;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
public class VoucherTestController {

    @Resource
    private OcrService voucherService;


    @GetMapping("/test/voucher")
    public Result<VoucherRecognitionResponseVO> recognize() {

        File file = new File("C:\\Users\\lxd02\\Desktop\\错误测试（公开）\\20260810120549289_已执行\\20260610151734370.jpg");


        VoucherRecognitionResponseVO recognize = voucherService.recognizeVoucher(file);


        return Result.success(recognize);
    }

    @GetMapping("/test/invoice")
    public Result<InvoiceRecognitionResponseVO> recognizeInvoice() {

        File file = new File("C:\\Users\\lxd02\\Desktop\\错误测试（公开）\\20260810120549289_已执行\\20260610143713694.jpg");


        InvoiceRecognitionResponseVO recognize = voucherService.recognizeInvoice(file);


        return Result.success(recognize);
    }


}
