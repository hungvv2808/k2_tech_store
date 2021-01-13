package vn.compedia.website.auction.controller.frontend;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.frontend.asset.AssetFEController;
import vn.compedia.website.auction.controller.frontend.regulation.RegulationFEController;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.auction.AssetSearchDto;
import vn.compedia.website.auction.dto.auction.AuctionRegisterDto;
import vn.compedia.website.auction.dto.auction.AuctionRegisterSearchDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.dto.regulation.RegulationSearchDto;
import vn.compedia.website.auction.dto.system.DecisionNewsDto;
import vn.compedia.website.auction.dto.system.DecisionNewsSearchDto;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Named
@Scope(value = "session")
@Getter
@Setter
public class HomeFEController extends BaseFEController {

    @Inject
    private AuthorizationFEController authorizationFEController;
    @Inject
    private RegulationFEController regulationFEController;
    @Inject
    private AssetFEController assetFEController;

    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private BannerRepository bannerRepository;
    @Autowired
    private DecisionNewsRepository decisionNewsRepository;

    private AssetSearchDto assetSearchDto;
    private List<AssetDto> allAssetDtoList;
    private List<AssetDto> comingAssetDtoList;
    private List<AssetDto> doingAssetDtoList;
    private List<AssetDto> expiredAssetDtoList;
    private List<AuctionRegisterDto> auctionRegisterDtoList;
    private AuctionRegisterSearchDto auctionRegisterSearchDto;
    private List<RegulationDto> regulationDtoList;
    private List<DecisionNewsDto> decisionNewsDtoList;
    private Regulation regulation;
    private RegulationSearchDto regulationSearchDto;
    private AssetDto assetDto;
    private AssetImage assetImage;
    private String keyword;
    private Long itemValue;
    private List<Banner> bannerList;
    private Date now;
    private Asset asset;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {


            init();
            resetAll();
        }
    }

    public void resetAll() {
        auctionRegisterDtoList = new ArrayList<>();
        auctionRegisterSearchDto = new AuctionRegisterSearchDto();
        regulation = new Regulation();
        regulationSearchDto = new RegulationSearchDto();
        assetSearchDto = new AssetSearchDto();
        comingAssetDtoList = new ArrayList<>();
        doingAssetDtoList = new ArrayList<>();
        expiredAssetDtoList = new ArrayList<>();
        bannerList = new ArrayList<>();
        now = new Date();
        DecisionNewsSearchDto decisionNewsSearchDto = new DecisionNewsSearchDto();
        AssetSearchDto comingAssetSearchDto = new AssetSearchDto();
        AssetSearchDto doingAssetSearchDto = new AssetSearchDto();
        AssetSearchDto expiredAssetSearchDto = new AssetSearchDto();
        AssetSearchDto endAssetSearchDto = new AssetSearchDto();

        decisionNewsSearchDto.setType(DbConstant.TYPE_NEWS);
        decisionNewsSearchDto.setPageSize(Constant.PAGE_SIZE_ROW);
        
        auctionRegisterSearchDto.setPageIndex(Constant.PAGE_INDEX);
        auctionRegisterSearchDto.setPageSize(Constant.PAGE_SIZE_ROW);
        
        regulationSearchDto.setPageIndex(Constant.PAGE_INDEX);
        regulationSearchDto.setPageSize(Constant.PAGE_SIZE_REGULATION_LIST);
        
        expiredAssetSearchDto.setPageIndex(Constant.PAGE_INDEX);
        expiredAssetSearchDto.setPageSize(Constant.PAGE_SIZE_ASSET_LIST);
        
        comingAssetSearchDto.setPageIndex(Constant.PAGE_INDEX);
        comingAssetSearchDto.setPageSize(Constant.PAGE_SIZE_ASSET_LIST);
        
        doingAssetSearchDto.setPageIndex(Constant.PAGE_INDEX);
        doingAssetSearchDto.setPageSize(Constant.PAGE_SIZE_ASSET_LIST);

        endAssetSearchDto.setPageIndex(Constant.PAGE_INDEX);
        endAssetSearchDto.setPageSize(Constant.PAGE_SIZE_ROW);

        comingAssetSearchDto.setRegulationStatus(DbConstant.REGULATION_STATUS_NOT_PUBLIC);
        comingAssetSearchDto.setStatus(Arrays.asList(DbConstant.ASSET_STATUS_WAITING));
        doingAssetSearchDto.setRegulationStatus(DbConstant.REGULATION_STATUS_NOT_PUBLIC);
        doingAssetSearchDto.setStatus(Arrays.asList(DbConstant.ASSET_STATUS_PLAYING));
        expiredAssetSearchDto.setRegulationStatus(DbConstant.REGULATION_STATUS_NOT_PUBLIC);
        expiredAssetSearchDto.setStatus(Arrays.asList(DbConstant.ASSET_STATUS_CANCELED, DbConstant.ASSET_STATUS_ENDED, DbConstant.ASSET_STATUS_NOT_SUCCESS));

        if (authorizationFEController.hasLogged()) {
            auctionRegisterSearchDto.setAccountId(authorizationFEController.getAccountDto().getAccountId());
            auctionRegisterSearchDto.setStatusList(Arrays.asList(DbConstant.ASSET_STATUS_ENDED, DbConstant.ASSET_STATUS_CANCELED, DbConstant.ASSET_STATUS_NOT_SUCCESS));
            auctionRegisterDtoList = auctionRegisterRepository.searchForGuest(auctionRegisterSearchDto);
        }

        bannerList = (List<Banner>) bannerRepository.findAll();
        
        decisionNewsDtoList = decisionNewsRepository.getDecisionNewsDto(decisionNewsSearchDto);
        regulationDtoList = regulationRepository.searchForGuest(regulationSearchDto);
        comingAssetDtoList = assetRepository.searchForGuestUser(comingAssetSearchDto);
        doingAssetDtoList = assetRepository.searchForGuestUser(doingAssetSearchDto);
        expiredAssetDtoList = assetRepository.searchForGuestUser(expiredAssetSearchDto);
        buildAllAssetDto();

        keyword = "";
    }

    public void onSearch() {
        if (itemValue == 1) {
            FacesUtil.redirect("/frontend/regulation/regulation_list.xhtml?keyword=" + keyword);
        }
        if (itemValue == 2) {
            FacesUtil.redirect("/frontend/asset/asset_list.xhtml?keyword=" + keyword);
        }
    }

    public boolean hasEnded(Integer status) {
        return Arrays.asList(
                DbConstant.ASSET_STATUS_ENDED,
                DbConstant.ASSET_STATUS_CANCELED,
                DbConstant.ASSET_STATUS_NOT_SUCCESS
        ).contains(status);
    }

    public String formatDate(Date date) {
        return DateUtil.formatToPattern(date, DateUtil.MMDDYYYYHHMMSS);
    }

    private void buildAllAssetDto() {
        allAssetDtoList = new ArrayList<>();
        if (appendToAllAssetDto(comingAssetDtoList)) {
            return;
        }
        if (appendToAllAssetDto(doingAssetDtoList)) {
            return;
        }
        if (appendToAllAssetDto(expiredAssetDtoList)) {
            return;
        }
    }

    private boolean appendToAllAssetDto(List<AssetDto> list) {
        for (AssetDto assetDto : list) {
            allAssetDtoList.add(assetDto);
            if (allAssetDtoList.size() >= Constant.PAGE_SIZE_ASSET_LIST) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_INDEX_FONTEND;
    }
}
