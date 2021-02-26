create table if not exists brand
(
    brand_id    int auto_increment
        constraint `PRIMARY`
        primary key,
    name        varchar(100) null,
    status      int(1)       null,
    create_date datetime     null,
    create_by   int          null,
    update_date datetime     null,
    update_by   int          null
);

create table if not exists category
(
    category_id int auto_increment
        constraint `PRIMARY`
        primary key,
    name        varchar(100) null,
    status      int(1)       null,
    create_date datetime     null,
    create_by   int          null,
    update_date datetime     null,
    update_by   int          null
);

create table if not exists news_category
(
    news_category_id int auto_increment
        constraint `PRIMARY`
        primary key,
    title            varchar(200) null,
    description      text         null,
    status           int(1)       null,
    create_date      datetime     null,
    create_by        int          null,
    update_date      datetime     null,
    update_by        int          null
);

create table if not exists news
(
    news_id          int auto_increment
        constraint `PRIMARY`
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

create table if not exists payment_type
(
    payment_type_id int auto_increment
        constraint `PRIMARY`
        primary key,
    name            varchar(200) null
);

create table if not exists product
(
    product_id  int auto_increment
        constraint `PRIMARY`
        primary key,
    category_id int          null,
    brand_id    int          null,
    name        varchar(200) null,
    code        varchar(10)  null,
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

create table if not exists color
(
    color_id   int auto_increment
        constraint `PRIMARY`
        primary key,
    product_id int         null,
    name       varchar(50) null,
    status     int(1)      null,
    constraint fk_color_product
        foreign key (product_id) references product (product_id)
);

create table if not exists image_product
(
    image_product_id int auto_increment
        constraint `PRIMARY`
        primary key,
    product_id       int          null,
    image_path       varchar(200) null,
    constraint `fk_image-product_product`
        foreign key (product_id) references product (product_id)
);

create table if not exists province
(
    province_id int auto_increment
        constraint `PRIMARY`
        primary key,
    name        varchar(200) null
);

create table if not exists district
(
    district_id int auto_increment
        constraint `PRIMARY`
        primary key,
    province_id int          null,
    name        varchar(200) null,
    constraint fk_district_province
        foreign key (province_id) references province (province_id)
);

create table if not exists commune
(
    commune_id  int auto_increment
        constraint `PRIMARY`
        primary key,
    province_id int          null,
    district_id int          null,
    name        varchar(200) null,
    constraint fk_commune_district
        foreign key (district_id) references district (district_id),
    constraint fk_commune_province
        foreign key (province_id) references province (province_id)
);

create table if not exists role
(
    role_id     int auto_increment
        constraint `PRIMARY`
        primary key,
    code        varchar(20)  null,
    name        varchar(200) null,
    description text         null,
    status      int(1)       null,
    type        int(1)       null,
    can_modify  int(1)       null
);

create table if not exists account
(
    account_id    int auto_increment
        constraint `PRIMARY`
        primary key,
    user_name     varchar(32)  null,
    first_name    varchar(50)  null,
    last_name     varchar(50)  null,
    date_of_birth date         null,
    gender        int(1)       null comment '0: Nam
1: Nữ',
    role_id       int          null,
    password      varchar(32)  null,
    salt          varchar(50)  null,
    email         varchar(50)  null,
    phone         char(11)     null,
    address       text         null,
    image_path    varchar(200) null,
    status        int(1)       null,
    province_id   int          null,
    district_id   int          null,
    commune_id    int          null,
    create_date   datetime     null,
    create_by     int          null,
    update_date   datetime     null,
    update_by     int          null,
    constraint fk_account_commune
        foreign key (commune_id) references commune (commune_id),
    constraint fk_account_district
        foreign key (district_id) references district (district_id),
    constraint fk_account_province
        foreign key (province_id) references province (province_id),
    constraint fk_account_role
        foreign key (role_id) references role (role_id)
);

create table if not exists `order`
(
    order_id        int auto_increment
        constraint `PRIMARY`
        primary key,
    account_id      int           null,
    payment_type_id int           null,
    code            varchar(10)   null,
    address         text          null,
    phone           char(11)      null,
    total_amount    double(22, 0) null,
    create_date     datetime      null,
    create_by       int           null,
    update_date     datetime      null,
    update_by       int           null,
    constraint fk_order_account
        foreign key (account_id) references account (account_id),
    constraint `fk_order_payment-type`
        foreign key (payment_type_id) references payment_type (payment_type_id)
);

create table if not exists order_detail
(
    order_detail_id int auto_increment
        constraint `PRIMARY`
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

create table if not exists size
(
    size_id    int auto_increment
        constraint `PRIMARY`
        primary key,
    product_id int          null,
    name       varchar(100) null,
    status     int(1)       null,
    constraint fk_size_product
        foreign key (product_id) references product (product_id)
);


