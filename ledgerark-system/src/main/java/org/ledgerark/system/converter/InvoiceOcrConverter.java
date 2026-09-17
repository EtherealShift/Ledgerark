package org.ledgerark.system.converter;

import org.ledgerark.common.enums.InvoiceType;
import org.ledgerark.system.entity.ocr.Invoice;
import org.ledgerark.system.entity.ocr.InvoiceBank;
import org.ledgerark.system.entity.ocr.InvoiceCustoms;
import org.ledgerark.system.entity.ocr.InvoiceDetail;
import org.ledgerark.system.entity.ocr.InvoiceMedical;
import org.ledgerark.system.entity.ocr.InvoiceNontax;
import org.ledgerark.system.entity.ocr.InvoiceTravel;
import org.ledgerark.system.entity.ocr.InvoiceVehicle;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * OCR 识别结果 → 发票实体 转换器（纯转换，不落库）。
 * <p>
 * 约定：
 * 1. 输入为接口返回 data[].invoice（JsonNode，字段随 invoiceType 变化），
 *    空白值统一转 null（便于查询与 qrLists 补全）；
 * 2. 识别附加信息（orientation/dataMsg/coord/flag/code/imgOrgsize/region/
 *    regionFourPoint/regionFourPointOri/dataCode/confidence/cutUrlImage）不转换、不落库；
 * 3. qrLists 中的发票代码/号码/开票日期/金额在主表对应字段为空时补全；
 * 4. 类型特有字段按 invoiceType 由调用方路由到对应扩展表（toTravel/toVehicle/...），
 *    扩展表无任何有效字段时返回 null（不产生空行）；
 * 5. 通用合并字段（地址电话/开户行账号）优先取接口合并键，缺失时用拆分键拼接。
 */
@Component
public class InvoiceOcrConverter {

    // ==================== 主表 ====================

    /**
     * invoice JsonNode → 主表 Invoice（含 3 个低频 JSON 列与 qrLists 补全）
     */
    public Invoice toInvoice(JsonNode node, JsonNode qrLists) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        Invoice invoice = new Invoice();

        // 发票类型
        Integer typeCode = intVal(node, "invoiceType");
        invoice.setInvoiceTypeCode(typeCode);
        InvoiceType type = InvoiceType.fromCode(typeCode);
        // 未知代码兜底为代码字符串，保证 invoice_type 非空
        invoice.setInvoiceType(type != null ? type.getName() : (typeCode == null ? null : String.valueOf(typeCode)));

        // 基本通用信息
        invoice.setTitle(text(node, "title"));
        invoice.setAdministrativeDivisionName(text(node, "administrativeDivisionName"));
        invoice.setFullInvoiceNumber(text(node, "fullInvoiceNumber"));
        invoice.setInvoiceCode(text(node, "invoiceCode"));
        invoice.setInvoiceNumber(text(node, "invoiceNumber"));
        invoice.setInvoiceNumberOcr(text(node, "invoiceNumberocr"));
        invoice.setBillingDate(text(node, "billingDate"));
        invoice.setBillingDateOcr(text(node, "billingDateocr"));
        invoice.setTotalAmount(text(node, "totalAmount"));
        invoice.setTotalAmountOcr(text(node, "totalAmountocr"));
        invoice.setTotalTax(text(node, "totalTax"));
        invoice.setAmountTax(text(node, "amountTax"));
        invoice.setAmountTaxOcr(text(node, "amountTaxocr"));
        invoice.setAmountTaxCn(text(node, "amountTaxCN"));
        invoice.setCheckCode(text(node, "checkCode"));
        invoice.setMachineCode(text(node, "machineCode"));
        invoice.setPasswordField(text(node, "passwordField"));
        invoice.setTaxControlCode(text(node, "taxControlCode"));
        invoice.setPriInvoiceCode(text(node, "priInvoiceCode"));
        invoice.setPriInvoiceNumber(text(node, "priInvoiceNumber"));
        invoice.setAftInvoiceCode(text(node, "aftInvoiceCode"));
        invoice.setAftInvoiceNumber(text(node, "aftInvoiceNumber"));
        invoice.setKind(text(node, "kind"));
        invoice.setCategory(text(node, "category"));
        invoice.setAmount(text(node, "amount"));
        invoice.setTaxRate(text(node, "taxRate"));
        invoice.setTaxRateValue(text(node, "taxRateValue"));
        invoice.setSpecialTag(text(node, "specialTag"));
        invoice.setPage(text(node, "page"));
        invoice.setState(text(node, "state"));
        invoice.setInvoiceForm(text(node, "invoiceForm"));
        invoice.setInvoiceFormNum(text(node, "invoiceFormNum"));
        invoice.setReceiverName(text(node, "receiverName"));
        invoice.setRecheckName(text(node, "recheckName"));
        invoice.setDrawerName(text(node, "drawerName"));
        invoice.setTravelTax(text(node, "travelTax"));
        invoice.setSupervisionSeal(text(node, "supervisionSeal"));
        invoice.setRedSeal(text(node, "redSeal"));
        invoice.setOilMark(text(node, "oilMark"));
        invoice.setInvTaxSign(text(node, "invTaxSign"));
        invoice.setTollSign(text(node, "tollSign"));
        invoice.setSealMark(text(node, "sealMark"));
        invoice.setJiangsuTollSign(text(node, "jiangsutollSign"));
        invoice.setHintInfo(text(node, "hintInfo"));
        invoice.setSignatureFlag(intVal(node, "signatureFlag"));
        invoice.setSignature(jsonText(node, "signature"));
        invoice.setPdfPage(text(node, "pdfPage"));
        invoice.setBlurFlag(text(node, "blurFlag"));

