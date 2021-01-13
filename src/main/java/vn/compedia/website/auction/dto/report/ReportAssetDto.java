package vn.compedia.website.auction.dto.report;

import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;

import java.util.Date;

@Getter
@Setter
public class ReportAssetDto extends AssetDto {
    private int stt;
    private String statusString;
    private String startingTimeString;
    private String createDateString;
    private Long endedPrice;
    private String regulationFilePath;

    public String getStatusString() {
        if (getStatus() == DbConstant.ASSET_STATUS_WAITING) {
            return "Chưa diễn ra";
        }
        if (getStatus() == DbConstant.ASSET_STATUS_PLAYING) {
            return "Đang diễn ra";
        }
        if (getStatus() == DbConstant.ASSET_STATUS_ENDED) {
            return "Đã kết thúc";
        }
        if (getStatus() == DbConstant.ASSET_STATUS_NOT_SUCCESS) {
            return "Không thành công";
        }
        if (getStatus() == DbConstant.ASSET_STATUS_CANCELED) {
            return "Đã tạm dừng đấu giá";
        }
        return statusString;
    }

    public String getStartingTimeString() {
        Date tmp = getStartTime();
        if (getRealStartTime() != null) {
            tmp = getRealStartTime();
        }
        return DateUtil.formatToPattern(tmp, DateUtil.DATE_FORMAT);
    }

    public String getCreateDateString() {
        return DateUtil.formatToPattern(createDate, DateUtil.DATE_FORMAT);
    }
}
