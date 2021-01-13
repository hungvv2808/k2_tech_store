package vn.compedia.website.auction.controller.frontend.regulation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.model.Asset;
import vn.compedia.website.auction.model.AuctionRegister;
import vn.compedia.website.auction.model.Regulation;
import vn.compedia.website.auction.model.RegulationFile;
import vn.compedia.website.auction.repository.AssetRepository;
import vn.compedia.website.auction.repository.AuctionRegisterRepository;
import vn.compedia.website.auction.repository.RegulationFileRepository;
import vn.compedia.website.auction.repository.RegulationRepository;
import vn.compedia.website.auction.util.Constant;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@Scope(value = "session")
@Getter
@Setter
public class RegulationIsInProgressController extends BaseFEController {
    private Long regulationId;
    private Regulation regulation;
    private AuctionRegister auctionRegister;
    private RegulationDto regulationDto;
    private RegulationFile regulationFile;
    private List<Asset> assetList;

    @Inject
    private AuthorizationController authorizationController;

    @Autowired
    private RegulationRepository regulationRepository;

    @Autowired
    protected RegulationFileRepository regulationFileRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;

    public void resetAll() {
        regulation = new Regulation();
        regulationFile = new RegulationFile();
        regulationDto = new RegulationDto();
        auctionRegister = new AuctionRegister();
        regulation = regulationRepository.findRegulationByRegulationId(regulationId);
        assetList = assetRepository.findAssetsByRegulationId(regulationId);
        regulationFile = regulationFileRepository.findRegulationFileByRegulationIdAndType(regulationId,1);
    }

    public void RegistrationOfPropertyAuctions (Asset asset) {
        auctionRegister.setAssetId(asset.getAssetId());
        auctionRegister.setRegulationId(regulation.getRegulationId());
        auctionRegister.setAccountId(authorizationController.getAccountDto().getAccountId());
        auctionRegister.setFilePath(auctionRegister.getFilePath());
        auctionRegisterRepository.save(auctionRegister);
    }


    @Override
    protected String getMenuId() {
        return Constant.ID_REGULATION_DETAIL_FRONTEND;
    }
}
