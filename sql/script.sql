create table account
(
    account_id  int auto_increment
        primary key,
    user_name   tinytext  null,
    first_name  tinytext  null,
    last_name   tinytext  null,
    role_id     int       null,
    password    tinytext  null,
    email       tinytext  null,
    phone       char(11)  null,
    address     tinytext  null,
    image_path  tinytext  null,
    status      int       null,
    create_date timestamp null,
    create_by   int       null,
    update_date timestamp null,
    update_by   int       null
);

create table brand
(
    brand_id    int auto_increment
        primary key,
    name        varchar(100) null,
    status      int(1)       null,
    create_date datetime     null,
    create_by   int          null,
    update_date datetime     null,
    update_by   int          null
);

create table category
(
    category_id int auto_increment
        primary key,
    name        varchar(100) null,
    status      int(1)       null,
    create_date datetime     null,
    create_by   int          null,
    update_date datetime     null,
    update_by   int          null
);

create table news_category
(
    news_category_id int auto_increment
        primary key,
    title            varchar(200) null,
    description      text         null,
    status           int(1)       null,
    create_date      datetime     null,
    create_by        int          null,
    update_date      datetime     null,
    update_by        int          null
);

create table news
(
    news_id          int auto_increment
        primary key,
    news_category_id int          null,
    title            varchar(200) null,
    description      text         null,
    status           int(1)       null,
    create_date      datetime     null,
    create_by        int          null,
    update_date      datetime     null,
    update_by        int          null,
    constraint `fk_news_news-category`
        foreign key (news_category_id) references news_category (news_category_id)
);

create table payment_type
(
    payment_type_id int auto_increment
        primary key,
    name            varchar(200) null
);

create table `order`
(
    order_id        int auto_increment
        primary key,
    account_id      int           null,
    payment_type_id int           null,
    name            tinytext      null,
    address         tinytext      null,
    phone           char(11)      null,
    total_amount    double(22, 0) null,
    create_date     timestamp     null,
    create_by       int           null,
    update_date     timestamp     null,
    update_by       int           null,
    constraint fk_order_account
        foreign key (account_id) references account (account_id),
    constraint `fk_order_payment-type`
        foreign key (payment_type_id) references payment_type (payment_type_id)
);

create table product
(
    product_id  int auto_increment
        primary key,
    category_id int          null,
    brand_id    int          null,
    name        varchar(200) null,
    description text         null,
    unit_price  double       null,
    discount    float        null,
    status      int(1)       null,
    create_date datetime     null,
    create_by   int          null,
    update_date datetime     null,
    update_by   int          null,
    constraint fk_product_brand
        foreign key (brand_id) references brand (brand_id),
    constraint fk_product_category
        foreign key (category_id) references category (category_id)
);

create table color
(
    color_id   int auto_increment
        primary key,
    product_id int         null,
    name       varchar(50) null,
    status     int(1)      null,
    constraint fk_color_product
        foreign key (product_id) references product (product_id)
);

create table image_product
(
    image_product_id int auto_increment
        primary key,
    product_id       int          null,
    image_path       varchar(200) null,
    constraint `fk_image-product_product`
        foreign key (product_id) references product (product_id)
);

create table order_detail
(
    order_detail_id int auto_increment
        primary key,
    order_id        int    null,
    product_id      int    null,
    quantity        int(2) null,
    amount          double null,
    constraint `fk_order-detail_order`
        foreign key (order_id) references `order` (order_id),
    constraint `fk_order-detail_product`
        foreign key (product_id) references product (product_id)
);

create table size
(
    size_id    int auto_increment
        primary key,
    product_id int          null,
    name       varchar(100) null,
    status     int(1)       null,
    constraint fk_size_product
        foreign key (product_id) references product (product_id)
);


