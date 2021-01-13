package vn.compedia.website.auction.dto.system;


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
public class ProcedureGuideSearchDto extends BaseSearchDto {
    private Long procedureGuideId;
    private String title;
    private String filePath;
    private Boolean status;
    private Date createDate;
}
