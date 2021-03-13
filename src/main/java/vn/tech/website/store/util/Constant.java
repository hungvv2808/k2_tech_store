package vn.tech.website.store.util;

public class Constant {

    public static final String TEXT_MESSAGE = "Text";
    public static final String ERROR_MESSAGE = "Error";
    public static final String ERROR_MESSAGE_ID = "errorMsgDialog";
    public static final String ERROR_MESSAGE_ID_1 = "errorMsgDialog1";
    public static final String ERROR_FE_GROWL_ID = ":notification";
    public static final String ERROR_GROWL_ID = "growl";
    public static final String ERROR_GROWL_DETAIL_ID = "growlDetail";

    // page size
    public static final int PAGE_INDEX = 0;
    public static final int PAGE_SIZE_ROW = 4;
    public static final int PAGE_SIZE_REGULATION_LIST = 6;
    public static final int PAGE_SIZE_ASSET_LIST = 8;
    public static final int PAGE_SIZE_MAX = 10;

    //Max file size
    public static final int MAX_FILE_SIZE = 50000000;

    // expire session
    public static final int SESSION_EXPIRE_IN = 30 * 60 * 1000; // 30 minutes

    //number wrong show captcha
    public static final int NUMBER_WRONG_PASSWORD_TO_SHOW_CAPTCHA = 3;

    //Menu trang chu
    public static final String NO_IMAGE_URL = "/images/no-image.jpeg";
    public static final String ASSET_NO_IMAGE_URL = "/frontend-layout/images/asset_image_default.jpg";
    public static final String ID_DON_YEU_CAU_DAU_GIA = "qldycdg";
    public static final String ID_THONG_TIN_LH = "ttlh";
    public static final String ID_CAU_HINH_TIN_TUC = "chtt";
    public static final String ID_CAP_NHAT_THONG_TIN_CA_NHAN = "cnttcn";
    public static final String ID_PROPERTY_SALE_HISTORY = "lsmbts";
    public static final String ID_QUAN_LY_TAI_KHOAN_NGUOI_DUNG = "qltknd";
    public static final String ID_PAYMENT_WALLET_RETURN_URL = "pwr";
    public static final String ID_MY_ACCOUNT="mac";

    //Screen frontend
    public static final String ID_INDEX_FONTEND = "hpfe";
    public static final String ID_ASSET_FRONTEND = "afe";
    public static final String ID_REGULATION_FRONTEND = "rfe";
    public static final String ID_REGULATION_DETAIL_FRONTEND = "rdfe";
    public static final String ID_ASSET_DETAIL_FRONTEND = "adfe";
    public static final String ID_MY_AUCTION = "ID_MY_AUCTION";
    public static final String ID_MY_PAYMENT = "ID_MY_PAYMENT";
    public static final String ID_COUNTDOWN_LIBRARY = "countdown-id";

    //Private key captcha
    public static final String PRIVATE_CAPTCHA_KEY = "6Ld5X84UAAAAAMnu0-8-2X9bfLEn_oTSy8JIfBFy";

    //cookie
    public static final String COOKIE_ACCOUNT = "_JCA";
    public static final String COOKIE_PASSWORD = "_JCP";

    // ID components update
    public static final String AREA_DESCRIPTION = "area-description";
    public static final String AREA_BUTTON = "area-button";
    public static final String AREA_BUTTON_BARGAIN = "area-button-bargain";
    public static final String AREA_BUTTON_ACCEPT_PRICE = "area-button-accept-price";
    public static final String AREA_BUTTON_REFUSE_WINNER = "area-button-refuse-winner";
    public static final String AREA_COUNTDOWN = "area-countdown";
    public static final String AREA_WINNER = "area-winner";
    public static final String AREA_MODAL_CANCEL_ASSET = "area-modal-cancel-asset";
    public static final String AREA_MODAL_DEPOSIT = "area-modal-deposit";
    public static final String AREA_MODAL_REJECTED = "area-modal-rejected";
    public static final String AREA_MODAL_RECEIPT_FILE_PATH = "area-modal-receipt-file-path";
    public static final String AREA_MODAL_CHANGE_ROUND = "area-modal-change-round";
    public static final String AREA_IMAGE_WINNER = "area-image-winner";
    public static final String AREA_TABLE = "area-table";
    public static final String AREA_TABLE_LIBRARY = "area-table-library";
    public static final String AREA_TABLE_ACCOUNT_LIST = "area-table-account-list";
    public static final String AREA_TABLE_AUCTION_HISTORY = "area-table-store-history";
    public static final String AREA_INCLUDE = "area-include";

    //Url template export direct payment
    public static final String TEMPLETE_EXPORT_FILE_DIRECT_PAYMENT = "/WEB-INF/export/template_direct_payment.xlsx";
    public static final String REPORT_EXPORT_FILE_DIRECT_PAYMENT = "/WEB-INF/report/";

    //Index row start
    public static final int INDEX_ROW_START_EXPORT = 10;

    //Column width
    public static final int COLUMN_WIDTH = 7168;
    public static final int COLUMN_REGISTER_NAME_WIDTH = 8700;
    //Column width STT
    public static final int COLUMN_WIDTH_STT = 2048;

    public static final String REPORT_EXPORT_FILE_VOTE = "/WEB-INF/report/";

    // Url template export vote
    public static final String TEMPLATE_EXPORT_FILE_VOTE = "/WEB-INF/export/template_vote.xlsx";
    public static final String REPORT_EXPORT_FILE_BILL = "/WEB-INF/report/Bill.docx";
    public static final String REPORT_EXPORT_FILE_BILL_TRANSACTION_CONTROL = "/WEB-INF/report/BillTransactionControl.docx";
    public static final String TEMPLATE_REPORT_ASSET_LIST_EXPORT_FILE = "/WEB-INF/template/TemplateReportAssetList.xlsx";
    public static final String TEMPLATE_REPORT_AND_STATISTICAL = "/WEB-INF/template/ReportAndStatistical.xlsx";

    // Excel export report
    public static final String REPORT_ASSET_LIST_EXPORT_FILE = "/WEB-INF/report/PrintReportAssetList.xlsx";
    public static final String REPORT_REPORT_AND_STATISTICAL = "/WEB-INF/report/ReportAndStatistical.xlsx";

    //API
    public static final int BATCH_SIZE = 30;

    //Trang dieu hanh
    public static final String NO_IMAGE_NEWS_URL = "/frontend-layout/images/tintuc.png";

    // status regulation property sale history
    public static final int HIS_REFUSE_TO_PARTICIPATE_IN_AUCTION = 1;
    public static final int HIS_INVALID_REGISTRATION = 2;
    public static final int HIS_JOIN_THE_AUCTION = 3;
    public static final int HIS_DO_NOT_PARTICIPATE_IN_THE_AUCTION = 4;
    public static final int HIS_DISQUALIFIED_BY_THE_PROFESSORS = 5;
    public static final int HIS_REFUSING_TO_WIN_THE_AUCTION = 6;
    public static final int HIS_WINNING_THE_AUCTION = 7;
    public static final int HIS_WITHDRAW_THE_PRICE_PAID = 8;

    public static final String TIME_ZONE_DEFAULT = "Asia/Ho_Chi_Minh";

    //acronym for create code
    public static final String ACRONYM_PRODUCT = "SP";
    public static final String ACRONYM_ORDER = "DH";
    public static final String ACRONYM_BILL = "HD";

    //Account
    public static final String ACCOUNT_PASSWORD_DEFAULT = "123456a@";
}
