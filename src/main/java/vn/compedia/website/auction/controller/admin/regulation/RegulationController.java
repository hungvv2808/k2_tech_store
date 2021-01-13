package vn.compedia.website.auction.controller.admin.regulation;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.*;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.dto.regulation.RegulationSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.service.AssetService;
import vn.compedia.website.auction.service.RegulationService;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@Named
@Scope(value = "session")
@Getter
@Setter
public class RegulationController extends BaseController {
    @Inject
    private UploadSingleFileController uploadSingleFileController;
    @Inject
    private UploadSingleFileRegulationController uploadSingleFileRegulationController;
    @Inject
    private UploadSingleImageController uploadSingleImageController;
    @Inject
    private UploadMultipleImageController uploadMultipleImageController;
    @Inject
    private UploadMultipleFileController uploadMultipleFileController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;

    @Autowired
    protected AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private AuctionFormalityRepository auctionFormalityRepository;
    @Autowired
    private AssetFileRepository assetFileRepository;
    @Autowired
    private AssetService assetService;
    @Autowired
    protected AuctionReqRepository auctionReqRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected TypeAssetRepository typeAssetRepository;
    @Autowired
    protected RegulationRepository regulationRepository;
    @Autowired
    protected RegulationService regulationService;
    @Autowired
    protected RegulationFileRepository regulationFileRepository;
    @Autowired
    protected AssetImageRepository assetImageRepository;
    @Autowired
    protected AssetRepository assetRepository;

