package vn.compedia.website.auction.controller.frontend.payment.momo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.frontend.AuthorizationFEController;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.controller.frontend.payment.other.CaptureWallet;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.AuctionRegister;
import vn.compedia.website.auction.model.payment.Payment;
import vn.compedia.website.auction.model.payment.other.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.EmailUtil;
import vn.compedia.website.auction.util.payment.PaymentVariableUtil;
import vn.compedia.website.auction.util.payment.other.constants.PaymentConstant;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Date;

@Getter
@Setter
@Named
@Scope(value = "session")
public class PaymentWalletReturnUrlController extends BaseFEController {
    @Inject
    private AuthorizationFEController authorizationFEController;
    @Autowired
    private PaymentReturnUrlResponseRepository paymentReturnUrlResponseRepository;
    @Autowired
    private PaymentReturnUrlResponseRefRepository paymentReturnUrlResponseRefRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private PaymentCheckOrderRequestRepository paymentCheckOrderRequestRepository;
    @Autowired
    private PaymentCheckOrderResponseRepository paymentCheckOrderResponseRepository;
    @Autowired
    private PaymentCheckOrderResponseRefRepository paymentCheckOrderResponseRefRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PaymentReturnUrlRequestRepository paymentReturnUrlRequestRepository;

    private String tokenCode;
    private Integer status;
    private String localMessage;
    private Long auctionRegisterId;
    private Long assetDeposit;
    private String assetName;
    private Long paymentReturnUrlRequestId;

    public void initData() throws IOException {
        if(!FacesContext.getCurrentInstance().isPostback()){
            init();
            resetAll();
        }
    }

