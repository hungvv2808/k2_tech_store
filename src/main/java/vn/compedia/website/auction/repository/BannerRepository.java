package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.dto.system.BannerDto;
import vn.compedia.website.auction.model.Banner;

import java.util.List;

public interface BannerRepository extends CrudRepository <Banner,Long> , BannerRepositoryCustom{
    @Query("select new vn.compedia.website.auction.dto.system.BannerDto(bn.bannerId, bn.imagePath, bn.bannerLink,bn.createDate,bn.createBy,bn.updateDate,bn.updateBy) from Banner bn ")
    List<BannerDto> getAllBannerDto();

}
