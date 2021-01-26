package vn.zeus.website.store.util;

public class DbConstant {
    public static final Integer ACCOUNT_INACTIVE_STATUS = 0;
    public static final Integer ACCOUNT_ACTIVE_STATUS = 1;
    public static final Integer ACCOUNT_LOCK_STATUS = 2;
    public static final Integer ACCOUNT_DELETE_STATUS = 3;
    public static final boolean TYPE_NEWS = false;
    public static final boolean TYPE_DECISION_TEXT = true;

    //---------------------
    // Trang thai tai san dau gia
    public static final int AUCTION_ASSET_STATUS_SUCCESS = 1; // trung dau gia
    public static final int AUCTION_ASSET_STATUS_SUCCESS_ENDED = 2; // chien thang
    //---------------------

    // Trang thai dong bo
    public static final int STATUS_API_STATUS_ACTIVE = 1; // đang đồng bộ
    public static final int STATUS_API_STATUS_INACTIVE = 0; // không hoạt động
    public static final long STATUS_API_ID = 1;

    // Trang thai thay doi version
    public static final int STATUS_VERSION_STATUS_ACTIVE = 1; // đang thay đổi version
    public static final int STATUS_VERSION_STATUS_INACTIVE = 0; // không thay đổi
    public static final long STATUS_VERSION_ID = 1;

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

    //status nguời đăng ký tham gia đấu giá
    public static final int DA_NOP_HO_SO = 0;
    public static final int DANG_KY_THANH_CONG = 1;
    public static final int DANG_KY_THAT_BAI = 2;
    public static final int TU_CHOI_THAM_GIA = 3;

    // status tình trạng đặt cọc
    public static final int CHUA_HOAN_LAI_TIEN_DAT_TRUOC = 1;
    public static final int DA_HOAN_LAI_TIEN_DAT_COC = 2;

    //status nguời đăng ký tham gia đấu giá
    public static final int ASSIGN_AUCTION_STATUS_COMING_SOON = 0;
    public static final int ASSIGN_AUCTION_STATUS_INACTIVE = 1;
    public static final int ASSIGN_AUCTION_STATUS_ACTIVE = 2;
    public static final int ASSIGN_AUCTION_STATUS_DELETED = 3;
    public static final String ASSIGN_AUCTION_STATUS_1 = "Sắp diễn ra";
    public static final String ASSIGN_AUCTION_STATUS_2 = "Đang diễn ra";
    public static final String ASSIGN_AUCTION_STATUS_3 = "Đã kết thúc";
    public static final String ASSIGN_AUCTION_STATUS_4 = "Đã tạm dừng";
    public static final String ASSIGN_AUCTION_STATUS_1_NFD = "sắp diễn ra sap dien ra";
    public static final String ASSIGN_AUCTION_STATUS_2_NFD = "đang diễn ra dang dien ra";
    public static final String ASSIGN_AUCTION_STATUS_3_NFD = "đã kết thúc da ket thuc";
    public static final String ASSIGN_AUCTION_STATUS_4_NFD = "đã tạm dừng da tam dung";

    //check asset
    public static final String ASSET_STATUS_1_NFD = "sắp diễn ra sap dien ra";
    public static final String ASSET_STATUS_2_NFD = "đang diễn ra dang dien ra";
    public static final String ASSET_STATUS_3_NFD = "đã kết thúc da ket thuc";
    public static final String ASSET_STATUS_4_NFD = "đã tạm dừng da tam dung";
    //married
    public static final boolean ACCOUNT_MARRIED = true;

    //role
    public static final int ROLE_ID_NOT_LOGIN = 0;
    public static final int ROLE_ID_USER = 1;
    public static final int ROLE_TYPE_USER = 0;
    public static final int ROLE_TYPE_ADMIN = 1;
    public static final int ROLE_STATUS_ACTIVE = 1;
    public static final int ROLE_STATUS_INACTIVE = 0;

    //org
    public static final boolean ACCOUNT_COMPANY = true;

    public static final int PAGE_SIZE = 5;

    // bid
    public static final boolean BID_STATUS_RETRACT_TRUE = true;
    public static final boolean BID_STATUS_RETRACT_FALSE = false;
    public static final int BID_WINNER_SN_1TH = 1;
    public static final int BID_WINNER_SN_2TH = 2;
    public static final int BID_WINNER_NTH_1TH = 1;
    public static final int BID_WINNER_NTH_2TH = 2;

