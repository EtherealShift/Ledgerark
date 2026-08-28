package org.ledgerark.system.service;

import java.io.File;

import org.ledgerark.system.entity.ocr.vo.InvoiceRecognitionResponseVO;
import org.ledgerark.system.entity.ocr.vo.VoucherRecognitionResponseVO;

public interface OcrService {

    /**
     * 凭证识别。
     *
     * @param file 待识别的图片资源
     * @return OCR 识别结果
     */
    VoucherRecognitionResponseVO recognizeVoucher(File file);



    InvoiceRecognitionResponseVO recognizeInvoice(File file);
}
