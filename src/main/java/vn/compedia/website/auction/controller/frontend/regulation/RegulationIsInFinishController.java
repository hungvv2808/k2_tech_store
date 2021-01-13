package vn.compedia.website.auction.controller.frontend.regulation;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.frontend.AuthorizationFEController;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.controller.frontend.CommonFEController;
import vn.compedia.website.auction.controller.frontend.asset.AssetFEController;
import vn.compedia.website.auction.controller.frontend.auction.AuctionController;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.auction.AssetSearchDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.dto.regulation.RegulationSearchDto;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@Scope(value = "session")
@Getter
@Setter
public class RegulationIsInFinishController extends BaseFEController {
    @Inject
    private HttpServletRequest request;
    @Inject
    private AssetFEController assetFEController;
    @Inject
    private AuthorizationFEController authorizationFEController;
    @Inject
    private FacesNoticeController facesNoticeController;
    @Inject
    private AuctionController auctionController;
    @Inject
    private CommonFEController commonFEController;

    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private AssetImageRepository assetImageRepository;
    @Autowired
    protected RegulationFileRepository regulationFileRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AuctionReqRepository auctionReqRepository;
    @Autowired
    private AccountRepository accountRepository;

    private AssetSearchDto assetSearchDto;
    private Long regulationId;
    private Long assetId;
    private AssetDto assetDto;
    private RegulationSearchDto regulationSearchDto;
    private Regulation regulationDetail;
    private RegulationDto regulationDto;
    private RegulationFile regulationFile;
    private RegulationFile regulationFileCancel;
    private AssetImage assetImage;
    private List<RegulationFile> regulationFileList;
    private List<AssetDto> assetDtoList;
    private Date now;
    private AuctionReq auctionReq;
    private String id;
    private String assId;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            id = request.getParameter("id");
            assId = request.getParameter("assId");
            if (id == null || !StringUtil.isNumber(id)) {
                FacesUtil.redirect("/error/404.xhtml");
            } else {
                regulationId = Long.valueOf(id);
            }
            if (assId != null && !authorizationFEController.isCheck()) {
                Asset asset = assetRepository.findAssetByAssetId(Long.valueOf(assId));
                AssetDto assetDto = new AssetDto();
                BeanUtils.copyProperties(asset, assetDto);
                signUpToJoin(assetDto);
            }

            resetAll();
        }
    }


    public void resetAll() {
        assetDto = new AssetDto();
        regulationDetail = new RegulationDto();
        regulationFile = new RegulationFile();
        regulationDto = new RegulationDto();
        assetSearchDto = new AssetSearchDto();
        assetDtoList = new ArrayList<>();
        if (regulationId != null) {
            assetSearchDto.setPageIndex(0);
            assetSearchDto.setPageSize(2);
            assetSearchDto.setRegulationId(regulationId);
            assetSearchDto.setSortField("serialNumber");
            assetSearchDto.setSortOrder("ASC");
            assetSearchDto.setAccountId(authorizationFEController.getAccountDto().getAccountId());
            regulationDetail = regulationRepository.findRegulationByRegulationId(regulationId);
            assetDtoList = assetRepository.searchForGuestUser(assetSearchDto);
            assetImage = assetImageRepository.getByAssetId(assetId);
            regulationFile = regulationFileRepository.getByRegulationId(DbConstant.REGULATION_FILE_TYPE_QUY_CHE, regulationId);
            regulationFileCancel = regulationFileRepository.getByRegulationId(DbConstant.REGULATION_FILE_TYPE_HUY_BO, regulationId);
            auctionReq = auctionReqRepository.findAuctionReqByAuctionReqId(regulationDetail.getAuctionReqId());
        } else {
            assetDto = new AssetDto();
            regulationDetail = new RegulationDto();
            regulationFile = new RegulationFile();
            regulationDto = new RegulationDto();
        }
    }

    public String formatDate(Date date) {
        return DateUtil.formatToPattern(date, DateUtil.MMDDYYYYHHMMSS);
    }

    public void signUpToJoin(AssetDto dto) {
        if (!commonFEController.updateInfoBeforeJoin(dto) && authorizationFEController.hasLogged()) {
            if (authorizationFEController.hasLogged()) {
                setErrorForm("Bạn cần cập nhật thêm thông tin để đăng ký tham gia đấu giá");
            }
            authorizationFEController.setRegulationId(dto.getRegulationId());
            authorizationFEController.setAssId(dto.getAssetId());
            return;
        }
        if(!authorizationFEController.hasLogged()){
            facesNoticeController.addErrorMessage("Bạn vui lòng đăng nhập để đăng ký tham gia đấu giá");
            return;
        }
        facesNoticeController.openModal("auctionRegisterPopup");
        auctionController.setAssetDto(dto);
        auctionController.getUploadMultipleFileFEController().resetAll(null);
    }

    public void signUpToJoinOpen() {
        if (authorizationFEController.getAccountDto().getAccountId() == null ) {
            facesNoticeController.openModal("auctionRegisterPopupsecond");
        }
    }

    public boolean showButtonRegister(AssetDto dto) {
        return commonFEController.showButtonRegister(dto, auctionReq);
    }

    public long countdownShowButton() {
        try {
            long temp = -1;

            // register
            if (DbConstant.REGULATION_AUCTION_STATUS_WAITING == regulationDetail.getAuctionStatus()
                    && !auctionReq.getAccountId().equals(authorizationFEController.getAccountDto().getAccountId())) {
                Date startRegistrationDate = DateUtil.formatDate(regulationDetail.getStartRegistrationDate(), DateUtil.DATE_FORMAT_MINUTE);
                Date endRegistrationDate = DateUtil.formatDate(regulationDetail.getEndRegistrationDate(), DateUtil.DATE_FORMAT_SECOND_END);
                if (getNow().compareTo(startRegistrationDate) <= 0) {
                    temp = countMax(startRegistrationDate.getTime() - getNow().getTime());
                } else if (getNow().compareTo(endRegistrationDate) <= 0) {
                    temp = countMax(endRegistrationDate.getTime() - getNow().getTime());
                }
            }

            return temp;
        } catch (Exception e) {
            return -1;
        }
    }

    private long countMax(long number) {
        long maxCount = 24 * 60 * 60 * 1000;
        if (number <= maxCount) {
            return number;
        }
        return -1;
    }

    public Date getNow() {
        return new Date();
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_REGULATION_DETAIL_FRONTEND;
    }
}
