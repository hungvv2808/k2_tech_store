package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.AssetFile;

import javax.transaction.Transactional;
import java.util.List;

public interface AssetFileRepository extends CrudRepository<AssetFile, Long>, AssetImageRepositoryCustom {
    List<AssetFile> getByAssetId(Long assetId);
    List<AssetFile> deleteAssetFilesByAssetFileId(Long assetId);
    Long countAllByAssetId(Long id);
    List<AssetFile> findAllByAssetIdAndType(Long assetId, Integer type);
    List<AssetFile> findAllByAssetId(Long assetId);
    AssetFile findAssetFileByAssetIdAndType(Long id, int type);

    @Transactional
    void deleteAssetFileByAssetId(Long assetId);
    @Transactional
    void deleteAssetFileByAssetIdAndType(Long assetId, Integer type);
}