    // asset_management
    public static final boolean ASSET_MANAGEMENT_ENDING_BAD = false;
    public static final boolean ASSET_MANAGEMENT_ENDING_GOOD = true;

    // asset
    public static final int ASSET_STATUS_WAITING = 1;
    public static final int ASSET_STATUS_PLAYING = 2;
    public static final int ASSET_STATUS_ENDED = 3;
    public static final int ASSET_STATUS_NOT_SUCCESS = 4;
    public static final int ASSET_STATUS_CANCELED = 5;

    // asset_file
    public static final int ASSET_FILE_TYPE_ATTACH = 1;
    public static final int ASSET_FILE_TYPE_CANCEL = 2;

    // system_config
    public static final long SYSTEM_CONFIG_ID_DEPOSITION = 1;
    public static final long SYSTEM_CONFIG_ID_WAITING_1TH = 2;
    public static final long SYSTEM_CONFIG_ID_WAITING_2TH = 3;
    public static final long SYSTEM_CONFIG_ID_BREAK_TIME = 4;
    public static final long SYSTEM_CONFIG_ID_SHOW_CAPTCHA = 5;
    public static final long SYSTEM_CONFIG_ID_NUMBER_WRONG_SHOW_CAPTCHA = 6;
    public static final long SYSTEM_CONFIG_ID_NUMBER_LOGIN_FAILED = 7;

    // regulation
    public static final int REGULATION_AUCTION_STATUS_WAITING = 1;
    public static final int REGULATION_AUCTION_STATUS_PLAYING = 2;
    public static final int REGULATION_AUCTION_STATUS_ENDED = 3;
    public static final int REGULATION_AUCTION_STATUS_CANCELLED = 4;
    public static final int REGULATION_AUCTION_STATUS_NOT_HAPPEN = 5;
    public static final int REGULATION_STATUS_NOT_PUBLIC = 0;
    public static final int REGULATION_STATUS_PUBLIC = 1;
    public static final int REGULATION_STATUS_CANCELED = 2;
    public static final int REGULATION_STATUS_NOT_HAPPENED = 5;

    // store_register
    public static final int AUCTION_REGISTER_STATUS_WAITING = 0; //Đã nộp hồ sơ
    public static final int AUCTION_REGISTER_STATUS_ACCEPTED = 1; // Đăng ký thành công
    public static final int AUCTION_REGISTER_STATUS_REJECTED = 2; // bị đấu giá viên từ chối
    public static final int AUCTION_REGISTER_STATUS_REJECTED_JOIN = 3; // từ chối tham gia

    public static final int AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE = 0; // đã bị truất quyền
    public static final int AUCTION_REGISTER_STATUS_DEPOSIT_NORMAL = 1; // bình thường
    public static final boolean AUCTION_REGISTER_STATUS_REFUSE_WIN_NORMAL = false; // bình thường
    public static final boolean AUCTION_REGISTER_STATUS_REFUSE_WIN_REFUSED = true; // đã từ chối thắng cuộc
    public static final boolean AUCTION_REGISTER_STATUS_JOINED_JOIN = true; // đã tham gia
    public static final boolean AUCTION_REGISTER_STATUS_JOINED_NOT_JOIN = false; // không tham gia

    // trạng thái hoàn tiền
    public static final int AUCTION_REGISTER_STATUS_REFUND_ZERO = 0; // chưa đặt cọc
    public static final int AUCTION_REGISTER_STATUS_REFUND_ONE = 1; /*chưa hoàn tiền*/
    public static final int AUCTION_REGISTER_STATUS_REFUND_TWO = 2; /*đã hoàn tiền*/
    public static final int AUCTION_REGISTER_STATUS_REFUND_THREE = 3; /*đã nộp tiền*/
    public static final int AUCTION_REGISTER_STATUS_REFUND_FOUR = 4; /*Chờ đối soát*/

    // store_formality
    public static final int AUCTION_FORMALITY_ID_POLL = 1;
    public static final String AUCTION_FORMALITY_CODE_POLL = "HT01";
    public static final int AUCTION_FORMALITY_ID_DIRECT = 2;
    public static final String AUCTION_FORMALITY_CODE_DIRECT = "HT02";
    public static final boolean AUCTION_FORMALITY_STATUS_ACTIVE = true;
    public static final boolean AUCTION_FORMALITY_STATUS_INACTIVE = false;

