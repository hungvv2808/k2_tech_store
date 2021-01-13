package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.model.AssetImage;

import java.util.List;

public interface AssetImageRepository extends CrudRepository<AssetImage, Long>, AssetImageRepositoryCustom {
    AssetImage findAssetImageByAssetId(Long id);
    List<AssetImage> findAllByAssetId(Long assetId);
    @Query("select r from AssetImage r, Asset re where r.assetId = re.assetId and r.assetId = :assetId")
    AssetImage getByAssetId(@Param("assetId") Long assetId);
}
