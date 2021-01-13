package vn.compedia.website.auction.dto.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.primefaces.model.UploadedFile;
import vn.compedia.website.auction.model.Banner;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BannerDto extends Banner{
    private UploadedFile uploadedFile;

    public BannerDto(Long bannerId, String imagePath, String bannerLink, Date createDate,Long createBy, Date updateDate, Long updateBy) {
        setBannerId(bannerId);
        setImagePath(imagePath);
        setBannerLink(bannerLink);
        setCreateDate(createDate);
        setCreateBy(createBy);
        setUpdateDate(updateDate);
        setUpdateBy(updateBy);
    }

}
