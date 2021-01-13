package vn.compedia.website.auction.controller.frontend.user;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.CommonController;
import vn.compedia.website.auction.controller.frontend.AuthorizationFEController;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.controller.frontend.common.PaginationAjaxController;
import vn.compedia.website.auction.dto.payment.PaymentDto;
import vn.compedia.website.auction.dto.payment.PaymentSearchDto;
import vn.compedia.website.auction.model.payment.Payment;
import vn.compedia.website.auction.repository.PaymentRepository;
import vn.compedia.website.auction.system.task.AuctionTask;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.StringUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Named
@Scope(value = "session")
@Log4j2
public class PaymentHistoryController extends BaseFEController {
    @Inject
    private AuctionTask auctionTask;
    @Inject
    private AuthorizationFEController authorizationFEController;
    @Inject
    private CommonController commonController;
    @Autowired
    private PaymentRepository paymentRepository;

    private PaginationAjaxController<String> pagination;
    private PaymentSearchDto paymentSearchDto;
    private List<Payment> paymentList;

    private Payment payment;
    private Long paymentId;
    private boolean renderPopup;

    public PaymentHistoryController() {
        pagination = new PaginationAjaxController<>();
        pagination.resetAll();
    }

    public void initData(){
        if(!FacesContext.getCurrentInstance().isPostback()){
            init();
            resetAll();
        }
    }

    public void resetAll() {
        renderPopup = false;
        paymentSearchDto = new PaymentSearchDto();
        onSearch();
    }

    public void onSearch() {
        paymentSearchDto.setAccountId(authorizationFEController.getAccountDto().getAccountId());
        paymentSearchDto.setStatusList(Arrays.asList(DbConstant.PAYMENT_STATUS_PAID, DbConstant.PAYMENT_STATUS_REFUND));
        pagination.setLazyDataModel(new LazyDataModel<String>() {
            @Override
            public List<String> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                paymentSearchDto.setPageIndex(first);
                paymentSearchDto.setPageSize(pageSize);
                paymentSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder == null || sortOrder.equals(SortOrder.DESCENDING)) {
                    sort = "DESC";
                } else {
                    sort = "ASC";
                }
                paymentSearchDto.setSortOrder(sort);
                List<String> temp = new ArrayList<>();
                temp.add(buildDataString(paymentRepository.search(paymentSearchDto)));
                return temp;
            }

            @Override
            public int getRowCount() {
                return paymentRepository.countSearch(paymentSearchDto).intValue();
            }
        });
        pagination.loadData();
    }

    private String buildDataString(List<PaymentDto> paymentList) {
        int pageNumber = (pagination.getPageIndex() - 1) * pagination.getPageSize();
        int i = 1;
        for (PaymentDto paymentDto : paymentList) {
            paymentDto.setSerialNumber(pageNumber + i++);
        }
        return StringUtil.toJson(paymentList);
    }

    public void getReceiptFilePathByPaymentId() {
        renderPopup = false;
        payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment != null && StringUtils.isNotBlank(payment.getReceiptFilePath())) {
            renderPopup = true;
        }
        facesNotice().openModal("paymentDetailPopup");
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_MY_PAYMENT;
    }
}
