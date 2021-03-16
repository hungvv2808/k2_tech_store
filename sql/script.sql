use k2_tech_store;
# missing notification table
### table province/district/commune ###
drop table if exists province;
create table if not exists province
(
    province_id int auto_increment primary key,
    name        nvarchar(200) null comment 'Tên tỉnh'
);

drop table if exists district;
create table if not exists district
(
    district_id int auto_increment primary key,
    province_id int           null comment 'Id tỉnh',
    name        nvarchar(200) null comment 'Tên quận/huyện',
    constraint fk_district_province
        foreign key (province_id) references province (province_id)
);

drop table if exists commune;
create table if not exists commune
(
    commune_id  int auto_increment primary key,
    province_id int           null comment 'Id tỉnh',
    district_id int           null comment 'Id quận/huyện',
    name        nvarchar(200) null comment 'Tên phường/xã',
    constraint fk_commune_district
        foreign key (district_id) references district (district_id),
    constraint fk_commune_province
        foreign key (province_id) references province (province_id)
);
### close province/district/commune ###

### table account ###
drop table if exists role;
create table if not exists role
(
    role_id    int primary key comment '0: admin / 1: quản trị - nhân viên / 2: quản trị - quản lý / 3: khách hàng',
    name       nvarchar(200) null comment 'admin/employee/manager/customer',
    status     int(1)        null comment '0: active / 1: disable',
    can_modify int(1) default 0 comment 'F(full)/A(all)/C(create)/U(update)/D(delete)/W(watch) - Cấu hình quyền thêm sửa xoá cho từng quyền trong constant'
);

drop table if exists account;
create table if not exists account
(
    account_id    int auto_increment primary key,
    user_name     nvarchar(100) null comment 'Tên đăng nhập',
    full_name     nvarchar(100) null comment 'Họ và tên',
    date_of_birth datetime      null comment 'Ngày sinh',
    gender        int(1) default 0 comment '0: Không xác định / 1: Nam / 2: Nữ',
    role_id       int           null comment 'Quyền truy cập',
    password      nvarchar(200) null comment 'Mật khẩu - mã hoá',
    salt          nvarchar(100) null comment 'Mã sha5 để mã hoá mật khẩu',
    email         nvarchar(50)  null comment 'Địa chỉ email',
    phone         char(11)      null comment 'Số điện thoại',
    address       text          null comment 'Địa chi',
    avatar_path   nvarchar(200) null comment 'Ảnh đại diện',
    status        int(1)        null comment 'Trạng thái kích hoạt tài khoản: 0: active / 1: disable',
    province_id   int           null comment 'Tỉnh id',
    district_id   int           null comment 'Quận/Huyện id',
    commune_id    int           null comment 'Phường/Xã id',
    create_date   datetime      null comment 'Ngày tạo',
    create_by     int           null comment 'Nười tạo',
    update_date   datetime      null comment 'Ngày cập nhật',
    update_by     int           null comment 'Người cập nhật',
    constraint fk_account_role
        foreign key (role_id) references role (role_id),
    constraint fk_province
        foreign key (province_id) references province (province_id),
    constraint fk_district
        foreign key (district_id) references district (district_id),
    constraint fk_commune
        foreign key (commune_id) references commune (commune_id)
);
### close account ###

### table brand ###
drop table if exists brand;
create table if not exists brand
(
    brand_id    int auto_increment primary key,
    code        nvarchar(100) null comment 'Mã thương hiệu',
    name        nvarchar(100) null comment 'Tên thương hiệu',
    status      int(1)        null comment 'Trạng thái hoạt động của thương hiệu. 0: active / 1: disable',
    create_date datetime      null comment 'Ngày tạo',
    create_by   int           null comment 'Nười tạo',
    update_date datetime      null comment 'Ngày cập nhật',
    update_by   int           null comment 'Người cập nhật'
);
### close brand ###

### table category ###
drop table if exists category;
create table if not exists category
(
    category_id int auto_increment primary key,
    code        nvarchar(100) null comment 'Mã nhãn hiệu',
    name        nvarchar(100) null comment 'Tên nhãn hiệu',
    type        nvarchar(10)  null comment 'Kiểu cate: 0 - news / 1 - products',
    status      int(1)        null comment 'Trạng thái hoạt động nhãn hiệu. 0: active / 1: disable',
    create_date datetime      null comment 'Ngày tạo',
    create_by   int           null comment 'Nười tạo',
    update_date datetime      null comment 'Ngày cập nhật',
    update_by   int           null comment 'Người cập nhật'
);
### close category ###

### table news ###
drop table if exists news;
create table if not exists news
(
    news_id       int auto_increment primary key,
    title         nvarchar(200) null comment 'Tiêu đề tin tức',
    short_content nvarchar(200) null comment 'Tóm tắt tin tức',
    content       text          null comment 'Chi tiết bài viết',
    category_id   int           null comment 'cate',
    status        int(1)        null comment 'Trạng thái hiển thị tin tức: 0: active / 1: disable',
    img_path      varchar(200),
    create_date   datetime      null comment 'Ngày tạo',
    create_by     int           null comment 'Nười tạo',
    update_date   datetime      null comment 'Ngày cập nhật',
    update_by     int           null comment 'Người cập nhật',
    constraint fk_category foreign key (category_id) references category (category_id)
);
### close news ###

