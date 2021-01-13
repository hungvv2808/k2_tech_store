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
public class ApiOfficialsSearchDto extends BaseSearchDto {
    private Long name;
    private String position;
    private String organization;
    private String email;
    private Date createDate;
    private Long createBy;
    private Date updaeDate;
    private Long updateBy;

}