    // store_method
    public static final int AUCTION_METHOD_ID_UP = 1;
    public static final String AUCTION_METHOD_CODE_UP = "PT01";
    public static final int AUCTION_METHOD_ID_DOWN = 2;
    public static final String AUCTION_METHOD_CODE_DOWN = "PT02";
    public static final boolean AUCTION_METHOD_STATUS_ACTIVE = true;
    public static final boolean AUCTION_METHOD_STATUS_INACTIVE = false;

    // payment
    public static final int PAYMENT_STATUS_UNPAID = 0;
    public static final int PAYMENT_STATUS_PAID = 1;
    public static final int PAYMENT_STATUS_REFUND = 2;
    public static final boolean PAYMENT_SEND_BILL_STATUS_UNPAID = false;
    public static final boolean PAYMENT_SEND_BILL_STATUS_PAID = true;
    public static final int PAYMENT_FORMALITY_ONLINE = 1;
    public static final int PAYMENT_FORMALITY_OFFLINE = 2;

    // regulation_file
    public static final int REGULATION_FILE_TYPE_HUY_BO = 0;
    public static final int REGULATION_FILE_TYPE_QUY_CHE = 1;
    public static final int REGULATION_FILE_TYPE_BIEN_BAN = 2;

    // trạng thái Công bố quy chế
    public static final int CHUA_DANG_TAI = 0;
    public static final int DA_DANG_TAI = 1;
    public static final int DA_HUY_QUY_CHE = 2;

    // cấu hình thông tin liên hệ
    public static final long MA_TITLE = 2;
    public static final long MA_ADDRESS = 3;
    public static final long MA_EMAIL = 4;
    public static final long MA_PHONE = 5;
    public static final long MA_MAPS = 6;

    // Status trạng thái đơn yêu cầu đấu giá
    public static final int CHUA_XU_LY_ID = 0;
    public static final int DANG_XU_LY_ID = 1;
    public static final int KY_HOP_DONG_ID = 2;
    public static final int KHONG_KY_HOP_DONG_ID = 3;

    // store request status - did fix name table
    public static final int AUCTION_REQ_STATUS_NO_PROCESS_ID = 0;
    public static final int AUCTION_REQ_STATUS_PROCESSED_ID = 1;
    public static final int AUCTION_REQ_STATUS_CONTRACT_ID = 2;
    public static final int AUCTION_REQ_STATUS_NO_CONTRACT_ID = 3;

    // Tiêu đề thông tin giới thiệu
    public static final long ABOUT_UNIT_INFOR = 7;
    public static final long ABOUT_HUMAN_RESOURCE = 8;
    public static final long ABOUT_FUNCTION = 9;

    // Giới hạn hiển thị ký tự
    public static final int LIMITCHARACTER = 50;
    public static final int LIMITASSETTYPE = 40;
    public static final int LIMITCHARACTER_HOME = 75;
    public static final int LIMITCHARACTER_ASSET = 93;
    public static final int LIMITCHARACTER_TITLE = 100;
    public static final int LIMITCHARACTER_SHORT_CONTENT = 150;

    public static final boolean RECEIPT_MANAGEMENT_SEND_FAIL = false;
    public static final boolean RECEIPT_MANAGEMENT_SENDED = true;

    public static final boolean IS_ORG = true;
    public static final boolean NOT_ORG = false;

    // Time to change password
    public static final Integer ACCOUNT_TIME_TO_CHANGE_PASSWORD = 90;

    // Status create receipt manager payment
    public static final int NOTIFY_PAYMENT_STATUS_NON_CREATE = 0;
    public static final int NOTIFY_PAYMENT_STATUS_CREATED = 1;

    // Status regulation report file
    public static final int REGULATION_REPORT_FILE_STATUS_NOT_YET = 0; // Chưa có biên bản
    public static final int REGULATION_REPORT_FILE_STATUS_PROCESSING = 1; // Đang xử lý
    public static final int REGULATION_REPORT_FILE_STATUS_FINISH = 2; // Đã hoàn tất

    // Init people signed report regulation
    public static final int REGULATION_REPORT_FILE_PEOPLE_SIGNED = 0;

    // Status signed of user
    public static final int REGULATION_REPORT_USER_NOT_YET_SIGNED = 0;
    public static final int REGULATION_REPORT_USER_SIGNED = 1;

    // type of image upload accuracy
    public static final boolean ACCURACY_TYPE_PERSON = false;
    public static final boolean ACCURACY_TYPE_COMPANY = true;
}