### table product ###
drop table if exists product;
create table if not exists product
(
    product_id  int auto_increment primary key,
    brand_id    int           null comment 'Thương hiệu id',
    category_id int           null comment 'Nhãn hiệu id',
    name        nvarchar(200) null comment 'Tên sản phẩm',
    code        nvarchar(100) null comment 'Mã sản phẩm',
    quantity    bigint        null comment 'Số lượng sản phẩm',
    type        int(1)        null comment 'Loại sản phẩm: 0 - parent / 1 - child / 2 - none',
    description text          null comment 'Tóm tắt thông tin sản phẩm',
    price       double        null comment 'Giá sản phẩm theo từng lựa chọn',
    discount    float         null comment 'Mức giảm giá cho từng sản phẩm nhỏ',
    status      int(1)        null comment 'Trạng thái hiển thị cho sản phẩm. 0: active / 1: disable',
    create_date datetime      null comment 'Ngày tạo',
    create_by   int           null comment 'Nười tạo',
    update_date datetime      null comment 'Ngày cập nhật',
    update_by   int           null comment 'Người cập nhật',
    constraint fk_product_brand
        foreign key (brand_id) references brand (brand_id),
    constraint fk_product_category
        foreign key (category_id) references category (category_id)
);

drop table if exists product_link;
create table if not exists product_link
(
    product_link_id int auto_increment primary key,
    parent_id       int null comment 'Id sản phẩm cha',
    child_id        int null comment 'Id sản phẩm con'
);

drop table if exists product_image;
create table if not exists product_image
(
    product_image_id int auto_increment primary key,
    product_id       int           null comment 'Id sản phẩm',
    image_name       nvarchar(200) null comment 'Tên ảnh',
    image_path       nvarchar(200) null comment 'Địa chỉ ảnh',
    is_main          int(1)        null comment '0 - ảnh phụ / 1 - ảnh chính',
    create_date      datetime      null comment 'Ngày tạo',
    create_by        int           null comment 'Nười tạo',
    update_date      datetime      null comment 'Ngày cập nhật',
    update_by        int           null comment 'Người cập nhật',
    constraint fk_product_detail
        foreign key (product_id) references product (product_id)
);

drop table if exists product_option;
create table if not exists product_option
(
    product_option_id int auto_increment primary key,
    name              nvarchar(100) null comment 'Tên lựa chọn: Size, Color, Version',
    value             nvarchar(200) null comment 'Tóm tắt thông tin lựa chọn',
    type              nvarchar(100) null comment 'Loại lựa chọn size / color / version(release year)',
    status            int(1)        null comment 'Trạng thái hiển thị của lựa chọn. 0: active / 1: disable',
    create_date       datetime      null comment 'Ngày tạo',
    create_by         int           null comment 'Nười tạo',
    update_date       datetime      null comment 'Ngày cập nhật',
    update_by         int           null comment 'Người cập nhật'
);

drop table if exists product_option_detail;
create table if not exists product_option_detail
(
    product_option_detail_id int auto_increment primary key,
    product_id               int null comment 'Id sản phẩm - tất cả sản phẩm dùng chung tên và code, khác nhau ở những chi tiết trong bảng này',
    product_option_id        int null comment 'Id lựa chọn - mỗi sản phẩm có lựa chọn size, color khác nhau',
    constraint fk_product_option
        foreign key (product_option_id) references product_option (product_option_id),
    constraint fk_product
        foreign key (product_id) references product (product_id)
);
### close product ###

### table order ###
drop table if exists orders;
create table if not exists orders
(
    orders_id    int auto_increment primary key,
    account_id   int           null comment 'Id khách đặt hoá đơn',
    code         nvarchar(100) null comment 'Mã hoá đơn',
    address      text          null comment 'Địa chỉ giao hàng',
    phone        nvarchar(11)  null comment 'Số điện thoại nhận hàng',
    note         text          null comment 'Ghi chú của khách hàng',
    total_amount double(22, 0) null comment 'Tổng tiền đơn hàng',
    shipping     double(22, 0) null comment 'Phí ship cho đơn hàng',
    create_date  datetime      null comment 'Ngày tạo',
    create_by    int           null comment 'Nười tạo',
    update_date  datetime      null comment 'Ngày cập nhật',
    update_by    int           null comment 'Người cập nhật',
    constraint fk_order_account
        foreign key (account_id) references account (account_id)
);

drop table if exists order_detail;
create table if not exists order_detail
(
    order_detail_id int auto_increment primary key,
    orders_id       int      null comment 'Id hoá đơn',
    product_id      int      null comment 'Id sản phẩm - 1 hoá đơn có nhiều sản phẩm',
    quantity        bigint   null comment 'Số lượng của từng sản phẩm',
    amount          double   null comment 'Giá tiền của từng sản phẩm',
    create_date     datetime null comment 'Ngày tạo',
    create_by       int      null comment 'Nười tạo',
    update_date     datetime null comment 'Ngày cập nhật',
    update_by       int      null comment 'Người cập nhật',
    constraint fk_orders_id foreign key (orders_id) references orders (orders_id),
    constraint fk_product_id foreign key (product_id) references product (product_id)
);

drop table if exists payments;
create table if not exists payments
(
    payment_id   int auto_increment primary key,
    orders_id    int           null comment 'Id đơn đặt hàng',
    code         nvarchar(100) null comment 'Mã thanh toán',
    total_amount double(22, 0) null comment 'Tổng tiền của hoá đơn bao gồm tổng tiền sản phẩm + phí ship',
    type         int(1)        null comment 'Loại thanh toán. 0: offline / 1: online',
    status       int(1)        null comment 'Trạng thái thanh toán. 0: unpaid / 1: paid',
    create_date  datetime      null comment 'Ngày tạo',
    create_by    int           null comment 'Nười tạo',
    update_date  datetime      null comment 'Ngày cập nhật',
    update_by    int           null comment 'Người cập nhật',
    constraint fk_orders foreign key (orders_id) references orders (orders_id)
);
### close orders ###