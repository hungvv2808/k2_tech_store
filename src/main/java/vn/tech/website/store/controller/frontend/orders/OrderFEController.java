package vn.tech.website.store.controller.frontend.orders;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.frontend.AuthorizationFEController;
import vn.tech.website.store.controller.frontend.BaseFEController;
import vn.tech.website.store.controller.frontend.common.PaginationController;
import vn.tech.website.store.controller.frontend.product.ProductFEController;
import vn.tech.website.store.dto.OrdersDetailDto;
import vn.tech.website.store.dto.OrdersDto;
import vn.tech.website.store.repository.OrderDetailRepository;
import vn.tech.website.store.repository.OrderRepository;
import vn.tech.website.store.repository.ProductRepository;
import vn.tech.website.store.util.FacesUtil;

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
    private ProductFEController productFEController;
    @Inject
    private AuthorizationFEController authorizationFEController;
    @Inject
    private HttpServletRequest request;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductRepository productRepository;

    private PaginationController<OrdersDto> pagination;
    private Map<String, OrdersDetailDto> productCartMap;

    private Long totalQty = 0L;
    private Double totalMoney = 0D;

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
        totalQty = 0L;
        totalMoney = 0D;

        HttpSession session = request.getSession(false);
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

    public void removeProduct(OrdersDetailDto dto) {
        HttpSession session = request.getSession(false);

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

    public void redirectFillInfo() {
        FacesUtil.redirect("/frontend/order/my-orders-info.xhtml");
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
