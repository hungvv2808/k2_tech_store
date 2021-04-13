package vn.tech.website.store.controller.admin.orders;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.CollectionUtils;
import vn.tech.website.store.controller.admin.BaseController;
import vn.tech.website.store.controller.admin.auth.AuthorizationController;
import vn.tech.website.store.dto.*;
import vn.tech.website.store.entity.EScope;
import vn.tech.website.store.model.*;
import vn.tech.website.store.repository.*;
import vn.tech.website.store.util.*;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
public class OrdersController extends BaseController {
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
    private SendNotificationRepository sendNotificationRepository;
    @Autowired
    private ReceiveNotificationRepository receiveNotificationRepository;

    private LazyDataModel<OrdersDto> lazyDataModel;
    private OrdersDto ordersDto;
    private OrdersSearchDto searchDto;
    private int orderType;
    private List<OrdersDetailDto> ordersDetailDtoList;
    private List<OrdersDetailDto> ordersDetailDtoListBak;
    private List<SelectItem> productList;
    private List<SelectItem> accountList;
    private OrdersDto ordersDtoBak;
    private OrdersDetailDto ordersDetailDto;
    private OrdersDetailDto ordersDetailDtoBak;
    private int paymentMethod;

    public void initDataOrder() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            orderType = DbConstant.ORDER_TYPE_ORDER;
            resetAll(orderType);
        }
    }

    public void initDataBill() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            orderType = DbConstant.ORDER_TYPE_BILL;
            resetAll(orderType);
        }
    }

    public void resetAll(Integer orderType) {
        ordersDto = new OrdersDto();
        searchDto = new OrdersSearchDto();
        productList = new ArrayList<>();
        List<Product> products = productRepository.getAllExpertType(DbConstant.PRODUCT_TYPE_PARENT);
        for (Product obj : products) {
            productList.add(new SelectItem(obj.getProductId(), obj.getProductName()));
        }
        ordersDetailDtoList = new ArrayList<>();
        accountList = new ArrayList<>();
        List<Account> accounts = accountRepository.findAccountByRoleId(3);
        for (Account obj : accounts) {
            accountList.add(new SelectItem(obj.getAccountId(), obj.getFullName()));
        }
        onSearch(orderType);
    }

    public void onSearch(Integer orderType) {
        searchDto.setStatusInit(orderType);
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<OrdersDto>() {
            @Override
            public List<OrdersDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
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
                return orderRepository.search(searchDto);
            }

            @Override
            public OrdersDto getRowData(String rowKey) {
                List<OrdersDto> dtoList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (OrdersDto obj : dtoList) {
                    if (obj.getOrdersId().equals(Long.valueOf(value))) {
                        return obj;
                    }
                }
                return null;
            }
        };

        int count = orderRepository.countSearch(searchDto).intValue();
        lazyDataModel.setRowCount(count);
    }

    public void resetDialog() {
        ordersDto = new OrdersDto();
        ordersDetailDtoList = new ArrayList<>();
    }

    public void updatePanel() {
        if (ordersDto.getAccountId() != null) {
            Account account = accountRepository.findByAccountId(ordersDto.getAccountId());
            ordersDto.setCustomerName(account.getFullName());
            ordersDto.setPhone(account.getPhone());
            ordersDto.setAddress(account.getAddress());
        }
        FacesUtil.updateView("dlForm");
    }

    public void updatePrice(Integer index) {
        Double unitPrice = 0D;
        Float discount = 0F;
        if (ordersDetailDtoList.get(index).getProductId() != null) {
            unitPrice = productRepository.getByProductId(ordersDetailDtoList.get(index).getProductId()).getPrice();
            ordersDetailDtoList.get(index).setUnitPrice(unitPrice);
            discount = productRepository.getByProductId(ordersDetailDtoList.get(index).getProductId()).getDiscount() != null ? productRepository.getByProductId(ordersDetailDtoList.get(index).getProductId()).getDiscount() : 0;
            ordersDetailDtoList.get(index).setDiscount(discount);
            FacesUtil.updateView("dlForm");
        }
        if (ordersDetailDtoList.get(index).getQuantity() != null) {
            Double amount = (unitPrice - unitPrice * discount / 100) * ordersDetailDtoList.get(index).getQuantity();
            ordersDetailDtoList.get(index).setAmount(amount);
            if (index == 0) {
                ordersDto.setTotalAmount(amount);
            } else {
                ordersDto.setTotalAmount(ordersDto.getTotalAmount() + amount);
            }
            FacesUtil.updateView("dlForm");
        }
    }

    public boolean validateData() {
        if (StringUtils.isBlank(ordersDto.getCustomerName())) {
            FacesUtil.addErrorMessage("Bạn vui lòng nhập tên khách hàng hoặc chọn khách hàng");
            return false;
        }
        if (StringUtils.isBlank(ordersDto.getPhone())) {
            FacesUtil.addErrorMessage("Bạn vui lòng nhập số điện thoại");
            return false;
        }
        if (StringUtils.isBlank(ordersDto.getAddress())) {
            FacesUtil.addErrorMessage("Bạn vui lòng nhập địa chỉ");
            return false;
        }
        if (CollectionUtils.isEmpty(ordersDetailDtoList)) {
            FacesUtil.addErrorMessage("Bạn vui lòng thêm loại sản phẩm");
            return false;
        }
        for (OrdersDetailDto obj : ordersDetailDtoList) {
            if (obj.getProductId() == null) {
                FacesUtil.addErrorMessage("Bạn vui lòng chọn sản phẩm");
                return false;
            }
            if (obj.getQuantity() == null) {
                FacesUtil.addErrorMessage("Bạn vui lòng nhập số lượng sản phẩm");
                return false;
            }
        }
        return true;
    }

    public void onSave() {
        if (!validateData()) {
            return;
        }
        Long countCode = orderRepository.findMaxCountCode() != null ? orderRepository.findMaxCountCode() : 0;
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDto, orders);
        if (orders.getOrdersId() == null) {
            orders.setCode(StringUtil.createCode(null, Constant.ACRONYM_ORDER, countCode));
            orders.setCountCode(StringUtil.createCountCode(orders.getCode(), Constant.ACRONYM_ORDER));
            orders.setStatus(DbConstant.ORDER_STATUS_NOT_APPROVED);
            orders.setCreateBy(authorizationController.getAccountDto().getAccountId());
            orders.setCreateDate(new Date());
        }
        orders.setShipping(ordersDto.getShipping() != null ? ordersDto.getShipping() : 0);
        orders.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        orders.setUpdateDate(new Date());
        orderRepository.save(orders);
        for (OrdersDetailDto dto : ordersDetailDtoList) {
            OrdersDetail ordersDetail = new OrdersDetail();
            ordersDetail.setOrdersId(orders.getOrdersId());
            ordersDetail.setProductId(dto.getProductId());
            ordersDetail.setQuantity(dto.getQuantity());
            ordersDetail.setAmount(dto.getAmount());
            orderDetailRepository.save(ordersDetail);
        }
        FacesUtil.addSuccessMessage("Lưu thành công.");
        FacesUtil.closeDialog("dialogInsertUpdate");
        onSearch(orderType);
    }

    public void onDeleteProduct(OrdersDetailDto ordersDetailDto) {
        ordersDto.setTotalAmount(ordersDto.getTotalAmount() - ordersDetailDto.getAmount());
        ordersDetailDtoList.remove(ordersDetailDto);
        FacesUtil.updateView("dlForm");
    }

    public void onAddNew() {
        ordersDetailDtoList.add(new OrdersDetailDto());
    }

    public void onUpdate(OrdersDto resultDto) {
        ordersDtoBak = new OrdersDto();
        ordersDetailDtoList = new ArrayList<>();
        ordersDto = new OrdersDto();
        OrdersDetailDto ordersDetailDtoNew = new OrdersDetailDto();
        List<OrdersDetail> listOrdersDetails = orderDetailRepository.getAllByOrdersId(resultDto.getOrdersId());
        ordersDetailDtoBak = new OrdersDetailDto();
        BeanUtils.copyProperties(resultDto, ordersDtoBak);
        convertEntityToDto(listOrdersDetails);
        BeanUtils.copyProperties(ordersDetailDtoNew, ordersDetailDtoBak);
        ordersDetailDtoListBak = new ArrayList<>();
        copyList(ordersDetailDtoList, ordersDetailDtoListBak);
        BeanUtils.copyProperties(resultDto, ordersDto);
        ordersDetailDto = ordersDetailDtoNew;
    }

    private void convertEntityToDto(List<OrdersDetail> ordersDetails) {
        ordersDetailDtoList = new ArrayList<>();
        for (OrdersDetail obj : ordersDetails) {
            OrdersDetailDto dto = new OrdersDetailDto();
            BeanUtils.copyProperties(obj, dto);
            Product product = productRepository.getByProductId(obj.getProductId());
            dto.setUnitPrice(product.getPrice());
            dto.setDiscount(product.getDiscount());
            ordersDetailDtoList.add(dto);
        }
    }

    private void copyList(List<OrdersDetailDto> fromList, List<OrdersDetailDto> toList) {
        for (OrdersDetailDto dto : fromList) {
            OrdersDetailDto obj = new OrdersDetailDto();
            BeanUtils.copyProperties(dto, obj);
            toList.add(obj);
        }
    }

    public void onChangeStatusApproved(OrdersDto resultDto) {
        if (resultDto.getStatus() == DbConstant.ORDER_STATUS_NOT_APPROVED) {
            Orders orders = orderRepository.getByOrdersId(resultDto.getOrdersId());
            orders.setStatus(DbConstant.ORDER_STATUS_APPROVED);
            orders.setUpdateBy(authorizationController.getAccountDto().getAccountId());
            orders.setUpdateDate(new Date());
            orderRepository.save(orders);

            if (orders.getAccountId() != null) {
                SendNotification sendNotification = new SendNotification();
                sendNotification.setAccountId(authorizationController.getAccountDto().getAccountId());
                sendNotification.setContent("Đơn hàng của bạn đã được duyệt");
                sendNotification.setStatus(DbConstant.SNOTIFICATION_STATUS_ACTIVE);
                sendNotification.setCreateDate(new Date());
                sendNotification.setUpdateDate(new Date());
                sendNotificationRepository.save(sendNotification);

                //receive notification
                ReceiveNotification receiveNotification = new ReceiveNotification();
                receiveNotification.setAccountId(orders.getAccountId());
                receiveNotification.setSendNotificationId(sendNotification.getSendNotificationId());
                receiveNotification.setStatus(DbConstant.RNOTIFICATION_STATUS_NOT_SEEN);
                receiveNotification.setStatusBell(DbConstant.RNOTIFICATION_STATUS_BELL_NOT_SEEN);
                receiveNotification.setCreateDate(new Date());
                receiveNotification.setUpdateDate(new Date());
                receiveNotificationRepository.save(receiveNotification);
            } else {
                EmailUtil.getInstance().sendNotificationApprovedOrder(orders.getEmail(), orders.getCustomerName(), orders.getCode(), orders.getCreateDate());
            }

        }
        if (resultDto.getStatus() == DbConstant.ORDER_STATUS_APPROVED) {
            Orders orders = orderRepository.getByOrdersId(resultDto.getOrdersId());
            orders.setStatus(DbConstant.ORDER_STATUS_NOT_APPROVED);
            orders.setUpdateBy(authorizationController.getAccountDto().getAccountId());
            orders.setUpdateDate(new Date());
            orderRepository.save(orders);
        }
        FacesUtil.addSuccessMessage("Đổi trạng thái thành công.");
        onSearch(orderType);
    }

    public void onCancelOrder(OrdersDto resultDto) {
        Orders orders = orderRepository.getByOrdersId(resultDto.getOrdersId());
        orders.setStatus(DbConstant.ORDER_STATUS_CANCEL);
        orders.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        orders.setUpdateDate(new Date());
        orderRepository.save(orders);
        FacesUtil.addSuccessMessage("Đã hủy đơn hàng.");
        onSearch(orderType);
    }

    public void onChangeStatusToPaid(OrdersDto resultDto) {
        if (paymentMethod == 2) {
            return;
        }
        Orders orders = orderRepository.getByOrdersId(resultDto.getOrdersId());
        orders.setStatus(DbConstant.ORDER_STATUS_PAID);
        String code = orders.getCode().replace(Constant.ACRONYM_ORDER, Constant.ACRONYM_BILL);
        orders.setCode(code);
        orders.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        orders.setUpdateDate(new Date());
        orderRepository.save(orders);
        Payments payments = new Payments();
        BeanUtils.copyProperties(orders, payments);
        payments.setType(paymentMethod);
        payments.setTotalAmount(orders.getTotalAmount() + ordersDto.getShipping());
        payments.setStatus(DbConstant.PAYMENT_STATUS_PAID);
        paymentsRepository.save(payments);
        FacesUtil.addSuccessMessage("Thanh toán thành công.");
        FacesUtil.closeDialog("dialogPaymentMethod");
        onSearch(orderType);
    }

    public void showChoosePaymentMethod(OrdersDto resultDto) {
        paymentMethod = 2;
        BeanUtils.copyProperties(resultDto, ordersDto);
    }

    @Override
    protected EScope getMenuId() {
        return null;
    }
}
