package vn.tech.website.store.controller.admin.orders;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.admin.BaseController;
import vn.tech.website.store.controller.admin.auth.AuthorizationController;
import vn.tech.website.store.dto.OrdersDetailDto;
import vn.tech.website.store.dto.payment.PaymentDto;
import vn.tech.website.store.dto.payment.PaymentSearchDto;
import vn.tech.website.store.entity.EScope;
import vn.tech.website.store.model.*;
import vn.tech.website.store.repository.*;
import vn.tech.website.store.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
public class PaymentController extends BaseController {
    @Inject
    private AuthorizationController authorizationController;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private PaymentsRepository paymentsRepository;
    @Autowired
    private ShippingRepository shippingRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private ProductOptionDetailRepository productOptionDetailRepository;
    @Autowired
    private ProductOptionRepository productOptionRepository;


    private LazyDataModel<PaymentDto> lazyDataModel;
    private PaymentDto paymentDto;
    private PaymentSearchDto searchDto;
    private List<OrdersDetailDto> ordersDetailDtoList;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        paymentDto = new PaymentDto();
        searchDto = new PaymentSearchDto();
        ordersDetailDtoList = new ArrayList<>();
        onSearch();
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<PaymentDto>() {
            @Override
            public List<PaymentDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                searchDto.setPageIndex(first);
                searchDto.setPageSize(pageSize);
                searchDto.setSortField(sortField);
                String sort;
                if (sortOrder.equals(sortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                searchDto.setSortOrder(sort);
                return paymentsRepository.search(searchDto);
            }

            @Override
            public PaymentDto getRowData(String rowKey) {
                List<PaymentDto> dtoList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (PaymentDto obj : dtoList) {
                    if (obj.getPaymentId().equals(Long.valueOf(value))) {
                        return obj;
                    }
                }
                return null;
            }
        };

        int count = paymentsRepository.countSearch(searchDto).intValue();
        lazyDataModel.setRowCount(count);
    }

    public void onUpdate(PaymentDto resultDto) {
        ordersDetailDtoList = new ArrayList<>();
        paymentDto = new PaymentDto();
        List<OrdersDetail> listOrdersDetails = orderDetailRepository.getAllByOrdersId(resultDto.getOrdersId());
        convertEntityToDto(listOrdersDetails);
        Shipping shipping = shippingRepository.getByShippingId(resultDto.getOrdersDto().getShippingId());
        resultDto.setShippingName(shipping.getName());
        BeanUtils.copyProperties(resultDto, paymentDto);
    }

    private void convertEntityToDto(List<OrdersDetail> ordersDetails) {
        ordersDetailDtoList = new ArrayList<>();
        for (OrdersDetail obj : ordersDetails) {
            OrdersDetailDto dto = new OrdersDetailDto();
            BeanUtils.copyProperties(obj, dto);
            Product product = productRepository.getByProductId(obj.getProductId());
            dto.getProductDto().setProductName(product.getProductName());
            //set optionName to Show
            List<ProductOptionDetail> productOptionDetails = productOptionDetailRepository.findAllByProductId(obj.getProductId());
            String option = "";
            for (ProductOptionDetail productOptionDetail : productOptionDetails) {
                ProductOption productOption = productOptionRepository.getByProductOptionId(productOptionDetail.getProductOptionId());
                if (option == "") {
                    option += productOption.getOptionName();
                } else {
                    option += " - " + productOption.getOptionName();
                }
            }
            dto.getProductDto().setProductNameToShow(dto.getProductDto().getProductName() + "(" + option + ")");
            //set price & discount
            dto.getProductDto().setPrice(product.getPrice());
            dto.getProductDto().setDiscount(product.getDiscount() != null ? product.getDiscount() : 0);
            //set imageToShow
            dto.getProductDto().setProductImages(new LinkedHashSet<>());
            dto.getProductDto().setProductImages(productImageRepository.getImagePathByProductId(dto.getProductId()));
            if (dto.getProductDto().getProductImages().size() != 0) {
                dto.getProductDto().setImageToShow(dto.getProductDto().getProductImages().iterator().next());
            }
            ordersDetailDtoList.add(dto);
        }
    }

    @Override
    protected EScope getMenuId() {
        return null;
    }
}
