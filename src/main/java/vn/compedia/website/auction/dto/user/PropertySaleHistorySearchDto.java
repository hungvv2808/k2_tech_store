package vn.compedia.website.auction.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseSearchDto;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertySaleHistorySearchDto extends BaseSearchDto {
    private Long accountId;
    private boolean historyType;
    private Integer checkStatusSearch;
    private String assetName;
    private String regulationCode;
    private Integer activeStatus;
    private boolean statusWin;
}
