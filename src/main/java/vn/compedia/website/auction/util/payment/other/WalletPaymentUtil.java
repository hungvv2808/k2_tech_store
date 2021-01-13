package vn.compedia.website.auction.util.payment.other;

import vn.compedia.website.auction.controller.frontend.payment.other.CaptureWallet;
import vn.compedia.website.auction.model.payment.other.*;

import java.io.IOException;
import java.util.Date;

public class WalletPaymentUtil {
    public static PaymentReturnUrlRequest paymentReturnUrlRequest;
    public static PaymentReturnUrlResponse paymentReturnUrlResponse;
    public static PaymentReturnUrlResponseRef paymentReturnUrlResponseRef;

    public static String getPayUrl(CaptureWalletRequest captureWalletRequest, Long accountId) throws IOException {
        CaptureWalletResponse captureWalletResponse = CaptureWallet.processCreateOrder(captureWalletRequest);

        Date date = new Date();
        paymentReturnUrlRequest = CaptureWallet.captureWalletRequestCoppy.getParent();
        paymentReturnUrlRequest.setCreateDate(date);
        paymentReturnUrlRequest.setCreateBy(accountId);

        paymentReturnUrlResponse = CaptureWallet.captureWalletResponseCoppy.getParent();
        paymentReturnUrlResponse.setCreateDate(date);
        paymentReturnUrlResponse.setCreateBy(accountId);

        paymentReturnUrlResponseRef = CaptureWallet.captureWalletResponseCoppy.getResult_data();
        paymentReturnUrlResponseRef.setCreateDate(date);
        paymentReturnUrlResponseRef.setCreateBy(accountId);

        return captureWalletResponse.getResult_data().getCheckout_url();
    }
}
