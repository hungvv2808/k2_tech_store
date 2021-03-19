package vn.tech.website.store.controller.frontend.orders;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.CollectionUtils;
import vn.tech.website.store.controller.frontend.AuthorizationFEController;
import vn.tech.website.store.controller.frontend.BaseFEController;
import vn.tech.website.store.controller.frontend.common.PaginationController;
import vn.tech.website.store.controller.frontend.product.ProductFEController;
import vn.tech.website.store.dto.OrdersDetailDto;
import vn.tech.website.store.dto.OrdersDto;
import vn.tech.website.store.dto.user.AccountDto;
import vn.tech.website.store.model.Orders;
import vn.tech.website.store.model.OrdersDetail;
import vn.tech.website.store.model.Product;
import vn.tech.website.store.repository.OrderDetailRepository;
import vn.tech.website.store.repository.OrderRepository;
import vn.tech.website.store.repository.ProductRepository;
import vn.tech.website.store.util.Constant;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.FacesUtil;
import vn.tech.website.store.util.StringUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Named
@Scope(value = "session")
@Getter
@Setter
public class OrderFEController extends BaseFEController {
    @Inject
    private HttpServletRequest request;
    @Inject
    private ProductFEController productFEController;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductRepository productRepository;

    private PaginationController<OrdersDto> pagination;
    private Map<String, OrdersDetailDto> productCartMap;
    private AccountDto accountDto;
    private OrdersDto ordersDto;

    private boolean info;
    private Long totalQty = 0L;
    private Double totalMoney = 0D;
    private HttpSession session;

    public OrderFEController() {
        pagination = new PaginationController<>();
    }

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        info = false;
        totalQty = 0L;
        totalMoney = 0D;
        accountDto = new AccountDto();
        ordersDto = new OrdersDto();

        session = request.getSession(false);
        if (session.getAttribute("cartList") == null) {
            productCartMap = new LinkedHashMap<>();
        } else {
            productCartMap = new LinkedHashMap<>(productFEController.getMapAddToCart());
            for (OrdersDetailDto dto : productCartMap.values()) {
                totalQty += dto.getQuantity();
                totalMoney += dto.getAmount() * dto.getQuantity();
            }
        }
    }

    public void resetAllOrderInfo() {
        info = true;

        AccountDto accDto = getAuthorizationFEController().getAccountDto();
        // process account info
        accountDto = accDto == null ? new AccountDto() : accDto;

        // set value for order
        ordersDto = new OrdersDto();

        Long countCode = orderRepository.findMaxCountCode() != null ? orderRepository.findMaxCountCode() : 0;
        ordersDto.setCode(StringUtil.createCode(null, Constant.ACRONYM_ORDER, countCode));
        ordersDto.setCountCode(StringUtil.createCountCode(ordersDto.getCode(), Constant.ACRONYM_ORDER));
        ordersDto.setTotalAmount(totalMoney);
        ordersDto.setShipping(Constant.SHIPPING);
        ordersDto.setStatus(DbConstant.ORDER_STATUS_NOT_APPROVED);

        if (accountDto != null) {
            ordersDto.setAccountId(accountDto.getAccountId());
            String address = (accountDto.getAddress() == null ? "" : accountDto.getAddress() + ", ")
                    + (accountDto.getCommuneName() == null ? "" : accountDto.getCommuneName() + ", ")
                    + (accountDto.getDistrictName() == null ? "" : accountDto.getDistrictName() + ", ")
                    + (accountDto.getProvinceName() == null ? "" : accountDto.getProvinceName());
            ordersDto.setAddress(address);
            ordersDto.setPhone(accountDto.getPhone());
            ordersDto.setCustomerName(accountDto.getFullName());
        }
    }

    public void removeProduct(OrdersDetailDto dto) {
        totalQty -= dto.getQuantity();
        totalMoney -= dto.getAmount() * dto.getQuantity();

        if (productCartMap.size() == 1) {
            session.removeAttribute("cartList");
            resetAll();
            facesNotice().reload();
            return;
        }

        productCartMap.remove(dto.getCodeProduct());
        productFEController.getMapAddToCart().remove(dto.getCodeProduct());
        session.setAttribute("cartList", productFEController.getMapAddToCart());
        setSuccessForm("Xoá sản phẩm \"" + dto.getProductDto().getProductName() + "\" khỏi giỏ hàng thành công.");
        FacesUtil.updateView("orderList");
    }

    public void confirmInfo() {
        setSuccessForm("Mua hàng thành công, vui lòng xác thực thông tin của bạn.");
        resetAllOrderInfo();
        FacesUtil.updateView("orderList");
    }

    public boolean validateDataFE() {
        if (StringUtils.isBlank(ordersDto.getCustomerName())) {
            setErrorForm("Bạn vui lòng nhập tên khách hàng hoặc chọn khách hàng");
            return false;
        }
        if (StringUtils.isBlank(ordersDto.getPhone())) {
            setErrorForm("Bạn vui lòng nhập số điện thoại");
            return false;
        }
        if (StringUtils.isBlank(ordersDto.getAddress())) {
            setErrorForm("Bạn vui lòng nhập địa chỉ");
            return false;
        }
        return true;
    }

    public void onSaveOrderFE() {
        if (!validateDataFE()) {
            return;
        }
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDto, orders);
        orders.setStatus(DbConstant.ORDER_STATUS_NOT_APPROVED);
        orders.setCreateDate(new Date());
        orders.setUpdateDate(new Date());
        if (accountDto != null) {
            orders.setCreateBy(accountDto.getAccountId());
            orders.setUpdateBy(accountDto.getAccountId());
        }
        Orders ordersSave = orderRepository.save(orders);
        for (OrdersDetailDto dto : productCartMap.values()) {
            OrdersDetail ordersDetail = new OrdersDetail();
            BeanUtils.copyProperties(dto, ordersDetail);
            ordersDetail.setOrdersId(ordersSave.getOrdersId());
            orderDetailRepository.save(ordersDetail);

            // change quality product in stock
            Product product = new Product();
            BeanUtils.copyProperties(dto.getProductDto(), product);
            product.setQuantity(product.getQuantity() - dto.getQuantity());
            productRepository.save(product);
        }
        session.removeAttribute("cartList");
        setSuccessForm("Đặt hàng thành công, vui lòng chờ thông báo từ cửa hàng.");
        FacesUtil.redirect("/frontend/index.xhtml");
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
