package vn.compedia.website.auction.dto.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseSearchDto;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DecisionNewsSearchDto extends BaseSearchDto {
    private Long newId;
    private String title;
    private String content;
    private boolean type;
    private Boolean status;
    private String imagePath;
    private Date CreateDate;
    private Long DecisionNewsId;
}