        // 购方/销方（通用合并字段）
        invoice.setPurchaserName(text(node, "purchaserName"));
        invoice.setPurchaserTaxNo(text(node, "purchaserTaxNo"));
        invoice.setPurchaserAddressPhone(purchaserAddressPhone(node));
        invoice.setPurchaserBank(text(node, "purchaserBank"));
        invoice.setSalesName(text(node, "salesName"));
        invoice.setSalesTaxNo(text(node, "salesTaxNo"));
        invoice.setSalesAddressPhone(salesAddressPhone(node));
        invoice.setSalesBankAndNo(salesBankAndNo(node));

        // 低频明细 JSON 列（原样存接口数组文本）
        invoice.setFlightsJson(jsonText(node, "flights"));
        invoice.setGoodsJson(jsonText(node, "goodsInvoiceLists"));
        invoice.setTravelerJson(jsonText(node, "travelerInvoiceLists"));

        // qrLists 补全
        fillFromQrLists(invoice, qrLists);

        return invoice;
    }

    // ==================== 交通出行类扩展（20/22/24/25/26/27/28/31/39/61/62） ====================

    public InvoiceTravel toTravel(JsonNode node) {
        if (!hasAny(node, "number", "trainNumber", "from", "to", "carriageNumber", "seat", "seatNumber",
                "airSign", "name", "userCardNo", "salestation", "serialNumber", "ticketCheck", "remark",
                "billingTime", "fare", "mileage", "timeGeton", "timeGetoff", "unitPrice", "licensePlate",
                "fuelSurcharge", "otherSurcharge", "otherCallcharge", "busInsurance", "dateStart", "dateEnd",
                "phone", "provider", "province", "city", "currencyCode", "highwayFlag", "officialSerialNumber",
                "printNumber", "endorsement", "caacDevelopmentFund", "taxFee", "eticketNumber", "insurance",
                "agentcode", "issueBy", "issueParty", "internationalFlag", "ridingDate", "ridingTime",
                "originalInvoiceNumber", "amountTaxType", "reFund", "rebook", "replace", "cancle")) {
            return null;
        }
        InvoiceTravel ext = new InvoiceTravel();
        ext.setNumber(text(node, "number"));
        ext.setTrainNumber(text(node, "trainNumber"));
        ext.setTravelerFrom(text(node, "from"));
        ext.setTravelerTo(text(node, "to"));
        ext.setCarriageNumber(text(node, "carriageNumber"));
        ext.setSeat(text(node, "seat"));
        ext.setSeatNumber(text(node, "seatNumber"));
        ext.setAirSign(text(node, "airSign"));
        ext.setName(text(node, "name"));
        ext.setUserCardNo(text(node, "userCardNo"));
        ext.setSalestation(text(node, "salestation"));
        ext.setSerialNumber(text(node, "serialNumber"));
        ext.setTicketCheck(text(node, "ticketCheck"));
        ext.setRemark(text(node, "remark"));
        ext.setBillingTime(text(node, "billingTime"));
        ext.setFare(text(node, "fare"));
        ext.setMileage(text(node, "mileage"));
        ext.setTimeGeton(text(node, "timeGeton"));
        ext.setTimeGetoff(text(node, "timeGetoff"));
        ext.setUnitPrice(text(node, "unitPrice"));
        ext.setLicensePlate(text(node, "licensePlate"));
        ext.setFuelSurcharge(text(node, "fuelSurcharge"));
        ext.setOtherSurcharge(text(node, "otherSurcharge"));
        ext.setOtherCallcharge(text(node, "otherCallcharge"));
        ext.setBusInsurance(text(node, "busInsurance"));
        ext.setDateStart(text(node, "dateStart"));
        ext.setDateEnd(text(node, "dateEnd"));
        ext.setPhone(text(node, "phone"));
        ext.setProvider(text(node, "provider"));
        ext.setProvince(text(node, "province"));
        ext.setCity(text(node, "city"));
        ext.setCurrencyCode(text(node, "currencyCode"));
        ext.setHighwayFlag(text(node, "highwayFlag"));
        // 航空行程单（27/61）
        ext.setOfficialSerialNumber(text(node, "officialSerialNumber"));
        ext.setPrintNumber(text(node, "printNumber"));
        ext.setEndorsement(text(node, "endorsement"));
        ext.setCaacDevelopmentFund(text(node, "caacDevelopmentFund"));
        ext.setTaxFee(text(node, "taxFee"));
        ext.setEticketNumber(text(node, "eticketNumber"));
        ext.setInsurance(text(node, "insurance"));
        ext.setAgentCode(text(node, "agentcode"));
        String issueBy = text(node, "issueBy");
        ext.setIssueBy(issueBy != null ? issueBy : text(node, "issueParty"));
        ext.setInternationalFlag(text(node, "internationalFlag"));
        // 铁路电子客票（62）
        ext.setRidingDate(text(node, "ridingDate"));
        ext.setRidingTime(text(node, "ridingTime"));
        ext.setOriginalInvoiceNumber(text(node, "originalInvoiceNumber"));
        ext.setAmountTaxType(text(node, "amountTaxType"));
        ext.setReFund(text(node, "reFund"));
        ext.setRebook(text(node, "rebook"));
        ext.setReplace(text(node, "replace"));
        ext.setCancle(text(node, "cancle"));
        return ext;
    }

    // ==================== 机动车/二手车类扩展（3/15/63/64/93/94） ====================

    public InvoiceVehicle toVehicle(JsonNode node) {
        if (!hasAny(node, "idCardNo", "vehicleType", "brandModel", "originPlace", "certificateNo",
                "importCertificateNo", "inspectionListNo", "engineNo", "vehicleNo", "carNumber",
                "registrationNumber", "vehiclePlaceName", "usedCarName", "usedCarTaxNo", "usedCarAddress",
                "usedCarbank", "usedCarPhone", "auctionAddress", "auctionName", "auctionPhone", "auctionTaxNo",
                "auctionbank", "taxAuthorityName", "taxAuthorityNo", "paymentVoucherNo", "tonnage",
                "passengersLimited")) {
            return null;
        }
        InvoiceVehicle ext = new InvoiceVehicle();
        ext.setIdCardNo(text(node, "idCardNo"));
        ext.setVehicleType(text(node, "vehicleType"));
        ext.setBrandModel(text(node, "brandModel"));
        ext.setOriginPlace(text(node, "originPlace"));
        ext.setCertificateNo(text(node, "certificateNo"));
        ext.setImportCertificateNo(text(node, "importCertificateNo"));
        ext.setInspectionListNo(text(node, "inspectionListNo"));
        ext.setEngineNo(text(node, "engineNo"));
        ext.setVehicleNo(text(node, "vehicleNo"));
        ext.setCarNumber(text(node, "carNumber"));
        ext.setRegistrationNumber(text(node, "registrationNumber"));
        ext.setVehiclePlaceName(text(node, "vehiclePlaceName"));
        ext.setUsedCarName(text(node, "usedCarName"));
        ext.setUsedCarTaxNo(text(node, "usedCarTaxNo"));
        ext.setUsedCarAddress(text(node, "usedCarAddress"));
        ext.setUsedCarBank(text(node, "usedCarbank"));
        ext.setUsedCarPhone(text(node, "usedCarPhone"));
        ext.setAuctionAddress(text(node, "auctionAddress"));
        ext.setAuctionName(text(node, "auctionName"));
        ext.setAuctionPhone(text(node, "auctionPhone"));
        ext.setAuctionTaxNo(text(node, "auctionTaxNo"));
        ext.setAuctionBank(text(node, "auctionbank"));
        ext.setTaxAuthorityName(text(node, "taxAuthorityName"));
        ext.setTaxAuthorityNo(text(node, "taxAuthorityNo"));
        ext.setPaymentVoucherNo(text(node, "paymentVoucherNo"));
        ext.setTonnage(text(node, "tonnage"));
        ext.setPassengersLimited(text(node, "passengersLimited"));
        return ext;
    }

    // ==================== 医疗票据类扩展（38/43） ====================

    public InvoiceMedical toMedical(JsonNode node) {
        if (!hasAny(node, "eleinvoiceCode", "eleinvoiceNumber", "payer", "outpatientNumber", "visitDate",
                "institutionType", "insuranceType", "insuranceNumber", "gender", "insuranceFundPayment",
                "personalPayment", "businessNumber", "otherPayment", "personalCashPayment", "personalSelfPayment",
                "personalSelfFunded", "medicalRecordNumber", "admissionNumber", "inpatientDepartment",
                "hospitalStay", "eleMedical")) {
            return null;
        }
        InvoiceMedical ext = new InvoiceMedical();
        ext.setEleInvoiceCode(text(node, "eleinvoiceCode"));
        ext.setEleInvoiceNumber(text(node, "eleinvoiceNumber"));
        ext.setPayer(text(node, "payer"));
        ext.setOutpatientNumber(text(node, "outpatientNumber"));
        ext.setVisitDate(text(node, "visitDate"));
        ext.setInstitutionType(text(node, "institutionType"));
        ext.setInsuranceType(text(node, "insuranceType"));
        ext.setInsuranceNumber(text(node, "insuranceNumber"));
        ext.setGender(text(node, "gender"));
        ext.setInsuranceFundPayment(text(node, "insuranceFundPayment"));
        ext.setPersonalPayment(text(node, "personalPayment"));
        ext.setBusinessNumber(text(node, "businessNumber"));
        ext.setOtherPayment(text(node, "otherPayment"));
        ext.setPersonalCashPayment(text(node, "personalCashPayment"));
        ext.setPersonalSelfPayment(text(node, "personalSelfPayment"));
        ext.setPersonalSelfFunded(text(node, "personalSelfFunded"));
        ext.setMedicalRecordNumber(text(node, "medicalRecordNumber"));
        ext.setAdmissionNumber(text(node, "admissionNumber"));
        ext.setInpatientDepartment(text(node, "inpatientDepartment"));
        ext.setHospitalStay(text(node, "hospitalStay"));
        ext.setEleMedical(text(node, "eleMedical"));
        return ext;
    }

    // ==================== 海关缴款书类扩展（35） ====================

    public InvoiceCustoms toCustoms(JsonNode node) {
        if (!hasAny(node, "customsNo", "revenueSys", "revenueOrg", "subject", "budgetLevel", "exchequer",
                "corporateName", "corporateAccountNo", "corporateBank", "corporateNo", "customsBillNo",
                "contractNo", "transport", "endTime", "loadBillNo", "fillingOrg", "producer", "reuniteName")) {
            return null;
        }
        InvoiceCustoms ext = new InvoiceCustoms();
        ext.setCustomsNo(text(node, "customsNo"));
        ext.setRevenueSys(text(node, "revenueSys"));
        ext.setRevenueOrg(text(node, "revenueOrg"));
        ext.setSubject(text(node, "subject"));
        ext.setBudgetLevel(text(node, "budgetLevel"));
        ext.setExchequer(text(node, "exchequer"));
        ext.setCorporateName(text(node, "corporateName"));
        ext.setCorporateAccountNo(text(node, "corporateAccountNo"));
        ext.setCorporateBank(text(node, "corporateBank"));
        ext.setCorporateNo(text(node, "corporateNo"));
        ext.setCustomsBillNo(text(node, "customsBillNo"));
        ext.setContractNo(text(node, "contractNo"));
        ext.setTransport(text(node, "transport"));
        ext.setEndTime(text(node, "endTime"));
        ext.setLoadBillNo(text(node, "loadBillNo"));
        ext.setFillingOrg(text(node, "fillingOrg"));
        ext.setProducer(text(node, "producer"));
        ext.setReuniteName(text(node, "reuniteName"));
        return ext;
    }

    // ==================== 银行回单类扩展（42） ====================

    public InvoiceBank toBank(JsonNode node) {
        if (!hasAny(node, "purchaserNo", "purchaserBankAdd", "salesNo", "salesBank", "summary", "serialNo",
                "receiptNo", "used", "bankName", "postscript", "tradeName", "bankCategory")) {
            return null;
        }
        InvoiceBank ext = new InvoiceBank();
        ext.setPurchaserNo(text(node, "purchaserNo"));
        ext.setPurchaserBankAdd(text(node, "purchaserBankAdd"));
        ext.setSalesNo(text(node, "salesNo"));
        ext.setSalesBank(text(node, "salesBank"));
        ext.setSummary(text(node, "summary"));
        ext.setSerialNo(text(node, "serialNo"));
        ext.setReceiptNo(text(node, "receiptNo"));
        ext.setUsed(text(node, "used"));
        ext.setBankName(text(node, "bankName"));
        ext.setPostscript(text(node, "postscript"));
        ext.setTradeName(text(node, "tradeName"));
        ext.setBankCategory(text(node, "bankCategory"));
        return ext;
    }

    // ==================== 非税/财政/完税/通用电子类扩展（34/36/37/40） ====================

    public InvoiceNontax toNontax(JsonNode node) {
        if (!hasAny(node, "collectionUser", "payInfo", "merchantNo", "orderNo", "paymentCode", "receivingCode",
                "receivingName", "payerName", "payerNumber", "payerBank", "payeeName", "payeeNumber", "payeeBank",
                "sealReceiving", "eleNontaxInvoice", "voucherNumber", "auctionTaxName", "auctionTaxNo",
                "taxAuthorityNo")) {
            return null;
        }
        InvoiceNontax ext = new InvoiceNontax();
        ext.setCollectionUser(text(node, "collectionUser"));
        ext.setPayInfo(text(node, "payInfo"));
        ext.setMerchantNo(text(node, "merchantNo"));
        ext.setOrderNo(text(node, "orderNo"));
        ext.setPaymentCode(text(node, "paymentCode"));
        ext.setReceivingCode(text(node, "receivingCode"));
        ext.setReceivingName(text(node, "receivingName"));
        ext.setPayerName(text(node, "payerName"));
        ext.setPayerNumber(text(node, "payerNumber"));
        ext.setPayerBank(text(node, "payerBank"));
        ext.setPayeeName(text(node, "payeeName"));
        ext.setPayeeNumber(text(node, "payeeNumber"));
        ext.setPayeeBank(text(node, "payeeBank"));
        ext.setSealReceiving(text(node, "sealReceiving"));
        ext.setEleNontaxInvoice(text(node, "eleNontaxInvoice"));
        ext.setVoucherNumber(text(node, "voucherNumber"));
        ext.setAuctionTaxName(text(node, "auctionTaxName"));
        ext.setAuctionTaxNo(text(node, "auctionTaxNo"));
        ext.setTaxAuthorityNo(text(node, "taxAuthorityNo"));
        return ext;
    }

    // ==================== 明细（invoiceLists） ====================

    /**
     * invoice JsonNode → 明细列表；空数组返回空列表；全空行跳过
     */
    public List<InvoiceDetail> toDetails(JsonNode node) {
        List<InvoiceDetail> details = new ArrayList<>();
        if (node == null || node.isNull() || node.isMissingNode()) {
            return details;
        }
        JsonNode list = node.get("invoiceLists");
        if (list == null || !list.isArray()) {
            return details;
        }
        for (JsonNode row : list) {
            if (row == null || row.isNull() || row.isMissingNode()) {
                continue;
            }
            InvoiceDetail d = new InvoiceDetail();
            d.setCommodityNo(text(row, "commodityNo"));
            d.setCommodityName(text(row, "commodityName"));
            d.setSpecificationModel(text(row, "specificationModel"));
            d.setUnit(text(row, "unit"));
            d.setQuantity(text(row, "quantity"));
            d.setQuantityUnit(text(row, "quantity_unit"));
            d.setUnitPrice(text(row, "unitPrice"));
            d.setAmount(text(row, "amount"));
            d.setTaxRate(text(row, "taxRate"));
            d.setTax(text(row, "tax"));
            // 兼容接口两种税率百分比键：taxPercentage / taxRatePercentage
            String taxPercentage = text(row, "taxPercentage");
            d.setTaxPercentage(taxPercentage != null ? taxPercentage : text(row, "taxRatePercentage"));
            d.setTaxPaid(text(row, "taxPaid"));
            d.setDutyNo(text(row, "dutyNo"));
            d.setStandard(text(row, "standard"));
            d.setVoucherNumber(text(row, "voucherNumber"));
            d.setTimeHorizon(text(row, "timeHorizon"));
            d.setStorageDate(text(row, "storageDate"));
            d.setTaxCategories(text(row, "taxCategories"));
            d.setItemsName(text(row, "itemsName"));
            // 滴滴明细
            d.setCarType(text(row, "carType"));
            d.setTimeGetOn(text(row, "timeGeton"));
            d.setCity(text(row, "city"));
            d.setFromPlace(text(row, "from"));
            d.setToPlace(text(row, "to"));
            d.setMileage(text(row, "mileage"));
            // 建筑服务明细
            d.setLocationConstructionService(text(row, "locationConstructionService"));
            d.setConstructionName(text(row, "constructionName"));
            // 明细备注 → BaseEntity.remark
            d.setRemark(text(row, "remarks"));
            // 原始行 JSON 冗余（保留类型特有字段）
            d.setDetailJson(row.toString());

            // 全空行跳过
            if (d.getCommodityName() == null && d.getAmount() == null
                    && d.getTax() == null && d.getCommodityNo() == null) {
                continue;
            }
            details.add(d);
        }
        return details;
    }

    // ==================== 私有工具方法 ====================

    /** 取字符串值：缺失/空串统一转 null */
    private String text(JsonNode node, String key) {
        if (node == null) {
            return null;
        }
        JsonNode value = node.get(key);
        if (value == null || value.isNull() || value.isMissingNode()) {
            return null;
        }
        String s = value.asText();
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    /** 取整数值：数字或数字字符串，无法解析返回 null */
    private Integer intVal(JsonNode node, String key) {
        if (node == null) {
            return null;
        }
        JsonNode value = node.get(key);
        if (value == null || value.isNull() || value.isMissingNode()) {
            return null;
        }
        if (value.canConvertToInt()) {
            return value.asInt();
        }
        String s = value.asText();
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 取 JSON 文本（数组/对象原样 toString），缺失返回 null */
    private String jsonText(JsonNode node, String key) {
        if (node == null) {
            return null;
        }
        JsonNode value = node.get(key);
        if (value == null || value.isNull() || value.isMissingNode()) {
            return null;
        }
        return value.toString();
    }

    /** 判断节点中是否存在任一非空字段 */
    private boolean hasAny(JsonNode node, String... keys) {
        if (node == null) {
            return false;
        }
        for (String key : keys) {
            JsonNode value = node.get(key);
            if (value != null && !value.isNull() && !value.isMissingNode() && !value.asText().isBlank()) {
                return true;
            }
        }
        return false;
    }

    /** 购方地址电话：优先合并键 purchaserAddressPhone，缺失时用 address + phone 拼接 */
    private String purchaserAddressPhone(JsonNode node) {
        String merged = text(node, "purchaserAddressPhone");
        return merged != null ? merged : concat(text(node, "purchaserAddress"), text(node, "purchaserPhone"));
    }

    /** 销方地址电话：优先合并键 salesAddressPhone，缺失时用 address + phone 拼接 */
    private String salesAddressPhone(JsonNode node) {
        String merged = text(node, "salesAddressPhone");
        return merged != null ? merged : concat(text(node, "salesAddress"), text(node, "salesPhone"));
    }

    /** 销方开户行及账号：优先合并键 salesBankAndNo，缺失时用 bankNo + bank 拼接 */
    private String salesBankAndNo(JsonNode node) {
        String merged = text(node, "salesBankAndNo");
        return merged != null ? merged : concat(text(node, "salesBankNo"), text(node, "salesBank"));
    }

    /** 两个值用空格拼接；均空返回 null */
    private String concat(String a, String b) {
        if (a == null && b == null) {
            return null;
        }
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a + " " + b;
    }

    /** qrLists 补全主表：发票代码/号码/开票日期/不含税金额为空时回填 */
    private void fillFromQrLists(Invoice invoice, JsonNode qrLists) {
        if (qrLists == null || qrLists.isNull() || qrLists.isMissingNode()) {
            return;
        }
        if (invoice.getInvoiceCode() == null) {
            invoice.setInvoiceCode(text(qrLists, "invoiceCode"));
        }
        if (invoice.getInvoiceNumber() == null) {
            invoice.setInvoiceNumber(text(qrLists, "invoiceNumber"));
        }
        if (invoice.getBillingDate() == null) {
            invoice.setBillingDate(text(qrLists, "billingDate"));
        }
        if (invoice.getTotalAmount() == null) {
            invoice.setTotalAmount(text(qrLists, "totalAmount"));
        }
    }
}