    public void resetAll() throws IOException {
        // find object by token code
        PaymentReturnUrlResponseRef paymentReturnUrlResponseRef = paymentReturnUrlResponseRefRepository.getAllByTokenCode(tokenCode);
        // find to update status object
        PaymentReturnUrlResponse paymentReturnUrlResponse = paymentReturnUrlResponseRepository.findById(paymentReturnUrlResponseRef.getPayment_return_url_response_id()).orElse(null);

        // set value to send request
        CaptureWalletRequest captureWalletRequest = new CaptureWalletRequest();
        captureWalletRequest.setFunction(PaymentVariableUtil.getApiWalletFunctionCheck());
        captureWalletRequest.setMerchant_site_code(PaymentVariableUtil.getApiWalletMerchantSiteCode());
        captureWalletRequest.setApplication_id(Integer.parseInt(PaymentVariableUtil.getApiWalletApplicationId()));
        captureWalletRequest.setToken_code(tokenCode);
        // get response from api
        CaptureWalletResponseCheckOrder captureWalletResponseCheckOrder = CaptureWallet.processCheckOrder(captureWalletRequest);

        Date date = new Date();
        // save all field send to api to table payment_check_order_request
        CaptureWalletRequest captureWalletRequestCoppy = CaptureWallet.captureWalletRequestCoppy;
        Long paymentReturnUrlRequestId = paymentCheckOrderRequestRepository.getPaymentReturnUrlRequestId(captureWalletRequestCoppy.getToken_code());

        PaymentCheckOrderRequest paymentCheckOrderRequest = new PaymentCheckOrderRequest();
        paymentCheckOrderRequest.setFunction(captureWalletRequestCoppy.getFunction());
        paymentCheckOrderRequest.setMerchantSiteCode(captureWalletRequestCoppy.getMerchant_site_code());
        paymentCheckOrderRequest.setTokenCode(captureWalletRequestCoppy.getToken_code());
        paymentCheckOrderRequest.setChecksum(captureWalletRequestCoppy.getChecksum());
        paymentCheckOrderRequest.setApplicationId(captureWalletRequestCoppy.getApplication_id());
        paymentCheckOrderRequest.setPaymentReturnUrlRequestId(paymentReturnUrlRequestId);
        paymentCheckOrderRequest.setCreateDate(date);
        paymentCheckOrderRequest.setCreateBy(authorizationFEController.getAccountDto().getAccountId());
        paymentCheckOrderRequestRepository.save(paymentCheckOrderRequest);
        System.err.println("Save data to table payment_check_order_request complete !!!");

        // save all field send to api to table payment_check_order_response
        PaymentCheckOrderResponse paymentCheckOrderResponse = new PaymentCheckOrderResponse();
        paymentCheckOrderResponse.setResult_code(CaptureWallet.captureWalletResponseCheckOrderCoppy.getResult_code());
        paymentCheckOrderResponse.setResult_message(CaptureWallet.captureWalletResponseCheckOrderCoppy.getResult_message());
        paymentCheckOrderResponse.setPaymentReturnUrlRequestId(paymentReturnUrlRequestId);
        paymentCheckOrderResponse.setCreateDate(date);
        paymentCheckOrderResponse.setCreateBy(authorizationFEController.getAccountDto().getAccountId());
        paymentCheckOrderResponseRepository.save(paymentCheckOrderResponse);
        System.err.println("Save data to table payment_check_order_response complete !!!");

        // save all field send to api to table payment_check_order_response_ref
        PaymentCheckOrderResponseRef paymentCheckOrderResponseRef = CaptureWallet.captureWalletResponseCheckOrderCoppy.getResult_data();
        paymentCheckOrderResponseRef.setPaymentCheckOrderResponseId(paymentCheckOrderResponse.getPaymentCheckOrderResponseId());
        paymentCheckOrderResponseRef.setCreateDate(date);
        paymentCheckOrderResponseRef.setCreateBy(authorizationFEController.getAccountDto().getAccountId());
        getPaymentCheckOrderResponseRefRepository().save(paymentCheckOrderResponseRef);
        System.err.println("Save data to table payment_check_order_response_ref complete !!!");

        // set value variable for change view
        status = captureWalletResponseCheckOrder.getResult_data().getStatus();
        setLocalMessage();

        // update infor status after api response on table payment_return_url_response and payment
        paymentReturnUrlResponse.setStatus(status);
        paymentReturnUrlResponseRepository.save(paymentReturnUrlResponse);
        System.err.println("Update status on table payment_return_url_response after payment complete !!!");

        // save data on table payment
        Payment payment = new Payment();
        payment.setAuctionRegisterId(auctionRegisterId);
        payment.setMoney(assetDeposit);
        payment.setStatus(DbConstant.PAYMENT_STATUS_UNPAID);
        payment.setPaymentFormality(DbConstant.PAYMENT_FORMALITY_ONLINE);
        payment.setNote("Thu tiền đặt trước tài sản đấu giá \"" + assetName + "\"");
        payment.setCreateDate(new Date());
        payment.setCreateBy(authorizationFEController.getAccountDto().getAccountId());
        payment.setSendBillStatus(DbConstant.PAYMENT_SEND_BILL_STATUS_UNPAID);
        Payment paymentTmp = paymentRepository.save(payment);

        // update status on table payment
        paymentTmp.setCode("GD" + String.format("%06d", payment.getPaymentId()));
        paymentTmp.setStatus(status == PaymentConstant.API_WALLET_STATUS_ALREADY_PAID ? DbConstant.PAYMENT_STATUS_PAID : DbConstant.PAYMENT_STATUS_UNPAID);
        paymentRepository.save(paymentTmp);
        System.err.println("Update data on table payment complete !!!");

        // update payment id on payment return url request table
        PaymentReturnUrlRequest paymentReturnUrlRequest = paymentReturnUrlRequestRepository.findById(paymentReturnUrlRequestId).orElse(null);
        paymentReturnUrlRequest.setPaymentId(paymentTmp.getPaymentId());
        paymentReturnUrlRequestRepository.save(paymentReturnUrlRequest);
        System.err.println("Update data on table payment_return_url_request complete !!!");

        // update status on auction_register table to approved
        AuctionRegister auctionRegister = auctionRegisterRepository.findById(auctionRegisterId).orElse(null);
        auctionRegister.setStatus(status == PaymentConstant.API_WALLET_STATUS_ALREADY_PAID ? DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED : DbConstant.AUCTION_REGISTER_STATUS_WAITING);
        if (status == PaymentConstant.API_WALLET_STATUS_ALREADY_PAID) {
            auctionRegister.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE);
        }
        auctionRegisterRepository.save(auctionRegister);
        String assetName = assetRepository.getNameByAssetId(auctionRegister.getAssetId());
        String regulationCode = regulationRepository.getCodeByRegulationId(auctionRegister.getRegulationId());
        Account account = accountRepository.findAccountByAccountId(auctionRegister.getAccountId());
        String name = account.isOrg() ? account.getOrgName() : account.getFullName();
        EmailUtil.getInstance().sendNotificationAccepted(account.getEmail(),name, assetName, regulationCode, auctionRegister.getCode());
        System.err.println("Update status on table auction register complete !!!");
    }
    
    public void setLocalMessage() {
        switch (status) {
            case PaymentConstant.API_WALLET_STATUS_NEWLY_CREATED:
                localMessage = PaymentVariableUtil.getApiWalletStatusNewlyCreated();
                break;
            case PaymentConstant.API_WALLET_STATUS_PATMENT:
                localMessage = PaymentVariableUtil.getApiWalletStatusPayment();
                break;
            case PaymentConstant.API_WALLET_STATUS_ALREADY_PAID:
                localMessage = PaymentVariableUtil.getApiWalletStatusAlreadyPaid();
                break;
            case PaymentConstant.API_WALLET_STATUS_CANCELED:
                localMessage = PaymentVariableUtil.getApiWalletStatusCanceled();
                break;
            case PaymentConstant.API_WALLET_STATUS_BEING_REVIEWED:
                localMessage = PaymentVariableUtil.getApiWalletStatusBeingReviewed();
                break;
            case PaymentConstant.API_WALLET_STATUS_REFUNDING:
                localMessage = PaymentVariableUtil.getApiWalletStatusRefunding();
                break;
            case PaymentConstant.API_WALLET_STATUS_REDUNDED:
                localMessage = PaymentVariableUtil.getApiWalletStatusRefunded();
                break;
            case PaymentConstant.API_WALLET_STATUS_WITHDRAWING_MONEY:
                localMessage = PaymentVariableUtil.getApiWalletStatusWithdrawingMoney();
                break;
            case PaymentConstant.API_WALLET_STATUS_WITHDRAWED_MONEY:
                localMessage = PaymentVariableUtil.getApiWalletStatusWithdrawedMoney();
                break;
            case PaymentConstant.API_WALLET_STATUS_AWAITING_INSALLMENT_PAYMENT:
                localMessage = PaymentVariableUtil.getApiWalletStatusAwaitingInstallmentPayment();
                break;
            default:
                localMessage = "Thanh toán không thành công";
        }
    }
    
    public boolean checkErrorCode() {
        return status == PaymentConstant.API_WALLET_STATUS_ALREADY_PAID;
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_PAYMENT_WALLET_RETURN_URL;
    }
}
