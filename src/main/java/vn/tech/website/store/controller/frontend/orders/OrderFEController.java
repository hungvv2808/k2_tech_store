package vn.tech.website.store.controller.frontend.orders;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.frontend.AuthorizationFEController;
import vn.tech.website.store.controller.frontend.BaseFEController;
import vn.tech.website.store.controller.frontend.product.ProductFEController;
import vn.tech.website.store.repository.OrderDetailRepository;
import vn.tech.website.store.repository.OrderRepository;
import vn.tech.website.store.repository.ProductRepository;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@Scope(value = "session")
@Getter
@Setter
public class OrderFEController extends BaseFEController {
    @Inject
    private ProductFEController productFEController;
    @Inject
    private AuthorizationFEController authorizationFEController;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductRepository productRepository;


    @Override
    protected String getMenuId() {
        return null;
    }
}