    private Regulation regulation;
    private RegulationDto regulationDto;
    private RegulationDto regulationDtoCopy;
    private RegulationSearchDto regulationSearchDto;
    private LazyDataModel<RegulationDto> lazyDataModel;
    private List<SelectItem> auctionFormalityList;
    private Long auctionReqId;
    private Long regulationId;
    private Long assetId;
    private Long accountId;
    private boolean autionId;
    private boolean autionMethodId;
    private AuctionReq auctionReq;
    private Account account;
    private RegulationFile regulationFile;
    private List<RegulationFile> regulationFileList;
    private List<AssetFile> assetFileList;
    private List<AssetImage> assetImageList;
    private AssetFile assetFile;
    private AssetImage assetImage;
    private Asset asset;
    private List<Asset> assetList;
    private int assetIndex;
    private List<AssetDto> assetDtoList;
    private AssetDto assetDto;
    private boolean disableCRUD;
    private Date toDay;
    private Regulation objBackup;
    private Date dateNow;
    private Date minusDays;
    private String timePerRound;
    private List<SelectItem> typeAssetList;
    private Long range;
    private AssetDto assetDtoTmp;
    private boolean checkButtonEdit;
    private int i = 0;
    private AssetFile assetFileShowDialog;
    private String regulationFileDialog;
    private boolean checkClosePopup;
    private boolean statusDisable;
    private RegulationFile regulationFileReason;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            if (regulationId != null) {
                getRegulationByRegulationId();
            }
        }
    }

    public void getRegulationByRegulationId() {
        regulationDto = regulationRepository.getRegulationDtoByRegulationId(regulationId);
        auctionReq = auctionReqRepository.findById(regulationDto.getAuctionReqId()).orElse(null);
        auctionReqId = auctionReq.getAuctionReqId();
        regulationFile = regulationFileRepository.getByRegulationId(DbConstant.REGULATION_FILE_TYPE_QUY_CHE, regulationId);
        uploadSingleFileRegulationController.resetAll(regulationFile.getFilePath());
        uploadSingleFileRegulationController.setFileName(regulationFile.getFileName());
        assetDtoList = assetRepository.getByRegulationId(regulationId);
        account = accountRepository.findAccountByAccountId(auctionReq.getAccountId());
        regulationDto.setNameAccount(account.getFullName());
        regulationDto.setOrgName(account.getOrgName());
        dateNow = new Date();
       /* if (regulationDto.getAuctionFormalityId() == DbConstant.AUCTION_METHOD_ID_UP || regulationDto.getAuctionFormalityId() == DbConstant.AUCTION_METHOD_ID_DOWN) {
            regulationDto.setNumberOfRounds(1);
        }*/
        for (AssetDto assetDto : assetDtoList) {
            assetImageList = assetImageRepository.findAllByAssetId(assetDto.getAssetId());
            uploadMultipleImageController.resetAll(assetImageList.stream().map(AssetImage::getFilePath).collect(Collectors.toSet()));
            assetFileList = assetFileRepository.getByAssetId(assetDto.getAssetId());
            assetDto.setAssetImageList(assetImageList);
            assetDto.setAssetFileList(assetFileList);
        }
    }

    public void resetAll() {
        if (auctionReqId == null) {
            FacesUtil.redirect("/admin/dashboard.xhtml");
            return;
        }
        regulationFileDialog = null;
        checkButtonEdit = true;
        assetDtoTmp = new AssetDto();
        assetList = new ArrayList<>();
        assetFileList = new ArrayList<>();
        assetImageList = new ArrayList<>();
        typeAssetList = new ArrayList<>();
        autionId = false;
        timePerRound = "";
        regulation = new Regulation();
        regulationFile = new RegulationFile();
        regulationFileReason = new RegulationFile();
        regulationDto = new RegulationDto();
        auctionFormalityList = new ArrayList<>();
        regulationFileList = new ArrayList<>();
        uploadSingleFileController.resetAll(null, null);
        uploadSingleFileRegulationController.resetAll(null);
        uploadSingleImageController.resetAll(null);
        uploadMultipleImageController.resetAll(null);
        uploadMultipleFileController.resetAll(null);
        auctionReq = auctionReqRepository.findAuctionReqByAuctionReqId(auctionReqId);
        regulation = regulationRepository.findRegulationByRegulationId(regulationId);
        regulationDto.setAuctionReqId(auctionReq.getAuctionReqId());
        account = accountRepository.findAccountByAccountId(auctionReq.getAccountId());
        regulationDto.setNameAccount(account.getFullName());
        regulationDto.setOrgName(account.getOrgName());
        regulationDto.setAuctionFormalityId(1L);
        regulationDto.setAuctionMethodId(1L);
        assetIndex = -1;
        toDay = DateUtil.parseDatePattern(DateUtil.plusMinute(new Date(), -24 * 60), DateUtil.FROM_DATE_FORMAT);
        assetDtoList = new ArrayList<>();
        assetDto = new AssetDto();
        dateNow= new Date();
        List<TypeAsset> typeAssets = typeAssetRepository.findTypeAssetByStatus();
        for (TypeAsset ac : typeAssets) {
            typeAssetList.add(new SelectItem(ac.getTypeAssetId(), ac.getName()));
        }
        lazyDataModel = new LazyDataModel<RegulationDto>() {
            @Override
            public List<RegulationDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        regulationDto.setHistoryStatus(true);
        actionSystemController.resetAll();
        minusDays = null;
    }

    public void initAssetFile() {
        assetFile = new AssetFile();
        uploadSingleFileController.resetAll(null, null);
        FacesUtil.updateView("dlForm1");
    }

    public void onChangeStartPrice(Long value){
        range = value;
    }

    public void addAssetFile() {
        assetFile.setFilePathName(uploadSingleFileController.getFileName());
        assetFile.setFilePath(uploadSingleFileController.getFilePath());
        assetFile.setType(DbConstant.ASSET_FILE_TYPE_ATTACH);
        if (StringUtils.isBlank(assetFile.getFileName().trim())) {
            setErrorForm("Bạn vui lòng nhập tên file tài liệu");
            return;
        }
        if (StringUtils.isBlank(uploadSingleFileController.getFilePath())) {
            setErrorForm("Bạn vui lòng tải file đính kèm");
            return;
        }
        assetFileList.add(assetFile);
        FacesUtil.addSuccessMessage("Thêm mới file đính kèm thành công");
        uploadSingleFileController.resetAll(null, null);
        FacesUtil.closeDialog("dialogInsertUpdate");
    }

    public void initAsset() {
        checkButtonEdit = true;
        i = 0;
        asset = new Asset();
        assetIndex = -1;
        assetDto = new AssetDto();
//        assetDto.setAssetFileList(new ArrayList<>());
//        assetDto.setAssetImageList(new ArrayList<>());
        uploadMultipleImageController.resetAll(null);
        assetFileList = new ArrayList<>();
        assetImageList = new ArrayList<>();
        FacesUtil.updateView("dlForm");
    }

    private boolean checkAddAsset() {

        if (StringUtils.isBlank(assetDto.getName().trim())) {
            setErrorForm("Bạn vui lòng nhập tên tài sản");
            return false;
        }

        if (assetDto.getStartingPrice() == null || assetDto.getStartingPrice() <= 0L) {
            setErrorForm("Bạn vui lòng nhập giá khởi điểm, giá khởi điểm phải lớn hơn 0.");
            return false;
        }

        if (assetDto.getDeposit() == null || assetDto.getDeposit() <= 0L) {
            setErrorForm("Bạn vui lòng nhập tiền đặt trước, tiền đặt trước phải lớn hơn 0.");
            return false;
        }

        Double minValue = (assetDto.getStartingPrice() * 0.05);
        Double maxValue = (assetDto.getStartingPrice() * 0.20);
        if (assetDto.getDeposit() < minValue.longValue() || assetDto.getDeposit() > maxValue.longValue()){
            setErrorForm("Tiền đặt trước phải nằm trong khoảng từ 5% - 20% của giá khởi điểm");
            return false;
        }

        if ( regulationDto.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_DOWN && regulationDto.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_DIRECT) {
            if (assetDto.getPriceStep() == null || assetDto.getPriceStep() <= 0L) {
                setErrorForm("Bạn vui lòng nhập mức giảm giá, mức giá giảm phải lớn hơn 0. ");
                return false;
            }

            if (assetDto.getMinPrice() == null || assetDto.getMinPrice() <= 0L || assetDto.getMinPrice() > assetDto.getStartingPrice()) {
                setErrorForm("Mức giá tổi thiểu phải lớn hơn 0 và nhỏ hơn giá khởi điểm");
                return false;
            }

            if (assetDto.getStartingPrice() != null && assetDto.getMinPrice() != null && assetDto.getPriceStep() != null) {
                Double checkDouble = (assetDto.getStartingPrice().doubleValue() - assetDto.getMinPrice().doubleValue()) / assetDto.getPriceStep().doubleValue();
                Integer checkInt = checkDouble.intValue();
                Double check = checkDouble - checkInt.doubleValue();

                if (!(check.equals(0.0))) {
                    setErrorForm("Mức giảm giá không hợp lệ, mức giảm giá phải là ước của giá khởi điểm và mức giá tối thiểu");
                    return false;
                }
            }
        }
//        else {
//            if (assetDto.getPriceStep() == null || assetDto.getPriceStep() <= 0L) {
//                setErrorForm("Bước giá là trường bắt buộc và phải lớn hơn 0. ");
//                FacesUtil.updateView("growl");
//                return;
//            }
//
//        }

        if (assetDto.getTypeAssetId() == null ) {
            setErrorForm("Bạn vui lòng chọn loại tài sản");
            return false;
        }

        if (CollectionUtils.isEmpty(uploadMultipleImageController.getUploadMultipleFileDto().getListToShow())) {
            setErrorForm("Bạn vui lòng chọn hình ảnh cho tài sản");
            return false;
        }
        return true;
    }

    public void addAssetAndRefresh() {
        checkClosePopup = false;
        if (!checkAddAsset()) {
            FacesUtil.updateView(Constant.ERROR_GROWL_ID);
            return;
        }

        assetDto.setStatus(DbConstant.ASSET_STATUS_WAITING);
        assetDto.setAssetImageList(new ArrayList<>());
        for (String imagePath : uploadMultipleImageController.getUploadMultipleFileDto().getListToShow()) {
            AssetImage assetImage = new AssetImage();
            assetImage.setFilePath(imagePath);
            assetDto.getAssetImageList().add(assetImage);
        }
        assetDto.setAssetFileList(assetFileList);
        if (assetIndex == -1) {
            assetDtoList.add(assetDto);
            setSuccessForm("Thêm mới tài sản thành công");
        } else {
            assetDtoList.set(assetIndex, assetDto);
            setSuccessForm("Chỉnh sửa tài sản thành công");
        }
        assetDto = new AssetDto();
        assetFileList = new ArrayList<>();
        uploadSingleFileController.resetAll(null, null);
        uploadMultipleImageController.resetAll(null);
        checkClosePopup = true;
        // update growl
        FacesUtil.updateView(Constant.ERROR_GROWL_ID);
    }

    public void addAsset() {
        addAssetAndRefresh();
        if (checkClosePopup) {
            FacesUtil.closeDialog("dialogInsertAsset");
        }
    }

    public void addAssetAndCopy() {
        checkClosePopup = false;
        if (!checkAddAsset()) {
            FacesUtil.updateView(Constant.ERROR_GROWL_ID);
            return;
        }

        assetDto.setStatus(DbConstant.ASSET_STATUS_WAITING);
        assetDto.setAssetImageList(new ArrayList<>());
        for (String imagePath : uploadMultipleImageController.getUploadMultipleFileDto().getListToShow()) {
            AssetImage assetImage = new AssetImage();
            assetImage.setFilePath(imagePath);
            assetDto.getAssetImageList().add(assetImage);
        }
        assetDto.setAssetFileList(assetFileList);
        if (assetIndex == -1) {
            assetDtoList.add(assetDto);
            setSuccessForm("Thêm mới tài sản thành công");
            FacesUtil.updateView("growl");
        } else {
            assetDtoList.set(assetIndex, assetDto);
            setSuccessForm("Chỉnh sửa tài sản thành công");
            FacesUtil.updateView("growl");
        }
        assetDto = new AssetDto();
        assetFileList = new ArrayList<>();
        uploadSingleFileController.resetAll(null, null);
        uploadMultipleImageController.resetAll(null);
        checkClosePopup = true;

        // copy
        AssetDto assetDtoTemp = new AssetDto();
        BeanUtils.copyProperties(assetDtoList.get(assetDtoList.size() - 1), assetDtoTemp);
        assetDto = assetDtoTemp;

        Set<String> assetStringSet = new HashSet<>();
        for (AssetImage image : assetDtoTemp.getAssetImageList()) {
            assetStringSet.add(image.getFilePath());
        }
        uploadMultipleImageController.resetAll(assetStringSet);

        assetFileList.clear();
        for (AssetFile file : assetDtoTemp.getAssetFileList()) {
            AssetFile assetFile = new AssetFile();
            assetFile.setFileName(file.getFileName());
            assetFile.setFilePath(FileUtil.clone(file.getFilePath(), false));
            assetFileList.add(assetFile);
        }
    }

    public void onSaveData() {
        if (!dateSelect()) {
            return;
        }
        if (!validateData()) {
            return;
        }
        if (regulationDto.getAuctionMethodId() != null && regulationDto.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_DOWN && regulationDto.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_DIRECT) {
            for (AssetDto assetDto : assetDtoList) {
                if (assetDto.getMinPrice() == null || assetDto.getMinPrice() <= 0L || assetDto.getMinPrice() > assetDto.getStartingPrice()) {
                    setErrorForm("Bạn vui lòng nhập mức giá tối thiểu cho từng tài sản, mức giá tối thiểu phải lớn hơn 0 và phải nhỏ hơn giá khởi điểm");
                    FacesUtil.updateView("growl");
                    return;
                }
            }
        }

        if (regulationDto.getRegulationId() != null) {
            regulation = regulationRepository.findById(regulationDto.getRegulationId()).orElse(null);
            if (regulation == null) {
                setErrorForm("Quy chế không tồn tại");
                return;
            }
            if (regulation.getStatus() == DbConstant.REGULATION_STATUS_PUBLIC) {
                setErrorForm("Quy chế đã được công bố, vì thế không thể chỉnh sửa");
                return;
            }
            if (regulation.getStatus() != DbConstant.REGULATION_STATUS_NOT_PUBLIC) {
                setErrorForm("Quy chế đã bị tạm dừng hoặc đã được công bố");
                return;
            }
        }

        regulationDto.setAuctionStatus(DbConstant.REGULATION_AUCTION_STATUS_WAITING);
        auctionReq = auctionReqRepository.findAuctionReqByAuctionReqId(auctionReqId);

        if (regulationDto.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
            regulationDto.setAuctionMethodId((long) DbConstant.AUCTION_METHOD_ID_UP);
        }

        regulationDto.setAuctionReqId(auctionReq.getAuctionReqId());
        account = accountRepository.findAccountByAccountId(auctionReq.getAccountId());
        regulationDto.setNameAccount(account.getFullName());
        regulationDto.setCreateBy(authorizationController.getAccountDto().getAccountId());
        regulation = regulationId == null ? new Regulation() : regulationRepository.findById(regulationId).orElse(new Regulation());
        BeanUtils.copyProperties(regulationDto, regulation);

        regulationService.saveSingleFile(regulation, uploadSingleFileRegulationController.getFileName(), uploadSingleFileRegulationController.getFilePath(), assetDtoList);

        if(regulationDto.getRegulationId() == null) {
            actionSystemController.onSave("Thêm quy chế \"" + regulation.getCode() + "\"", authorizationController.getAccountDto().getAccountId());
        } else {
            actionSystemController.onSave("Sửa quy chế \"" + regulation.getCode() + "\"", authorizationController.getAccountDto().getAccountId());
        }
        setSuccessForm("Lưu thành công");
        FacesUtil.redirect("/admin/quan-ly-dau-gia/quy-che-dau-gia.xhtml");
    }

    public boolean validateData() {
        if (regulationDto.getStartRegistrationDate() == null) {
            setErrorForm("Bạn vui lòng chọn thời gian bắt đầu đăng ký đấu giá");
            FacesUtil.updateView("growl");
            return false;
        }
        if (regulationDto.getEndRegistrationDate() == null) {
            setErrorForm("Bạn vui lòng chọn thời gian kết thúc đăng ký đấu giá");
            FacesUtil.updateView("growl");
            return false;
        }
        if (regulationDto.getAuctionFormalityId() == null){
            setErrorForm("Bạn vui lòng chọn hình thức đấu giá trực tuyến");
            FacesUtil.updateView("growl");
            return false;
        }
        if (regulationDto.getAuctionMethodId() == null) {
            setErrorForm("Bạn vui lòng chọn phương thức đấu giá trực tuyến");
            FacesUtil.updateView("growl");
            return false;
        }
        if (regulationDto.getStartTime() == null) {
            setErrorForm("Bạn vui lòng chọn thời gian bắt đầu đấu giá");
            FacesUtil.updateView("growl");
            return false;
        }
//        if (regulationDto.getPaymentStartTime() == null){
//            setErrorForm("Thời gian bắt đầu nộp tiền đặt trước là trường bắt buộc.");
//            FacesUtil.updateView("growl");
//            return false;
//        }
//        if (regulationDto.getPaymentEndTime() == null) {
//            setErrorForm("Thời gian kết thúc nộp tiền đặt trước là trường bắt buộc.");
//            FacesUtil.updateView("growl");
//            return false;
//        }
        if (uploadSingleFileRegulationController.getFileName() == null) {
            setErrorForm("Bạn vui lòng tải file quy chế");
            FacesUtil.updateView("growl");
            return false;
        }
        if (regulationDto.getNumberOfRounds() == null && regulationDto.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
            setErrorForm("Bạn vui nhập số vòng đấu giá");
            FacesUtil.updateView("growl");
            return false;
        }

//        if (regulationDto.getAuctionMethodId().equals(2L) && regulationDto.getAuctionFormalityId().equals(2L)) {
//            if (regulationDto.getTimeSelfOf() == null) {
//                setErrorForm("Thời gian đặt giá xuống là trường bắt buộc.");
//                return false;
//            }
//        }
/*        if (regulationDto.getAuctionFormalityId().equals(1L)) {
            if (StringUtils.isNotBlank(timePerRound)) {
                setErrorForm("Thời gian mỗi vòng là trường bắt buộc và phải lớn hơn 0.");
                return false;
            }
        }
        if (regulationDto.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_DIRECT && regulationDto.getAuctionMethodId().equals(2L)) {
            if (StringUtils.isNotBlank(timePerRound)) {
                setErrorForm("Thời gian kết thúc là trường bắt buộc và phải lớn hơn 0.");
                return false;
            }
        }*/
        if (CollectionUtils.isEmpty(assetDtoList)) {
            setErrorForm("Bạn vui lòng thêm tài sản.");
            FacesUtil.updateView("growl");
            return false;
        }
        return true;
    }

    public boolean dateSelect() {
        if (null != this.regulationDto.getStartRegistrationDate() && null != this.regulationDto.getEndRegistrationDate()
                && this.regulationDto.getStartRegistrationDate().compareTo(this.regulationDto.getEndRegistrationDate()) > 0) {
            this.regulationDto.setEndRegistrationDate(null);
            setErrorForm("Ngày và giờ thời gian bắt đầu đăng ký tham gia phải nhỏ hơn thời gian kết thúc đăng ký tham gia");
            FacesUtil.updateView("growl");
            return false;
        }

        if (null != this.regulationDto.getPaymentStartTime() && null != this.regulationDto.getPaymentEndTime()
                && this.regulationDto.getPaymentStartTime().compareTo(this.regulationDto.getPaymentEndTime()) > 0) {
            this.regulationDto.setPaymentEndTime(null);
            setErrorForm("Ngày và giờ thời gian bắt đầu nộp tiền đặt trước phải nhỏ hơn thời gian kết thúc nộp tiền đặt trước");
            FacesUtil.updateView("growl");
            return false;
        }

        if (null != this.regulationDto.getPaymentStartTime() && null != this.regulationDto.getStartRegistrationDate()
                && this.regulationDto.getPaymentStartTime().compareTo(this.regulationDto.getStartRegistrationDate()) < 0) {
            this.regulationDto.setPaymentStartTime(null);
            setErrorForm("Ngày và giờ thời gian bắt đầu nộp tiền đặt trước phải lớn hơn hoặc bằng thời gian bắt đầu đăng ký tham gia đấu giá");
            FacesUtil.updateView("growl");
            return false;
        }

        if (null != this.regulationDto.getPaymentEndTime() && null != this.regulationDto.getStartTime()
                && this.regulationDto.getPaymentEndTime().compareTo(this.regulationDto.getStartTime()) > 0) {
            this.regulationDto.setStartTime(null);
            setErrorForm("Ngày và giờ thời gian bắt đầu tổ chức đấu giá phải lớn hơn thời gian kết thúc nộp tiền đặt trước");
            FacesUtil.updateView("growl");
            return false;
        }

        if (null != this.regulationDto.getEndRegistrationDate() && null != this.regulationDto.getStartTime()
                && this.regulationDto.getEndRegistrationDate().compareTo(this.regulationDto.getStartTime()) > 0) {
            this.regulationDto.setStartTime(null);
            setErrorForm("Ngày và giờ thời gian bắt đầu tổ chức đấu giá phải lớn hơn thời gian kết thúc đăng ký tham gia đấu giá");
            FacesUtil.updateView("growl");
            return false;
        }
        return true;
    }

    public void onDateSelect() {
//       if ( null != this.getRegulationDto().getStartRegistrationDate() && this.getRegulationDto().getStartRegistrationDate().compareTo(dateNow) < 0 ){
//            this.getRegulationDto().setStartRegistrationDate(null);
//            setErrorForm("Thời gian bắt đầu đăng ký phải lớn hơn ngày hiện tại ");
//            FacesUtil.updateView("dlForm2");
//            return;
//        }
//        if (null != this.getRegulationDto().getStartRegistrationDate() && null != this.getRegulationDto().getStartTime()
//                && this.getRegulationDto().getStartRegistrationDate().compareTo(this.getRegulationDto().getStartTime()) > 0) {
//            this.getRegulationDto().setStartRegistrationDate(null);
//            setErrorForm("Thời gian bắt đầu đăng ký phải nhỏ hơn thời gian bắt đầu đấu giá ");
//            FacesUtil.updateView("dlForm2");
//            return;
//        }
//        if (null != this.getRegulationDto().getEndRegistrationDate() && null != this.getRegulationDto().getStartTime()
//                && this.getRegulationDto().getEndRegistrationDate().compareTo(this.getRegulationDto().getStartTime()) > 0) {
//            this.getRegulationDto().setEndRegistrationDate(null);
//            setErrorForm("Thời gian kết thúc đăng ký phải nhỏ hơn thời gian bắt đầu đấu giá ");
//            FacesUtil.updateView("dlForm2");
//            return;
//        }
        if (null != this.getRegulationDto().getStartRegistrationDate() && null != this.getRegulationDto().getEndRegistrationDate()
                && this.getRegulationDto().getStartRegistrationDate().compareTo(this.getRegulationDto().getEndRegistrationDate()) > 0) {
            this.getRegulationDto().setEndRegistrationDate(null);
            setErrorForm("Thời gian bắt đầu đăng ký phải nhỏ hơn thời gian kết thúc đăng ký ");
            return;
        }
        if (null != this.regulationDto.getPaymentStartTime() && null != this.regulationDto.getStartRegistrationDate()
                && this.regulationDto.getPaymentStartTime().compareTo(this.getRegulationDto().getStartRegistrationDate()) < 0) {
            this.getRegulationDto().setStartRegistrationDate(null);
            setErrorForm("Thời gian bắt đầu nộp tiền đặt trước phải nhỏ hơn thời gian kết thúc đăng ký đấu giá ");
            return;
        }
    }

    public boolean checkStarttimeWithActions(RegulationDto dto){
        if (dto.getStartTime().compareTo(new Date()) < 0){
            return true;
        }
        return false;
    }

    public void onShow(RegulationDto dto) {
        if (dto.getStatus() == DbConstant.CHUA_DANG_TAI) {
            FacesUtil.addErrorMessage("Không tạm dừng được quy chế " + dto.getCode() + "  vì quy chế này chưa được đăng tải lên Trang đấu giá");
            FacesUtil.updateView("growl");
            return;
        } else {
            statusDisable = false;
            regulationDto = dto;
            FacesUtil.showDialog("dialoglidohuybo");
        }
    }

    public StreamedContent downloadDepositionReasonFile() {
        return getFileDownload(regulationFile.getFilePath());
    }
    public void checkConfirm() {
        if (regulationDto.getStatus() != DbConstant.DA_HUY_QUY_CHE) {
            if (StringUtils.isBlank(regulationDto.getReasonCancel().trim())) {
                setErrorForm("Bạn vui lòng nhập lý do huỷ bỏ");
                return;
            }
            if (uploadSingleFileController.getFilePath() == null) {
                setErrorForm("Bạn vui lòng tải file văn bản");
                return;
            }
            regulation = regulationRepository.findById(regulationDto.getRegulationId()).orElse(null);
            if (regulation == null) {
                setErrorForm("Quy chế không tồn tại");
                return;
            }
            FacesUtil.showDialog("confirm");
        }
    }


    public void onSend() {
        // save regulation
        regulation.setReasonCancel(regulationDto.getReasonCancel());
        regulation.setStatus(DbConstant.REGULATION_STATUS_CANCELED);
        regulation.setAuctionStatus(DbConstant.REGULATION_AUCTION_STATUS_CANCELLED);
        regulationRepository.save(regulation);

        // save regulation file
        regulationFileReason.setFilePath(uploadSingleFileController.getFilePath());
        regulationFileReason.setFileName(uploadSingleFileController.getFileName());
        regulationFileReason.setType(DbConstant.REGULATION_FILE_TYPE_HUY_BO);
        regulationFileReason.setRegulationId(regulation.getRegulationId());
        regulationFileRepository.save(regulationFileReason);

        List<Asset> assetList = assetRepository.getAllByRegulationId(regulation.getRegulationId());
        for (Asset asset : assetList) {
            asset.setStatus(DbConstant.ASSET_STATUS_CANCELED);
        }
        assetRepository.saveAll(assetList);
        List<AuctionRegister> auctionRegisters = auctionRegisterRepository.findAuctionRegistersByRegulationId(regulation.getRegulationId());
        for (AuctionRegister ar : auctionRegisters) {
            Account account = accountRepository.findAccountByAccountId(ar.getAccountId());
            if (account.getAccountId() != null) {
                EmailUtil.getInstance().sendReasonCancelRegulation(account.getEmail(), FacesUtil.getRealPath(FileUtil.getFilePathFromDatabase(uploadSingleFileController.getFilePath())), uploadSingleFileController.getFileName(), account.getFullName(), regulation.getCode(), regulation.getReasonCancel());
            }
        }

        actionSystemController.onSave(" Tạm dừng quy chế " + regulation.getCode(), authorizationController.getAccountDto().getAccountId());
        setSuccessForm("Tạm dừng quy chế thành công");
        FacesUtil.closeDialog("dialoglidohuybo");
        resetAll();
        FacesUtil.redirect("/admin/quan-ly-dau-gia/quy-che-dau-gia.xhtml");
    }

    public void onDateSelectStart() {
//        minusDays = new Date();
//        minusDays = DateUtil.minusDay(regulationDto.getStartTime(),2);
//        if (null != this.regulationDto.getPaymentEndTime() && null != this.regulationDto.getStartTime()
//                && this.regulationDto.getPaymentEndTime().compareTo(this.getRegulationDto().getStartTime()) > 0) {
//            this.getRegulationDto().setStartTime(null);
//            setErrorForm("Thời gian kết thúc nộp tiền đặt trước phải nhỏ hơn thời gian bắt đầu đấu giá");
//            return;
//        }
//        if (null != this.getRegulationDto().getStartRegistrationDate() && null != this.getRegulationDto().getStartTime()
//                && this.getRegulationDto().getStartRegistrationDate().compareTo(this.getRegulationDto().getStartTime()) > 0) {
//            this.getRegulationDto().setStartTime(null);
//            setErrorForm("Thời gian bắt đầu đăng ký phải nhỏ hơn thời gian bắt đầu đấu giá ");
//            return;
//        }
//        if (null != this.getRegulationDto().getEndRegistrationDate() && null != this.getRegulationDto().getStartTime()
//                && this.getRegulationDto().getEndRegistrationDate().compareTo(this.getRegulationDto().getStartTime()) > 0) {
//            this.getRegulationDto().setStartTime(null);
//            setErrorForm("Thời gian bắt đầu đăng ký phải nhỏ hơn thời gian bắt đầu đấu giá ");
//            return;
//        }
//        if (null != this.getRegulationDto().getPaymentStartTime() && null != this.getRegulationDto().getStartTime()
//                && this.getRegulationDto().getPaymentStartTime().compareTo(this.getRegulationDto().getStartTime()) > 0) {
//            this.getRegulationDto().setStartTime(null);
//            setErrorForm("Thời gian bắt đầu nộp tiền đặt trước phải nhỏ hơn thời gian bắt đầu đấu giá ");
//            return;
//        }
//        if (null != this.getRegulationDto().getPaymentEndTime() && null != this.getRegulationDto().getStartTime()
//                && this.getRegulationDto().getPaymentEndTime().compareTo(this.getRegulationDto().getStartTime()) > 0) {
//            this.getRegulationDto().setStartTime(null);
//            setErrorForm("Thời gian kết thúc nộp tiền đặt trước phải nhỏ hơn thời gian bắt đầu đấu giá ");
//            return;
//        }
    }

    public void onDatePayMent() {
        if (null != this.getRegulationDto().getPaymentStartTime() && null != this.getRegulationDto().getStartRegistrationDate()
                && this.getRegulationDto().getPaymentStartTime().compareTo(this.getRegulationDto().getStartRegistrationDate()) < 0) {
            this.getRegulationDto().setPaymentStartTime(null);
            setErrorForm("Thời gian bắt đầu nộp tiền đặt trước phải nhỏ hơn thời gian bắt đầu đăng ký đấu giá");
            return;
        }
        if (null != this.getRegulationDto().getPaymentStartTime() && null != this.getRegulationDto().getStartTime()
                && this.getRegulationDto().getStartTime().compareTo(this.getRegulationDto().getPaymentStartTime()) < 0) {
            this.getRegulationDto().setPaymentStartTime(null);
            setErrorForm("Thời gian bắt đầu nộp tiền đặt trước phải nhỏ hơn thời gian bắt đầu đấu giá");
            return;
        }
        if (null != this.getRegulationDto().getPaymentStartTime() && null != this.getRegulationDto().getPaymentEndTime()
                && this.getRegulationDto().getPaymentStartTime().compareTo(this.getRegulationDto().getPaymentEndTime()) > 0) {
            this.getRegulationDto().setPaymentEndTime(null);
            setErrorForm("Thời gian bắt đầu nộp tiền đặt trước phải nhỏ hơn thời gian kết thúc nộp tiền đặt trước ");
            return;
        }
        if (null != this.regulationDto.getPaymentEndTime() && null != this.regulationDto.getStartTime()
                && this.regulationDto.getPaymentEndTime().compareTo(this.getRegulationDto().getStartTime()) > 0) {
            this.getRegulationDto().setPaymentEndTime(null);
            setErrorForm("Thời gian kết thúc nộp tiền đặt trước phải nhỏ hơn thời gian bắt đầu đấu giá");
            return;
        }
    }

    public String formatDate(Date date) {
        return DateUtil.formatToPattern(date, DateUtil.MMDDYYYYHHMMSS);
    }

    public void onDeleteFile(AssetFile obj) {
        assetFileList.remove(obj);
        setSuccessForm("Xóa thành công");
    }

    public void onDeleteAsset(AssetDto obj) {
        assetDtoList.remove(obj);
    }

    public void showUpdatePopupAsset(AssetDto obj) {
        checkButtonEdit = false;
        assetDto = new AssetDto();
        BeanUtils.copyProperties(obj, assetDto);
        assetFileList = new ArrayList<>();
        assetImageList = new ArrayList<>();
        assetIndex = assetDtoList.indexOf(obj);
        range = obj.getStartingPrice();
        assetImageList.addAll(assetDto.getAssetImageList());
        assetFileList.addAll(assetDto.getAssetFileList());
        uploadMultipleImageController.resetAll(assetImageList.stream().map(AssetImage::getFilePath).collect(Collectors.toSet()));
    }

    public void resetDialog() {
        autionId = false;
        regulation = new Regulation();
        regulationFile = new RegulationFile();
        regulationDto = new RegulationDto();
        auctionFormalityList = new ArrayList<>();
        regulationFileList = new ArrayList<>();
        uploadSingleFileController.resetAll(null, null);
        uploadSingleFileRegulationController.resetAll(null);
    }

    public void resetRegulation() {
        if (regulationId != null) {
            FacesUtil.redirect("/admin/quan-ly-dau-gia/quy-che-dau-gia.xhtml");
        } else {
            FacesUtil.redirect("/admin/don-yeu-cau-dau-gia/quan-ly-don-yeu-cau-dau-gia-tai-san.xhtml");
        }
    }

    public void editRegulation() {
        disableCRUD = false;
        FacesUtil.updateView("dlForm2");
    }

    public void onUpload(RegulationDto obj) {
        Regulation regulation = regulationRepository.findById(obj.getRegulationId()).orElse(null);
        regulation.setStatus(DbConstant.REGULATION_STATUS_PUBLIC);
        regulation.setAuctionStatus(DbConstant.REGULATION_AUCTION_STATUS_WAITING);
        regulationRepository.save(regulation);
        actionSystemController.onSave(" Đăng tải quy chế " + regulation.getCode(), authorizationController.getAccountDto().getAccountId());
        setSuccessForm("Đăng tải thành công");
        FacesUtil.redirect("/admin/quan-ly-dau-gia/quy-che-dau-gia.xhtml");
    }

    public void changeAution() {
        /*autionId = regulationDto.getAuctionFormalityId().equals(1L);*/
        if (regulationDto.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
            regulationDto.setNumberOfRounds(null);
            regulationDto.setAuctionMethodId((long) DbConstant.AUCTION_METHOD_ID_UP);
        }
        if (regulationDto.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_DIRECT) {
            regulationDto.setNumberOfRounds(1);
        }
        FacesUtil.updateView("dlForm");
        FacesUtil.updateView("columnRight");
    }

    public void onDeleteRegulation(RegulationDto obj) {
        try {
            regulationRepository.deleteById(obj.getRegulationId());
            assetList = assetRepository.findAssetsByRegulationId(obj.getRegulationId());
            if (assetList.size() != 0) {
                for (Asset as : assetList) {
                    assetRepository.deleteById(as.getAssetId());
                    assetFileList = assetFileRepository.findAllByAssetId(as.getAssetId());
                    if (assetFileList.size() != 0) {
                        for (AssetFile af : assetFileList) {
                            assetFileRepository.deleteById(af.getAssetFileId());
                        }
                    }
                    assetImageList = assetImageRepository.findAllByAssetId(as.getAssetId());
                    if (assetImageList.size() != 0) {
                        for (AssetImage ai : assetImageList) {
                            assetImageRepository.deleteById(ai.getAssetImageId());
                        }
                    }
                }
            }
            regulationFileList = regulationFileRepository.findAllByRegulationId(obj.getRegulationId());
            if (regulationFileList.size() != 0) {
                for (RegulationFile rf : regulationFileList) {
                    regulationFileRepository.deleteById(rf.getRegulationFileId());
                }
            }
            setSuccessForm("Xóa thành công");
            FacesUtil.redirect("/admin/quan-ly-dau-gia/quy-che-dau-gia.xhtml");
        } catch (Exception e) {
            setErrorForm("Xóa thất bại");
        }
    }

    public Date setDateSelectAfterChosseEndRegis() {
        if (regulationDto.getEndRegistrationDate() != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(regulationDto.getEndRegistrationDate());
            c.add(Calendar.DATE, 2);
            return c.getTime();
        }
        return null;
    }

    public void changeAuctionMethod() {
        autionMethodId = regulationDto.getAuctionMethodId().equals(1L);
    }

    public void getListImageByAssetId(AssetDto obj) {
        assetImageList = assetImageRepository.findAllByAssetId(obj.getAssetId());
    }

    public void getListFileByAssetId(AssetDto obj) {
        assetFileList = assetFileRepository.getByAssetId(obj.getAssetId());
    }

    public void resetDateRegisterStart() {
        regulationDto.setStartRegistrationDate(null);
    }

    public void resetDateRegisterEnd() {
        regulationDto.setEndRegistrationDate(null);
    }

    public void resetDateStartTime() {
        regulationDto.setStartTime(null);
        minusDays = null;
    }

    public void resetDatePaymentStart() {
        regulationDto.setPaymentStartTime(null);
    }

    public void resetDatePaymentEnd() {
        regulationDto.setPaymentEndTime(null);
    }

    public void resetTypeAssetId() {
        assetDto.setTypeAssetId(null);
    }

    public static String abbreviate(String text, int size) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        if (text.length() > size) {
            return text.substring(0, size).trim() + "...";
        }
        return text;
    }

    public void setAssetFileShowForDialog(AssetFile assetFile) {
        assetFileShowDialog = new AssetFile();
        BeanUtils.copyProperties(assetFile, assetFileShowDialog);
    }

    public void setRegulationFileDialogForShow(String regulationFile) {
        if (regulationFile == null) {
            return;
        }
        regulationFileDialog = regulationFile;
    }

    public Date getDateNow() {
        return DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_MINUTE);
    }

    @Override
    protected EScope getMenuId() {
        return EScope.REGULATION;
    }
}
