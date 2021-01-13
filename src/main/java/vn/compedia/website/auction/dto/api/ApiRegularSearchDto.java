package vn.compedia.website.auction.dto.api;

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
public class ApiRegularSearchDto extends BaseSearchDto {
    private Long name;
    private Date createDate;
    private Long createBy;
    private Date updaeDate;
    private Long updateBy;

}
