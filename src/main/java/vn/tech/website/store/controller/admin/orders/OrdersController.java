package vn.tech.website.store.controller.admin.orders;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.admin.auth.AuthorizationController;
import vn.tech.website.store.dto.*;
import vn.tech.website.store.model.Account;
import vn.tech.website.store.model.OrdersDetail;
import vn.tech.website.store.model.Product;
import vn.tech.website.store.repository.AccountRepository;
import vn.tech.website.store.repository.OrderRepository;
import vn.tech.website.store.repository.ProductRepository;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
public class OrdersController {
    @Inject
    private AuthorizationController authorizationController;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AccountRepository accountRepository;

    private LazyDataModel<OrdersDto> lazyDataModel;
    private OrdersDto ordersDto;
    private OrdersSearchDto searchDto;
    private int orderType;
    private List<OrdersDetailDto> ordersDetailDtoList;
    private List<SelectItem> productList;
    private List<SelectItem> accountList;

    public void initDataOrder() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            //init();
            orderType = DbConstant.ORDER_TYPE_ORDER;
            resetAll(orderType);
        }
    }

    public void initDataBill() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            //init();
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

    public void resetDialog(){
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
    }

    public void updatePrice(Integer index) {
        Double unitPrice = 0D;
        Float discount = 0F;
        if (ordersDetailDtoList.get(index).getProductId() != null) {
            unitPrice = productRepository.getByProductId(ordersDetailDtoList.get(index).getProductId()).getPrice();
            ordersDetailDtoList.get(index).setUnitPrice(unitPrice);
            discount = productRepository.getByProductId(ordersDetailDtoList.get(index).getProductId()).getDiscount() != null ? productRepository.getByProductId(ordersDetailDtoList.get(index).getProductId()).getDiscount() : 0;
            ordersDetailDtoList.get(index).setDiscount(discount);
        }
        if (ordersDetailDtoList.get(index).getQuantity() != null) {
            Double amount = (unitPrice - unitPrice * discount / 100) * ordersDetailDtoList.get(index).getQuantity();
            ordersDetailDtoList.get(index).setAmount(amount);
        }
    }

    public void onDeleteProduct(OrdersDetailDto ordersDetailDto) {
        ordersDetailDtoList.remove(ordersDetailDto);
    }

    public void onAddNew() {
        ordersDetailDtoList.add(new OrdersDetailDto());
    }

}
