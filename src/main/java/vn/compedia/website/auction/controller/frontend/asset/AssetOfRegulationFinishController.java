package vn.compedia.website.auction.controller.frontend.asset;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.model.Asset;
import vn.compedia.website.auction.model.AssetImage;
import vn.compedia.website.auction.repository.AssetImageRepository;
import vn.compedia.website.auction.repository.AssetRepository;
import vn.compedia.website.auction.repository.RegulationFileRepository;
import vn.compedia.website.auction.repository.RegulationRepository;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.util.List;

@Named
@Scope(value = "session")
@Getter
@Setter
public class AssetOfRegulationFinishController extends BaseFEController {
    private Long assetId;
    private Asset asset;
    private AssetDto assetDto;
    private AssetImage assetImage;
    private List<Asset> assetList;
   // private List

    @Autowired
    private RegulationRepository regulationRepository;

    @Autowired
    private AssetImageRepository assetImageRepository;

    @Autowired
    protected RegulationFileRepository regulationFileRepository;

    @Autowired
    private AssetRepository assetRepository;


    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        asset = new Asset();
        assetDto = new AssetDto();
        assetImage = assetImageRepository.findAssetImageByAssetId(assetId);
        assetList = assetRepository.findAssetsByRegulationId(assetId);
    }


    @Override
    protected String getMenuId() {
        return null;
    }
}
