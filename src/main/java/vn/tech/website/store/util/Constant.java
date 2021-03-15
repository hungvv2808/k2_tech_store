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

    public static final String TIME_ZONE_DEFAULT = "Asia/Ho_Chi_Minh";

    //acronym for create code
    public static final String ACRONYM_PRODUCT = "SP";
    public static final String ACRONYM_ORDER = "DH";
    public static final String ACRONYM_BILL = "HD";

    //Account
    public static final String ACCOUNT_PASSWORD_DEFAULT = "123456a@";

    //resources
    public static final String RESOURCES_FE = "../frontend";

    //param cate search product FE
    public static final int CATE_PHONE = 1;
    public static final int CATE_LAPTOP = 7;
    public static final int CATE_WATCH = 2;
    public static final int CATE_HEADPHONE = 6;
    public static final int CATE_MALE = 1;
    public static final int CATE_FEMALE = 2;
    public static final int CATE_KID = 3;
}
