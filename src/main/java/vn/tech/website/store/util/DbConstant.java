package vn.tech.website.store.util;

public class DbConstant {
    public static final Integer ACCOUNT_INACTIVE_STATUS = 0;
    public static final Integer ACCOUNT_ACTIVE_STATUS = 1;
    public static final Integer ACCOUNT_LOCK_STATUS = 2;
    public static final Integer ACCOUNT_DELETE_STATUS = 3;
    public static final boolean TYPE_NEWS = false;
    public static final boolean TYPE_DECISION_TEXT = true;

    //---------------------

    //Status int
    public static final int ACTIVE = 1;
    public static final int DEACTIVE = 2;

    //maxlengthPassword
    public static final int ACCOUNT_MAXLENGTH_PASSWORD_ADMIN = 12;
    public static final int ACCOUNT_MINLENGTH_PASSWORD_USER = 12;
    //isOrg
    public static final boolean PESONAL = false;
    public static final boolean ORGANIZATION = true;
    public static final int notOrg = 0;
    public static final int isOrg = 1;
    //username
    public static final int ACCOUNT_MINLENGTH_USERNAME = 6;

    //sex
    public static final int MAN = 0;
    public static final int WOMAN = 1;
    public static final int UNKNOW = 2;
    public static final int NOGENDER = -1;

    //role
    public static final int ROLE_ID_NOT_LOGIN = 0;
    public static final int ROLE_ID_USER = 1;
    public static final int ROLE_TYPE_USER = 0;
    public static final int ROLE_TYPE_ADMIN = 1;
    public static final int ROLE_STATUS_ACTIVE = 1;
    public static final int ROLE_STATUS_INACTIVE = 0;


    public static final int PAGE_SIZE = 5;
    // asset_management
    public static final boolean ASSET_MANAGEMENT_ENDING_BAD = false;
    public static final boolean ASSET_MANAGEMENT_ENDING_GOOD = true;

    // asset
    public static final int ASSET_STATUS_WAITING = 1;
    public static final int ASSET_STATUS_PLAYING = 2;
    public static final int ASSET_STATUS_ENDED = 3;
    public static final int ASSET_STATUS_NOT_SUCCESS = 4;
    public static final int ASSET_STATUS_CANCELED = 5;
    // system_config
    public static final long SYSTEM_CONFIG_ID_DEPOSITION = 1;
    public static final long SYSTEM_CONFIG_ID_WAITING_1TH = 2;
    public static final long SYSTEM_CONFIG_ID_WAITING_2TH = 3;
    public static final long SYSTEM_CONFIG_ID_BREAK_TIME = 4;
    public static final long SYSTEM_CONFIG_ID_SHOW_CAPTCHA = 5;
    public static final long SYSTEM_CONFIG_ID_NUMBER_WRONG_SHOW_CAPTCHA = 6;
    public static final long SYSTEM_CONFIG_ID_NUMBER_LOGIN_FAILED = 7;


    // payment
    public static final int PAYMENT_STATUS_UNPAID = 0;
    public static final int PAYMENT_STATUS_PAID = 1;
    public static final int PAYMENT_STATUS_REFUND = 2;
    public static final boolean PAYMENT_SEND_BILL_STATUS_UNPAID = false;
    public static final boolean PAYMENT_SEND_BILL_STATUS_PAID = true;
    public static final int PAYMENT_FORMALITY_ONLINE = 1;
    public static final int PAYMENT_FORMALITY_OFFLINE = 2;

    // cấu hình thông tin liên hệ
    public static final long MA_TITLE = 2;
    public static final long MA_ADDRESS = 3;
    public static final long MA_EMAIL = 4;
    public static final long MA_PHONE = 5;
    public static final long MA_MAPS = 6;


    public static final boolean IS_ORG = true;
    public static final boolean NOT_ORG = false;

    // Time to change password
    public static final Integer ACCOUNT_TIME_TO_CHANGE_PASSWORD = 90;

    // Status create receipt manager payment
    public static final int NOTIFY_PAYMENT_STATUS_NON_CREATE = 0;
    public static final int NOTIFY_PAYMENT_STATUS_CREATED = 1;

    //status brand
    public static final int STATUS_BRAND_INACTIVE = 0;
    public static final int STATUS_BRAND_ACTIVE = 1;

    //status category
    public static final int STATUS_CATEGORY_INACTIVE = 0;
    public static final int STATUS_CATEGORY_ACTIVE = 1;

    //product option
    public static final int STATUS_OPTION_INACTIVE = 0;
    public static final int STATUS_OPTION_ACTIVE = 1;
    public static final int TYPE_OPTION_SIZE = 0; public static final String TYPE_OPTION_SIZE_STRING = "Size";
    public static final int TYPE_OPTION_COLOR = 1; public static final String TYPE_OPTION_COLOR_STRING = "Màu";
    public static final int TYPE_OPTION_RELEASE = 2; public static final String TYPE_OPTION_RELEASE_STRING = "Năm sản xuất";

    //type product
    public static final int TYPE_PRODUCT_PARENT = 0;
    public static final int TYPE_PRODUCT_CHILD = 1;
    public static final int TYPE_PRODUCT_NONE = 2;

    public static final String TYPE_PRODUCT_PARENT_STRING = "Sản phẩm cha";
    public static final String TYPE_PRODUCT_CHILD_STRING = "Sản phẩm con";
    public static final String TYPE_PRODUCT_NONE_STRING = "Sản phẩm duy nhất";
}
