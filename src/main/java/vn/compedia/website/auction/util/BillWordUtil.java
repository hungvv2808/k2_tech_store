package vn.compedia.website.auction.util;

import org.primefaces.model.StreamedContent;
import vn.compedia.website.auction.dto.payment.PaymentDto;
import vn.compedia.website.auction.dto.payment.ReceiptManagementDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;

public class BillWordUtil {
    public static final String TEMPLETE_FILE_BILL = "Bill.docx";
    public static final String TEMPLETE_FILE_BILL_TRANSACTION_CONTROL = "BillTransactionControl.docx";

    public static StreamedContent downloadBillTemplate(PaymentDto paymentDto) {
        ExportWordUtil<PaymentDto> exportWordUtil = new ExportWordUtil<>();
        return exportWordUtil.downloadWordFile(paymentDto, "Bill", TEMPLETE_FILE_BILL);
    }

    public static StreamedContent downloadBillTransactionControlTemplate(PaymentDto paymentDto) {
        ExportWordUtil<PaymentDto> exportWordUtil = new ExportWordUtil<>();
        return exportWordUtil.downloadWordFile(paymentDto, "BillTransactionControl", TEMPLETE_FILE_BILL_TRANSACTION_CONTROL);
    }

    public static StreamedContent downloadBillTemplate1(ReceiptManagementDto receiptManagementDto) {
        ExportWordUtil<ReceiptManagementDto> exportWordUtil = new ExportWordUtil<>();
        return exportWordUtil.downloadWordFile(receiptManagementDto, "Bill", TEMPLETE_FILE_BILL);
    }

    public static StreamedContent downloadReasonTemplate(RegulationDto regulationDto){
        ExportWordUtil<RegulationDto> exportWordUtil = new ExportWordUtil<>();
        return exportWordUtil.downloadWordFile(regulationDto, "Reason", regulationDto.getFilePathRegulation());
    }
}
